package net.rodofire.easierworldcreator.blockdata.blocklist.basic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.rodofire.easierworldcreator.blockdata.BlockForceData;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * BlockList class that has force parameters
 */
@SuppressWarnings("unused")
public class ForceBlockList extends DefaultBlockList implements BlockForceData {
    private boolean force;
    private Set<Block> blocksToForce = new HashSet<>();

    /**
     * init a ForceBlockList
     *
     * @param posList       pos of the blockState
     * @param state         the blockState related to the pos list
     * @param force         set if any block can be replaced by any blockState in this BlockList
     * @param blocksToForce set all blocks that can be forced by this BlockList
     */
    public ForceBlockList(List<BlockPos> posList, BlockState state, boolean force, Set<Block> blocksToForce) {
        super(posList, state);
    }

    /**
     * init a ForceBlockList
     *
     * @param posList pos of the blockState
     * @param state   the blockState related to the pos list
     * @param force   set if any block can be replaced by any blockState in this BlockList
     */
    public ForceBlockList(List<BlockPos> posList, BlockState state, boolean force) {
        super(posList, state);
    }

    /**
     * init a ForceBlockList
     *
     * @param posList       pos of the blockState
     * @param state         the blockState related to the pos list
     * @param blocksToForce set all blocks that can be forced by this BlockList
     */
    public ForceBlockList(List<BlockPos> posList, BlockState state, Set<Block> blocksToForce) {
        super(posList, state);
    }

    /**
     * init a ForceBlockList
     *
     * @param pos           pos of the blockState
     * @param state         the blockState related to the pos list
     * @param force         set if any block can be replaced by any blockState in this BlockList
     * @param blocksToForce set all blocks that can be forced by this BlockList
     */
    public ForceBlockList(BlockPos pos, BlockState state, boolean force, Set<Block> blocksToForce) {
        super(pos, state);
    }

    /**
     * init a ForceBlockList
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     * @param force set if any block can be replaced by any blockState in this BlockList
     */
    public ForceBlockList(BlockPos pos, BlockState state, boolean force) {
        super(pos, state);
    }

    /**
     * init a ForceBlockList
     *
     * @param pos           pos of the blockState
     * @param state         the blockState related to the pos list
     * @param blocksToForce set all blocks that can be forced by this BlockList
     */
    public ForceBlockList(BlockPos pos, BlockState state, Set<Block> blocksToForce) {
        super(pos, state);
    }

    /**
     * gives you the Set of every block that can be forced
     *
     * @return the set of the blocks that can be forced
     */
    @Override
    public Set<Block> getBlocksToForce() {
        return blocksToForce;
    }

    /**
     * sets all the blocks that can be forced in the case force == false
     *
     * @param blocksToForce the set of blocks that can be forced
     */
    @Override
    public void setBlocksToForce(Set<Block> blocksToForce) {
        this.blocksToForce = new HashSet<>(blocksToForce);
    }

    /**
     * add a block to the set
     *
     * @param block the block added
     */
    @Override
    public void addBlocksToForce(Block block) {
        blocksToForce.add(block);
    }

    /**
     * add a set of blocks to the set
     *
     * @param blocksToForce the set that will be added
     */
    @Override
    public void addBlocksToForce(Set<Block> blocksToForce) {
        this.blocksToForce.addAll(blocksToForce);
    }

    /**
     * remove a block to the set
     *
     * @param block the block removed
     */
    @Override
    public void removeBlocksToForce(Block block) {
        blocksToForce.remove(block);
    }

    /**
     * remove a set of blocks to the set
     *
     * @param blocksToForce the set that will be removed
     */
    @Override
    public void removeBlocksToForce(Set<Block> blocksToForce) {
        this.blocksToForce.removeAll(blocksToForce);
    }

    /**
     * get if any block can be replaced by any BlockState of this BlockShapeManager
     *
     * @return the boolean related to it
     */
    @Override
    public boolean isForce() {
        return force;
    }

    /**
     * sets if any block can be replaced by any BlockState of this BlockShapeManager
     *
     * @param force the boolean used
     */
    @Override
    public void setForce(boolean force) {
        this.force = force;
    }
}
