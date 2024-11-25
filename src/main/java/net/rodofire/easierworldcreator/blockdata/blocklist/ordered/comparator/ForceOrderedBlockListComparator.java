package net.rodofire.easierworldcreator.blockdata.blocklist.ordered.comparator;

import com.google.common.collect.BiMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.placer.blocks.util.BlockPlaceUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Class to manage an ordered list with force parameters.
 * This class uses a BiMap that connects each BlockState to a pair of Boolean and Set.
 * Force parameters works like that:
 * <pre>
 *     {@code
 * worldBlock : block at the blockPos
 * placeBlock : block to place at the blockPos
 * if force :
 *      place placeBlock
 * else if worldBlock == air :
 *      place placeBlock
 * else if worldBlock in forceBlock :
 *      place placeBlock
 * else :
 *      dont place
 *}
 * </pre>
 */
@SuppressWarnings("unused")
public class ForceOrderedBlockListComparator extends DefaultOrderedBlockListComparator {
    private BiMap<Short, Pair<Boolean, Set<Block>>> forceBlocks;

    /**
     * init a force ordered blockList comparator
     *
     * @param state   the state that will be tested and put (in the case it doesn't exist)
     * @param posList the blockPos that will be put related to the given state
     */
    public ForceOrderedBlockListComparator(BlockState state, List<BlockPos> posList) {
        super(state, posList);
    }

    /**
     * init a force ordered blockList comparator
     *
     * @param state         the state that will be tested and put (in the case it doesn't exists)
     * @param posList       the blockPos that will be put related to the given state
     * @param force         set if the block should replace all blocks or none
     * @param blocksToForce the set of blocks that the BlockState can still force
     */
    public ForceOrderedBlockListComparator(BlockState state, List<BlockPos> posList, boolean force, Set<Block> blocksToForce) {
        super(state, posList);
        Pair<Boolean, Set<Block>> forceData = new Pair<>(force, blocksToForce);
        this.forceBlocks.put((short) 0, forceData);
    }

    /**
     * init a comparator based on a given map
     *
     * @param info the map that will serve to initialize the comparator
     */
    public ForceOrderedBlockListComparator(Map<BlockState, List<BlockPos>> info) {
        super(info);
    }

    /**
     * init an empty comparator
     */
    public ForceOrderedBlockListComparator() {
    }

    /**
     * Method to put some states and some blockpos in the comparator
     *
     * @param state         the state that will be tested and put (in the case it doesn't exists)
     * @param pos           the blockPos that will be put related to the given state
     * @param force         set if the block should replace all blocks or none
     * @param blocksToForce the set of blocks that the BlockState can still force
     */
    public void put(BlockState state, BlockPos pos, boolean force, Set<Block> blocksToForce) {
        this.put(state, pos);
        this.forceBlocks.put(getStateIndex(state), new Pair<>(force, blocksToForce));
    }

    /**
     * Method to put some states and some blockpos in the comparator
     *
     * @param state         the state that will be tested and put (in the case it doesn't exists)
     * @param posList       the list of blockPos that will be put related to the given state
     * @param force         set if the block should replace all blocks or none
     * @param blocksToForce the set of blocks that the BlockState can still force
     */
    public void put(BlockState state, List<BlockPos> posList, boolean force, Set<Block> blocksToForce) {
        this.put(state, posList);
        this.forceBlocks.put(getStateIndex(state), new Pair<>(force, blocksToForce));
    }

    /**
     * Method to put some states and some blockpos in the comparator
     *
     * @param state   the state that will be tested and put (in the case it doesn't exists)
     * @param posList the list of blockPos that will be put related to the given state
     * @param force   set if the block should replace all blocks or none
     */
    public void put(BlockState state, List<BlockPos> posList, boolean force) {
        this.put(state, posList);
        this.forceBlocks.put(getStateIndex(state), new Pair<>(force, null));
    }

    /**
     * Method to put some states and some blockpos in the comparator
     *
     * @param state         the state that will be tested and put (in the case it doesn't exists)
     * @param posList       the list of blockPos that will be put related to the given state
     * @param blocksToForce the set of blocks that the BlockState can still force
     */
    public void put(BlockState state, List<BlockPos> posList, Set<Block> blocksToForce) {
        this.put(state, posList);
        this.forceBlocks.put(getStateIndex(state), new Pair<>(false, blocksToForce));
    }

    /**
     * gives you the Set of every block that can be forced
     *
     * @return the set of the blocks that can be forced
     */

    public Set<Block> getBlocksToForce(BlockState state) {
        return forceBlocks.get(getStateIndex(state)).getRight();
    }

    /**
     * sets all the blocks that can be forced in the case force == false
     *
     * @param blocksToForce the set of blocks that can be forced
     */
    public void setBlocksToForce(BlockState state, Set<Block> blocksToForce) {
        this.forceBlocks.get(getStateIndex(state)).setRight(blocksToForce);
    }

