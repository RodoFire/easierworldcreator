package net.rodofire.easierworldcreator.fileutil;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.rodofire.easierworldcreator.Easierworldcreator;
import net.rodofire.easierworldcreator.shapeutil.BlockList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * class to load JSON files related to multi-chunk features
 */
public class LoadChunkShapeInfo {
    /**
     * method to load structure from a JSON file
     *
     * @param world         the world the shape will spawn in
     * @param chunkFilePath the path of the shape
     * @return a {@link List} used later to place the BlockStates
     */
    public static List<BlockList> loadFromJson(StructureWorldAccess world, Path chunkFilePath) throws IOException {
        String jsonContent = Files.readString(chunkFilePath);

        Gson gson = new Gson();

        JsonArray jsonArray = gson.fromJson(jsonContent, JsonArray.class);

        List<BlockList> blockLists = new ArrayList<>();
        String fileName = chunkFilePath.getFileName().toString();

        Pattern pattern = Pattern.compile("\\[(-?\\d+),(-?\\d+)]_.*\\.json");
        Matcher matcher = pattern.matcher(fileName);
        int offsetX = 0, offsetZ = 0;

        if (matcher.matches()) {
            offsetX = Integer.parseInt(matcher.group(1));
            offsetZ = Integer.parseInt(matcher.group(2));
        }


        for (JsonElement element : jsonArray) {
            JsonObject jsonObject = element.getAsJsonObject();

            // Get the block state
            String stateString = jsonObject.get("state").getAsString();
            BlockState blockState = parseBlockState(world, stateString);  // You need to implement this method

            // Get the positions
            List<BlockPos> posList = new ArrayList<>();
            JsonArray positionsArray = jsonObject.getAsJsonArray("positions");

            for (JsonElement posElement : positionsArray) {
                JsonObject posObject = posElement.getAsJsonObject();
                int x = posObject.get("x").getAsInt() + offsetX;
                int y = posObject.get("y").getAsInt();
                int z = posObject.get("z").getAsInt() + offsetZ;
                posList.add(new BlockPos(x, y, z));
            }

            // Create a new BlockList and add it to the set
            BlockList blockList = new BlockList(posList, blockState);
            blockLists.add(blockList);
        }

        return blockLists;
    }

    /**
     * method to place the structure
     *
     * @param world      the world the structure will spawn in
     * @param blockLists the list of blockList that compose the structure
     */
    public static void placeStructure(StructureWorldAccess world, List<BlockList> blockLists) {
        for (BlockList blockList : blockLists) {
            BlockState state = blockList.getBlockstate();
            for (BlockPos pos : blockList.getPoslist()) {
                world.setBlockState(pos, state, 3);
            }
        }
    }

    /**
     * method used to convert {@link String} to BlockState
     *
     * @param world       used to get the registry entry
     * @param stateString the {@link String} related to the{@link BlockState}
     * @return the {@link BlockState} converted
     */
    private static BlockState parseBlockState(StructureWorldAccess world, String stateString) {
        RegistryEntryLookup<Block> blockLookup = world.createCommandRegistryWrapper(RegistryKeys.BLOCK);

        //Identifier identifier = new Identifier(stateString.split("\\[")[0]);
        Identifier identifier = new Identifier(extractBlockName(stateString.split("\\[")[0]));
        Optional<? extends RegistryEntry<Block>> optional = blockLookup.getOptional(RegistryKey.of(RegistryKeys.BLOCK, identifier));
        if (optional.isEmpty()) {
            Easierworldcreator.LOGGER.error("error parsing BlockState: " + stateString.split("\\[")[0]);
            return Blocks.AIR.getDefaultState();
        }
        ;

        Block block = (Block) ((RegistryEntry) optional.get()).value();
        BlockState blockState = block.getDefaultState();

        if (stateString.contains("[")) {
            String propertiesPart = stateString.split("\\[")[1].replace("]", "");
            String[] keyValuePairs = propertiesPart.split(",");

            StateManager<Block, BlockState> stateManager = block.getStateManager();

            for (String pair : keyValuePairs) {
                String[] keyValue = pair.split("=");
                Property<?> property = stateManager.getProperty(keyValue[0]);
                if (property != null) {
                    blockState = applyProperty(blockState, property, keyValue[1]);
                }
            }
        }

        return blockState;
    }

    /**
     * methods to apply the property to a BlockState
     *
     * @param state    the previous states of the {@link BlockState}
     * @param property the property related
     * @param value    the value of the property
     * @param <T>      the type of the property value, must be Comparable
     * @return the changed {@link BlockState}
     */
    private static <T extends Comparable<T>> BlockState applyProperty(BlockState state, Property<T> property, String value) {
        T propertyValue = property.parse(value).orElseThrow(() -> new IllegalArgumentException("Invalid property value"));
        return state.with(property, propertyValue);
    }

    /**
     * method to verify if there's a JSON files in the chunk folder
     *
     * @param world the world of the structure
     * @param chunk the chunk that will be converted into a {@link ChunkPos}
     * @return the list of the structure path to be placed later
     */
    public static List<Path> verifyFiles(StructureWorldAccess world, Chunk chunk) throws IOException {
        return verifyFiles(world, chunk.getPos());
    }

    /**
     * method to verify if there's a JSON files in the chunk folder
     *
     * @param world the world of the structure
     * @param pos   the {@link BlockPos} that will be converted into a {@link ChunkPos}
     * @return the list of the structure path to be placed later
     */
    public static List<Path> verifyFiles(StructureWorldAccess world, BlockPos pos) throws IOException {
        return verifyFiles(world, new ChunkPos(pos));
    }

    /**
     * method to verify if there's a JSON files in the chunk folder
     *
     * @param world the world of the structure
     * @param chunk the chunk that needs to be verified
     * @return the list of the structure path to be placed later
     */
    public static List<Path> verifyFiles(StructureWorldAccess world, ChunkPos chunk) {
        List<Path> pathList = new ArrayList<>();
        Path generatedPath = Objects.requireNonNull(world.getServer()).getSavePath(WorldSavePath.GENERATED).normalize();
        String chunkDirPrefix = "chunk_" + chunk.x + "_" + chunk.z;  // Prefix to match chunk directories
        Path directoryPath = generatedPath.resolve(Easierworldcreator.MOD_ID).resolve("structures").resolve(chunkDirPrefix);

        if (Files.exists(generatedPath) && Files.isDirectory(generatedPath)) {
            // List all directories in the generated path
            //Files.list(generatedPath).forEach(directoryPath -> {
            if (Files.exists(directoryPath) && Files.isDirectory(directoryPath)) {
                try {
                    Files.list(directoryPath).forEach(filePath -> {
                        if (filePath.toString().endsWith(".json") && !filePath.getFileName().toString().startsWith("false")) {
                            pathList.add(filePath);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //});
        }

        return pathList;
    }

    /**
     * method to remove the {@code Block{}} to only get the {@link String} related to the block {@link Identifier}
     *
     * @param blockString
     * @return
     */
    public static String extractBlockName(String blockString) {
        if (blockString.startsWith("Block{") && blockString.endsWith("}")) {
            return blockString.substring(6, blockString.length() - 1);
        }
        throw new IllegalArgumentException("Invalid block string format: " + blockString);
    }
}
