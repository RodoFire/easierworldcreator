package net.rodofire.easierworldcreator.blockdata.blocklist;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.rodofire.easierworldcreator.blockdata.sorter.BlockSorter;
import net.rodofire.easierworldcreator.util.LongPosHelper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public class BlockListHelper {
    //TODO use divide to reign for better performance

    /**
     * method to combine a number of {@code List<BlockList>} superior to 2
     *
     * @param lists the list to combine
     * @return a {@code List<BlockList>} that correspond to the combined List
     */
    @SafeVarargs
    public static List<BlockList> combineNBlockList(List<BlockList>... lists) {
        if (lists.length == 0) {
            return new ArrayList<>();
        }
        if (lists.length == 1) {
            return new ArrayList<>(lists[0]);
        }
        return Arrays.stream(lists).parallel()
                .reduce(new ArrayList<>(), (list1, list2) -> {
                    List<BlockList> result = new ArrayList<>(list1);
                    combine2BlockList(result, list2);
                    return result;
                });
    }


    /**
     * method to sort a BlockList
     *
     * @param blockList the blockList that will be sorted
     * @param sorter    the sorter object that will be used to get the sorted BlockPos
     * @return the sorted BlockList
     */
    public static BlockListManager getSorted(List<BlockList> blockList, BlockSorter sorter) {
        BlockListManager sortedList = new BlockListManager(blockList);
        sorter.sortInsideBlockList(sortedList);
        return sortedList;
    }

    /**
     * method to combine 2 {@code List<BlockList>}
     *
     * @param defaultBlockList1 the first list that will contain the modifications
     * @param defaultBlockList2 the second list that will get merged
     */
    public static void combine2BlockList(List<BlockList> defaultBlockList1, List<BlockList> defaultBlockList2) {
        Map<BlockState, Integer> blockStateIndexMap = new HashMap<>();
        int i = 0;
        for (BlockList list : defaultBlockList1) {
            blockStateIndexMap.put(list.getBlockState(), i++);
        }
        for (BlockList list : defaultBlockList2) {
            BlockState state = list.getBlockState();
            if (blockStateIndexMap.containsKey(state)) {
                int index = blockStateIndexMap.get(state);
                defaultBlockList1.get(index).addAll(list.getPosList());
            } else {
                defaultBlockList1.add(list);
            }

        }

    }
}