    /**
     * add a block to the set
     *
     * @param block the block added
     */
    public void addBlocksToForce(BlockState state, Block block) {
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
    public void addBlocksToForce(BlockState state, Set<Block> blocksToForce) {
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
    public void removeBlocksToForce(BlockState state, Block block) {
        short index = getStateIndex(state);
        Set<Block> blockToForce = this.forceBlocks.get(index).getRight();
        blockToForce.remove(block);
    }

    /**
     * remove a set of blocks to the set
     *
     * @param blocksToForce the set that will be removed
     */
    public void removeBlocksToForce(BlockState state, Set<Block> blocksToForce) {
        short index = getStateIndex(state);
        Set<Block> blockToForce = this.forceBlocks.get(index).getRight();
        blockToForce.removeAll(blocksToForce);
    }

    /**
     * get if any block can be replaced by any BlockState of this BlockShapeManager
     *
     * @return the boolean related to it
     */
    public boolean isForce(BlockState state) {
        return this.forceBlocks.get(getStateIndex(state)).getLeft();
    }

    /**
     * sets if any block can be replaced by any BlockState of this BlockShapeManager
     *
     * @param force the boolean used
     */
    public void setForce(BlockState state, boolean force) {
        this.forceBlocks.get(getStateIndex(state)).setLeft(force);
    }

    /**
     * Method to place the block related to the index.
     * The method also performs verification to know if the block can be placed.
     *
     * @param world the world the block will be placed
     * @param index the index of the block
     * @return true if the block was placed, false if not
     */
    @Override
    public boolean placeWithVerification(StructureWorldAccess world, int index) {
        Pair<BlockPos, BlockState> data = this.getPosPair(index);
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight();
        short ind = this.statesMap.inverse().get(state);
        return BlockPlaceUtil.placeVerifiedBlock(world, this.forceBlocks.get(ind).getLeft(), this.forceBlocks.get(ind).getRight(), pos, state);
    }

    /**
     * Method to place the block with the deletion of the BlockPos
     * The method also performs verification to know if the block can be placed.
     *
     * @param world the world the block will be placed
     * @param index the index of the block
     * @return true if the block was placed, false if not
     */
    @Override
    public boolean placeWithVerificationDeletion(StructureWorldAccess world, int index) {
        Pair<BlockPos, BlockState> data = this.removeBlockPosPair(index);
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight();
        short ind = this.statesMap.inverse().get(state);
        return BlockPlaceUtil.placeVerifiedBlock(world, this.forceBlocks.get(ind).getLeft(), this.forceBlocks.get(ind).getRight(), pos, state);
    }

    /**
     * Method to place the last Block.
     *
     * @param world the world the last block will be placed
     *              The method also performs verification to know if the block can be placed.
     * @return true if the block was placed, false if not
     */
    @Override
    public boolean placeLastWithVerification(StructureWorldAccess world) {
        Pair<BlockPos, BlockState> data = this.getLastPosPair();
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight();
        short ind = this.statesMap.inverse().get(state);
        return BlockPlaceUtil.placeVerifiedBlock(world, this.forceBlocks.get(ind).getLeft(), this.forceBlocks.get(ind).getRight(), pos, state);
    }

    /**
     * Method to place the last Block of the comparator and removing it then.
     * The method also performs verification to know if the block can be placed.
     * Consider using this method because it gives you better performance.
     *
     * @param world the world the last block will be placed
     * @return true if the block was placed, false if not
     */
    @Override
    public boolean placeLastWithVerificationDeletion(StructureWorldAccess world) {
        Pair<BlockPos, BlockState> data = this.removeLastBlockPosPair();
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight();
        short ind = this.statesMap.inverse().get(state);
        return BlockPlaceUtil.placeVerifiedBlock(world, this.forceBlocks.get(ind).getLeft(), this.forceBlocks.get(ind).getRight(), pos, state);
    }

    /**
     * Method to place the first Block.
     * <p>The method also performs verification to know if the block can be placed.
     *
     * @param world the world where the block will be placed
     * @return true if the block was placed, false if not.
     */
    @Override
    public boolean placeFirstWithVerification(StructureWorldAccess world) {
        Pair<BlockPos, BlockState> data = this.getFirstPosPair();
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight();
        short ind = this.statesMap.inverse().get(state);
        return BlockPlaceUtil.placeVerifiedBlock(world, this.forceBlocks.get(ind).getLeft(), this.forceBlocks.get(ind).getRight(), pos, state);
    }

    /**
     * <p>Method to place the first Block and deleting it.
     * <p>The method also performs verification to know if the block can be placed.
     * <p>You shouldn't use this method in normal case since that the method is pretty costly O(n).
     * <p>Use instead {@code placeLastWithDeletion()} that is faster O(1).
     *
     * @param world the world where the block will be placed
     * @return true if the block was placed, false if not.
     */
    @Override
    public boolean placeFirstWithVerificationDeletion(StructureWorldAccess world) {
        Pair<BlockPos, BlockState> data = this.removeFirstBlockPos();
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight();
        short ind = this.statesMap.inverse().get(state);
        return BlockPlaceUtil.placeVerifiedBlock(world, this.forceBlocks.get(ind).getLeft(), this.forceBlocks.get(ind).getRight(), pos, state);
    }
}
