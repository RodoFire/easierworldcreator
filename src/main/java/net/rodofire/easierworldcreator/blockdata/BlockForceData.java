package net.rodofire.easierworldcreator.blockdata;

import net.minecraft.block.Block;

import java.util.Set;

/**
 * interface for manipulating how blocks can be forced or not
 */
@SuppressWarnings("unused")
public interface BlockForceData {
    /**
     * gives you the Set of every block that can be forced
     *
     * @return the set of the blocks that can be forced
     */
    Set<Block> getBlocksToForce();

    /**
     * sets all the blocks that can be forced in the case force == false
     *
     * @param blocksToForce the set of blocks that can be forced
     */
    void setBlocksToForce(Set<Block> blocksToForce);

    /**
     * add a block to the set
     *
     * @param block the block added
     */
    void addBlocksToForce(Block block);

    /**
     * add a set of blocks to the set
     *
     * @param blocksToForce the set that will be added
     */
    void addBlocksToForce(Set<Block> blocksToForce);

    /**
     * remove a block to the set
     *
     * @param block the block removed
     */
    void removeBlocksToForce(Block block);

    /**
     * remove a set of blocks to the set
     *
     * @param blocksToForce the set that will be removed
     */
    void removeBlocksToForce(Set<Block> blocksToForce);

    /**
     * get if any block can be replaced by any BlockState of this BlockShapeManager
     *
     * @return the boolean related to it
     */
    boolean isForce();

    /**
     * sets if any block can be replaced by any BlockState of this BlockShapeManager
     *
     * @param force the boolean used
     */
    void setForce(boolean force);
}
