package net.rodofire.easierworldcreator.worldgenutil;

import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.shapeutil.BlockList;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class BlockStateUtil {
    //huge names lol
    public static void convertBlockListToBlockStatePair(List<BlockList> posList, List<Pair<BlockState, BlockPos>> sortedBlockList) {
        for (BlockList blockList : posList) {
            BlockState state = blockList.getBlockState();
            for (BlockPos blockPos : blockList.getPosList()) {
                sortedBlockList.add(new Pair<>(state, blockPos));
            }
        }
    }

    public static void convertDividedBlockListToBlockStatePair(List<Set<BlockList>> posList, List<Pair<BlockState, BlockPos>> sortedBlockList) {
        for (Set<BlockList> set : posList) {
            for (BlockList blockList1 : set) {
                BlockState state = blockList1.getBlockState();
                for (BlockPos blockPos : blockList1.getPosList()) {
                    sortedBlockList.add(new Pair<>(state, blockPos));
                }
            }
        }
    }

    public static void getBlockStatesFromWorld(List<Set<BlockPos>> posList, Map<BlockPos, BlockState> blockStateMap, StructureWorldAccess world) {
        for (Set<BlockPos> set : posList) {
            for (BlockPos blockPos : set) {
                blockStateMap.put(blockPos, world.getBlockState(blockPos));
            }
        }
    }
}
