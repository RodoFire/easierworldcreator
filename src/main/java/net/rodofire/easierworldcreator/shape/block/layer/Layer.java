package net.rodofire.easierworldcreator.shape.block.layer;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListManager;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;

import java.util.Collection;
import java.util.Map;

public interface Layer {
    BlockListManager get(Map<ChunkPos, LongOpenHashSet> posMap);

    void place(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap);

    BlockListManager getVerified(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap);

    DividedBlockListManager getDivided(Map<ChunkPos, LongOpenHashSet> posMap);

    DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap);

    <T extends Collection<BlockPos>> BlockListManager get(T posList);

    <T extends Collection<BlockPos>> void place(StructureWorldAccess world, T posList);

    <T extends Collection<BlockPos>> BlockListManager getVerified(StructureWorldAccess world, T posList);

    <T extends Collection<BlockPos>> DividedBlockListManager getDivided(T posList);

    <T extends Collection<BlockPos>> DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, T posList);

    <U extends AbstractLongCollection> BlockListManager get(U posList);

    <U extends AbstractLongCollection> void place(StructureWorldAccess world, U posList);

    <U extends AbstractLongCollection> BlockListManager getVerified(StructureWorldAccess world, U posList);

    <U extends AbstractLongCollection> DividedBlockListManager getDivided(U posList);

    <U extends AbstractLongCollection> DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, U posList);

    Vec3d getCenterPos();

    void setCenterPos(Vec3d centerPos);

    Vec3d getDirectionVector();

    void setDirectionVector(Vec3i directionVector);
}
