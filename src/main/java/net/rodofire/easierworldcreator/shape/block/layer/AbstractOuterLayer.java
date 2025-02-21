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
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerManager;
import net.rodofire.easierworldcreator.util.LongPosHelper;
import net.rodofire.easierworldcreator.util.WorldGenUtil;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractOuterLayer extends AbstractRadialLikeLayer {
    protected float maxDistance;

    AbstractOuterLayer(BlockLayerManager blockLayer, Vec3d center, Vec3i direction) {
        super(blockLayer, center, direction);
    }

    AbstractOuterLayer(BlockLayerManager blockLayer, Vec3d center) {
        super(blockLayer, center);
    }

    @Override
    public <T extends Collection<BlockPos>> BlockListManager get(T posList) {
        for (BlockPos pos : posList) {
            float distance = maxDistance - this.getDistance(pos.getX(), pos.getY(), pos.getZ());
            maxDistance = Math.max(distance, maxDistance);
        }
        return super.get(posList);
    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager get(U posList) {
        for (long pos : posList) {
            float distance = maxDistance - this.getDistance(LongPosHelper.decodeBlockPos2Array(pos));
            maxDistance = Math.max(distance, maxDistance);
        }
        return super.get(posList);
    }

    @Override
    public BlockListManager get(Map<ChunkPos, LongOpenHashSet> posMap) {
        for (LongOpenHashSet set : posMap.values()) {
            for (long pos : set) {
                float distance = maxDistance - this.getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                maxDistance = Math.max(distance, maxDistance);
            }
        }
        return super.get(posMap);
    }

    @Override
    public <T extends Collection<BlockPos>> BlockListManager getVerified(StructureWorldAccess world, T posList) {
        for (BlockPos pos : posList) {
            float distance = maxDistance - this.getDistance(pos.getX(), pos.getY(), pos.getZ());
            maxDistance = Math.max(distance, maxDistance);
        }
        return super.getVerified(world, posList);
    }

    @Override
    public <T extends Collection<BlockPos>> void place(StructureWorldAccess world, T posList) {
        for (BlockPos pos : posList) {
            float distance = maxDistance - this.getDistance(pos.getX(), pos.getY(), pos.getZ());
            maxDistance = Math.max(distance, maxDistance);
        }
        super.place(world, posList);
    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager getVerified(StructureWorldAccess world, U posList) {
        for (long pos : posList) {
            float distance = maxDistance - this.getDistance(LongPosHelper.decodeBlockPos2Array(pos));
            maxDistance = Math.max(distance, maxDistance);
        }
        return super.getVerified(world, posList);
    }

    @Override
    public BlockListManager getVerified(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap) {
        for (LongOpenHashSet set : posMap.values()) {
            for (long pos : set) {
                float distance = maxDistance - this.getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                maxDistance = Math.max(distance, maxDistance);
            }
        }
        return super.getVerified(world, posMap);
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getDivided(T posList) {
        for (BlockPos pos : posList) {
            float distance = maxDistance - this.getDistance(pos.getX(), pos.getY(), pos.getZ());
            maxDistance = Math.max(distance, maxDistance);
        }
        return super.getDivided(posList);
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getDivided(U posList) {
        for (long pos : posList) {
            float distance = maxDistance - this.getDistance(LongPosHelper.decodeBlockPos2Array(pos));
            maxDistance = Math.max(distance, maxDistance);
        }
        return super.getDivided(posList);
    }

    @Override
    public DividedBlockListManager getDivided(Map<ChunkPos, LongOpenHashSet> posMap) {
        for (LongOpenHashSet set : posMap.values()) {
            for (long pos : set) {
                float distance = maxDistance - this.getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                maxDistance = Math.max(distance, maxDistance);
            }
        }
        return super.getDivided(posMap);
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, T posList) {
        for (BlockPos pos : posList) {
            float distance = maxDistance - this.getDistance(pos.getX(), pos.getY(), pos.getZ());
            maxDistance = Math.max(distance, maxDistance);
        }
        return super.getVerifiedDivided(world, posList);
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, U posList) {
        for (long pos : posList) {
            float distance = maxDistance - this.getDistance(LongPosHelper.decodeBlockPos2Array(pos));
            maxDistance = Math.max(distance, maxDistance);
        }
        return super.getVerifiedDivided(world, posList);
    }

    @Override
    public DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap) {
        for (LongOpenHashSet set : posMap.values()) {
            for (long pos : set) {
                float distance = maxDistance - this.getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                maxDistance = Math.max(distance, maxDistance);
            }
        }
        return super.getVerifiedDivided(world, posMap);
    }

    @Override
    public <U extends AbstractLongCollection> void place(StructureWorldAccess world, U posList) {
        for (long pos : posList) {
            float distance = maxDistance - this.getDistance(LongPosHelper.decodeBlockPos2Array(pos));
            maxDistance = Math.max(distance, maxDistance);
        }
        super.place(world, posList);
    }

    @Override
    public void place(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap) {
        for (LongOpenHashSet set : posMap.values()) {
            for (long pos : set) {
                float distance = maxDistance - this.getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                maxDistance = Math.max(distance, maxDistance);
            }
        }
        super.place(world, posMap);
    }
}
