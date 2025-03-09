package net.rodofire.easierworldcreator.shape.block.layer;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.StructurePlacementRuleManager;
import net.rodofire.easierworldcreator.blockdata.WorldStateCollector;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListManager;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;
import net.rodofire.easierworldcreator.blockdata.blocklist.OrderedBlockListManager;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayer;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerManager;
import net.rodofire.easierworldcreator.shape.block.placer.LayerPlacer;
import net.rodofire.easierworldcreator.util.LongPosHelper;
import net.rodofire.easierworldcreator.util.consumer.QuadConsumer;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

class SurfaceLayer extends AbstractLayer {

    SurfaceLayer(BlockLayerManager blockLayer) {
        super(blockLayer);
    }

    //TODO see which performs better: iterator or LongOpenHashSet allocation.
    // See if pool good parameterized.
    // see about synchronised, if that's not better to store the result in a map and to join them later.
    @Override
    public BlockListManager get(Map<ChunkPos, LongOpenHashSet> posMap) {
        BlockListManager manager = new BlockListManager();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(posMap.size(), Runtime.getRuntime().availableProcessors()));

        //System.out.println("get");
        for (Map.Entry<ChunkPos, LongOpenHashSet> entry : posMap.entrySet()) {
            futures.add(CompletableFuture.runAsync(() -> {
                //System.out.println("future");
                BlockListManager threadedManager = new BlockListManager();
                LongSet leftPositions = entry.getValue();

                processCommonGet(leftPositions, (placer, states, ruler, pos) -> {
                    threadedManager.put(placer.get(states, LongPosHelper.decodeBlockPos(pos)), pos, ruler);
                });
                //System.out.println("process common");

                synchronized (manager) {
                    manager.put(threadedManager);
                    //System.out.println("synchronised");
                }
                //System.out.println("processed layer");
            }, pool));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        pool.shutdown();
        return manager;
    }

    @Override
    public void place(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap) {
        for (Map.Entry<ChunkPos, LongOpenHashSet> entry : posMap.entrySet()) {
            LongSet leftPositions = entry.getValue();

            for (int i = 1; i < blockLayer.size(); i++) {
                if (leftPositions.isEmpty()) {
                    break;
                }

                BlockLayer layer = blockLayer.get(i - 1);
                int depth = layer.getDepth();
                LongSet difference = new LongOpenHashSet();

                LayerPlacer placer = layer.getPlacer();
                List<BlockState> states = layer.getBlockStates();
                StructurePlacementRuleManager ruler = layer.getRuler();

                for (long pos : leftPositions) {
                    if (!leftPositions.contains(LongPosHelper.up(pos, depth))) {
                        difference.add(pos);
                        placer.place(world, states, LongPosHelper.decodeBlockPos(pos), ruler);
                    }
                }
                leftPositions.removeAll(difference);
            }

            if (!leftPositions.isEmpty()) {
                List<BlockState> states = blockLayer.getLastLayer().getBlockStates();
                LayerPlacer placer = blockLayer.getLastLayer().getPlacer();
                StructurePlacementRuleManager ruler = blockLayer.getLastLayer().getRuler();
                for (long pos : leftPositions) {
                    placer.place(world, states, LongPosHelper.decodeBlockPos(pos), ruler);
                }
            }

        }
    }

