package net.rodofire.easierworldcreator.blockdata.blocklist;

import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CompoundBlockList extends BlockList{
    private NbtCompound tag;

    public CompoundBlockList(List<BlockPos> posList, BlockState blockState) {
        super(posList, blockState);
    }

    public CompoundBlockList(BlockPos pos, BlockState state) {
        super(pos, state);
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
    public void addCustomProperty(JsonObject json) {
        json.addProperty("nbt", tag.toString());
    }
}
