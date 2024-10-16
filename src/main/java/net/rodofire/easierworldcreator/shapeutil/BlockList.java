package net.rodofire.easierworldcreator.shapeutil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * class used for the generation or the modification of a NBT file
 * this is an easier version of the {@link StructureTemplate.StructureBlockInfo}
 */
@SuppressWarnings("unused")
public class BlockList {

    private List<BlockPos> posList;
    private BlockState blockState;

    @Nullable
    private NbtCompound tag;
    private Set<Block> blocksToForce = new HashSet<>();
    private boolean force = false;

    /**
     * init a BlockList
     *
     * @param posList    pos of the blockState
     * @param blockState the blockState related to the pos list
     * @param tag        the nbt of the block if it has a nbt
     * @param force      set if any block can be replaced by any blockState in this BlockList
     */
    public BlockList(List<BlockPos> posList, BlockState blockState, @Nullable NbtCompound tag, boolean force) {
        this.posList = posList;
        this.blockState = blockState;
        this.tag = tag;
        this.force = force;
    }

    /**
     * init a BlockList
     *
     * @param posList       pos of the blockState
     * @param blockState    the blockState related to the pos list
     * @param tag           the nbt of the block if it has a nbt
     * @param blocksToForce list of blocks that can be forced by any blockStates of this posList
     */
    public BlockList(List<BlockPos> posList, BlockState blockState, @Nullable NbtCompound tag, Set<Block> blocksToForce) {
        this.posList = posList;
        this.blockState = blockState;
        this.tag = tag;
        this.blocksToForce = new HashSet<>(blocksToForce);
    }

    /**
     * init a BlockList
     *
     * @param posList       pos of the blockState
     * @param blockState    the blockState related to the pos list
     * @param blocksToForce list of blocks that can be forced by any blockStates of this posList
     */
    public BlockList(List<BlockPos> posList, BlockState blockState, Set<Block> blocksToForce) {
        this.posList = posList;
        this.blockState = blockState;
        this.blocksToForce = new HashSet<>(blocksToForce);
    }

    /**
     * init a BlockList
     *
     * @param posList    pos of the blockState
     * @param blockState the blockState related to the pos list
     * @param force      set if any block can be replaced by any blockState in this BlockList
     */
    public BlockList(List<BlockPos> posList, BlockState blockState, boolean force) {
        this.posList = posList;
        this.blockState = blockState;
        this.force = force;
    }

    /**
     * init a BlockList
     *
     * @param pos        pos of the blockState
     * @param blockState the blockState related to the pos list
     * @param force      set if any block can be replaced by any blockState in this BlockList
     */
    public BlockList(BlockPos pos, BlockState blockState, boolean force) {
        this.posList = new ArrayList<>();
        this.posList.add(pos);
        this.blockState = blockState;
        this.force = force;
    }

    /**
     * init a BlockList
     *
     * @param pos           pos of the blockState
     * @param blockState    the blockState related to the pos list
     * @param blocksToForce list of blocks that can be forced by any blockStates of this posList
     */
    public BlockList(BlockPos pos, BlockState blockState, Set<Block> blocksToForce) {
        this.posList = new ArrayList<>();
        this.posList.add(pos);
        this.blockState = blockState;
        this.blocksToForce = new HashSet<>(blocksToForce);
    }

    /**
     * init a BlockList
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     */
    public BlockList(BlockPos pos, BlockState state) {
        this.posList = new ArrayList<>();
        this.posList.add(pos);
        this.blockState = state;
        this.tag = null;
    }

    /**
     * init a BlockList
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     * @param tag   the nbt of the block if it has a nbt
     */
    public BlockList(BlockPos pos, BlockState state, @Nullable NbtCompound tag) {
        this.posList = new ArrayList<>();
        this.posList.add(pos);
        this.blockState = state;
        this.tag = tag;
    }

    /**
     * init a blockList
     *
     * @param posList the list of pos of the blockState
     * @param state   the blockState related to the pos
     */
    public BlockList(List<BlockPos> posList, BlockState state) {
        this.posList = new ArrayList<>(posList);
        this.blockState = state;
        this.tag = null;
    }

    /**
     * init a BlockList
     *
     * @param posList the list of pos of the blockState
     * @param state   the blockState related to the pos
     * @param tag     the nbt of the block if it has a nbt
     */
    public BlockList(List<BlockPos> posList, BlockState state, @Nullable NbtCompound tag) {
        this.posList = new ArrayList<>(posList);
        this.blockState = state;
        this.tag = tag;
    }

    /**
     * used to get the list of blockPos related to a layer
     *
     * @return the list of BlockPos
     */
    public List<BlockPos> getPosList() {
        return posList;
    }

    /**
     * gives you the Set of every blocks that can be forced
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

    /**
     * It uses a list of blockPos to allow multiple BlockPos to have a BlockState
     *
     * @param posList a list of BlockPos
     */
    public void setPosList(List<BlockPos> posList) {
        this.posList = posList;
    }

    /**
     * allow you to add a BlockPos to the existing list
     *
     * @param pos the pos added
     */
    public void addBlockPos(BlockPos pos) {
        this.posList.add(pos);
    }

    /**
     * allow you to add multiple BlockPos to the existing list
     *
     * @param pos the pos list added
     */
    public void addBlockPos(List<BlockPos> pos) {
        this.posList.addAll(pos);
    }

    /**
     * allow you to remove a BlockPos to the existing list
     *
     * @param pos the pos removed
     */
    public void removeBlockPos(BlockPos pos) {
        this.posList.remove(pos);
    }

    /**
     * allow you to remove a list of BlockPos to the existing list
     *
     * @param pos the list pos removed
     */
    public void removeBlockPos(List<BlockPos> pos) {
        this.posList.removeAll(pos);
    }

    /**
     * used to get the blockState
     *
     * @return the blockState of the BlockList
     */
    public BlockState getBlockState() {
        return blockState;
    }

    /**
     * change the blockState of the BlockList
     *
     * @param blockState the blockState related to the BlockPos list
     */
    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
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


    @Override
    public String toString() {
        return "BlockList{" +
                "posList=" + posList +
                ", blockState=" + blockState +
                '}';
    }
}
