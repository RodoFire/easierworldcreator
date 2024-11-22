package net.rodofire.easierworldcreator.blockdata.blocklist.basic.comparator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.BlockListManager;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.DefaultBlockList;
import net.rodofire.easierworldcreator.blockdata.blocklist.ordered.comparator.OrderedBlockListComparator;
import net.rodofire.easierworldcreator.blockdata.sorter.BlockSorter;

import java.util.*;

/**
 * <p>
 * Class to manage a {@code List<BlockList>}. It is composed of a list of BlockList and a Map of indexes.
 * </p>
 * <p>
 * For better performance:
 * <li>Instead of searching for a {@code BlockState} inside the list, we use the {@code indexMap}.</li>
 * <li>The map allows us to get the place of the related {@code BlockState} in the {@code posList}.</li>
 * <li>Instead of having a time complexity of O(n), we get O(1) by using only a small amount of memory in more.</li>
 * </p>
 * <br>
 * <p>
 * The class provides some useful methods:
 * <li> avoid the duplicate of BlockState
 * (that can be pretty annoying or counter productive as well as having a performance impact in the case where a lot {@link BlockState} are duplicated).</li>
 * <li> ability to sort each {@link BlockPos} related to one {@code T}</li>
 * <li> convert this {@link BlockListManager} into a {@link OrderedBlockListComparator} that would allow for more flexibility on how are the BlockPos kept</li>
 * <li> provides some custom getters.</li>
 * </p>
 *
 * @param <T> The type of the {@link BlockListManager} that will be managed by the comparator
 * @param <U> The type of the {@code BlockData}. This represents everything to describe the {@link Block}.
 *            Usually, it would be {@link BlockState}. But there are some cases that require more data like NbtCompounds.
 * @param <V> The type of the {@link OrderedBlockListComparator}
 *            (class to manage BlockList where the {@link BlockPos} are ordered no matter the {@code BlockData},
 *            contrary to this class where each {@link BlockPos} are organized depending on the {@code BlockData}),
 *            related to the object so that no data is lost, or no incompatibilities are present
 * @param <W> The type of the {@code BlockData}, like {@code <U>}, but for the {@link OrderedBlockListComparator}.
 *            Copy and paste the object that is required to the related {@link OrderedBlockListComparator}
 */
@SuppressWarnings("unused")
public abstract class BlockListComparator<T extends DefaultBlockList, U, V extends OrderedBlockListComparator<W>, W> {
    /**
     * the List of BlockList that are managed
     */
    protected List<T> blockLists = new ArrayList<>();
    /**
     * For better performance:
     * <li>Instead of searching for a {@code BlockState} inside the list, we use the {@code indexMap}.</li>
     * <li>The map allows us to get the place of the related {@code BlockState} in the {@code posList}.</li>
     * <li>Instead of having a time complexity of O(n), we get O(1) by using only a small amount of memory in more.</li>
     * </p>
     */
    protected final Map<BlockState, U> indexes = new HashMap<>();

    /**
     * init a comparator
     *
     * @param blockLists the list of blockList that will be indexed
     */
    public BlockListComparator(List<T> blockLists) {
        List<T> cleanedList = getCleaned(blockLists);
        this.blockLists = new ArrayList<>(cleanedList);
        initIndexes();
    }

    /**
     * init a comparator
     *
     * @param blockList a blockList that will be indexed
     */
    public BlockListComparator(T blockList) {
        this.blockLists = new ArrayList<>(Collections.singletonList(blockList));
        initIndexes();
    }

    /**
     * init an empty comparator
     */
    public BlockListComparator() {
    }

    /**
     * method tu initialize the indexes.
     */
    protected abstract void initIndexes();

    /**
     * method to combine a number of {@code List<T>}
     *
     * @param lists the list to combine
     */
    @SafeVarargs
    public final void put(List<T>... lists) {
        if (lists.length == 0) {
            return;
        }
        if (lists.length == 1) {
            this.put(lists[0]);
            return;
        }
        this.blockLists.addAll(Arrays.stream(lists).parallel()
                .reduce(new ArrayList<>(), (list1, list2) -> {
                    List<T> result = new ArrayList<>(list1);
                    put(list2);
                    return result;
                }));
    }

    /**
     * add a List of BlockList
     *
     * @param blockLists the list that will be put
     */
    public void put(List<T> blockLists) {
        for (T defaultBlockList : blockLists) {
            put(defaultBlockList);
        }
    }

    /**
     * add a BlockList to the list
     *
     * @param type the blockList that will be added
     */
    public abstract void put(T type);

    /**
     * method to get the list
     *
     * @return the list of the comparator
     */
    public List<T> get() {
        return this.blockLists;
    }

    /**
     * method to get a {@link DefaultBlockList} based on the index
     *
     * @param index the index that will be used to get the DefaultBlockList
     * @return the DefaultBlockList related to the index
     */
    public T get(int index) {
        return this.blockLists.get(index);
    }

    /**
     * method to get the first {@link DefaultBlockList} of the list
     *
     * @return the first {@link DefaultBlockList} of the list
     */
    public T getFirst() {
        return this.blockLists.get(0);
    }

    /**
     * method to get the last {@link DefaultBlockList} of the list
     *
     * @return the last {@link DefaultBlockList} of the list
     */
    public T getLast() {
        return this.blockLists.get(size() - 1);
    }

    /**
     * method to get a random {@link DefaultBlockList} of the list
     *
     * @return a random DefaultBlockList of the list
     */
    public T getRandom() {
        return this.blockLists.get(net.minecraft.util.math.random.Random.create().nextInt(size() - 1));
    }

    /**
     * method to get a random {@link DefaultBlockList} of the list
     *
     * @param random the random object that will be used to get the index
     * @return a random DefaultBlockList of the list
     */
    public T getRandom(Random random) {
        return this.blockLists.get(random.nextInt(size() - 1));
    }

    /**
     * method to know the size of the list of the {@link DefaultBlockList}
     *
     * @return the size of the list
     */
    public int size() {
        return this.blockLists.size();
    }

    /**
     * method to know if a {@link BlockState} is present
     *
     * @param state the state that will be tested
     * @return true if the {@link BlockState} is present, false if not
     */
    public boolean contains(BlockState state) {
        return indexes.containsKey(state);
    }

    /**
     * <p>Method to clean a blockList.
     * <p>In the case, there are multiple common BlockState.
     * <p>All the blockPos common of a BlockState will be fused in a single BlockState
     *
     * @param blockList the blockList that will bea cleaned
     * @return the cleaned version of the list
     */
    public abstract List<T> getCleaned(List<T> blockList);

    /**
     * Method to sort all the BlockPos List
     *
     * @param sorter the sorter that will sort the BlockPos
     */
    public void sort(BlockSorter sorter) {
        sorter.sortInsideBlockList(this.blockLists);
    }

    /**
     * method to get the list with the blockPos being sorted
     *
     * @param sorter the sorter object that will be used to sort the list
     * @return the list of the comparator
     */
    public List<T> getSorted(BlockSorter sorter) {
        sorter.sortInsideBlockList(this.blockLists);
        return this.blockLists;
    }

    /**
     * Method to get the ordered version of the comparator
     *
     * @return the ordered version
     */
    public abstract V getOrdered();

    /**
     * Method to get the ordered version of the comparator as well as being sorted
     *
     * @param sorter the sorter that will sort the BlockPos
     * @return the sorted comparator related to this object
     */
    public V getIndividualSorted(BlockSorter sorter) {
        return sorter.sortBlockList(this.getOrdered());
    }
}
