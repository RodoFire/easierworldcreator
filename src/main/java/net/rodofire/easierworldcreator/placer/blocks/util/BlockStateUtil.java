package net.rodofire.easierworldcreator.placer.blocks.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Util class related to BlockStates
 */
@SuppressWarnings("unused")
public class BlockStateUtil {
    /**
     * method to get all blockStates from the world from a list of BlockPos
     *
     * @param posList       the list of blockPos that will be used to get the blockStates
     * @param blockStateMap the map that will be modified
     * @param world         the world where the blockStates will be collected
     */
    public static void getBlockStatesFromWorld(List<Set<BlockPos>> posList, Map<BlockPos, BlockState> blockStateMap, StructureWorldAccess world) {
        for (Set<BlockPos> set : posList) {
            for (BlockPos blockPos : set) {
                blockStateMap.put(blockPos, world.getBlockState(blockPos));
            }
        }
    }
}
