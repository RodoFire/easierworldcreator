package net.rodofire.easierworldcreator.blockdata.blocklist.ordered.comparator;

import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.placer.blocks.util.BlockPlaceUtil;

import java.util.List;
import java.util.Map;

/**
 * Default Ordered BlockList comparator. The class provides the basic related to order blockList comparator
 */
@SuppressWarnings("unused")
public class DefaultOrderedBlockListComparator extends OrderedBlockListComparator<BlockState> {
    /**
     * init a default ordered blockList comparator
     *
     * @param state   the state that will be tested and put (in the case it doesn't exist)
     * @param posList the blockPos that will be put related to the given state
     */
    public DefaultOrderedBlockListComparator(BlockState state, List<BlockPos> posList) {
        super(state, posList);
    }

    /**
     * init a comparator
     *
     * @param info the map that will be used to init the comparator
     */
    public DefaultOrderedBlockListComparator(Map<BlockState, List<BlockPos>> info) {
        super(info);
    }

    /**
     * init an empty comparator
     */
    public DefaultOrderedBlockListComparator() {
    }

    /**
     * Method to get the List of BlockStates that are saved in the comparator
     *
     * @return the list of blockStates
     */
    public List<BlockState> getBlockStates() {
        return getT();
    }

    /**
     * method to get the blockState related to the index
     *
     * @param index the index of the BlockState
     * @return the blockState related to the index
     */
    public BlockState getBlockState(short index) {
        return this.statesMap.get(index);
    }

    /**
     * Method to get the first BlockState
     *
     * @return the first BlockState
     */
    public BlockState getFirstBlockState() {
        return this.statesMap.get((short) 0);
    }

    /**
     * Method to get the last BlockState
     *
     * @return the last BlockState
     */
    public BlockState getLastBlockState() {
        return this.statesMap.get((short) (statesMap.size() - 1));
    }

    /**
     * method to place the first Block
     *
     * @param world the world the block will be placed
     */
    @Override
    public void placeFirst(StructureWorldAccess world) {
        Pair<BlockPos, BlockState> data = this.getFirstPosPair();
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight();
        BlockPlaceUtil.placeBlock(world, pos, state);
    }

    /**
     * Method to place the first Block and deleting it.
     * You shouldn't use this method in normal case since that the method is pretty costly O(n).
     * Use instead {@code placeLastWithDeletion()} that is faster O(1).
     *
     * @param world the world where the block will be placed
     */
    @Override
    public void placeFirstWithDeletion(StructureWorldAccess world) {
        Pair<BlockPos, BlockState> data = this.removeFirstBlockPos();
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight();
        BlockPlaceUtil.placeBlock(world, pos, state);
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
        return BlockPlaceUtil.placeVerifiedBlock(world, false, null, pos, state);
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
        return BlockPlaceUtil.placeVerifiedBlock(world, false, null, pos, state);
    }

    /**
     * Method to place the last Block of the comparator and removing it then.
     * Consider using this method because it gives you better performance.
     *
     * @param world the world the last block will be placed
     */
    @Override
    public void placeLastWithDeletion(StructureWorldAccess world) {
        Pair<BlockPos, BlockState> data = this.removeLastBlockPosPair();
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight();
        BlockPlaceUtil.placeBlock(world, pos, state);
    }

    /**
     * Method to place the last Block of the comparator.
     *
     * @param world the world the last block will be placed
     */
    @Override
    public void placeLast(StructureWorldAccess world) {
        Pair<BlockPos, BlockState> data = this.getLastPosPair();
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight();
        BlockPlaceUtil.placeBlock(world, pos, state);
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
        return BlockPlaceUtil.placeVerifiedBlock(world, false, null, pos, state);
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
        return BlockPlaceUtil.placeVerifiedBlock(world, false, null, pos, state);
    }

    /**
     * method to place the Block related to the index
     *
     * @param world the world the block will be placed
     * @param index the index of the BlockPos
     */
    @Override
    public void place(StructureWorldAccess world, int index) {
        Pair<BlockPos, BlockState> data = this.getPosPair(index);
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight();
        BlockPlaceUtil.placeBlock(world, pos, state);
    }

    /**
     * method to place the block with the deletion of the BlockPos
     *
     * @param world the world the block will be placed
     * @param index the index of the block
     */
    @Override
    public void placeWithDeletion(StructureWorldAccess world, int index) {
        Pair<BlockPos, BlockState> data = this.removeBlockPosPair(index);
        BlockPos pos = data.getLeft();
        BlockState state = data.getRight();
        BlockPlaceUtil.placeBlock(world, pos, state);
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
        return BlockPlaceUtil.placeVerifiedBlock(world, false, null, pos, state);
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
        return BlockPlaceUtil.placeVerifiedBlock(world, false, null, pos, state);
    }
}
