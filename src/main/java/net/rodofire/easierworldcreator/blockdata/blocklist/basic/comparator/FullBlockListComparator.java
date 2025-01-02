package net.rodofire.easierworldcreator.blockdata.blocklist.basic.comparator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.FullBlockList;
import net.rodofire.easierworldcreator.blockdata.blocklist.ordered.comparator.FullOrderedBlockListComparator;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * class to manage a list of FullBlockList automatically
 */
@SuppressWarnings("unused")
public class FullBlockListComparator extends AbstractBlockListComparator<FullBlockList, Pair<NbtCompound, Integer>, FullOrderedBlockListComparator, Pair<BlockState, NbtCompound>> {
    /**
     * init a comparator
     * @param comparator the comparator that will be fused
     */
    public FullBlockListComparator(FullBlockListComparator comparator){
        super(comparator);
    }
    /**
     * init a comparator
     *
     * @param fullBlockLists the list of blockList that will be indexed
     */
    public FullBlockListComparator(List<FullBlockList> fullBlockLists) {
        super(fullBlockLists);
    }

    /**
     * init a comparator
     *
     * @param fullBlockList a blockList that will be indexed
     */
    public FullBlockListComparator(FullBlockList fullBlockList) {
        super(fullBlockList);
    }

    /**
     * init an empty comparator
     */
    public FullBlockListComparator() {
    }

    /**
     * method tu initialize the indexes.
     */
    @Override
    protected void initIndexes() {
        for (FullBlockList blockList : this.blockLists) {
            this.indexes.put(blockList.getBlockState(), new Pair<>(blockList.getTag(), this.indexes.size()));
        }
    }

    /**
     * Method to add a state and a pos to the list. If the state already exists, no blockList will be created. The method will also update the indexes
     *
     * @param state       the state that will be tested
     * @param pos         the pos that you want to use
     * @param nbtCompound the nbt data of the block that'll be put
     */
    public void put(BlockState state, BlockPos pos, @Nullable NbtCompound nbtCompound, boolean force, Set<Block> blocksToForce) {
        put(state, List.of(pos), nbtCompound, force, blocksToForce);
    }

    /**
     * Method to add a state and a pos to the list. If the state already exists, no blockList will be created. The method will also update the indexes
     *
     * @param state       the state that will be tested
     * @param posList     the list of pos that you want to use
     * @param nbtCompound the nbt data of the block that'll be put
     */
    public void put(BlockState state, List<BlockPos> posList, @Nullable NbtCompound nbtCompound, boolean force, Set<Block> blocksToForce) {
        if (indexes.containsKey(state) && indexes.get(state).getLeft().equals(nbtCompound)) {
            blockLists.get(indexes.get(state).getRight()).addBlockPos(posList);
            return;
        }
        indexes.put(state, new Pair<>(nbtCompound, indexes.size()));
        blockLists.add(new FullBlockList(posList, state, nbtCompound, force, blocksToForce));
    }

    /**
     * Method to add a state and a pos to the list. If the state already exists, no blockList will be created. The method will also update the indexes
     *
     * @param type the BlockList that will be added in the comparator
     */
    @Override
    public void put(FullBlockList type) {
        BlockState state = type.getBlockState();
        NbtCompound compound = type.getTag();
        if (this.indexes.containsKey(state) && this.indexes.get(state).getLeft().equals(compound)) {
            this.blockLists.get(this.indexes.get(state).getRight()).addBlockPos(type.getPosList());
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
    public FullOrderedBlockListComparator getOrdered() {
        FullOrderedBlockListComparator comparator = new FullOrderedBlockListComparator();
        for (FullBlockList blockList : this.blockLists) {
            comparator.put(new Pair<>(blockList.getBlockState(), blockList.getTag()), blockList.getPosList(), blockList.isForce(), blockList.getBlocksToForce());
        }
        return comparator;
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
    public List<FullBlockList> getCleaned(List<FullBlockList> blockList) {
        List<FullBlockList> cleanedList = new ArrayList<>();
        for (FullBlockList fullBlockList : blockList) {
            BlockState state = fullBlockList.getBlockState();
            NbtCompound tag = fullBlockList.getTag();
            if (this.indexes.containsKey(state) && this.indexes.get(state).getLeft().equals(tag)) {
                cleanedList.get(this.indexes.get(state).getRight()).addBlockPos(fullBlockList.getPosList());
                continue;
            }
            cleanedList.add(new FullBlockList(fullBlockList.getPosList(), state, tag, fullBlockList.isForce(), fullBlockList.getBlocksToForce()));
            this.indexes.put(state, new Pair<>(tag, this.indexes.size()));

        }
        return cleanedList;
    }
}
