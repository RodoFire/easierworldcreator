package net.rodofire.easierworldcreator.fileutil;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Ewc;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.DefaultBlockList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * <p>
 * That is a way to generate a really large structure.
 * Since that the nbt is saved during world gen, no block entity should be present.
 * </p>
 * <p>This class is used to create JSON files.
 * <p>It is used by this mod to divide a structure into chunks.
 * <p>If the structure is larger than a chunk, it will save the structure into chunks that will be saved into JSON files.
 * <p>The JSON files will then be saved into the following path : [save_name]/generated/easierworldcreator/[chunk.x-chunk.z]/custom_feature_[Random long].
 * <p>It will then be read by the following class {@link LoadChunkShapeInfo}
 * </p>
 * <p>
 * Since that to generate large structures, it requires to write and read the json file, be careful to don't have a too big structure.
 * It may use a lot of performance during the write of every files
 * </p>
 */
@SuppressWarnings("unused")
public class SaveChunkShapeInfo {

    //determines the number of threads that the class can use
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();


    /**
     * This is the main method on saving the structure into JSON files
     * <p>
     * I multithreaded this class for optimal performance.
     * Since that the structure is divided into chunks, we can multithreading the generation of files
     * </p>
     *
     * @param defaultBlockLists the list to divide into chunks and then saving it into JSON files
     * @param worldAccess       the world the structure will spawn in
     * @throws IOException avoid errors
     */
    public static void saveDuringWorldGen(Set<DefaultBlockList> defaultBlockLists, StructureWorldAccess worldAccess, String name, BlockPos offset) throws IOException {
        Path generatedPath = Objects.requireNonNull(worldAccess.getServer()).getSavePath(WorldSavePath.GENERATED).normalize();
        Path path = createFolders(generatedPath);
        Set<DefaultBlockList> sortedList = sortBlockPos(defaultBlockLists);
        List<Set<DefaultBlockList>> dividedList = divideBlocks(sortedList);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        for (Set<DefaultBlockList> chunkDefaultBlockLists : dividedList) {
            executorService.submit(() -> {
                try {
                    saveToJson(chunkDefaultBlockLists, path, name, offset);
                } catch (IOException e) {
                    e.fillInStackTrace();
                }
            });
        }
    }

    /**
     * This is the main method on saving the structure into JSON files
     * <p>
     * I multithreaded this class for optimal performance.
     * Since that the structure is divided into chunks, we can multithreading the generation of files
     * </p>
     *
     * @param defaultBlockLists the list to divide into chunks and then saving it into JSON files
     * @param worldAccess       the world the structure will spawn in
     */
    public static void saveChunkWorldGen(Set<DefaultBlockList> defaultBlockLists, StructureWorldAccess worldAccess, String name, BlockPos offset) throws IOException {
        Path generatedPath = Objects.requireNonNull(worldAccess.getServer()).getSavePath(WorldSavePath.GENERATED).normalize();
        Path path = createFolders(generatedPath);
        Set<DefaultBlockList> sortedList = sortBlockPos(defaultBlockLists);
        //ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        //executorService.submit(() -> {
        try {
            saveToJson(sortedList, path, name, offset);
        } catch (IOException e) {
            e.fillInStackTrace();
        }
        //});

    }

