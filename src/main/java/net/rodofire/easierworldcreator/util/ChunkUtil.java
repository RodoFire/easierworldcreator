package net.rodofire.easierworldcreator.util;

import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldAccess;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.*;

public class ChunkUtil {
    private static final int REGION_SIZE = 32;
    private WorldAccess worldAccess;

    private static final WorldSavePath DIRECTORY = createInstance("chunkslist");
    private static final int CHUNK_DATA_SIZE = 8;
    static {
        int i;
    }

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


}
