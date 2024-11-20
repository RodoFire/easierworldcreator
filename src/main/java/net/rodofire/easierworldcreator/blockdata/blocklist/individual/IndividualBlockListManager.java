package net.rodofire.easierworldcreator.blockdata.blocklist.individual;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

/**
 * <p>Interface to manage single BlockPos BlockList.
 * <p>It is necessary in a scenario where the BlockList needs to have the BlockPos sorted, no matter the state
 */
public interface IndividualBlockListManager {
    /**
     * method to set the BlockState related to the BlockList
     *
     * @param state the BlockState that will be set in the class
     */
    void setBlockState(BlockState state);

    /**
     * method to get the BlockState related to the object
     *
     * @return the blockState of the class
     */
    BlockState getBlockState();

    /**
     * method to set the blockPos related to the object
     */
    void setBlockPos(BlockPos pos);

    /**
     * method to get the blockPos related to the object
     *
     * @return the blockPos of the class
     */
    BlockPos getBlockPos();


}
