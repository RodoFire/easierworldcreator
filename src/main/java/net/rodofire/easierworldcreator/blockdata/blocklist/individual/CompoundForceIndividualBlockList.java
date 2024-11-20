package net.rodofire.easierworldcreator.blockdata.blocklist.individual;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.rodofire.easierworldcreator.blockdata.BlockForceData;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * add the possibility of having force in compound individual blockList
 */
@SuppressWarnings("unused")
public class CompoundForceIndividualBlockList extends CompoundIndividualBlockList implements BlockForceData {
    private boolean force;
    private Set<Block> blocksToForce = new HashSet<>();


    /**
     * init a CompoundForceIndividualBlockList
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     */
    public CompoundForceIndividualBlockList(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    /**
     * init a CompoundForceIndividualBlockList
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     * @param tag   the nbt tag that is related to the blockState
     */
    public CompoundForceIndividualBlockList(BlockPos pos, BlockState state, @Nullable NbtCompound tag) {
        super(pos, state, tag);
    }

    /**
     * init a CompoundForceIndividualBlockList
     *
     * @param pos           pos of the blockState
     * @param state         the blockState related to the pos list
     * @param force         set if any block can be replaced by any blockState in this BlockList
     * @param blocksToForce set all blocks that can be forced by this BlockList
     */
    public CompoundForceIndividualBlockList(BlockPos pos, BlockState state, boolean force, Set<Block> blocksToForce) {
        super(pos, state);
        this.force = force;
        this.blocksToForce = new HashSet<>(blocksToForce);
    }

    /**
     * init a CompoundForceIndividualBlockList
     *
     * @param pos           pos of the blockState
     * @param state         the blockState related to the pos list
     * @param force         set if any block can be replaced by any blockState in this BlockList
     * @param blocksToForce set all blocks that can be forced by this BlockList
     * @param tag           the nbt tag that is related to the blockState
     */
    public CompoundForceIndividualBlockList(BlockPos pos, BlockState state, @Nullable NbtCompound tag, boolean force, Set<Block> blocksToForce) {
        super(pos, state, tag);
        this.force = force;
        this.blocksToForce = new HashSet<>(blocksToForce);
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
