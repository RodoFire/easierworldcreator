package net.rodofire.easierworldcreator.blockdata.blocklist.individual;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;


/**
 * Default class for IndividualBlockList
 */
@SuppressWarnings("unused")
public class DefaultIndividualBlockList implements IndividualBlockListManager {
    private BlockState state;
    private BlockPos pos;


    /**
     * init a Default Individual BlockList
     *
     * @param pos   the BlockPos related to the class
     * @param state the BlockState related to the class
     */
    public DefaultIndividualBlockList(BlockPos pos, BlockState state) {
        this.pos = pos;
        this.state = state;
    }


    /**
     * method to set the BlockState related to the BlockList
     *
     * @param state the BlockState that will be set in the class
     */
    @Override
    public void setBlockState(BlockState state) {
        this.state = state;
    }

    /**
     * method to get the BlockState related to the object
     *
     * @return the blockState of the class
     */
    @Override
    public BlockState getBlockState() {
        return this.state;
    }

    /**
     * method to set the blockPos related to the object
     */
    @Override
    public void setBlockPos(BlockPos pos) {
        this.pos = pos;
    }

    /**
     * method to get the blockPos related to the object
     *
     * @return the blockPos of the class
     */
    @Override
    public BlockPos getBlockPos() {
        return this.pos;
    }
}
