package net.rodofire.easierworldcreator.blockdata.blocklist.basic;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.placer.blocks.util.BlockPlaceUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * add possibility of having NbtCompound in compound blockList
 */
@SuppressWarnings("unused")
public class CompoundBlockList extends DefaultBlockList {
    @Nullable
    private NbtCompound tag;

    /**
     * init a CompoundBlockList
     *
     * @param posList pos of the blockState
     * @param state   the blockState related to the pos list
     */
    public CompoundBlockList(List<BlockPos> posList, BlockState state) {
        super(posList, state);
    }

    /**
     * init a CompoundBlockList
     *
     * @param posList pos of the blockState
     * @param state   the blockState related to the pos list
     * @param tag     the nbt tag that is related to the blockState
     */
    public CompoundBlockList(List<BlockPos> posList, BlockState state, @Nullable NbtCompound tag) {
        super(posList, state);
        this.tag = tag;
    }

    /**
     * init a CompoundBlockList
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     */
    public CompoundBlockList(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    /**
     * init a CompoundBlockList
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     * @param tag   the nbt tag that is related to the blockState
     */
    public CompoundBlockList(BlockPos pos, BlockState state, @Nullable NbtCompound tag) {
        super(pos, state);
        this.tag = tag;
    }

    /**
     * used to get the NBT tag of the specified NBT
     *
     * @return the tag of the BlockList if it exists
     */
    public @Nullable NbtCompound getTag() {
        return tag;
    }

    /**
     * allow you to change the tag of the relatedBlock
     *
     * @param tag the nbt parameter of the related Block
     */
    public void setTag(@Nullable NbtCompound tag) {
        this.tag = tag;
    }

    /**
     * method to place the Block related to the index
     *
     * @param world the world the block will be placed
     * @param index the index of the BlockPos
     */
    public void place(StructureWorldAccess world, int index) {
        BlockPlaceUtil.placeBlockWithNbt(world, this.getPos(index), this.getBlockState(), this.tag);
    }


    /**
     * method to place the block with the deletion of the BlockPos
     *
     * @param world the world the block will be placed
     * @param index the index of the block
     */
    public void placeWithDeletion(StructureWorldAccess world, int index) {
        BlockPlaceUtil.placeBlockWithNbt(world, this.removePos(index), this.getBlockState(), this.tag);
    }

    /**
     * Method to place the block related to the index.
     * The method also performs verification to know if the block can be placed.
     *
     * @param world the world the block will be placed
     * @param index the index of the block
     * @return true if the block was placed, false if not
     */
    public boolean placeWithVerification(StructureWorldAccess world, int index) {
        return BlockPlaceUtil.placeVerifiedBlockWithNbt(world, false, null, this.getPos(index), this.getBlockState(), this.tag);
    }

    /**
     * Method to place the block with the deletion of the BlockPos
     * The method also performs verification to know if the block can be placed.
     *
     * @param world the world the block will be placed
     * @param index the index of the block
     * @return true if the block was placed, false if not
     */
    public boolean placeWithVerificationDeletion(StructureWorldAccess world, int index) {
        return BlockPlaceUtil.placeVerifiedBlockWithNbt(world, false, null, this.removePos(index), this.getBlockState(), this.tag);
    }

    /**
     * method to place the first Block
     *
     * @param world the world the block will be placed
     */
    public void placeFirst(StructureWorldAccess world) {
        BlockPlaceUtil.placeBlockWithNbt(world, this.getFirstPos(), this.getBlockState(), this.tag);
    }

    /**
     * Method to place the first Block and deleting it.
     * You shouldn't use this method in normal case since that the method is pretty costly O(n).
     * Use instead {@code placeLastWithDeletion()} that is faster O(1).
     *
     * @param world the world where the block will be placed
     */
    public void placeFirstWithDeletion(StructureWorldAccess world) {
        BlockPlaceUtil.placeBlockWithNbt(world, this.removeFirstPos(), this.getBlockState(), this.tag);

    }

    /**
     * Method to place the first Block.
     * <p>The method also performs verification to know if the block can be placed.
     *
     * @param world the world where the block will be placed
     * @return true if the block was placed, false if not.
     */
    public boolean placeFirstWithVerification(StructureWorldAccess world) {
        return BlockPlaceUtil.placeVerifiedBlockWithNbt(world, false, null, this.getFirstPos(), this.getBlockState(), this.tag);
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
    public boolean placeFirstWithVerificationDeletion(StructureWorldAccess world) {
        return BlockPlaceUtil.placeVerifiedBlockWithNbt(world, false, null, this.removeFirstPos(), this.getBlockState(), this.tag);
    }

    /**
     * Method to place the last Block of the comparator and removing it then.
     * Consider using this method because it gives you better performance.
     *
     * @param world the world the last block will be placed
     */
    public void placeLastWithDeletion(StructureWorldAccess world) {
        BlockPlaceUtil.placeBlockWithNbt(world, this.getLastPos(), this.getBlockState(), this.tag);
    }

    /**
     * Method to place the last Block of the comparator.
     *
     * @param world the world the last block will be placed
     */
    public void placeLast(StructureWorldAccess world) {
        BlockPlaceUtil.placeBlockWithNbt(world, this.getLastPos(), this.getBlockState(), this.tag);

    }

    /**
     * Method to place the last Block.
     *
     * @param world the world the last block will be placed
     *              The method also performs verification to know if the block can be placed.
     * @return true if the block was placed, false if not
     */
    public boolean placeLastWithVerification(StructureWorldAccess world) {
        return BlockPlaceUtil.placeVerifiedBlockWithNbt(world, false, null, this.getLastPos(), this.getBlockState(), this.tag);

    }

    /**
     * Method to place the last Block of the comparator and removing it then.
     * The method also performs verification to know if the block can be placed.
     * Consider using this method because it gives you better performance.
     *
     * @param world the world the last block will be placed
     * @return true if the block was placed, false if not
     */
    public boolean placeLastWithVerificationDeletion(StructureWorldAccess world) {
        return BlockPlaceUtil.placeVerifiedBlockWithNbt(world, false, null, this.removeLastPos(), this.getBlockState(), this.tag);
    }
}
