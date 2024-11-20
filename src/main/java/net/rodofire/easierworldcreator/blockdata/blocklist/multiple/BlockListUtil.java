package net.rodofire.easierworldcreator.blockdata.blocklist.multiple;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public class BlockListUtil {
    public static void addBlockListToList(List<DefaultBlockList> defaultBlockList, BlockState state, BlockPos posList) {
        addBlockListToList(defaultBlockList, state, List.of(posList));
    }

    public static void addBlockListToList(List<DefaultBlockList> defaultBlockList, DefaultBlockList var) {
        addBlockListToList(defaultBlockList, var.getBlockState(), var.getPosList());
    }

    public static void addBlockListToList(List<DefaultBlockList> defaultBlockList, BlockState state, List<BlockPos> posList) {
        boolean added = false;
        for (DefaultBlockList defaultBlockList1 : defaultBlockList) {
            if (defaultBlockList1.getBlockState() == state) {
                defaultBlockList1.addBlockPos(posList);
                added = true;
            }
        }
        if (!added) {
            defaultBlockList.add(new DefaultBlockList(posList, state));
        }
    }

    public static List<DefaultBlockList> UnDivideBlockList(List<Set<DefaultBlockList>> blockList) {
        List<DefaultBlockList> combinedDefaultBlockList = new ArrayList<>();

        //map that store a BlockState.
        //  -if the blockState is present, then, you get the index at which you add some blockPos
        //  -if not, you add a new blockList inside the combinedBlockList
        Map<BlockState, Integer> blockStateIndexMap = new ConcurrentHashMap<>();

        AtomicInteger index = new AtomicInteger(0);

        //we combine the blockList in parallel for better performance
        blockList.parallelStream().forEach(set -> {
            for (DefaultBlockList block : set) {
                int posIndex = blockStateIndexMap.computeIfAbsent(block.getBlockState(), state -> {
                    synchronized (combinedDefaultBlockList) {
                        combinedDefaultBlockList.add(block);
                        return index.getAndIncrement();
                    }
                });

                synchronized (combinedDefaultBlockList) {
                    combinedDefaultBlockList.get(posIndex).addBlockPos(block.getPosList());
                }
            }
        });

        return combinedDefaultBlockList;
    }

    public static List<DefaultBlockList> cleanBlockList(List<DefaultBlockList> defaultBlockLists) {
        List<DefaultBlockList> cleanedDefaultBlockList = new ArrayList<>();
        Map<BlockState, Integer> blockStateIndexMap = new HashMap<>();

        int i = 0;
        for (DefaultBlockList defaultBlockList : defaultBlockLists) {
            if (blockStateIndexMap.containsKey(defaultBlockList.getBlockState())) {
                cleanedDefaultBlockList.get(blockStateIndexMap.get(defaultBlockList.getBlockState())).addBlockPos(defaultBlockList.getPosList());
            } else {
                blockStateIndexMap.put(defaultBlockList.getBlockState(), i);
                cleanedDefaultBlockList.add(defaultBlockList);
            }
        }
        return cleanedDefaultBlockList;
    }


    //TODO use divide to reign for better performance

    /**
     * method to combine a number of {@code List<BlockList>} superior to 2
     *
     * @param lists the list to combine
     * @return a {@code List<BlockList>} that correspond to the combined List
     */
    @SafeVarargs
    public static List<DefaultBlockList> combineNBlockList(List<DefaultBlockList>... lists) {
        if (lists.length == 0) {
            return new ArrayList<>();
        }
        if (lists.length == 1) {
            return new ArrayList<>(lists[0]);
        }
        return Arrays.stream(lists).parallel()
                .reduce(new ArrayList<>(), (list1, list2) -> {
                    List<DefaultBlockList> result = new ArrayList<>(list1);
                    combine2BlockList(result, list2);
                    return result;
                });
    }

    /**
     * method to combine 2 {@code List<BlockList>}
     *
     * @param defaultBlockList1 the first list that will contain the modifications
     * @param defaultBlockList2 the second list that will get merged
     */
    public static void combine2BlockList(List<DefaultBlockList> defaultBlockList1, List<DefaultBlockList> defaultBlockList2) {
        Map<BlockState, Integer> blockStateIndexMap = new HashMap<>();
        int i = 0;
        for (DefaultBlockList list : defaultBlockList1) {
            blockStateIndexMap.put(list.getBlockState(), i++);
        }
        for (DefaultBlockList list : defaultBlockList2) {
            BlockState state = list.getBlockState();
            if (blockStateIndexMap.containsKey(state)) {
                int index = blockStateIndexMap.get(state);
                defaultBlockList1.get(index).addBlockPos(list.getPosList());
            } else {
                defaultBlockList1.add(list);
            }

        }

    }
}
