package net.rodofire.easierworldcreator.fileutil;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Easierworldcreator;
import net.rodofire.easierworldcreator.shapeutil.BlockList;

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
     * @param blockLists  the list to divide into chunks and then saving it into JSON files
     * @param worldAccess the world the structure will spawn in
     * @throws IOException avoid errors
     */
    public static void saveDuringWorldGen(Set<BlockList> blockLists, StructureWorldAccess worldAccess, String name, BlockPos offset) throws IOException {
        Path generatedPath = Objects.requireNonNull(worldAccess.getServer()).getSavePath(WorldSavePath.GENERATED).normalize();
        Path path = createFolders(generatedPath);
        Set<BlockList> sortedList = sortBlockPos(blockLists);
        List<Set<BlockList>> dividedList = divideBlocks(sortedList);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        for (Set<BlockList> chunkBlockLists : dividedList) {
            executorService.submit(() -> {
                try {
                    saveToJson(chunkBlockLists, path, name, offset);
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
     * @param blockLists  the list to divide into chunks and then saving it into JSON files
     * @param worldAccess the world the structure will spawn in
     */
    public static void saveChunkWorldGen(Set<BlockList> blockLists, StructureWorldAccess worldAccess, String name, BlockPos offset) throws IOException {
        Path generatedPath = Objects.requireNonNull(worldAccess.getServer()).getSavePath(WorldSavePath.GENERATED).normalize();
        Path path = createFolders(generatedPath);
        Set<BlockList> sortedList = sortBlockPos(blockLists);
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
     * @param blockLists the list of blockList that will be converted into JSON
     * @throws IOException avoid errors
     */
    private static void saveToJson(Set<BlockList> blockLists, Path basePath, String name, BlockPos offset) throws IOException {
        // Determine chunk-specific file path
        // You might need to extract the chunk position information from blockLists to create the file name
        Optional<BlockList> optional = blockLists.stream().findFirst();
        if (optional.isEmpty()) {
            return;
        }

        BlockList firstBlockList = optional.get();
        ChunkPos chunkPos = new ChunkPos(firstBlockList.getPosList().get(0).add(offset)); // extract from blockLists

        Path chunkDirectoryPath = basePath.resolve("chunk_" + chunkPos.x + "_" + chunkPos.z);
        Files.createDirectories(chunkDirectoryPath);
        Path chunkFilePath = chunkDirectoryPath.resolve(name + ".json");


        // Serialize and save the BlockList to a JSON file
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();

        JsonObject jsonObject;


        for (BlockList blockList : blockLists) {
            jsonObject = new JsonObject();
            jsonObject.addProperty("state", blockList.getBlockState().toString());

            JsonArray positions = new JsonArray();
            for (BlockPos pos : blockList.getPosList()) {
                JsonObject posObject = new JsonObject();
                posObject.addProperty("x", pos.getX() + offset.getX());
                posObject.addProperty("y", pos.getY());
                posObject.addProperty("z", pos.getZ() + offset.getZ());
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
     * @param blockLists the list to sort
     * @return the sorted list
     */
    public static Set<BlockList> sortBlockPos(Set<BlockList> blockLists) {
        for (BlockList blockList : blockLists) {
            blockList.getPosList().sort(Comparator
                    .comparingInt(BlockPos::getX)
                    .thenComparingInt(BlockPos::getZ)
                    .thenComparingInt(BlockPos::getY));
        }
        return blockLists;
    }

    /**
     * divides a list of blockList into a list of blockList that represents every Chunk of the BlockList
     *
     * @param blockLists the list to divide into a list of chunks
     * @return the blockLists divided into chunks
     */
    public static List<Set<BlockList>> divideBlocks(Set<BlockList> blockLists) {
        Map<ChunkPos, Set<BlockList>> chunkMap = new HashMap<>();

        for (BlockList blockList : blockLists) {
            for (BlockPos pos : blockList.getPosList()) {
                ChunkPos chunkPos = new ChunkPos(pos);

                Set<BlockList> blockListsInChunk = chunkMap.computeIfAbsent(chunkPos, k -> new HashSet<>());

                Optional<BlockList> matchingBlockList = blockListsInChunk.stream()
                        .filter(bl -> bl.getBlockState().equals(blockList.getBlockState()))
                        .findFirst();

                if (matchingBlockList.isPresent()) {
                    matchingBlockList.get().addBlockPos(pos);
                } else {
                    BlockList newBlockList = new BlockList(List.of(pos), blockList.getBlockState(), blockList.getTag());
                    blockListsInChunk.add(newBlockList);
                }
            }
        }
        return new ArrayList<>(chunkMap.values());
    }

    /**
     * this method allows the creation of the generated and related folders
     * @param path the base path
     * @return the generated Path
     */
    public static Path createFolders(Path path) throws IOException {
        Files.createDirectories(path);
        path = path.resolve(Easierworldcreator.MOD_ID);
        Files.createDirectories(path);
        path = path.resolve("structures");
        Files.createDirectories(path);
        return path;
    }


}
