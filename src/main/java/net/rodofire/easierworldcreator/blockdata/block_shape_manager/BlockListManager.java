package net.rodofire.easierworldcreator.blockdata.block_shape_manager;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.List;

@SuppressWarnings("unused")
public interface BlockListManager {
    /**
     * used to get the list of blockPos related to a layer
     *
     * @return the list of BlockPos
     */
    List<BlockPos> getPosList();

    /**
     * It uses a list of blockPos to allow multiple BlockPos to have a BlockState
     *
     * @param posList a list of BlockPos
     */
    void setPosList(List<BlockPos> posList);

    /**
     * allow you to add a BlockPos to the existing list
     *
     * @param pos the pos added
     */
    void addBlockPos(BlockPos pos);

    /**
     * allow you to add multiple BlockPos to the existing list
     *
     * @param pos the pos list added
     */
    void addBlockPos(List<BlockPos> pos);

    /**
     * allow you to remove a BlockPos to the existing list
     *
     * @param pos the pos removed
     */
    void removeBlockPos(BlockPos pos);

    /**
     * allow you to remove a list of BlockPos to the existing list
     *
     * @param pos the list pos removed
     */
    void removeBlockPos(List<BlockPos> pos);

    /**
     * method to replace one blockPos to another one
     *
     * @param oldPos the oldPos that will be replaced
     * @param newPos the newPos that will replace the other blockPos
     */
    void replaceBlockPos(BlockPos oldPos, BlockPos newPos);

    /**
     * method to get a certain pos from an index in the posList
     *
     * @param index the index of the BlockPos
     * @return the BlockPos of the index
     */
    BlockPos getPos(int index);

    /**
     * method to get the last blockPos of the posList
     *
     * @return the last blockPos of the posList
     */
    BlockPos getLastPos();

    /**
     * method to get the first blockPos of the posList
     *
     * @return the first blockPos of the posList
     */
    BlockPos getFirstPos();

    /**
     * used to get the blockState
     *
     * @return the blockState of the BlockShapeManager
     */
    BlockState getBlockState();

    /**
     * change the blockState of the BlockShapeManager
     *
     * @param blockState the blockState related to the BlockPos list
     */
    void setBlockState(BlockState blockState);
}
