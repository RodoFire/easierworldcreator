package net.rodofire.easierworldcreator.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Ewc;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListManager;
import net.rodofire.easierworldcreator.blockdata.blocklist.OrderedBlockListManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockStateUtil {
    /**
     * method used to convert {@link String} to BlockState
     *
     * @param world       used to get the registry entry
     * @param stateString the {@link String} related to the{@link BlockState}
     * @return the {@link BlockState} converted
     */
    public static BlockState parseBlockState(StructureWorldAccess world, String stateString) {
        RegistryEntryLookup<Block> blockLookup = world.createCommandRegistryWrapper(RegistryKeys.BLOCK);

        //Identifier identifier = new Identifier(stateString.split("\\[")[0]);
        Identifier identifier = Identifier.of(extractBlockName(stateString.split("\\[")[0]));
        Optional<? extends RegistryEntry<Block>> optional = blockLookup.getOptional(RegistryKey.of(RegistryKeys.BLOCK, identifier));
        if (optional.isEmpty()) {
            Ewc.LOGGER.error("error parsing BlockState: {}", stateString.split("\\[")[0]);
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
     * method to remove the {@code Block{}} to only get the {@link String} related to the block {@link Identifier}
     *
     * @param blockString the {@link String} that needs to be separated
     * @return the String related to the {@link Block}
     */
    private static String extractBlockName(String blockString) {
        if (blockString.startsWith("Block{") && blockString.endsWith("}")) {
            return blockString.substring(6, blockString.length() - 1);
        }
        throw new IllegalArgumentException("Invalid block string format: " + blockString);
    }

    static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * method to get all blockStates from the world from a list of BlockPos
     *
     * @param posList       the list of blockPos that will be used to get the blockStates
     * @param blockStateMap the map that will be modified
     * @param world         the world where the blockStates will be collected
     */
    public static void getBlockStatesFromWorld(List<Set<BlockPos>> posList, Map<BlockPos, BlockState> blockStateMap, StructureWorldAccess world) {
        Map<BlockPos, BlockState> concurrentMap = new ConcurrentHashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        for (Set<BlockPos> set : posList) {
            //executorService.submit(() -> {
            for (BlockPos blockPos : set) {
                blockStateMap.put(blockPos, world.getBlockState(blockPos));
            }
            //});
        }

        executorService.shutdown();
        //blockStateMap.putAll(concurrentMap);
    }

    public static BlockListManager getCompoundBlockStatesFromWorld(List<Set<BlockPos>> posList, StructureWorldAccess world) {
        BlockListManager comparator = new BlockListManager();
        for (Set<BlockPos> set : posList) {
            for (BlockPos blockPos : set) {
                BlockState blockState = world.getBlockState(blockPos);
                BlockEntity entity = world.getBlockEntity(blockPos);
                if (entity != null) {
                    DynamicRegistryManager registry = world.getRegistryManager();
                    NbtCompound nbtCompound = entity.createNbtWithIdentifyingData(registry);
                    comparator.put(blockState, nbtCompound, blockPos);
                } else {
                    comparator.put(blockState, null, blockPos);
                }
            }
        }
        return comparator;
    }

    public static OrderedBlockListManager getBlockStatesFromWorld(List<Set<BlockPos>> posList, StructureWorldAccess world) {
        OrderedBlockListManager comparator = new OrderedBlockListManager();
        for (Set<BlockPos> set : posList) {
            for (BlockPos blockPos : set) {
                BlockState blockState = world.getBlockState(blockPos);
                comparator.put(blockState, blockPos);
            }
        }
        return comparator;
    }
}
