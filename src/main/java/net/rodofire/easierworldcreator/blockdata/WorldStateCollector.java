package net.rodofire.easierworldcreator.blockdata;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.Long2ShortOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.rodofire.easierworldcreator.util.LongPosHelper;
import net.rodofire.easierworldcreator.util.map.ObjectShortLinkHashBiMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class WorldStateCollector {
    ObjectShortLinkHashBiMap<BlockDataKey> data = new ObjectShortLinkHashBiMap<>();
    Long2ShortOpenHashMap posLink = new Long2ShortOpenHashMap();

    public WorldStateCollector() {
        this(30);
    }

    public WorldStateCollector(int capacity) {
        posLink = new Long2ShortOpenHashMap(capacity);
        posLink.defaultReturnValue((short) -1);
    }

    public <T extends AbstractLongCollection> WorldStateCollector collect(ServerWorld world, T posList) {
        for (long pos : posList) {
            if (posLink.containsKey(pos))
                continue;

            BlockPos decodedPos = LongPosHelper.decodeBlockPos(pos);
            BlockDataKey data = getDataKey(world, decodedPos);

            this.posLink.put(pos, this.data.put(data));
        }
        return this;
    }

    public <T extends Collection<BlockPos>> WorldStateCollector collect(ServerWorld world, T posList) {
        for (BlockPos decodedPos : posList) {

            long pos = LongPosHelper.encodeBlockPos(decodedPos);
            if (posLink.containsKey(pos))
                continue;

            BlockDataKey data = getDataKey(world, decodedPos);

            this.posLink.put(pos, this.data.put(data));
        }
        return this;
    }

    private static @NotNull BlockDataKey getDataKey(ServerWorld world, BlockPos decodedPos) {
        BlockState state = world.getBlockState(decodedPos);

        BlockDataKey data = new BlockDataKey(state);

        //get NbtTag of the block
        BlockEntity entity = world.getBlockEntity(decodedPos);
        if (entity != null) {
            DynamicRegistryManager registry = world.getRegistryManager();
            NbtCompound currentNbt = entity.createNbtWithIdentifyingData(registry);
            data.setTag(currentNbt);
        }
        return data;
    }

    public BlockState getState(long pos) {
        short index = posLink.get(pos);
        if (index == -1)
            return null;
        return this.data.get(index).getState();
    }

    public BlockDataKey getData(long pos) {
        short index = posLink.get(pos);
        if (index == -1)
            return null;
        return this.data.get(index);
    }

    public BlockState getState(BlockPos decodedPos) {
        long pos = LongPosHelper.encodeBlockPos(decodedPos);
        short index = posLink.get(pos);
        if (index == -1)
            return null;
        return this.data.get(index).getState();
    }

    public BlockDataKey getData(BlockPos decodedPos) {
        long pos = LongPosHelper.encodeBlockPos(decodedPos);
        short index = posLink.get(pos);
        if (index == -1)
            return null;
        return this.data.get(index);
    }
}