    @Override
    public BlockListManager getVerified(StructureWorldAccess world, Map<ChunkPos, LongOpenHashSet> posMap) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerWorld world1) {
            for(LongOpenHashSet set : posMap.values()) {
                worldStates.collect(world1, set);
            }
            BlockListManager manager = new BlockListManager();

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            ForkJoinPool pool = new ForkJoinPool(Math.min(posMap.size(), Runtime.getRuntime().availableProcessors()));

            for (Map.Entry<ChunkPos, LongOpenHashSet> entry : posMap.entrySet()) {
                futures.add(CompletableFuture.runAsync(() -> {
                    BlockListManager threadedManager = new BlockListManager();
                    LongSet leftPositions = entry.getValue();

                    processCommonGet(leftPositions, (placer, states, ruler, pos) -> {
                        if (ruler.canPlace(worldStates.getState(pos)))
                            manager.put(placer.get(states, LongPosHelper.decodeBlockPos(pos)), pos);
                    });

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

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ForkJoinPool pool = new ForkJoinPool(Math.min(posMap.size(), Runtime.getRuntime().availableProcessors()));

        for (Map.Entry<ChunkPos, LongOpenHashSet> entry : posMap.entrySet()) {
            futures.add(CompletableFuture.runAsync(() -> {
                BlockListManager threadedManager = new BlockListManager();
                LongSet leftPositions = entry.getValue();

                processCommonGet(leftPositions, (placer, states, ruler, pos) -> {
                    threadedManager.put(placer.get(states, LongPosHelper.decodeBlockPos(pos)), pos, ruler);
                });

                synchronized (manager) {
                    manager.putWithoutVerification(entry.getKey(), threadedManager);
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
            for(LongOpenHashSet set : posMap.values()) {
                worldStates.collect(world1, set);
            }

            DividedBlockListManager manager = new DividedBlockListManager();

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            ForkJoinPool pool = new ForkJoinPool(Math.min(posMap.size(), Runtime.getRuntime().availableProcessors()));

            for (Map.Entry<ChunkPos, LongOpenHashSet> entry : posMap.entrySet()) {
                futures.add(CompletableFuture.runAsync(() -> {
                    BlockListManager threadedManager = new BlockListManager();
                    LongSet leftPositions = entry.getValue();

                    processCommonGet(leftPositions, (placer, states, ruler, pos) -> {
                        if (ruler.canPlace(worldStates.getState(pos)))
                            threadedManager.put(placer.get(states, LongPosHelper.decodeBlockPos(pos)), pos);
                    });

                    synchronized (manager) {
                        manager.putWithoutVerification(entry.getKey(), threadedManager);
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

        Set<BlockPos> leftPositions = new HashSet<>(posList);

        for (int i = 1; i < blockLayer.size(); i++) {
            if (leftPositions.isEmpty()) {
                break;
            }

            BlockLayer layer = blockLayer.get(i - 1);
            int depth = layer.getDepth();
            Set<BlockPos> difference = new HashSet<>();

            LayerPlacer placer = layer.getPlacer();
            List<BlockState> states = layer.getBlockStates();
            StructurePlacementRuleManager ruler = layer.getRuler();

            for (BlockPos pos : leftPositions) {
                if (!leftPositions.contains(pos.up(depth))) {
                    difference.add(pos);
                    manager.put(placer.get(states, pos), pos, ruler);
                }
            }
            leftPositions.removeAll(difference);
        }

        if (!leftPositions.isEmpty()) {
            LayerPlacer placer = blockLayer.getLastLayer().getPlacer();
            List<BlockState> states = blockLayer.getLastLayer().getBlockStates();
            StructurePlacementRuleManager ruler = blockLayer.getLastLayer().getRuler();
            for (BlockPos pos : leftPositions) {
                manager.put(placer.get(states, pos), pos, ruler);
            }
        }

        return manager;
    }

    @Override
    public <T extends Collection<BlockPos>> void place(StructureWorldAccess world, T posList) {
        Set<BlockPos> leftPositions = new HashSet<>(posList);

        for (int i = 1; i < blockLayer.size(); i++) {
            if (leftPositions.isEmpty()) {
                break;
            }

            BlockLayer layer = blockLayer.get(i - 1);
            int depth = layer.getDepth();
            Set<BlockPos> difference = new HashSet<>();

            LayerPlacer placer = layer.getPlacer();
            List<BlockState> states = layer.getBlockStates();
            StructurePlacementRuleManager ruler = layer.getRuler();

            for (BlockPos pos : leftPositions) {
                if (!leftPositions.contains(pos.up(depth))) {
                    difference.add(pos);
                    placer.place(world, states, pos, ruler);
                }
            }
            leftPositions.removeAll(difference);
        }

        if (!leftPositions.isEmpty()) {
            LayerPlacer placer = blockLayer.getLastLayer().getPlacer();
            List<BlockState> states = blockLayer.getLastLayer().getBlockStates();
            StructurePlacementRuleManager ruler = blockLayer.getLastLayer().getRuler();
            for (BlockPos pos : leftPositions) {
                placer.place(world, states, pos, ruler);
            }
        }
    }

    @Override
    public <T extends Collection<BlockPos>> BlockListManager getVerified(StructureWorldAccess world, T posList) {
        OrderedBlockListManager worldStates = new OrderedBlockListManager();

        for (BlockPos pos : posList) {
            worldStates.put(world.getBlockState(pos), pos);
        }

        BlockListManager manager = new BlockListManager();
        Set<BlockPos> leftPositions = new HashSet<>(posList);

        for (int i = 1; i < blockLayer.size(); i++) {
            if (leftPositions.isEmpty()) {
                break;
            }

            BlockLayer layer = blockLayer.get(i - 1);
            int depth = layer.getDepth();
            Set<BlockPos> difference = new HashSet<>();

            LayerPlacer placer = layer.getPlacer();
            List<BlockState> states = layer.getBlockStates();

            for (BlockPos pos : leftPositions) {
                if (!leftPositions.contains(pos.up(depth))) {
                    difference.add(pos);
                    manager.put(placer.get(states, pos), pos);
                }
            }
            leftPositions.removeAll(difference);
        }

        if (!leftPositions.isEmpty()) {
            LayerPlacer placer = blockLayer.getLastLayer().getPlacer();
            List<BlockState> states = blockLayer.getLastLayer().getBlockStates();
            for (BlockPos pos : leftPositions) {
                manager.put(placer.get(states, pos), pos);
            }
        }

        return manager;
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getDivided(T posList) {
        DividedBlockListManager manager = new DividedBlockListManager();

        Set<BlockPos> leftPositions = new HashSet<>(posList);

        for (int i = 1; i < blockLayer.size(); i++) {
            if (leftPositions.isEmpty()) {
                break;
            }

            BlockLayer layer = blockLayer.get(i - 1);
            int depth = layer.getDepth();
            Set<BlockPos> difference = new HashSet<>();

            LayerPlacer placer = layer.getPlacer();
            List<BlockState> states = layer.getBlockStates();
            StructurePlacementRuleManager ruler = layer.getRuler();

            for (BlockPos pos : leftPositions) {
                if (!leftPositions.contains(pos.up(depth))) {
                    difference.add(pos);
                    manager.put(placer.get(states, pos), pos, ruler);
                }
            }
            leftPositions.removeAll(difference);
        }

        if (!leftPositions.isEmpty()) {
            LayerPlacer placer = blockLayer.getLastLayer().getPlacer();
            List<BlockState> states = blockLayer.getLastLayer().getBlockStates();
            StructurePlacementRuleManager ruler = blockLayer.getLastLayer().getRuler();
            for (BlockPos pos : leftPositions) {
                manager.put(placer.get(states, pos), pos, ruler);
            }
        }

        return manager;
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, T posList) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerWorld world1) {
            worldStates.collect(world1, posList);

            DividedBlockListManager manager = new DividedBlockListManager();
            Set<BlockPos> leftPositions = new HashSet<>(posList);

            for (int i = 1; i < blockLayer.size(); i++) {
                if (leftPositions.isEmpty()) {
                    break;
                }

                BlockLayer layer = blockLayer.get(i - 1);
                int depth = layer.getDepth();
                Set<BlockPos> difference = new HashSet<>();

                LayerPlacer placer = layer.getPlacer();
                List<BlockState> states = layer.getBlockStates();
                StructurePlacementRuleManager ruler = layer.getRuler();

                for (BlockPos pos : leftPositions) {
                    if (!leftPositions.contains(pos.up(depth))) {
                        difference.add(pos);
                        if (ruler.canPlace(worldStates.getState(LongPosHelper.encodeBlockPos(pos))))
                            manager.put(placer.get(states, pos), pos);
                    }
                }
                leftPositions.removeAll(difference);
            }

            if (!leftPositions.isEmpty()) {
                LayerPlacer placer = blockLayer.getLastLayer().getPlacer();
                List<BlockState> states = blockLayer.getLastLayer().getBlockStates();
                StructurePlacementRuleManager ruler = blockLayer.getLastLayer().getRuler();
                for (BlockPos pos : leftPositions) {
                    if (ruler.canPlace(worldStates.getState(LongPosHelper.encodeBlockPos(pos))))
                        manager.put(placer.get(states, pos), pos);
                }
            }

            return manager;
        }
        return null;
    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager get(U posList) {
        BlockListManager manager = new BlockListManager();

        LongSet leftPositions = new LongOpenHashSet(posList);

        processCommonGet(leftPositions, (placer, states, ruler, pos) -> {
            manager.put(placer.get(states, LongPosHelper.decodeBlockPos(pos)), pos, ruler);
        });

        return manager;
    }

    @Override
    public <U extends AbstractLongCollection> void place(StructureWorldAccess world, U posList) {
        LongSet leftPositions = new LongOpenHashSet(posList);

        for (int i = 1; i < blockLayer.size(); i++) {
            if (leftPositions.isEmpty()) {
                break;
            }

            BlockLayer layer = blockLayer.get(i - 1);
            int depth = layer.getDepth();
            LongSet difference = new LongOpenHashSet();

            LayerPlacer placer = layer.getPlacer();
            List<BlockState> states = layer.getBlockStates();
            StructurePlacementRuleManager ruler = layer.getRuler();

            for (long pos : leftPositions) {
                if (!leftPositions.contains(LongPosHelper.up(pos, depth))) {
                    difference.add(pos);
                    placer.place(world, states, LongPosHelper.decodeBlockPos(pos), ruler);
                }
            }
            leftPositions.removeAll(difference);
        }

        if (!leftPositions.isEmpty()) {
            LayerPlacer placer = blockLayer.getLastLayer().getPlacer();
            List<BlockState> states = blockLayer.getLastLayer().getBlockStates();
            StructurePlacementRuleManager ruler = blockLayer.getLastLayer().getRuler();
            for (long pos : leftPositions) {
                placer.place(world, states, LongPosHelper.decodeBlockPos(pos), ruler);
            }
        }
    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager getVerified(StructureWorldAccess world, U posList) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerWorld world1) {
            worldStates.collect(world1, posList);

            BlockListManager manager = new BlockListManager();
            LongSet leftPositions = new LongOpenHashSet(posList);


            processCommonGet(leftPositions, (placer, states, ruler, pos) -> {
                if (ruler.canPlace(worldStates.getState(pos)))
                    manager.put(placer.get(states, LongPosHelper.decodeBlockPos(pos)), pos);
            });

            return manager;
        }
        return null;
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getDivided(U posList) {
        DividedBlockListManager manager = new DividedBlockListManager();

        LongSet leftPositions = new LongOpenHashSet(posList);

        processCommonGet(leftPositions, (placer, states, ruler, pos) -> {
            manager.put(placer.get(states, LongPosHelper.decodeBlockPos(pos)), pos, ruler);
        });

        return manager;
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getVerifiedDivided(StructureWorldAccess world, U posList) {
        WorldStateCollector worldStates = new WorldStateCollector();
        if(world instanceof ServerWorld world1) {
            worldStates.collect(world1, posList);

            DividedBlockListManager manager = new DividedBlockListManager();

            LongSet leftPositions = new LongOpenHashSet(posList);

            processCommonGet(leftPositions, (placer, states, ruler, pos) -> {
                if (ruler.canPlace(worldStates.getState(pos)))
                    manager.put(placer.get(states, LongPosHelper.decodeBlockPos(pos)), pos);
            });

            return manager;
        }
        return null;
    }

    private void processCommonGet(LongSet leftPositions, QuadConsumer<LayerPlacer, List<BlockState>, StructurePlacementRuleManager, Long> consumer) {
        for (int i = 0; i < blockLayer.size(); i++) {
            if (leftPositions.isEmpty()) {
                break;
            }

            BlockLayer layer = blockLayer.get(i);
            int depth = layer.getDepth();
            LongSet difference = new LongOpenHashSet();

            LayerPlacer placer = layer.getPlacer();
            List<BlockState> states = layer.getBlockStates();
            StructurePlacementRuleManager ruler = layer.getRuler();

            for (long pos : leftPositions) {
                if (!leftPositions.contains(LongPosHelper.up(pos, depth))) {
                    difference.add(pos);
                    consumer.accept(placer, states, ruler, pos);
                }
            }
            leftPositions.removeAll(difference);
        }

        if (!leftPositions.isEmpty()) {
            BlockLayer layer = blockLayer.getLastLayer();

            LayerPlacer placer = blockLayer.getLastLayer().getPlacer();
            List<BlockState> states = blockLayer.getLastLayer().getBlockStates();
            StructurePlacementRuleManager ruler = layer.getRuler();

            for (long pos : leftPositions) {
                consumer.accept(placer, states, ruler, pos);
            }
        }
    }
}
