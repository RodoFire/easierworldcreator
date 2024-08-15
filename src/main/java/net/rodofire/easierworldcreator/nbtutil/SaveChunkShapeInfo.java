package net.rodofire.easierworldcreator.nbtutil;

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
 * <p>
 * This class is used to create JSON files.
 * It is used by this mod to divide a structure into chunks.
 * If the structure is larger than a chunk, it will save the structure into chunks that will be saved into JSON files.
 * The JSON files will then be saved into the following path : [save_name]/generated/easierworldcreator/[chunk.x-chunk.z]/custom_feature_[Random long].
 * It will then be read by the following class {@link LoadChunkSapeInfo}
 * </p>
 * <p>
 * Since that to generate large structures, it requires to write and read the json file, be careful to don't have a too big structure.
 * It may use a lot of performance during the write of every files
 * </p>
 */
public class SaveChunkShapeInfo {

    //determines the number of threads that the class can use
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();


    /**
     * This is the main method on saving the structure into JSON files
     * <p>
     * I multithreaded this class for optimal performance.
     * Since that the structure is divided into chunks, we can multithread the generation of files
     * </p>
     *
     * @param blockLists  the list to divide into chunks and then saving it into JSON files
     * @param worldAccess the world the structure will spawn in
     * @throws IOException avoid errors
     */
    public static void saveDuringWorldGen(List<BlockList> blockLists, StructureWorldAccess worldAccess, String name) throws IOException {
        Path generatedPath = Objects.requireNonNull(worldAccess.getServer()).getSavePath(WorldSavePath.GENERATED).normalize();
        Path path = createFolders(generatedPath);
        List<BlockList> sortedList = sortBlockPos(blockLists);
        List<List<BlockList>> dividedList = divideBlocks(sortedList);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        System.out.println("path " + generatedPath);

        if (generatedPath != null) {
            for (List<BlockList> chunkBlockLists : dividedList) {
                executorService.submit(() -> {
                    try {
                        saveToJson(chunkBlockLists, path, name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
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
     * @param blockLists the list of blockList that will be converted into json
     * @throws IOException avoid errors
     */
    private static void saveToJson(List<BlockList> blockLists, Path basePath, String name) throws IOException {
        // Determine chunk-specific file path
        // You might need to extract the chunk position information from blockLists to create the file name
        ChunkPos chunkPos = new ChunkPos(blockLists.get(0).getPoslist().get(0)); // extract from blockLists
        Path chunkDirectoryPath = basePath.resolve("chunk_" + chunkPos.x + "_" + chunkPos.z);
        Files.createDirectories(chunkDirectoryPath);
        Path chunkFilePath = chunkDirectoryPath.resolve(name + ".json");


        // Serialize and save the BlockList to a JSON file
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();

        for (BlockList blockList : blockLists) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("state", blockList.getBlockstate().toString());

            JsonArray positions = new JsonArray();
            for (BlockPos pos : blockList.getPoslist()) {
                JsonObject posObject = new JsonObject();
                posObject.addProperty("x", pos.getX());
                posObject.addProperty("y", pos.getY());
                posObject.addProperty("z", pos.getZ());
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
    public static List<BlockList> sortBlockPos(List<BlockList> blockLists) {
        for (BlockList blockList : blockLists) {
            // Tri des positions par axe X, puis Y, puis Z
            blockList.getPoslist().sort(Comparator
                    .comparingInt(BlockPos::getX)
                    .thenComparingInt(BlockPos::getZ)
                    .thenComparingInt(BlockPos::getY));
        }
        return blockLists;
    }

    /**
     * divides the a list of blockList into a list of list of blockList that represents every Chunk of the BlockList
     *
     * @param blockLists the list to divide into a list of chunks
     * @return the blockLists divided into chunks
     */
    public static List<List<BlockList>> divideBlocks(List<BlockList> blockLists) {
        Map<ChunkPos, List<BlockList>> chunkMap = new HashMap<>();

        for (BlockList blockList : blockLists) {
            for (BlockPos pos : blockList.getPoslist()) {
                ChunkPos chunkPos = new ChunkPos(pos);

                // Récupérer ou créer une liste pour ce chunk
                List<BlockList> blockListsInChunk = chunkMap.computeIfAbsent(chunkPos, k -> new ArrayList<>());

                // Cherche s'il existe déjà un BlockList dans ce chunk avec le même BlockState
                Optional<BlockList> matchingBlockList = blockListsInChunk.stream()
                        .filter(bl -> bl.getBlockstate().equals(blockList.getBlockstate()))
                        .findFirst();

                if (matchingBlockList.isPresent()) {
                    // Si trouvé, ajoutez le BlockPos à ce BlockList
                    matchingBlockList.get().addBlockPos(pos);
                } else {
                    // Sinon, créez un nouveau BlockList pour ce BlockPos
                    BlockList newBlockList = new BlockList(List.of(pos), blockList.getBlockstate(), blockList.getTag());
                    blockListsInChunk.add(newBlockList);
                }
            }
        }

        // Convertir le Map en une List de List
        return new ArrayList<>(chunkMap.values());
    }

    public static Path createFolders(Path path) throws IOException {
        Files.createDirectories(path);
        path = path.resolve(Easierworldcreator.MOD_ID);
        Files.createDirectories(path);
        path = path.resolve("structures");
        Files.createDirectories(path);
        return path;
    }


}