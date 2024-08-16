package net.rodofire.easierworldcreator.shapeutil;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * class used for the generation or the modification of a NBT file
 * this is an easier version of the {@link StructureTemplate.StructureBlockInfo}
 */
public class BlockList {
    private List<BlockPos> poslist = new ArrayList<BlockPos>();
    private BlockState blockstate;
    @Nullable
    private NbtCompound tag;

    /**
     * init a BlockList
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     */
    public BlockList(BlockPos pos, BlockState state) {
        this.poslist = new ArrayList<BlockPos>();
        this.poslist.add(pos);
        this.blockstate = state;
        this.tag = null;
    }

    /**
     * init a BlockList
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     * @param tag   the nbt of the block if it has an nbt
     */
    public BlockList(BlockPos pos, BlockState state, NbtCompound tag) {
        this.poslist = new ArrayList<BlockPos>();
        this.poslist.add(pos);
        this.blockstate = state;
        this.tag = tag;
    }

    /**
     * init a blockList
     *
     * @param poslist the list of pos of the blockState
     * @param state   the blockState related to the pos
     */
    public BlockList(List<BlockPos> poslist, BlockState state) {
        this.poslist = new ArrayList<>(poslist);
        this.blockstate = state;
        this.tag = null;
    }

    /**
     * init a BlockList
     *
     * @param poslist the list of pos of the blockState
     * @param state   the blockState related to the pos
     * @param tag     the nbt of the block if it has an nbt
     */
    public BlockList(List<BlockPos> poslist, BlockState state, @Nullable NbtCompound tag) {
        this.poslist = new ArrayList<>(poslist);
        this.blockstate = state;
        this.tag = tag;
    }

    /**
     * used to get the list of blockPos related to a layer
     *
     * @return the list of BlockPos
     */
    public List<BlockPos> getPoslist() {
        return poslist;
    }

    /**
     * It uses a list of blockPos to allow multiple BlockPos to have a BlockState
     *
     * @param poslist a list of BlockPos
     */
    public void setPoslist(List<BlockPos> poslist) {
        this.poslist = poslist;
    }

    /**
     * allow you to add a BlockPos to the existing list
     *
     * @param pos the pos added
     */
    public void addBlockPos(BlockPos pos) {
        this.poslist.add(pos);
    }

    /**
     * allow you to add multiple BlockPos to the existing list
     *
     * @param pos the pos list added
     */
    public void addBlockPos(List<BlockPos> pos) {
        this.poslist.addAll(pos);
    }

    /**
     * allow you to remove a BlockPos to the existing list
     *
     * @param pos the pos removed
     */
    public void removeBlockPos(BlockPos pos) {
        this.poslist.remove(pos);
    }

    /**
     * allow you to remove a list of BlockPos to the existing list
     *
     * @param pos the list pos removed
     */
    public void removeBlockPos(List<BlockPos> pos) {
        this.poslist.removeAll(pos);
    }

    /**
     * used to get the blockState
     *
     * @return the blockState of the BlockList
     */
    public BlockState getBlockstate() {
        return blockstate;
    }

    /**
     * change the blockState of the BlockList
     *
     * @param blockstate the blockState related to the BlockPos list
     */
    public void setBlockstate(BlockState blockstate) {
        this.blockstate = blockstate;
    }

    /**
     * used to get the NBT tag of the specified NBT
     *
     * @return the tag of the BlockList if it exists
     */
    public NbtCompound getTag() {
        return tag;
    }

    /**
     * allow you to change the tag of the relatedBlock
     *
     * @param tag the nbt parameter of the related Block
     */
    public void setTag(NbtCompound tag) {
        this.tag = tag;
    }


    @Override
    public String toString() {
        return "BlockList{" +
                "poslist=" + poslist +
                ", blockstate=" + blockstate +
                '}';
    }
}
