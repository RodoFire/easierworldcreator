package net.rodofire.easierworldcreator.blockdata.block_shape_manager;

import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.rodofire.easierworldcreator.blockdata.sorter.BlockSorter;

import java.util.*;

/**
 * class to manage a list of BlockList automatically
 */
@SuppressWarnings("unused")
public class DefaultBlockListComparator {
    private List<DefaultBlockList> defaultBlockLists = new ArrayList<>();
    private final Map<BlockState, Integer> indexes = new HashMap<>();

    /**
     * init a comparator
     *
     * @param defaultBlockLists the list of blockList that will be indexed
     */
    public DefaultBlockListComparator(List<DefaultBlockList> defaultBlockLists) {
        List<DefaultBlockList> cleanedList = BlockListUtil.cleanBlockList(defaultBlockLists);
        this.defaultBlockLists = new ArrayList<>(cleanedList);
        initIndexes();
    }

    /**
     * init a comparator
     *
     * @param defaultBlockList a blockList that will be indexed
     */
    public DefaultBlockListComparator(DefaultBlockList defaultBlockList) {
        this.defaultBlockLists = new ArrayList<>(Collections.singletonList(defaultBlockList));
        initIndexes();
    }

    /**
     * init an empty comparator
     */
    public DefaultBlockListComparator() {

    }

    /**
     * method to combine a number of {@code List<BlockList>}
     *
     * @param lists the list to combine
     */
    @SafeVarargs
    public final void put(List<DefaultBlockList>... lists) {
        if (lists.length == 0) {
            return;
        }
        if (lists.length == 1) {
            this.put(lists[0]);
            return;
        }
        this.defaultBlockLists.addAll(Arrays.stream(lists).parallel()
                .reduce(new ArrayList<>(), (list1, list2) -> {
                    List<DefaultBlockList> result = new ArrayList<>(list1);
                    put(list2);
                    return result;
                }));
    }

    /**
     * add a List of BlockList
     *
     * @param defaultBlockLists the list that will be put
     */
    public void put(List<DefaultBlockList> defaultBlockLists) {
        for (DefaultBlockList defaultBlockList : defaultBlockLists) {
            put(defaultBlockList);
        }
    }

    /**
     * Add a BlockList to the comparator. The method will also update the indexes
     *
     * @param defaultBlockList the blockList that will be put
     */
    public void put(DefaultBlockList defaultBlockList) {
        if (indexes.containsKey(defaultBlockList.getBlockState())) {
            defaultBlockLists.get(indexes.get(defaultBlockList.getBlockState())).addBlockPos(defaultBlockList.getPosList());
            return;
        }
        indexes.put(defaultBlockList.getBlockState(), indexes.size());
        defaultBlockLists.add(defaultBlockList);
    }

    /**
     * Method to add a state and a pos to the list. If the state already exists, no blockList will be created. The method will also update the indexes
     *
     * @param state the state that will be tested
     * @param pos   the pos that you want to use
     */
    public void put(BlockState state, BlockPos pos) {
        put(state, List.of(pos));
    }

    /**
     * Method to add a state and a pos to the list. If the state already exists, no blockList will be created. The method will also update the indexes
     *
     * @param state   the state that will be tested
     * @param posList the list of pos that you want to use
     */
    public void put(BlockState state, List<BlockPos> posList) {
        if (indexes.containsKey(state)) {
            defaultBlockLists.get(indexes.get(state)).addBlockPos(posList);
            return;
        }
        indexes.put(state, indexes.size());
        defaultBlockLists.add(new DefaultBlockList(posList, state));
    }

    /**
     * method to initialize the hashmap
     */
    private void initIndexes() {
        int i = 0;
        for (DefaultBlockList defaultBlockList : this.defaultBlockLists) {
            this.indexes.put(defaultBlockList.getBlockState(), i++);
        }
    }

    /**
     * method to convert the blockList to a List of pair
     *
     * @return the blockState related to every blockPos of the blockList
     */
    public List<Pair<BlockState, BlockPos>> convertToPair() {
        List<Pair<BlockState, BlockPos>> convertedList = new ArrayList<>();
        for (DefaultBlockList defaultBlockList : this.defaultBlockLists) {
            BlockState state = defaultBlockList.getBlockState();
            for (BlockPos blockPos : defaultBlockList.getPosList()) {
                convertedList.add(new Pair<>(state, blockPos));
            }
        }
        return convertedList;
    }

    /**
     * method to convert the blockList to a List of pair while being sorted
     *
     * @return the blockState related to every blockPos of the blockList
     */
    public List<Pair<BlockState, BlockPos>> convertToPairSorted(BlockSorter sorter) {
        return sorter.sortBlockList(this.defaultBlockLists);
    }

    /**
     * method to get the list
     *
     * @return the list of the comparator
     */
    public List<DefaultBlockList> get() {
        return this.defaultBlockLists;
    }

    /**
     * method to get the list with the blockPos being sorted
     *
     * @return the list of the comparator
     */
    public List<DefaultBlockList> getSorted(BlockSorter sorter) {
        sorter.sortInsideBlockList(this.defaultBlockLists);
        return this.defaultBlockLists;
    }
}
