package net.rodofire.easierworldcreator.blockdata.blocklist.ordered.comparator;

import com.google.common.collect.BiMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.placer.blocks.util.BlockPlaceUtil;

import java.util.List;
import java.util.Set;

/**
 * Class that combine Force and Nbt parameters
 */
@SuppressWarnings("unused")
public class FullOrderedBlockListComparator extends CompoundOrderedBlockListComparator {
    private BiMap<Short, Pair<Boolean, Set<Block>>> forceBlocks;


    /**
     * Method to put some states and some blockPos in the comparator
     *
     * @param state   the state that will be tested and put (in the case it doesn't exist)
     * @param posList the list of blockPos that will be put related to the given state
     * @param force   set if the block should replace all blocks or none
     */
    public void put(Pair<BlockState, NbtCompound> state, List<BlockPos> posList, boolean force) {
        this.put(state, posList);
        this.forceBlocks.put(getStateIndex(state), new Pair<>(force, null));
    }

    /**
     * Method to put some states and some blockPos in the comparator
     *
     * @param state         the state that will be tested and put (in the case it doesn't exist)
     * @param posList       the list of blockPos that will be put related to the given state
     * @param blocksToForce the set of blocks that the BlockState can still force
     */
    public void put(Pair<BlockState, NbtCompound> state, List<BlockPos> posList, Set<Block> blocksToForce) {
        this.put(state, posList);
        this.forceBlocks.put(getStateIndex(state), new Pair<>(false, blocksToForce));
    }

    /**
     * gives you the Set of every block that can be forced
     *
     * @return the set of the blocks that can be forced
     */

    public Set<Block> getBlocksToForce(Pair<BlockState, NbtCompound> state) {
        return forceBlocks.get(getStateIndex(state)).getRight();
    }

    /**
     * sets all the blocks that can be forced in the case force == false
     *
     * @param blocksToForce the set of blocks that can be forced
     */
    public void setBlocksToForce(Pair<BlockState, NbtCompound> state, Set<Block> blocksToForce) {
        this.forceBlocks.get(getStateIndex(state)).setRight(blocksToForce);
    }

    /**
     * add a block to the set
     *
     * @param block the block added
     */
    public void addBlocksToForce(Pair<BlockState, NbtCompound> state, Block block) {
        short index = getStateIndex(state);
        Set<Block> blockToForce = this.forceBlocks.get(index).getRight();
        blockToForce.add(block);
        this.forceBlocks.get(index).setRight(blockToForce);
    }

    /**
     * add a set of blocks to the set
     *
     * @param blocksToForce the set that will be added
     */
    public void addBlocksToForce(Pair<BlockState, NbtCompound> state, Set<Block> blocksToForce) {
        short index = getStateIndex(state);
        Set<Block> blockToForce = this.forceBlocks.get(index).getRight();
        blockToForce.addAll(blocksToForce);
        this.forceBlocks.get(index).setRight(blockToForce);
    }

    /**
     * remove a block to the set
     *
     * @param block the block removed
     */
    public void removeBlocksToForce(Pair<BlockState, NbtCompound> state, Block block) {
        short index = getStateIndex(state);
        Set<Block> blockToForce = this.forceBlocks.get(index).getRight();
        blockToForce.remove(block);
    }

    /**
     * remove a set of blocks to the set
     *
     * @param blocksToForce the set that will be removed
     */
    public void removeBlocksToForce(Pair<BlockState, NbtCompound> state, Set<Block> blocksToForce) {
        short index = getStateIndex(state);
        Set<Block> blockToForce = this.forceBlocks.get(index).getRight();
        blockToForce.removeAll(blocksToForce);
    }

    /**
     * get if any block can be replaced by any BlockState of this BlockShapeManager
     *
     * @return the boolean related to it
     */
    public boolean isForce(Pair<BlockState, NbtCompound> state) {
        return this.forceBlocks.get(getStateIndex(state)).getLeft();
    }

    /**
     * sets if any block can be replaced by any BlockState of this BlockShapeManager
     *
     * @param force the boolean used
     */
    public void setForce(Pair<BlockState, NbtCompound> state, boolean force) {
        this.forceBlocks.get(getStateIndex(state)).setLeft(force);
    }

    @Override
    public boolean placeFirst(StructureWorldAccess world) {
        short index = this.posMap.get(this.getFirstBlockPos());
        Pair<BlockPos, Pair<BlockState, NbtCompound>> data = this.getFirstPosPair();
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight().getLeft();
        NbtCompound nbtCompound = data.getRight().getRight();
        return BlockPlaceUtil.placeVerifiedBlockWithNbt(world, this.forceBlocks.get(index).getLeft(), this.forceBlocks.get(index).getRight(), pos, state, nbtCompound);
    }

    @Override
    public boolean placeFirstWithDeletion(StructureWorldAccess world) {
        short index = this.posMap.get(this.getFirstBlockPos());
        Pair<BlockPos, Pair<BlockState, NbtCompound>> data = this.removeFirstBlockPos();
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight().getLeft();
        NbtCompound nbtCompound = data.getRight().getRight();
        return BlockPlaceUtil.placeVerifiedBlockWithNbt(world, this.forceBlocks.get(index).getLeft(), this.forceBlocks.get(index).getRight(), pos, state, nbtCompound);
    }

    @Override
    public boolean place(StructureWorldAccess world, int index) {
        BlockPos pos = this.getBlockPos(index);
        short sh = this.posMap.get(pos);
        Pair<BlockState, NbtCompound> data = this.getPair(sh);
        return BlockPlaceUtil.placeVerifiedBlockWithNbt(world, this.forceBlocks.get(sh).getLeft(), this.forceBlocks.get(sh).getRight(), pos, data.getLeft(), data.getRight());
    }
}
