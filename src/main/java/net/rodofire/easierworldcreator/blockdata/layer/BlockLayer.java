package net.rodofire.easierworldcreator.blockdata.layer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;

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
    List<Short> chances;
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
        this.blockStates = new ArrayList<>(states);
        chances = new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {
            chances.add((short) 1);
        }
        this.depth = depth;
        this.blocksToForce = new HashSet<>(blocksToForce);
    }

    /**
     * init the BlockLayer
     *
     * @param states        list of BlockStates
     * @param depth         depth of the BlockStates
     * @param blocksToForce list of blocks that can be forced by any blockStates of this posList
     * @param chances       the chance of the related blockStates being chosen
     */
    public BlockLayer(List<BlockState> states, List<Short> chances, int depth, Set<Block> blocksToForce) {
        this.blockStates = new ArrayList<>(states);
        this.chances = new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {
            this.chances.add(chances.get(i));
        }
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
        this.blockStates = new ArrayList<>(states);
        chances = new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {
            chances.add((short) 1);
        }
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
        chances = new ArrayList<>();
        this.chances.add((short) 1);
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
        chances = new ArrayList<>();
        this.chances.add((short) 1);
        this.force = force;
    }

    /**
     * init the BlockLayer
     *
     * @param states list of BlockStates
     * @param depth  depth of the BlockStates
     */
    public BlockLayer(List<BlockState> states, int depth) {
        this.blockStates = new ArrayList<>(states);
        chances = new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {
            chances.add((short) 1);
        }
        this.depth = depth;
    }

    /**
     * init the BlockLayer
     *
     * @param states list of BlockStates
     */
    public BlockLayer(List<BlockState> states) {
        this.blockStates = new ArrayList<>(states);
        chances = new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {
            chances.add((short) 1);
        }
    }

    /**
     * init the BlockLayer
     *
     * @param states  list of BlockStates
     * @param chances the chance of the state being chosen
     */
    public BlockLayer(List<BlockState> states, List<Short> chances) {
        this.blockStates = new ArrayList<>(states);
        this.chances = new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {
            this.chances.add((chances.get(i)));
        }
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
        chances = new ArrayList<>();
        this.chances.add((short) 1);
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
        chances = new ArrayList<>();
        this.chances.add((short) 1);
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
     * Method used to get all the {@link BlockState} in a {@link BlockLayer}.
     * The list returned will contain the BlockStates of the list,
     * where each will be present n times, n being the chance related to that BlockState
     *
     * @return the blockStates list of the layer
     */
    public List<BlockState> getBlockStates() {
        List<BlockState> state = new ArrayList<>();
        for (int i = 0; i < this.blockStates.size(); i++) {
            for (int j = 0; j < this.chances.get(i); j++) {
                state.add(this.blockStates.get(i));
            }
        }
        return state;
    }

    /**
     * method to get the blockState as well as his chance
     *
     * @return a list of pair corresponding to blockState as well as his chance
     */
    public List<Pair<BlockState, Short>> get() {
        List<Pair<BlockState, Short>> list = new ArrayList<>();
        for (int i = 0; i < this.blockStates.size(); i++) {
            list.add(new Pair<>(this.blockStates.get(i), this.chances.get(i)));
        }
        return list;
    }

    /**
     * method used to set all the {@link BlockState} in a {@link BlockLayer}
     *
     * @param blocks change the BlockStates of a layer
     */
    public void setBlockStates(List<BlockState> blocks) {
        this.blockStates = new ArrayList<>(blocks);
        chances = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            chances.add((short) 1);
        }
    }

    /**
     * add a BlockState to the layer
     *
     * @param state BlockState to be added
     */
    public void addBlockState(BlockState state) {
        this.blockStates.add(state);
        this.chances.add((short) 1);
    }

    /**
     * add multiple BlockStates to the layer
     *
     * @param states List of BlockState to be added
     */
    public void addBlockStates(List<BlockState> states) {
        this.blockStates.addAll(states);
        for (int i = 0; i < states.size(); i++) {
            this.chances.add((short) 1);
        }
    }

    /**
     * add a BlockState to the layer
     *
     * @param state  BlockState to be added
     * @param chance the chance of a blockState being chosen
     */
    public void addBlockState(BlockState state, short chance) {
        this.blockStates.add(state);
        this.chances.add(chance);
    }

    /**
     * add multiple BlockStates to the layer
     *
     * @param states  List of BlockState to be added
     * @param chances the chance list related to the states of a blockState being chosen
     * @throws IndexOutOfBoundsException in the case, the chance list has a size inferior to the BlockStates chance.
     */
    public void addBlockStates(List<BlockState> states, List<Short> chances) {
        this.blockStates.addAll(states);
        for (int i = 0; i < states.size(); i++) {
            this.chances.add(chances.get(i));
        }
    }

    /**
     * removes some BlockStates of the Layer
     *
     * @param state list of BlockStates that will be removed
     */
    public void removeBlockState(List<BlockState> state) {
        for (BlockState blockState : state) {
            this.chances.remove(this.blockStates.indexOf(blockState));
        }
        this.blockStates.removeAll(state);
    }

    /**
     * removes a BlockState of the Layer
     *
     * @param state BlockState that will be removed
     */
    public void removeBlockState(BlockState state) {
        this.chances.remove(this.blockStates.indexOf(state));
        this.blockStates.remove(state);
    }

    /**
     * method used to remove a {@link BlockState} related to the index
     *
     * @param index remove the BlockState at the index
     */
    public void removeBlockState(int index) {
        this.chances.remove(index);
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
