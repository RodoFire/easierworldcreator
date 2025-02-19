package net.rodofire.easierworldcreator.shape.block.placer;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Ewc;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListManager;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;
import net.rodofire.easierworldcreator.blockdata.sorter.BlockSorter;
import net.rodofire.easierworldcreator.fileutil.LoadChunkShapeInfo;
import net.rodofire.easierworldcreator.fileutil.SaveChunkShapeInfo;
import net.rodofire.easierworldcreator.placer.blocks.animator.StructurePlaceAnimator;
import net.rodofire.easierworldcreator.shape.block.layer.LayerManager;
import net.rodofire.easierworldcreator.world.chunk.ChunkPosManager;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class ShapePlacer {
    StructureWorldAccess world;
    BlockPos center;
    private String featureName;
    private PlaceMoment placeMoment;
    private StructurePlaceAnimator animator;

    ChunkPosManager chunkPosManager;

    public ShapePlacer(StructureWorldAccess world, ShapePlacer.PlaceMoment placeMoment, BlockPos center) {
        this(world, placeMoment, center, "custom_shape" + Random.create().nextLong());
    }

    public ShapePlacer(StructureWorldAccess world, ShapePlacer.PlaceMoment placeMoment, BlockPos center, String featureName) {
        this.world = world;
        this.placeMoment = placeMoment;
        this.featureName = featureName;
        this.center = center;
    }


    public PlaceMoment getPlaceMoment() {
        return placeMoment;
    }

    public void place(BlockListManager defaultManager) {
        switch (placeMoment) {
            case WORLD_GEN -> {
                Ewc.LOGGER.error("cannot place structure during world generation using BlockListManager. Please use DividedBlockListManager.");
                IllegalStateException exception = new IllegalStateException();
                Ewc.LOGGER.info(ExceptionUtils.getStackTrace(exception));
            }
            case ANIMATED_OTHER -> {
                if (animator == null) {
                    animator = new StructurePlaceAnimator(world, new BlockSorter(BlockSorter.BlockSorterType.RANDOM), StructurePlaceAnimator.AnimatorTime.CONSTANT_TICKS);
                }
                animator.placeFromBlockList(defaultManager);
            }
            case OTHER -> {
                defaultManager.placeAll(world);
            }
        }
    }

    public void place(Map<ChunkPos, LongOpenHashSet> posLit, LayerManager manager) {
        ChunkPosManager chunkPosManager = new ChunkPosManager(world);
        if (placeMoment == PlaceMoment.WORLD_GEN &&
                chunkPosManager.isMultiChunk(posLit.keySet(), center)) {

            System.out.println("placing");
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

            chunkPosManager.canPlaceMultiChunk(posLit.keySet(), 6);


            for (Map.Entry<ChunkPos, LongOpenHashSet> posSet : posLit.entrySet()) {
                futures.add(CompletableFuture.runAsync(() -> {
                    Path path = SaveChunkShapeInfo.getMultiChunkPath(world, posSet.getKey());
                    System.out.println("path");
                    if (path != null) {
                        System.out.println("manager");
                        manager.get(posSet.getValue()).toJson(posSet.getKey(), path, chunkPosManager.getOffset(), featureName);
                        System.out.println("placed");
                    }
                }, pool));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            placeWorldGenFiles();

        } else if (placeMoment == PlaceMoment.ANIMATED_OTHER) {
            if (animator == null) {
                animator = new StructurePlaceAnimator(world, new BlockSorter(BlockSorter.BlockSorterType.RANDOM), StructurePlaceAnimator.AnimatorTime.CONSTANT_TICKS);
            }
            animator.placeFromBlockList(manager.get(posLit));
        } else {
            manager.get(posLit).placeAll(world);
        }
    }


    public void place(DividedBlockListManager manager) {
        ChunkPosManager chunkPosManager = new ChunkPosManager(world);
        if (placeMoment == PlaceMoment.WORLD_GEN &&
                chunkPosManager.isMultiChunk(manager.getChunkPos(), center)) {

            chunkPosManager.canPlaceMultiChunk(manager.getChunkPos(), 6);
            manager.placeAll(world);

            placeWorldGenFiles();

        } else if (placeMoment == PlaceMoment.ANIMATED_OTHER) {
            if (animator == null) {
                animator = new StructurePlaceAnimator(world, new BlockSorter(BlockSorter.BlockSorterType.RANDOM), StructurePlaceAnimator.AnimatorTime.CONSTANT_TICKS);
            }
            animator.place(manager.getOrdered());
        } else {
            manager.placeAll(world);
        }
    }

    private void placeWorldGenFiles() {
        List<Path> path = LoadChunkShapeInfo.getWorldGenFiles(world, this.center);
        for (Path path1 : path) {
            BlockListManager defaultBlockLists = LoadChunkShapeInfo.loadFromJson(world, path1);
            LoadChunkShapeInfo.placeStructure(world, defaultBlockLists);
        }
    }

    public enum PlaceMoment {
        WORLD_GEN,
        ANIMATED_OTHER,
        OTHER
    }
}
