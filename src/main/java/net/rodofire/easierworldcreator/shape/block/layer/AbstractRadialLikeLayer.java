package net.rodofire.easierworldcreator.shape.block.layer;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.block.BlockState;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

//TODO test to see if everything works
public abstract class AbstractRadialLikeLayer extends AbstractLayer {
    AbstractRadialLikeLayer(BlockLayerManager blockLayer, Vec3d center, Vec3i direction) {
        super(blockLayer, center, direction);
    }

    AbstractRadialLikeLayer(BlockLayerManager blockLayer, Vec3d center) {
        super(blockLayer, center);
    }

    @Override
    public BlockListManager get(Map<ChunkPos, LongOpenHashSet> posMap) {
        BlockListManager manager = new BlockListManager();
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posMap.size(), Runtime.getRuntime().availableProcessors())));

        for (LongOpenHashSet set : posMap.values()) {
            futures.add(CompletableFuture.runAsync(() -> {
                BlockListManager manager1 = new BlockListManager();
                for (long pos : set) {
                    float distance = getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                    int index = findLayerIndex(layerDistance, distance);
                    BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                    BlockState state = layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos));
                    manager1.put(state, pos);
                }
                synchronized (manager) {
                    manager.put(manager1);
                }
            }, pool));

        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        pool.shutdown();
        return manager;
    }

    @Override
    public void place(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap) {
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        for (LongOpenHashSet set : posMap.values()) {
            for (long pos : set) {
                float distance = getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                int index = findLayerIndex(layerDistance, distance);
                BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                layer.getPlacer().place(world, layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos), layer.getRuler());
            }
        }
    }

    @Override
    public BlockListManager getVerified(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerWorld world1) {
            for (LongOpenHashSet posList : posMap.values()) {
                worldStates.collect(world1, posList);
            }
            BlockListManager manager = new BlockListManager();
            int[] layerDistance = new int[this.blockLayer.size()];
            layerDistance[0] = this.blockLayer.get(0).getDepth();

            for (int i = 1; i < this.blockLayer.size(); i++) {
                layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
            }

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posMap.size(), Runtime.getRuntime().availableProcessors())));

            for (LongOpenHashSet set : posMap.values()) {
                futures.add(CompletableFuture.runAsync(() -> {
                    BlockListManager manager1 = new BlockListManager();
                    for (long pos : set) {
                        float distance = getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                        int index = findLayerIndex(layerDistance, distance);
                        BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                        if (!layer.getRuler().canPlace(worldStates.getState(pos)))
                            continue;
                        BlockState state = layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos));
                        manager1.put(state, pos);
                    }
                    synchronized (manager) {
                        manager.put(manager1);
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
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posMap.size(), Runtime.getRuntime().availableProcessors())));

        for (Map.Entry<ChunkPos, LongOpenHashSet> set : posMap.entrySet()) {
            futures.add(CompletableFuture.runAsync(() -> {
                BlockListManager manager1 = new BlockListManager();
                for (long pos : set.getValue()) {
                    float distance = getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                    int index = findLayerIndex(layerDistance, distance);
                    BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                    BlockState state = layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos));
                    manager1.put(state, pos);
                }
                synchronized (manager) {
                    manager.putWithoutVerification(set.getKey(), manager1);
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
            for (LongOpenHashSet posList : posMap.values()) {
                worldStates.collect(world1, posList);
            }

            DividedBlockListManager manager = new DividedBlockListManager();
            int[] layerDistance = new int[this.blockLayer.size()];
            layerDistance[0] = this.blockLayer.get(0).getDepth();

            for (int i = 1; i < this.blockLayer.size(); i++) {
                layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
            }

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posMap.size(), Runtime.getRuntime().availableProcessors())));

            for (Map.Entry<ChunkPos, LongOpenHashSet> set : posMap.entrySet()) {
                futures.add(CompletableFuture.runAsync(() -> {
                    BlockListManager manager1 = new BlockListManager();
                    for (long pos : set.getValue()) {
                        float distance = getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                        int index = findLayerIndex(layerDistance, distance);
                        BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                        if (!layer.getRuler().canPlace(worldStates.getState(pos)))
                            continue;

                        BlockState state = layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos));
                        manager1.put(state, pos);
                    }
                    synchronized (manager) {
                        manager.putWithoutVerification(set.getKey(), manager1);
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
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posList.size(), Runtime.getRuntime().availableProcessors())));


        for (BlockPos pos : posList) {
            futures.add(CompletableFuture.runAsync(() -> {
                float distance = getDistance(pos.getX(), pos.getY(), pos.getZ());
                int index = findLayerIndex(layerDistance, distance);
                BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                BlockState state = layer.getPlacer().get(layer.getBlockStates(), pos);
                synchronized (manager) {
                    manager.put(state, pos);
                }
            }, pool));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        pool.shutdown();
        return manager;
    }

    @Override
    public <T extends Collection<BlockPos>> void place(StructureWorldAccess world, T posList) {
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        for (BlockPos pos : posList) {
            float distance = getDistance(pos.getX(), pos.getY(), pos.getZ());
            int index = findLayerIndex(layerDistance, distance);
            BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
            layer.getPlacer().place(world, layer.getBlockStates(), pos, layer.getRuler());
        }
    }

    @Override
    public <T extends Collection<BlockPos>> BlockListManager getVerified(StructureWorldAccess world, T posList) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerWorld world1) {
            worldStates.collect(world1, posList);

            BlockListManager manager = new BlockListManager();
            int[] layerDistance = new int[this.blockLayer.size()];
            layerDistance[0] = this.blockLayer.get(0).getDepth();

            for (int i = 1; i < this.blockLayer.size(); i++) {
                layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
            }

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posList.size(), Runtime.getRuntime().availableProcessors())));


            for (BlockPos pos : posList) {
                futures.add(CompletableFuture.runAsync(() -> {
                    float distance = getDistance(pos.getX(), pos.getY(), pos.getZ());
                    int index = findLayerIndex(layerDistance, distance);
                    BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                    if (layer.getRuler().canPlace(worldStates.getState(LongPosHelper.encodeBlockPos(pos)))) {
                        BlockState state = layer.getPlacer().get(layer.getBlockStates(), pos);
                        synchronized (manager) {
                            manager.put(state, pos);
                        }
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
    public <T extends Collection<BlockPos>> DividedBlockListManager getDivided(T posList) {
        DividedBlockListManager manager = new DividedBlockListManager();
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posList.size(), Runtime.getRuntime().availableProcessors())));


        for (BlockPos pos : posList) {
            futures.add(CompletableFuture.runAsync(() -> {
                float distance = getDistance(pos.getX(), pos.getY(), pos.getZ());
                int index = findLayerIndex(layerDistance, distance);
                BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                BlockState state = layer.getPlacer().get(layer.getBlockStates(), pos);
                synchronized (manager) {
                    manager.put(state, pos);
                }
            }, pool));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        pool.shutdown();
        return manager;
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, T posList) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerWorld world1) {
            worldStates.collect(world1, posList);

            DividedBlockListManager manager = new DividedBlockListManager();
            int[] layerDistance = new int[this.blockLayer.size()];
            layerDistance[0] = this.blockLayer.get(0).getDepth();

            for (int i = 1; i < this.blockLayer.size(); i++) {
                layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
            }

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posList.size(), Runtime.getRuntime().availableProcessors())));


            for (BlockPos pos : posList) {
                futures.add(CompletableFuture.runAsync(() -> {
                    float distance = getDistance(pos.getX(), pos.getY(), pos.getZ());
                    int index = findLayerIndex(layerDistance, distance);
                    BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                    if (layer.getRuler().canPlace(worldStates.getState(LongPosHelper.encodeBlockPos(pos)))) {
                        BlockState state = layer.getPlacer().get(layer.getBlockStates(), pos);
                        synchronized (manager) {
                            manager.put(state, pos);
                        }
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
    public <U extends AbstractLongCollection> BlockListManager get(U posList) {
        BlockListManager manager = new BlockListManager();
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posList.size(), Runtime.getRuntime().availableProcessors())));

        for (long pos : posList) {
            futures.add(CompletableFuture.runAsync(() -> {
                float distance = getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                int index = findLayerIndex(layerDistance, distance);
                BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                BlockState state = layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos));
                synchronized (manager) {
                    manager.put(state, pos);
                }
            }, pool));

        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        pool.shutdown();
        return manager;
    }

    @Override
    public <U extends AbstractLongCollection> void place(StructureWorldAccess world, U posList) {
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        for (long pos : posList) {
            float distance = getDistance(LongPosHelper.decodeBlockPos2Array(pos));
            int index = findLayerIndex(layerDistance, distance);
            BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
            layer.getPlacer().place(world, layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos), layer.getRuler());
        }

    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager getVerified(StructureWorldAccess world, U posList) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerWorld world1) {
            worldStates.collect(world1, posList);

            BlockListManager manager = new BlockListManager();
            int[] layerDistance = new int[this.blockLayer.size()];
            layerDistance[0] = this.blockLayer.get(0).getDepth();

            for (int i = 1; i < this.blockLayer.size(); i++) {
                layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
            }

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posList.size(), Runtime.getRuntime().availableProcessors())));

            for (long pos : posList) {
                futures.add(CompletableFuture.runAsync(() -> {
                    float distance = getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                    int index = findLayerIndex(layerDistance, distance);
                    BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                    if (layer.getRuler().canPlace(worldStates.getState(pos))) {
                        BlockState state = layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos));
                        synchronized (manager) {
                            manager.put(state, pos);
                        }
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
    public <U extends AbstractLongCollection> DividedBlockListManager getDivided(U posList) {
        DividedBlockListManager manager = new DividedBlockListManager();
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posList.size(), Runtime.getRuntime().availableProcessors())));

        for (long pos : posList) {
            futures.add(CompletableFuture.runAsync(() -> {
                float distance = getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                int index = findLayerIndex(layerDistance, distance);
                BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                BlockState state = layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos));
                synchronized (manager) {
                    manager.put(state, pos);
                }
            }, pool));

        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        pool.shutdown();
        return manager;
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, U posList) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerWorld world1) {
            worldStates.collect(world1, posList);

            DividedBlockListManager manager = new DividedBlockListManager();
            int[] layerDistance = new int[this.blockLayer.size()];
            layerDistance[0] = this.blockLayer.get(0).getDepth();

            for (int i = 1; i < this.blockLayer.size(); i++) {
                layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
            }

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posList.size(), Runtime.getRuntime().availableProcessors())));

            for (long pos : posList) {
                futures.add(CompletableFuture.runAsync(() -> {
                    float distance = getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                    int index = findLayerIndex(layerDistance, distance);
                    BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                    if (layer.getRuler().canPlace(worldStates.getState(pos))) {
                        BlockState state = layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos));
                        synchronized (manager) {
                            manager.put(state, pos);
                        }
                    }
                }, pool));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            pool.shutdown();
            return manager;
        }
        return null;
    }

    protected abstract float getDistance(int[] pos);

    protected abstract float getDistance(int posX, int posY, int posZ);

    protected abstract int findLayerIndex(int[] layerDistance, float distance);

}
