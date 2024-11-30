package net.rodofire.easierworldcreator.blockdata.blocklist.basic.comparator;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.CompoundBlockList;
import net.rodofire.easierworldcreator.blockdata.blocklist.ordered.comparator.CompoundOrderedBlockListComparator;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * class to manage a list of Compound BlockList automatically
 */
@SuppressWarnings("unused")
public class CompoundBlockListComparator extends BlockListComparator<CompoundBlockList, Pair<NbtCompound, Integer>, CompoundOrderedBlockListComparator, Pair<BlockState, NbtCompound>> {
    /**
     * init a comparator
     *
     * @param compoundBlockLists the list of blockList that will be indexed
     */
    public CompoundBlockListComparator(List<CompoundBlockList> compoundBlockLists) {
        super(compoundBlockLists);
    }

    /**
     * init a comparator
     *
     * @param compoundBlockList a blockList that will be indexed
     */
    public CompoundBlockListComparator(CompoundBlockList compoundBlockList) {
        super(compoundBlockList);
    }

    /**
     * init an empty comparator
     */
    public CompoundBlockListComparator() {
    }


    /**
     * method tu initialize the indexes.
     */
    @Override
    protected void initIndexes() {
        for (CompoundBlockList blockList : this.blockLists) {
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
    public void put(BlockState state, BlockPos pos, @Nullable NbtCompound nbtCompound) {
        put(state, List.of(pos), nbtCompound);
    }

    /**
     * Method to add a state and a pos to the list. If the state already exists, no blockList will be created. The method will also update the indexes
     *
     * @param state       the state that will be tested
     * @param posList     the list of pos that you want to use
     * @param nbtCompound the nbt data of the block that'll be put
     */
    public void put(BlockState state, List<BlockPos> posList, @Nullable NbtCompound nbtCompound) {
        if (indexes.containsKey(state) && indexes.get(state).getLeft().equals(nbtCompound)) {
            blockLists.get(indexes.get(state).getRight()).addBlockPos(posList);
            return;
        }
        indexes.put(state, new Pair<>(nbtCompound, indexes.size()));
        blockLists.add(new CompoundBlockList(posList, state, nbtCompound));
    }

    /**
     * Method to add a state and a pos to the list. If the state already exists, no blockList will be created. The method will also update the indexes
     *
     * @param type the BlockList that will be added in the comparator
     */
    @Override
    public void put(CompoundBlockList type) {
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
    public CompoundOrderedBlockListComparator getOrdered() {
        CompoundOrderedBlockListComparator comparator = new CompoundOrderedBlockListComparator();
        for (CompoundBlockList blockList : this.blockLists) {
            comparator.put(new Pair<>(blockList.getBlockState(), blockList.getTag()), blockList.getPosList());
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
    public List<CompoundBlockList> getCleaned(List<CompoundBlockList> blockList) {
        List<CompoundBlockList> cleanedList = new ArrayList<>();
        for (CompoundBlockList forceBlockList : blockList) {
            BlockState state = forceBlockList.getBlockState();
            NbtCompound tag = forceBlockList.getTag();
            if (this.indexes.containsKey(state) && this.indexes.get(state).getLeft().equals(tag)) {
                cleanedList.get(this.indexes.get(state).getRight()).addBlockPos(forceBlockList.getPosList());
                continue;
            }
            cleanedList.add(new CompoundBlockList(forceBlockList.getPosList(), state, tag));
            this.indexes.put(state, new Pair<>(tag, this.indexes.size()));

        }
        return cleanedList;
    }
}
