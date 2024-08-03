package net.rodofire.easierworldcreator.shapegen;

import net.minecraft.block.BlockState;

import java.util.List;

/**
 * Class to create layers.
 * Layers are composed of a list of BlockStates and an int.
 * The list is all the blockStates present in a layer
 * the int represents the depth of the blockStates
 * <p>
 * Be careful with the depth it should never be < 0.
 * There is also no interest in having a depth == 0.
 */
public class BlockLayer {
    private List<BlockState> blocks;
    private int depth = 1;

    /**
     * init the BlockLayer
     *
     * @param states list of BlockStates
     * @param depth  depth of the BlockStates
     */
    public BlockLayer(List<BlockState> states, int depth) {
        this.blocks = states;
        this.depth = depth;
    }

    /**
     * init the BlockLayer
     *
     * @param states list of BlockStates
     */
    public BlockLayer(List<BlockState> states) {
        this.blocks = states;
    }

    /**
     * init the BlockLayer
     *
     * @param state if the layer is only composed of one BlockState, you don't necessary need to create a list (created automatically)
     * @param depth list of BlockStates
     */
    public BlockLayer(BlockState state, int depth) {
        this.blocks = List.of(state);
        this.depth = depth;
    }

    /**
     * init the BlockLayer
     *
     * @param state if the layer is only composed of one BlockState, you don't necessary need to create a list (created automatically)
     */
    public BlockLayer(BlockState state) {
        this.blocks = List.of(state);
    }

    /**
     * @return the depth of the layer
     */
    public int getDepth() {
        return depth;
    }

    /**
     * @param depth int to change the layer depth
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * @param depth int added to the layer depth
     */
    public void addDepth(int depth) {
        this.depth += depth;
    }

    /**
     * @return the blockStates list of the layer
     */
    public List<BlockState> getBlockStates() {
        return blocks;
    }

    /**
     *
     * @param blocks change the BlockStates of a layer
     */
    public void setBlockStates(List<BlockState> blocks) {
        this.blocks = blocks;
    }

    /**
     * add a BlockState to the layer
     * @param state BlockState to be added
     */
    public void addBlockState(BlockState state) {
        this.blocks.add(state);
    }

    /**
     * add multiple BlockStates to the layer
     * @param states List of BlockState to be added
     */
    public void addBlockStates(List<BlockState> states) {
        this.blocks.addAll(states);
    }

    /**
     * removes some BlockStates of the Layer
     * @param state list of BlockStates that will be removed
     */
    public void removeBlockState(List<BlockState> state) {
        this.blocks.removeAll(state);
    }

    /**
     * removes a BlockState of the Layer
     * @param state BlockState that will be removed
     */
    public void removeBlockState(BlockState state) {
        this.blocks.remove(state);
    }

    /**
     * @param index remove the BlockState at the index
     */
    public void removeBlockState(int index) {
        this.blocks.remove(index);
    }

    /**
     * @return the size of the BlockStates
     */
    public int size() {
        return blocks.size();
    }
}
