package net.rodofire.easierworldcreator.util;

import com.mojang.datafixers.util.Either;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.rodofire.easierworldcreator.Easierworldcreator;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ChunkUtil {
    private static final int REGION_SIZE = 32;
    private WorldAccess worldAccess;

    private static final WorldSavePath DIRECTORY = createInstance("chunkslist");
    private static final int CHUNK_DATA_SIZE = 8;
    private static final Set<ChunkPos> protectedChunks = new HashSet<>();
    private static final Queue<ChunkPos> chunkQueue = new LinkedList<>();

    private static void setDirectory(WorldAccess worldAccess) {
        Path generatedPath = Objects.requireNonNull(worldAccess.getServer()).getSavePath(DIRECTORY).normalize();
        File dir = new File(generatedPath.toString());
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    static WorldSavePath createInstance(String relativePath) {
        try {
            Constructor<WorldSavePath> constructor = WorldSavePath.class.getDeclaredConstructor(String.class);
            constructor.setAccessible(true);
            return constructor.newInstance(relativePath);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                 InvocationTargetException e) {
            throw new RuntimeException("Impossible d'instancier WorldSavePath", e);
        }
    }

    /**
     * Method to verify if a chunk has been generated. This method could be useful for generating multi-chunk shapes
     * @param world the world of the chunk
     * @param chunkPos the pos of the chunk
     * @return a {@link Boolean} that says if the chunk was generated
     */
    public static boolean isChunkGenerated(ChunkPos chunkPos, WorldAccess world) {
        int regionX = chunkPos.x / REGION_SIZE;
        int regionZ = chunkPos.z / REGION_SIZE;
        String fileName = getRegionFileName(regionX, regionZ, world);

        try (RandomAccessFile file = new RandomAccessFile(fileName, "r")) {
            return findChunkInFile(file, chunkPos);
        } catch (IOException e) {
            return false;
        }
    }

    public static String getRegionFileName(int regionX, int regionZ, WorldAccess worldAccess) {
        return Objects.requireNonNull(worldAccess.getServer()).getSavePath(DIRECTORY).normalize().toString() + File.separator + "region_" + regionX + "_" + regionZ + ".bin";
    }

    public static boolean findChunkInFile(RandomAccessFile file, ChunkPos chunkPos) throws IOException {
        long low = 0;
        long high = (file.length() / CHUNK_DATA_SIZE) - 1;

        while (low <= high) {
            long mid = (low + high) / 2;
            long pointer = mid * CHUNK_DATA_SIZE;
            file.seek(pointer);

            int x = file.readInt();
            int z = file.readInt();

            if (x == chunkPos.x && z == chunkPos.z) {
                return true;
            } else if (x < chunkPos.x || (x == chunkPos.x && z < chunkPos.z)) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return false;
    }


    public static void addChunk(ChunkPos chunkPos, WorldAccess world) {
        setDirectory(world);

        int regionX = chunkPos.x / REGION_SIZE;
        int regionZ = chunkPos.z / REGION_SIZE;

        String fileName = getRegionFileName(regionX, regionZ, world);
        File file = new File(fileName);

        if (file.exists()) {
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                if (findChunkInFile(raf, chunkPos)) {
                    return;
                }

                List<ChunkPos> chunks = readChunksFromFile(raf);
                chunks.add(chunkPos);
                chunks.sort(Comparator.comparingInt((ChunkPos c) -> c.x).thenComparingInt(c -> c.z));

                raf.setLength(0);
                for (ChunkPos pos : chunks) {
                    raf.writeInt(pos.x);
                    raf.writeInt(pos.z);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                raf.writeInt(chunkPos.x);
                raf.writeInt(chunkPos.z);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeChunks(Set<ChunkPos> chunks, WorldAccess world) {
        setDirectory(world);

        Map<String, List<ChunkPos>> regionMap = new HashMap<>();

        for (ChunkPos chunkPos : chunks) {
            int regionX = (int) ((float) chunkPos.x / (float) REGION_SIZE);
            int regionZ = (int) ((float) chunkPos.z / (float) REGION_SIZE);

            String fileName = getRegionFileName(regionX, regionZ, world);
            regionMap.computeIfAbsent(fileName, k -> new ArrayList<>()).add(chunkPos);
        }

        for (Map.Entry<String, List<ChunkPos>> entry : regionMap.entrySet()) {
            String fileName = entry.getKey();
            List<ChunkPos> chunkList = entry.getValue();
            File file = new File(fileName);

            if (file.exists()) {
                try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                    List<ChunkPos> existingChunks = readChunksFromFile(raf);
                    chunkList.addAll(existingChunks);
                    chunkList.sort(Comparator.comparingInt((ChunkPos c) -> c.x).thenComparingInt(c -> c.z));
                    raf.setLength(0);
                    for (ChunkPos chunkPos : chunkList) {
                        raf.writeInt(chunkPos.x);
                        raf.writeInt(chunkPos.z);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {

                    chunkList.sort(Comparator.comparingInt((ChunkPos c) -> c.x).thenComparingInt(c -> c.z));
                    for (ChunkPos chunkPos : chunkList) {
                        raf.writeInt(chunkPos.x);
                        raf.writeInt(chunkPos.z);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<ChunkPos> readChunksFromFile(RandomAccessFile file) throws IOException {
        List<ChunkPos> chunks = new ArrayList<>();
        long fileLength = file.length();
        for (long pointer = 0; pointer < fileLength; pointer += CHUNK_DATA_SIZE) {
            file.seek(pointer);
            int x = file.readInt();
            int z = file.readInt();
            chunks.add(new ChunkPos(x, z));
        }
        return chunks;
    }

    public static boolean isProtectedChunk(ChunkPos chunkPos) {
        return protectedChunks.contains(chunkPos);
    }

    public static void protectChunk(ChunkPos chunkPos) {
        protectedChunks.add(chunkPos);
    }

    public static void unprotectChunk(ChunkPos chunkPos) {
        protectedChunks.remove(chunkPos);
        queueChunkForLater(chunkPos);
    }

    public static void queueChunkForLater(ChunkPos chunkPos) {
        chunkQueue.add(chunkPos);
    }

    public static void processChunkQueue(StructureWorldAccess world) {
        while (!chunkQueue.isEmpty()) {
            ChunkPos chunkPos = chunkQueue.poll();

            Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.EMPTY);

            if (chunk.getStatus().isAtLeast(ChunkStatus.FULL)) {
                continue;
            }

            world.getChunkManager().getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);

            /*ChunkRegion chunkRegion = new ChunkRegion((ServerWorld) world, List.of(chunk), chunk.getStatus(), 1);

            for (ChunkStatus status : ChunkStatus.createOrderedList()) {
                if (chunk.getStatus().isAtLeast(status)) {
                    continue;
                }
                ServerChunkManager serverChunkManager = ((ServerWorld) world).getChunkManager();
                serverChunkManager.threadedAnvilChunkStorage.verifyChunkGenerator();

                // Exécutez la tâche de génération pour chaque statut
                CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> generationTask = status.runGenerationTask(
                        Runnable::run,  // Utilisez le thread actuel, vous pouvez utiliser un Executor si besoin
                        (ServerWorld) world,
                        serverChunkManager.getChunkGenerator(),
                        ((ServerWorld) world).getStructureTemplateManager(),
                        world.getLightingProvider(),
                        chunkToProcess -> {
                            return chunk;
                        },
                        List.of(chunk)
                );

                // Assurez-vous que la tâche est terminée
                try {
                    Either<Chunk, ChunkHolder.Unloaded> result = generationTask.get();
                    if (result.left().isPresent()) {
                        chunk = result.left().get();
                    } else {
                        // Gérez le cas où le chunk n'a pas pu être généré
                        Easierworldcreator.LOGGER.warn("Failed to generate chunk at " + chunkPos);
                        break;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    break;
                }
            }

            // Marquer le chunk comme mis à jour une fois qu'il est entièrement généré
            if (chunk.getStatus().isAtLeast(ChunkStatus.FULL)) {
                world.getChunkManager().markForUpdate(chunk);
            }*/
        }
    }

    public static void resumePausedChunks(StructureWorldAccess world) {
        Objects.requireNonNull(world.getServer()).execute(() -> {
            ChunkUtil.processChunkQueue(world);
        });
    }


}
