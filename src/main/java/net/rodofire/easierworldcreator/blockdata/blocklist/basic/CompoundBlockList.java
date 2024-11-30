package net.rodofire.easierworldcreator.blockdata.blocklist.basic;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * add possibility of having NbtCompound in compound blockList
 */
@SuppressWarnings("unused")
public class CompoundBlockList extends DefaultBlockList {
    @Nullable
    private NbtCompound tag;

    /**
     * init a CompoundBlockList
     *
     * @param posList pos of the blockState
     * @param state   the blockState related to the pos list
     */
    public CompoundBlockList(List<BlockPos> posList, BlockState state) {
        super(posList, state);
    }

    /**
     * init a CompoundBlockList
     *
     * @param posList pos of the blockState
     * @param state   the blockState related to the pos list
     * @param tag     the nbt tag that is related to the blockState
     */
    public CompoundBlockList(List<BlockPos> posList, BlockState state, @Nullable NbtCompound tag) {
        super(posList, state);
        this.tag = tag;
    }

    /**
     * init a CompoundBlockList
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     */
    public CompoundBlockList(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    /**
     * init a CompoundBlockList
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     * @param tag   the nbt tag that is related to the blockState
     */
    public CompoundBlockList(BlockPos pos, BlockState state, @Nullable NbtCompound tag) {
        super(pos, state);
        this.tag = tag;
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
}
