package net.rodofire.easierworldcreator.shape.block.layer;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListManager;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;
import net.rodofire.easierworldcreator.blockdata.blocklist.OrderedBlockListManager;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayer;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerManager;
import net.rodofire.easierworldcreator.util.LongPosHelper;
import net.rodofire.easierworldcreator.util.WorldGenUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

//TODO test to see if everything works
public abstract class AbstractRadialLikeLayer extends AbstractLayer {
    AbstractRadialLikeLayer(BlockLayerManager blockLayer) {
        super(blockLayer);
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

        int[] center = LongPosHelper.decodeBlockPos2Array(LongPosHelper.encodeVec3d(this.centerPos));
        for (LongOpenHashSet set : posMap.values()) {
            futures.add(CompletableFuture.runAsync(() -> {
                BlockListManager manager1 = new BlockListManager();
                for (long pos : set) {
                    float distance = getDistance(center[0], center[1], center[2], LongPosHelper.decodeBlockPos2Array(pos));
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
        return manager;
    }

    @Override
    public void place(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap) {
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        int[] center = LongPosHelper.decodeBlockPos2Array(LongPosHelper.encodeVec3d(this.centerPos));
        for (LongOpenHashSet set : posMap.values()) {
            for (long pos : set) {
                float distance = getDistance(center[0], center[1], center[2], LongPosHelper.decodeBlockPos2Array(pos));
                int index = findLayerIndex(layerDistance, distance);
                BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                layer.getPlacer().place(world, layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos), layer.getRuler());
            }
        }
    }

    @Override
    public BlockListManager getVerified(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap) {
        OrderedBlockListManager worldStates = new OrderedBlockListManager();
        for (LongOpenHashSet posList : posMap.values()) {
            for (long pos : posList) {
                worldStates.put(world.getBlockState(LongPosHelper.decodeBlockPos(pos)), pos);
            }
        }
        BlockListManager manager = new BlockListManager();
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posMap.size(), Runtime.getRuntime().availableProcessors())));

        int[] center = LongPosHelper.decodeBlockPos2Array(LongPosHelper.encodeVec3d(this.centerPos));
        for (LongOpenHashSet set : posMap.values()) {
            futures.add(CompletableFuture.runAsync(() -> {
                BlockListManager manager1 = new BlockListManager();
                for (long pos : set) {
                    float distance = getDistance(center[0], center[1], center[2], LongPosHelper.decodeBlockPos2Array(pos));
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
        return manager;
    }

    @Override
    public DividedBlockListManager getDivided(Map<ChunkPos, LongOpenHashSet> posMap) {
        DividedBlockListManager manager = new DividedBlockListManager();
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }
        int[] center = LongPosHelper.decodeBlockPos2Array(LongPosHelper.encodeVec3d(this.centerPos));

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posMap.size(), Runtime.getRuntime().availableProcessors())));

        for (Map.Entry<ChunkPos, LongOpenHashSet> set : posMap.entrySet()) {
            futures.add(CompletableFuture.runAsync(() -> {
                BlockListManager manager1 = new BlockListManager();
                for (long pos : set.getValue()) {
                    float distance = getDistance(center[0], center[1], center[2], LongPosHelper.decodeBlockPos2Array(pos));
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
        return manager;
    }

    @Override
    public DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap) {
        OrderedBlockListManager worldStates = new OrderedBlockListManager();
        for (LongOpenHashSet posList : posMap.values()) {
            for (long pos : posList) {
                worldStates.put(world.getBlockState(LongPosHelper.decodeBlockPos(pos)), pos);
            }
        }

        DividedBlockListManager manager = new DividedBlockListManager();
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posMap.size(), Runtime.getRuntime().availableProcessors())));

        int[] center = LongPosHelper.decodeBlockPos2Array(LongPosHelper.encodeVec3d(this.centerPos));
        for (Map.Entry<ChunkPos, LongOpenHashSet> set : posMap.entrySet()) {
            futures.add(CompletableFuture.runAsync(() -> {
                BlockListManager manager1 = new BlockListManager();
                for (long pos : set.getValue()) {
                    float distance = getDistance(center[0], center[1], center[2], LongPosHelper.decodeBlockPos2Array(pos));
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
        return manager;
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

        int[] center = LongPosHelper.decodeBlockPos2Array(LongPosHelper.encodeVec3d(this.centerPos));

        for (BlockPos pos : posList) {
            futures.add(CompletableFuture.runAsync(() -> {
                float distance = getDistance(center[0], center[1], center[2], pos.getX(), pos.getY(), pos.getZ());
                int index = findLayerIndex(layerDistance, distance);
                BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                BlockState state = layer.getPlacer().get(layer.getBlockStates(), pos);
                synchronized (manager) {
                    manager.put(state, pos);
                }
            }, pool));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return manager;
    }

    @Override
    public <T extends Collection<BlockPos>> void place(StructureWorldAccess world, T posList) {
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        int[] center = LongPosHelper.decodeBlockPos2Array(LongPosHelper.encodeVec3d(this.centerPos));
        for (BlockPos pos : posList) {
            float distance = getDistance(center[0], center[1], center[2], pos.getX(), pos.getY(), pos.getZ());
            int index = findLayerIndex(layerDistance, distance);
            BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
            layer.getPlacer().place(world, layer.getBlockStates(), pos, layer.getRuler());
        }
    }

    @Override
    public <T extends Collection<BlockPos>> BlockListManager getVerified(StructureWorldAccess world, T posList) {
        OrderedBlockListManager worldStates = new OrderedBlockListManager();
        for (BlockPos pos : posList) {
            worldStates.put(world.getBlockState(pos), pos);
        }

        BlockListManager manager = new BlockListManager();
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posList.size(), Runtime.getRuntime().availableProcessors())));

        int[] center = LongPosHelper.decodeBlockPos2Array(LongPosHelper.encodeVec3d(this.centerPos));

        for (BlockPos pos : posList) {
            futures.add(CompletableFuture.runAsync(() -> {
                float distance = getDistance(center[0], center[1], center[2], pos.getX(), pos.getY(), pos.getZ());
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
        return manager;
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

        int[] center = LongPosHelper.decodeBlockPos2Array(LongPosHelper.encodeVec3d(this.centerPos));

        for (BlockPos pos : posList) {
            futures.add(CompletableFuture.runAsync(() -> {
                float distance = getDistance(center[0], center[1], center[2], pos.getX(), pos.getY(), pos.getZ());
                int index = findLayerIndex(layerDistance, distance);
                BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                BlockState state = layer.getPlacer().get(layer.getBlockStates(), pos);
                synchronized (manager) {
                    manager.put(state, pos);
                }
            }, pool));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return manager;
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, T posList) {
        OrderedBlockListManager worldStates = new OrderedBlockListManager();
        for (BlockPos pos : posList) {
            worldStates.put(world.getBlockState(pos), pos);
        }

        DividedBlockListManager manager = new DividedBlockListManager();
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posList.size(), Runtime.getRuntime().availableProcessors())));

        int[] center = LongPosHelper.decodeBlockPos2Array(LongPosHelper.encodeVec3d(this.centerPos));

        for (BlockPos pos : posList) {
            futures.add(CompletableFuture.runAsync(() -> {
                float distance = getDistance(center[0], center[1], center[2], pos.getX(), pos.getY(), pos.getZ());
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
        return manager;
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

        int[] center = LongPosHelper.decodeBlockPos2Array(LongPosHelper.encodeVec3d(this.centerPos));
        for (long pos : posList) {
            futures.add(CompletableFuture.runAsync(() -> {
                float distance = getDistance(center[0], center[1], center[2], LongPosHelper.decodeBlockPos2Array(pos));
                int index = findLayerIndex(layerDistance, distance);
                BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                BlockState state = layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos));
                synchronized (manager) {
                    manager.put(state, pos);
                }
            }, pool));

        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return manager;
    }

    @Override
    public <U extends AbstractLongCollection> void place(StructureWorldAccess world, U posList) {
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        int[] center = LongPosHelper.decodeBlockPos2Array(LongPosHelper.encodeVec3d(this.centerPos));
        for (long pos : posList) {
            float distance = getDistance(center[0], center[1], center[2], LongPosHelper.decodeBlockPos2Array(pos));
            int index = findLayerIndex(layerDistance, distance);
            BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
            layer.getPlacer().place(world, layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos), layer.getRuler());
        }

    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager getVerified(StructureWorldAccess world, U posList) {
        OrderedBlockListManager worldStates = new OrderedBlockListManager();
        for (long pos : posList) {
            worldStates.put(world.getBlockState(LongPosHelper.decodeBlockPos(pos)), pos);
        }

        BlockListManager manager = new BlockListManager();
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posList.size(), Runtime.getRuntime().availableProcessors())));

        int[] center = LongPosHelper.decodeBlockPos2Array(LongPosHelper.encodeVec3d(this.centerPos));
        for (long pos : posList) {
            futures.add(CompletableFuture.runAsync(() -> {
                float distance = getDistance(center[0], center[1], center[2], LongPosHelper.decodeBlockPos2Array(pos));
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
        return manager;
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

        int[] center = LongPosHelper.decodeBlockPos2Array(LongPosHelper.encodeVec3d(this.centerPos));
        for (long pos : posList) {
            futures.add(CompletableFuture.runAsync(() -> {
                float distance = getDistance(center[0], center[1], center[2], LongPosHelper.decodeBlockPos2Array(pos));
                int index = findLayerIndex(layerDistance, distance);
                BlockLayer layer = (index >= 0) ? blockLayer.get(index) : blockLayer.get(0);
                BlockState state = layer.getPlacer().get(layer.getBlockStates(), LongPosHelper.decodeBlockPos(pos));
                synchronized (manager) {
                    manager.put(state, pos);
                }
            }, pool));

        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return manager;
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, U posList) {
        OrderedBlockListManager worldStates = new OrderedBlockListManager();
        for (long pos : posList) {
            worldStates.put(world.getBlockState(LongPosHelper.decodeBlockPos(pos)), pos);
        }

        DividedBlockListManager manager = new DividedBlockListManager();
        int[] layerDistance = new int[this.blockLayer.size()];
        layerDistance[0] = this.blockLayer.get(0).getDepth();

        for (int i = 1; i < this.blockLayer.size(); i++) {
            layerDistance[i] = this.blockLayer.get(i).getDepth() + layerDistance[i - 1];
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(2, Math.min(posList.size(), Runtime.getRuntime().availableProcessors())));

        int[] center = LongPosHelper.decodeBlockPos2Array(LongPosHelper.encodeVec3d(this.centerPos));
        for (long pos : posList) {
            futures.add(CompletableFuture.runAsync(() -> {
                float distance = getDistance(center[0], center[1], center[2], LongPosHelper.decodeBlockPos2Array(pos));
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
        return manager;
    }

    protected abstract float getDistance(int centerPosX, int centerPosY, int centerPosZ, int[] pos);

    protected abstract float getDistance(int centerPosX, int centerPosY, int centerPosZ, int posX, int posY, int posZ);

    protected abstract int findLayerIndex(int[] layerDistance, float distance);

}
