package net.rodofire.easierworldcreator.shape.block.layer;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.AbstractLongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListManager;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;

import java.util.Collection;
import java.util.Map;

public interface Layer {
    BlockListManager get(Map<ChunkPos, LongOpenHashSet> posMap);

    void place(Map<ChunkPos, LongOpenHashSet> posMap);

    BlockListManager getVerified(Map<ChunkPos, LongOpenHashSet> posMap);

    DividedBlockListManager getDivided(Map<ChunkPos, LongOpenHashSet> posMap);

    DividedBlockListManager getVerifiedDivided(Map<ChunkPos, LongOpenHashSet> posMap);

    <T extends Collection<BlockPos>> BlockListManager get(T posList);

    <T extends Collection<BlockPos>> void place(T posList);

    <T extends Collection<BlockPos>> BlockListManager getVerified(T posList);

    <T extends Collection<BlockPos>> DividedBlockListManager getDivided(T posList);

    <T extends Collection<BlockPos>> DividedBlockListManager getVerifiedDivided(T posList);

    <U extends AbstractLongCollection> BlockListManager get(U posList);

    <U extends AbstractLongCollection> void place(U posList);

    <U extends AbstractLongCollection> BlockListManager getVerified(U posList);

    <U extends AbstractLongCollection> DividedBlockListManager getDivided(U posList);

    <U extends AbstractLongCollection> DividedBlockListManager getVerifiedDivided(U posList);

}