    /**
     * This method allows you to generate files that have the blockStates as well as their BlockPos.
     * It is used instead of the method on top because the JSON file doesn't get compressed.
     * It allows better performance since that the processor doesn't have to compress as well as decompress the files.
     * <p>
     * That is a way to generate a really large structure.
     * Since that the nbt is saved during world gen, no block entity should be present.
     * </p>
     * <p>
     * The methods receive a list of blockList.
     * The list represents all the blocks of the generated structure.
     * for every BlockPos and BlockStates, the method verify the chunk it belongs to and add it to the Map chunkBlockInfoMap.
     * This divides the structure into chunks that will be saved just after converting the first list into a {@link StructureTemplate.PalettedBlockInfoList}
     * The Structure will be located in the following path : [save_name]/generated/easierworldcreator/[chunk.x-chunk.z]/custom_feature_[Random long]
     * </p>
     *
     * @param defaultBlockLists the list of blockList that will be converted into JSON
     * @throws IOException avoid errors
     */
    private static void saveToJson(Set<DefaultBlockList> defaultBlockLists, Path basePath, String name, BlockPos offset) throws IOException {
        // Determine chunk-specific file path
        // You might need to extract the chunk position information from blockLists to create the file name
        Optional<DefaultBlockList> optional = defaultBlockLists.stream().findFirst();
        if (optional.isEmpty()) {
            return;
        }

        DefaultBlockList firstDefaultBlockList = optional.get();
        ChunkPos chunkPos = new ChunkPos(firstDefaultBlockList.getPosList().get(0).add(offset)); // extract from blockLists

        Path chunkDirectoryPath = basePath.resolve("chunk_" + chunkPos.x + "_" + chunkPos.z);
        Files.createDirectories(chunkDirectoryPath);
        Path chunkFilePath = chunkDirectoryPath.resolve(name + ".json");


        // Serialize and save the BlockList to a JSON file
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();

        JsonObject jsonObject;

        int offsetX = offset.getX();
        int offsetZ = offset.getZ();

        for (DefaultBlockList defaultBlockList : defaultBlockLists) {
            jsonObject = new JsonObject();
            jsonObject.addProperty("state", defaultBlockList.getBlockState().toString());

            JsonArray positions = new JsonArray();
            for (BlockPos pos : defaultBlockList.getPosList()) {
                JsonObject posObject = new JsonObject();
                posObject.addProperty("x", pos.getX() + offsetX);
                posObject.addProperty("y", pos.getY());
                posObject.addProperty("z", pos.getZ() + offsetZ);
                positions.add(posObject);
            }
            jsonObject.add("positions", positions);
            jsonArray.add(jsonObject);
        }

        Files.writeString(chunkFilePath, gson.toJson(jsonArray));
    }


    /**
     * this method is used to sort the BlocKPos of a BlockList
     *
     * @param defaultBlockLists the list to sort
     * @return the sorted list
     */
    public static Set<DefaultBlockList> sortBlockPos(Set<DefaultBlockList> defaultBlockLists) {
        for (DefaultBlockList defaultBlockList : defaultBlockLists) {
            defaultBlockList.getPosList().sort(Comparator
                    .comparingInt(BlockPos::getX)
                    .thenComparingInt(BlockPos::getZ)
                    .thenComparingInt(BlockPos::getY));
        }
        return defaultBlockLists;
    }

    /**
     * divides a list of blockList into a list of blockList that represents every Chunk of the BlockList
     *
     * @param defaultBlockLists the list to divide into a list of chunks
     * @return the blockLists divided into chunks
     */
    public static List<Set<DefaultBlockList>> divideBlocks(Set<DefaultBlockList> defaultBlockLists) {
        Map<ChunkPos, Set<DefaultBlockList>> chunkMap = new HashMap<>();

        for (DefaultBlockList defaultBlockList : defaultBlockLists) {
            for (BlockPos pos : defaultBlockList.getPosList()) {
                ChunkPos chunkPos = new ChunkPos(pos);

                Set<DefaultBlockList> blockListsInChunk = chunkMap.computeIfAbsent(chunkPos, k -> new HashSet<>());

                Optional<DefaultBlockList> matchingBlockList = blockListsInChunk.stream()
                        .filter(bl -> bl.getBlockState().equals(defaultBlockList.getBlockState()))
                        .findFirst();

                if (matchingBlockList.isPresent()) {
                    matchingBlockList.get().addBlockPos(pos);
                } else {
                    DefaultBlockList newDefaultBlockList = new DefaultBlockList(List.of(pos), defaultBlockList.getBlockState()/*, defaultBlockList.getTag()*/);
                    blockListsInChunk.add(newDefaultBlockList);
                }
            }
        }
        return new ArrayList<>(chunkMap.values());
    }

    /**
     * this method allows the creation of the generated and related folders
     *
     * @param path the base path
     * @return the generated Path
     */
    public static Path createFolders(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        path = path.resolve(Ewc.MOD_ID);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        path = path.resolve("structures");
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return path;
    }

    public static Path getMultiChunkPath(StructureWorldAccess worldAccess, ChunkPos chunkPos) {
        MinecraftServer server = worldAccess.getServer();
        Path var;
        if(server != null) {
            Path generatedPath = server.getSavePath(WorldSavePath.GENERATED).normalize();
            var = createFolders(generatedPath).resolve("chunk_" + chunkPos.x + "_" + chunkPos.z);
            try {
                Files.createDirectories(var);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return var;
        }
        return null;
    }


}
