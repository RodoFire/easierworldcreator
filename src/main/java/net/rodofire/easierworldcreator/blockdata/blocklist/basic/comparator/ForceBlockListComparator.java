package net.rodofire.easierworldcreator.blockdata.blocklist.basic.comparator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.DefaultBlockList;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.ForceBlockList;
import net.rodofire.easierworldcreator.blockdata.blocklist.ordered.comparator.ForceOrderedBlockListComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * class to manage a list of FullBlockList automatically
 */
@SuppressWarnings("unused")
public class ForceBlockListComparator extends BlockListComparator<ForceBlockList, Integer, ForceOrderedBlockListComparator, BlockState> {
    /**
     * init a comparator
     *
     * @param forceBlockList the list of blockList that will be indexed
     */
    public ForceBlockListComparator(List<ForceBlockList> forceBlockList) {
        super(forceBlockList);
    }

    /**
     * init a comparator
     *
     * @param forceBlockList a blockList that will be indexed
     */
    public ForceBlockListComparator(ForceBlockList forceBlockList) {
        super(forceBlockList);
    }

    /**
     * init an empty comparator
     */
    public ForceBlockListComparator() {
    }

    /**
     * method tu initialize the indexes.
     */
    @Override
    protected void initIndexes() {
        for (DefaultBlockList defaultBlockList : this.blockLists) {
            this.indexes.put(defaultBlockList.getBlockState(), this.indexes.size());
        }
    }

    /**
     * Method to add a state and a pos to the list. If the state already exists, no blockList will be created. The method will also update the indexes
     *
     * @param state the state that will be tested
     * @param pos   the pos that you want to use
     */
    public void put(BlockState state, BlockPos pos, boolean force, Set<Block> blocksToForce) {
        put(state, List.of(pos), force, blocksToForce);
    }

    /**
     * Method to add a state and a pos to the list. If the state already exists, no blockList will be created. The method will also update the indexes
     *
     * @param state   the state that will be tested
     * @param posList the list of pos that you want to use
     */
    public void put(BlockState state, List<BlockPos> posList, boolean force, Set<Block> blocksToForce) {
        if (indexes.containsKey(state)) {
            blockLists.get(indexes.get(state)).addBlockPos(posList);
            return;
        }
        indexes.put(state, indexes.size());
        blockLists.add(new ForceBlockList(posList, state, force, blocksToForce));
    }

    /**
     * Method to add a state and a pos to the list. If the state already exists, no blockList will be created. The method will also update the indexes
     *
     * @param type the BlockList that will be added in the comparator
     */
    @Override
    public void put(ForceBlockList type) {
        BlockState state = type.getBlockState();
        if (this.indexes.containsKey(state)) {
            this.blockLists.get(this.indexes.get(state)).addBlockPos(type.getPosList());
            return;
        }
        this.blockLists.add(type);
    }

    /**
     * Method to get the ordered version of the comparator
     *
     * @return the ordered version
     */
    @Override
    public ForceOrderedBlockListComparator getOrdered() {
        ForceOrderedBlockListComparator comparator = new ForceOrderedBlockListComparator();
        for (ForceBlockList blockList : this.blockLists) {
            comparator.put(blockList.getBlockState(), blockList.getPosList(), blockList.isForce(), blockList.getBlocksToForce());
        }
        return null;
    }

    /**
     * <p>Method to clean a blockList.
     * <p>In the case, there are multiple common BlockState.
     * <p>All the blockPos common of a BlockState will be fused in a single BlockState
     *
     * @param blockList the blockList that will bea cleaned
     * @return the cleaned version of the list
     */
    @Override
    public List<ForceBlockList> getCleaned(List<ForceBlockList> blockList) {
        List<ForceBlockList> cleanedList = new ArrayList<>();
        for (ForceBlockList forceBlockList : blockList) {
            BlockState state = forceBlockList.getBlockState();
            if (this.indexes.containsKey(state)) {
                cleanedList.get(this.indexes.get(state)).addBlockPos(forceBlockList.getPosList());
                continue;
            }
            cleanedList.add(new ForceBlockList(forceBlockList.getPosList(), state, forceBlockList.isForce(), forceBlockList.getBlocksToForce()));
            this.indexes.put(state, this.indexes.size());

        }
        return cleanedList;
    }
}
