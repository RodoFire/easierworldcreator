package net.rodofire.easierworldcreator.nbtutil;

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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * class to load JSON files related to multi-chunk features
 */
public class LoadChunkShapeInfo {
    /**
     * method to load structure from a JSON file
     * @param world the world the shape will spawn in
     * @param chunkFilePath the path of the shape
     * @return a {@link List} used later to place the BlockStates
     */
    public static List<BlockList> loadFromJson(StructureWorldAccess world, Path chunkFilePath) throws IOException {
        String jsonContent = Files.readString(chunkFilePath);

        Gson gson = new Gson();

        JsonArray jsonArray = gson.fromJson(jsonContent, JsonArray.class);

        List<BlockList> blockLists = new ArrayList<>();

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
                int x = posObject.get("x").getAsInt();
                int y = posObject.get("y").getAsInt();
                int z = posObject.get("z").getAsInt();
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
     * methode used to convert {@link String} to BlockState
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

    private static <T extends Comparable<T>> BlockState applyProperty(BlockState state, Property<T> property, String value) {
        T propertyValue = property.parse(value).orElseThrow(() -> new IllegalArgumentException("Invalid property value"));
        return state.with(property, propertyValue);
    }


    public static List<Path> verifyFiles(StructureWorldAccess world, Chunk chunk) throws IOException {
        return verifyFiles(world, chunk.getPos());
    }

    public static List<Path> verifyFiles(StructureWorldAccess world, BlockPos pos) throws IOException {
        return verifyFiles(world, new ChunkPos(pos));
    }

    /**
     * method to verify if there is json files in the chunk folder
     * @return the list of the structure path to be placed later
     */
    public static List<Path> verifyFiles(StructureWorldAccess world, ChunkPos chunk) throws IOException {
        List<Path> path = new ArrayList<>();
        Path generatedPath = Objects.requireNonNull(world.getServer()).getSavePath(WorldSavePath.GENERATED).normalize();
        Path chunkDirectoryPath = generatedPath.resolve(Easierworldcreator.MOD_ID + "/structures/chunk_" + chunk.x + "_" + chunk.z);
        try {
            if (Files.exists(chunkDirectoryPath) && Files.isDirectory(chunkDirectoryPath)) {
                Files.list(chunkDirectoryPath).forEach(filePath -> {
                    System.out.println(filePath);
                    if (filePath.toString().endsWith(".json")) {
                        path.add(filePath);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * method to remove the {@code Block{}} to only get the {@link String} related to the block {@link Identifier}
     * @param blockString
     * @return
     */
    public static String extractBlockName(String blockString) {
        if (blockString.startsWith("Block{") && blockString.endsWith("}")) {
            return blockString.substring(6, blockString.length() - 1);
        }
        throw new IllegalArgumentException("Invalid block string format: " + blockString);
    }


    public static void removeFile(Path path) {
        File file = new File(path.toString());
        if (file.exists()) {
            boolean deleted = file.delete();
        } else {
        }
    }
}
