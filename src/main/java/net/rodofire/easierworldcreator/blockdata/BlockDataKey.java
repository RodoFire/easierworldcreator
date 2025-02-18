package net.rodofire.easierworldcreator.blockdata;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;

import java.util.Objects;

public record BlockDataKey(BlockState state, NbtCompound tag) {
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BlockDataKey that = (BlockDataKey) obj;
        return Objects.equals(state, that.state) &&
                Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, tag);
    }
}
