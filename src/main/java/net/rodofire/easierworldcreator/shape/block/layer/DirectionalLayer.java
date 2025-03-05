package net.rodofire.easierworldcreator.shape.block.layer;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.WorldStateCollector;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListManager;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;
import net.rodofire.easierworldcreator.blockdata.blocklist.OrderedBlockListManager;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayer;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerManager;
import net.rodofire.easierworldcreator.util.LongPosHelper;
import net.rodofire.easierworldcreator.util.WorldGenUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

//TODO need test

public class DirectionalLayer extends AbstractLayer {
    DirectionalLayer(BlockLayerManager blockLayer, Vec3d center, Vec3i direction) {
        super(blockLayer, center, direction);
    }

    @Override
    public BlockListManager get(Map<ChunkPos, LongOpenHashSet> posMap) {
        BlockListManager manager = new BlockListManager();

        int[] depth = initDepth();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(Runtime.getRuntime().availableProcessors() / 2, posMap.size()));
        double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

        this.directionVector = this.directionVector.normalize();
        for (Map.Entry<ChunkPos, LongOpenHashSet> entry : posMap.entrySet()) {
            futures.add(CompletableFuture.runAsync(() -> {
                BlockListManager threadedManager = new BlockListManager();
                for (long po : entry.getValue()) {
                    double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, LongPosHelper.decodeBlockPos(po).toCenterPos()) / distanceMin;

                    BlockLayer layer = blockLayer.get(binarySearch(depth, b));
                    threadedManager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(po)), po);
                }
                synchronized (manager) {
                    manager.put(threadedManager);
                }

            }, pool));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        pool.shutdown();

        return manager;
    }

    @Override
    public void place(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap) {

    }

    @Override
    public BlockListManager getVerified(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerWorld world1) {
            for (LongOpenHashSet set : posMap.values()) {
                worldStates.collect(world1, set);
            }
            BlockListManager manager = new BlockListManager();

            int[] depth = initDepth();

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            ForkJoinPool pool = new ForkJoinPool(Math.min(Runtime.getRuntime().availableProcessors() / 2, posMap.size()));
            double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

            this.directionVector = this.directionVector.normalize();
            for (Map.Entry<ChunkPos, LongOpenHashSet> entry : posMap.entrySet()) {
                futures.add(CompletableFuture.runAsync(() -> {
                    BlockListManager threadedManager = new BlockListManager();
                    for (long po : entry.getValue()) {
                        double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, LongPosHelper.decodeBlockPos(po).toCenterPos()) / distanceMin;

                        BlockLayer layer = blockLayer.get(binarySearch(depth, b));
                        if (layer.getRuler().canPlace(worldStates.getState(po)))
                            threadedManager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(po)), po);
                    }
                    synchronized (manager) {
                        manager.put(threadedManager);
                    }

                }, pool));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            pool.shutdown();

            return manager;
        }
        return null;
    }

    @Override
    public DividedBlockListManager getDivided(Map<ChunkPos, LongOpenHashSet> posMap) {
        DividedBlockListManager manager = new DividedBlockListManager();

        int[] depth = initDepth();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(Runtime.getRuntime().availableProcessors() / 2, posMap.size()));
        double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

        this.directionVector = this.directionVector.normalize();
        for (Map.Entry<ChunkPos, LongOpenHashSet> entry : posMap.entrySet()) {
            futures.add(CompletableFuture.runAsync(() -> {
                BlockListManager threadedManager = new BlockListManager();
                for (long po : entry.getValue()) {
                    double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, LongPosHelper.decodeBlockPos(po).toCenterPos()) / distanceMin;

                    BlockLayer layer = blockLayer.get(binarySearch(depth, b));
                    threadedManager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(po)), po);
                }
                synchronized (manager) {
                    manager.putWithoutVerification(threadedManager);
                }

            }, pool));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        pool.shutdown();

        return manager;
    }

    @Override
    public DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerWorld world1) {
            for (LongOpenHashSet set : posMap.values()) {
                worldStates.collect(world1, set);
            }
            DividedBlockListManager manager = new DividedBlockListManager();

            int[] depth = initDepth();

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            ForkJoinPool pool = new ForkJoinPool(Math.min(Runtime.getRuntime().availableProcessors() / 2, posMap.size()));
            double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

            this.directionVector = this.directionVector.normalize();
            for (Map.Entry<ChunkPos, LongOpenHashSet> entry : posMap.entrySet()) {
                futures.add(CompletableFuture.runAsync(() -> {
                    BlockListManager threadedManager = new BlockListManager();
                    for (long po : entry.getValue()) {
                        double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, LongPosHelper.decodeBlockPos(po).toCenterPos()) / distanceMin;

                        BlockLayer layer = blockLayer.get(binarySearch(depth, b));
                        if (layer.getRuler().canPlace(worldStates.getState(po)))
                            threadedManager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(po)), po);
                    }
                    synchronized (manager) {
                        manager.putWithoutVerification(threadedManager);
                    }

                }, pool));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            pool.shutdown();

            return manager;
        }
        return null;
    }

    @Override
    public <T extends Collection<BlockPos>> BlockListManager get(T posList) {
        BlockListManager manager = new BlockListManager();

        int[] depth = initDepth();

        double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

        this.directionVector = this.directionVector.normalize();
        for (BlockPos pos : posList) {
            double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, pos.toCenterPos()) / distanceMin;

            BlockLayer layer = blockLayer.get(binarySearch(depth, b));
            manager.put(layer.getPlacer().get(layer.getBlockStates(), pos), pos);
        }
        return manager;
    }

    @Override
    public <T extends Collection<BlockPos>> void place(StructureWorldAccess world, T posList) {

    }

    @Override
    public <T extends Collection<BlockPos>> BlockListManager getVerified(StructureWorldAccess world, T posList) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerWorld world1) {
            worldStates.collect(world1, posList);

            BlockListManager manager = new BlockListManager();

            int[] depth = initDepth();

            double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

            this.directionVector = this.directionVector.normalize();
            for (BlockPos pos : posList) {
                double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, pos.toCenterPos()) / distanceMin;

                BlockLayer layer = blockLayer.get(binarySearch(depth, b));
                if (layer.getRuler().canPlace(worldStates.getState(pos)))
                    manager.put(layer.getPlacer().get(layer.getBlockStates(), pos), pos);
            }
            return manager;
        }
        return null;
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getDivided(T posList) {
        DividedBlockListManager manager = new DividedBlockListManager();

        int[] depth = initDepth();

        double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

        this.directionVector = this.directionVector.normalize();
        for (BlockPos pos : posList) {
            double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, pos.toCenterPos()) / distanceMin;

            BlockLayer layer = blockLayer.get(binarySearch(depth, b));

            manager.put(layer.getPlacer().get(layer.getBlockStates(), pos), pos);
        }
        return manager;
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, T posList) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerWorld world1) {
            worldStates.collect(world1, posList);
            DividedBlockListManager manager = new DividedBlockListManager();

            int[] depth = initDepth();

            double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

            this.directionVector = this.directionVector.normalize();
            for (BlockPos pos : posList) {
                double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, pos.toCenterPos()) / distanceMin;

                BlockLayer layer = blockLayer.get(binarySearch(depth, b));
                if (layer.getRuler().canPlace(worldStates.getState(pos)))
                    manager.put(layer.getPlacer().get(layer.getBlockStates(), pos), pos);
            }
            return manager;
        }
        return null;
    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager get(U posList) {
        BlockListManager manager = new BlockListManager();

        int[] depth = initDepth();

        double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

        this.directionVector = this.directionVector.normalize();
        for (long pos : posList) {
            double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, LongPosHelper.decodeBlockPos(pos).toCenterPos()) / distanceMin;

            BlockLayer layer = blockLayer.get(binarySearch(depth, b));
            manager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos)), pos);
        }
        return manager;
    }

    @Override
    public <U extends AbstractLongCollection> void place(StructureWorldAccess world, U posList) {

    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager getVerified(StructureWorldAccess world, U posList) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerWorld world1) {
            worldStates.collect(world1, posList);

            BlockListManager manager = new BlockListManager();

            int[] depth = initDepth();

            double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

            this.directionVector = this.directionVector.normalize();
            for (long pos : posList) {
                double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, LongPosHelper.decodeBlockPos(pos).toCenterPos()) / distanceMin;

                BlockLayer layer = blockLayer.get(binarySearch(depth, b));
                if (layer.getRuler().canPlace(worldStates.getState(pos)))
                    manager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos)), pos);
            }
            return manager;
        }
        return null;
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getDivided(U posList) {
        DividedBlockListManager manager = new DividedBlockListManager();

        int[] depth = initDepth();

        double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

        this.directionVector = this.directionVector.normalize();
        for (long pos : posList) {
            double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, LongPosHelper.decodeBlockPos(pos).toCenterPos()) / distanceMin;

            BlockLayer layer = blockLayer.get(binarySearch(depth, b));

            manager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos)), pos);
        }
        return manager;
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, U posList) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerWorld world1) {
            worldStates.collect(world1, posList);

            DividedBlockListManager manager = new DividedBlockListManager();

            int[] depth = initDepth();

            double distanceMin = WorldGenUtil.getExactDistance(directionVector) / WorldGenUtil.getSquared(directionVector);

            this.directionVector = this.directionVector.normalize();
            for (long pos : posList) {
                double b = WorldGenUtil.getDistanceFromPointToPlane(this.directionVector, this.centerPos, LongPosHelper.decodeBlockPos(pos).toCenterPos()) / distanceMin;

                BlockLayer layer = blockLayer.get(binarySearch(depth, b));
                if (layer.getRuler().canPlace(worldStates.getState(pos)))
                    manager.put(layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos)), pos);
            }
            return manager;
        }
        return null;
    }

    private int @NotNull [] initDepth() {
        int[] depth = new int[blockLayer.size()];
        depth[0] = blockLayer.get(0).getDepth();

        for (int i = 1; i < blockLayer.size(); i++) {
            depth[i] = blockLayer.get(i).getDepth() + depth[i - 1];
        }
        return depth;
    }

    private int binarySearch(int[] depth, double distance) {
        int left = 0;
        int right = depth.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            double diff = Math.abs(depth[mid] - distance);

            if (diff < 1.0E-6) {
                return mid;
            } else if (depth[mid] < distance) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return left;
    }
}
