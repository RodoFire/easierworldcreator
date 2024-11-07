package net.rodofire.easierworldcreator.shapeutil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a layer in a structure.
 * Each layer consists of a list of {@link BlockState} objects and an integer.
 * The list contains all the {@link BlockState} objects present in the layer,
 * while the integer represents the depth of the layer.
 * <p>
 * Be cautious with the depth parameter:
 * The depth should never be less than 0.
 * There is no benefit to having a depth equal to 0.
 */
@SuppressWarnings("unused")
public class BlockLayer {
    private List<BlockState> blockStates;
    private int depth = 1;
    private Set<Block> blocksToForce = new HashSet<>();
    private boolean force;

    /**
     * init the BlockLayer
     *
     * @param states        list of BlockStates
     * @param depth         depth of the BlockStates
     * @param blocksToForce list of blocks that can be forced by any blockStates of this posList
     */
    public BlockLayer(List<BlockState> states, int depth, Set<Block> blocksToForce) {
        this.blockStates = new ArrayList<>();
        this.blockStates.addAll(states);
        this.depth = depth;
        this.blocksToForce = new HashSet<>(blocksToForce);
    }

    /**
     * init the BlockLayer
     *
     * @param states list of BlockStates
     * @param force  set if any block can be replaced by any blockState in this BlockList
     */
    public BlockLayer(List<BlockState> states, boolean force) {
        this.blockStates = new ArrayList<>();
        this.blockStates.addAll(states);
        this.force = force;
    }

    /**
     * init the BlockLayer
     *
     * @param state         if the layer is only composed of one BlockState, you don't necessary need to create a list (created automatically)
     * @param depth         list of BlockStates
     * @param blocksToForce list of blocks that can be forced by any blockStates of this posList
     */
    public BlockLayer(BlockState state, int depth, Set<Block> blocksToForce) {
        this.blockStates = new ArrayList<>();
        this.blockStates.add(state);
        this.depth = depth;
        this.blocksToForce = new HashSet<>(blocksToForce);
    }

    /**
     * init the BlockLayer
     *
     * @param state if the layer is only composed of one BlockState, you don't necessary need to create a list (created automatically)
     * @param force set if any block can be replaced by any blockState in this BlockList
     */
    public BlockLayer(BlockState state, boolean force) {
        this.blockStates = new ArrayList<>();
        this.blockStates.add(state);
        this.force = force;
    }

    /**
     * init the BlockLayer
     *
     * @param states list of BlockStates
     * @param depth  depth of the BlockStates
     */
    public BlockLayer(List<BlockState> states, int depth) {
        this.blockStates = new ArrayList<>();
        this.blockStates.addAll(states);
        this.depth = depth;
    }

    /**
     * init the BlockLayer
     *
     * @param states list of BlockStates
     */
    public BlockLayer(List<BlockState> states) {
        this.blockStates = new ArrayList<>();
        this.blockStates.addAll(states);
    }

    /**
     * init the BlockLayer
     *
     * @param state if the layer is only composed of one BlockState, you don't necessary need to create a list (created automatically)
     * @param depth list of BlockStates
     */
    public BlockLayer(BlockState state, int depth) {
        this.blockStates = new ArrayList<>();
        this.blockStates.add(state);
        this.depth = depth;
    }

    /**
     * init the BlockLayer
     *
     * @param state if the layer is only composed of one BlockState, you don't necessary need to create a list (created automatically)
     */
    public BlockLayer(BlockState state) {
        this.blockStates = new ArrayList<>();
        this.blockStates.add(state);
    }

    /**
     * method used to get all the depth in a {@link BlockLayer}
     *
     * @return the depth of the layer
     */
    public int getDepth() {
        return depth;
    }

    /**
     * set a depth to the {@link BlockLayer}
     *
     * @param depth int to change the layer depth
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * add a depth to the {@link BlockLayer}
     *
     * @param depth int added to the layer depth
     */
    public void addDepth(int depth) {
        this.depth += depth;
    }

    /**
     * method used to get all the {@link BlockState} in a {@link BlockLayer}
     *
     * @return the blockStates list of the layer
     */
    public List<BlockState> getBlockStates() {
        return blockStates;
    }

    /**
     * method used to set all the {@link BlockState} in a {@link BlockLayer}
     *
     * @param blocks change the BlockStates of a layer
     */
    public void setBlockStates(List<BlockState> blocks) {
        this.blockStates = blocks;
    }

    /**
     * add a BlockState to the layer
     *
     * @param state BlockState to be added
     */
    public void addBlockState(BlockState state) {
        this.blockStates.add(state);
    }

    /**
     * add multiple BlockStates to the layer
     *
     * @param states List of BlockState to be added
     */
    public void addBlockStates(List<BlockState> states) {
        this.blockStates.addAll(states);
    }

    /**
     * removes some BlockStates of the Layer
     *
     * @param state list of BlockStates that will be removed
     */
    public void removeBlockState(List<BlockState> state) {
        this.blockStates.removeAll(state);
    }

    /**
     * removes a BlockState of the Layer
     *
     * @param state BlockState that will be removed
     */
    public void removeBlockState(BlockState state) {
        this.blockStates.remove(state);
    }

    /**
     * method used to remove a {@link BlockState} related to the index
     *
     * @param index remove the BlockState at the index
     */
    public void removeBlockState(int index) {
        this.blockStates.remove(index);
    }

    /**
     * method used to get the size of the {@link BlockLayer}
     *
     * @return the size of the BlockStates
     */
    public int size() {
        return blockStates.size();
    }


    /**
     * gives you the Set of every block that can be forced
     *
     * @return the set of the blocks that can be forced
     */
    public Set<Block> getBlocksToForce() {
        return blocksToForce;
    }

    /**
     * sets all the blocks that can be forced in the case force == false
     *
     * @param blocksToForce the set of blocks that can be forced
     */
    public void setBlocksToForce(Set<Block> blocksToForce) {
        this.blocksToForce = new HashSet<>(blocksToForce);
    }

    /**
     * add a block to the set
     *
     * @param block the block added
     */
    public void addBlocksToForce(Block block) {
        blocksToForce.add(block);
    }

    /**
     * add a set of blocks to the set
     *
     * @param blocksToForce the set that will be added
     */
    public void addBlocksToForce(Set<Block> blocksToForce) {
        this.blocksToForce.addAll(blocksToForce);
    }

    /**
     * remove a block to the set
     *
     * @param block the block removed
     */
    public void removeBlocksToForce(Block block) {
        blocksToForce.remove(block);
    }

    /**
     * remove a set of blocks to the set
     *
     * @param blocksToForce the set that will be removed
     */
    public void removeBlocksToForce(Set<Block> blocksToForce) {
        this.blocksToForce.removeAll(blocksToForce);
    }

    /**
     * get if any block can be replaced by any BlockState of this blockList
     *
     * @return the boolean related to it
     */
    public boolean isForce() {
        return force;
    }

    /**
     * sets if any block can be replaced by any BlockState of this blockList
     *
     * @param force the boolean used
     */
    public void setForce(boolean force) {
        this.force = force;
    }

    @Override
    public String toString() {
        return "BlockLayer{" +
                "blocks=" + blockStates +
                ", depth=" + depth +
                '}';
    }
}
