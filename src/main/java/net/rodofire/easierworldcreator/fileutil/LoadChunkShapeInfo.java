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
import net.rodofire.easierworldcreator.EasierWorldCreator;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.DefaultBlockList;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.comparator.DefaultBlockListComparator;
import net.rodofire.easierworldcreator.config.ewc.EwcConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
    public static DefaultBlockListComparator loadFromJson(StructureWorldAccess world, Path chunkFilePath) {
        File file = new File(chunkFilePath.toString());
        if (!file.exists()) return new DefaultBlockListComparator();
        String jsonContent;
        try {
            jsonContent = Files.readString(chunkFilePath);
        } catch (IOException e) {
            e.fillInStackTrace();
            return new DefaultBlockListComparator();
        }

        Gson gson = new Gson();

        JsonArray jsonArray = gson.fromJson(jsonContent, JsonArray.class);
        DefaultBlockListComparator comparator = new DefaultBlockListComparator();
        String fileName = chunkFilePath.getParent().getFileName().toString();
        Pattern pattern = Pattern.compile("chunk_(-?\\d+)_(-?\\d+)$");
        Matcher matcher = pattern.matcher(fileName);
        int chunkX = 0;
        int chunkZ = 0;

        if (matcher.matches()) {
            chunkX = Integer.parseInt(matcher.group(1));
            chunkZ = Integer.parseInt(matcher.group(2));
        }

        for (JsonElement element : jsonArray) {
            JsonObject jsonObject = element.getAsJsonObject();

            // Get the block state
            String stateString = jsonObject.get("state").getAsString();
            BlockState blockState = parseBlockState(world, stateString);

            // Get the positions
            List<BlockPos> posList = new ArrayList<>();
            JsonArray positionsArray = jsonObject.getAsJsonArray("positions");

            for (JsonElement posElement : positionsArray) {
                int value = posElement.getAsInt();
                int x = chunkX * 16 + (value >> 24);
                int y = (value & 0x00FFFF00) >> 8;
                int z = chunkZ * 16 + (value & 0x000000FF);
                posList.add(new BlockPos(x, y, z));
            }

            // Create a new BlockList and add it to the set
            DefaultBlockList defaultBlockList = new DefaultBlockList(posList, blockState);
            comparator.put(defaultBlockList);
        }
        return comparator;
    }

    /**
     * method to place the structure
     *
     * @param world             the world the structure will spawn in
     * @param defaultBlockLists the list of blockList that compose the structure
     */
    public static void placeStructure(StructureWorldAccess world, DefaultBlockListComparator defaultBlockLists) {
        defaultBlockLists.placeAllWithVerification(world);
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
            EasierWorldCreator.LOGGER.error("error parsing BlockState: {}", stateString.split("\\[")[0]);
            return Blocks.AIR.getDefaultState();
        }

        Block block = (Block) ((RegistryEntry<?>) optional.get()).value();
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
     * @param <T>      the type of the property value must be Comparable
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
    public static List<Path> getWorldGenFiles(StructureWorldAccess world, Chunk chunk) {
        return getWorldGenFiles(world, chunk.getPos());
    }

    /**
     * method to verify if there's a JSON files in the chunk folder
     *
     * @param world the world of the structure
     * @param pos   the {@link BlockPos} that will be converted into a {@link ChunkPos}
     * @return the list of the structure path to be placed later
     */
    public static List<Path> getWorldGenFiles(StructureWorldAccess world, BlockPos pos) {
        return getWorldGenFiles(world, new ChunkPos(pos));
    }

    /**
     * method to verify if there's a JSON files in the chunk folder
     *
     * @param world the world of the structure
     * @param chunk the chunk that needs to be verified
     * @return the list of the structure path to be placed later
     */
    public static List<Path> getWorldGenFiles(StructureWorldAccess world, ChunkPos chunk) {
        List<Path> pathList = new ArrayList<>();
        int distance = EwcConfig.getFeaturesChunkDistance();
        Path generatedPath = Objects.requireNonNull(world.getServer()).getSavePath(WorldSavePath.GENERATED).normalize();
        if (Files.exists(generatedPath) && Files.isDirectory(generatedPath)) {
            generatedPath = generatedPath.resolve(EasierWorldCreator.MOD_ID).resolve("structures");
            if (Files.exists(generatedPath) && Files.isDirectory(generatedPath)) {
                for (int i = -distance; i <= distance; i++) {
                    for (int j = -distance; j <= distance; j++) {
                        int chunkX = chunk.x + i;
                        int chunkZ = chunk.z + j;
                        String chunkDirPrefix = "chunk_" + chunkX + "_" + chunkZ;
                        Path newPath = generatedPath.resolve(chunkDirPrefix);
                        getPathFromChunk(newPath, pathList);
                    }
                }
            }
        }

        return pathList;
    }

    /**
     * method to get all multi-chunk JSON files of a block
     *
     * @param generatedPath the path of generated/ewc/chunk_[chunk.x]_[chunk.z]
     * @param pathList      the other resolved paths
     */
    private static void getPathFromChunk(Path generatedPath, List<Path> pathList) {
        if (/*Files.exists(generatedPath) && */Files.isDirectory(generatedPath)) {
            try {
                try (Stream<Path> paths = Files.list(generatedPath)) {
                    paths.forEach(filePath -> {
                        if (filePath.toString().endsWith(".json") && !filePath.getFileName().toString().startsWith("false")) {
                            pathList.add(filePath);
                        }
                    });
                }
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        }
    }

    /**
     * method to remove the {@code Block{}} to only get the {@link String} related to the block {@link Identifier}
     *
     * @param blockString the {@link String} that needs to be separated
     * @return the String related to the {@link Block}
     */
    public static String extractBlockName(String blockString) {
        if (blockString.startsWith("Block{") && blockString.endsWith("}")) {
            return blockString.substring(6, blockString.length() - 1);
        }
        throw new IllegalArgumentException("Invalid block string format: " + blockString);
    }
}
