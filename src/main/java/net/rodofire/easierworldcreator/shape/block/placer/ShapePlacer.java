package net.rodofire.easierworldcreator.shape.block.placer;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.GenerationStep;
import net.rodofire.easierworldcreator.Ewc;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListHelper;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListManager;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;
import net.rodofire.easierworldcreator.blockdata.sorter.BlockSorter;
import net.rodofire.easierworldcreator.shape.block.placer.animator.StructurePlaceAnimator;
import net.rodofire.easierworldcreator.shape.block.layer.LayerManager;
import net.rodofire.easierworldcreator.util.file.LoadChunkShapeInfo;
import net.rodofire.easierworldcreator.world.chunk.ChunkPosManager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

/**
 * Class that allow you to place a structure based on a coordinate map or a {@link BlockListManager}.
 * It takes {@link PlaceMoment} argument which define when should the structure be placed.
 * Different behaviors will happen depending on it
 */
public class ShapePlacer {
    StructureWorldAccess world;
    BlockPos center;
    private final String featureName;
    private final PlaceMoment placeMoment;
    private StructurePlaceAnimator animator;

    private WGShapeData shapeData;

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

    public void setShapeData(WGShapeData shapeData) {
        this.shapeData = shapeData;
    }

    public void place(BlockListManager defaultManager) {
        switch (placeMoment) {
            case WORLD_GEN -> {
                Ewc.LOGGER.error("cannot place structure during world generation using BlockListManager. Please use DividedBlockListManager.");
                IllegalStateException exception = new IllegalStateException();
                exception.fillInStackTrace();
                throw exception;
                //Ewc.LOGGER.info(ExceptionUtils.getStackTrace(exception));
            }
            case ANIMATED_OTHER -> {
                if (animator == null) {
                    animator = new StructurePlaceAnimator(world, new BlockSorter(BlockSorter.BlockSorterType.RANDOM), StructurePlaceAnimator.AnimatorTime.CONSTANT_TICKS);
                }
                animator.place(defaultManager);
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

            if (!chunkPosManager.canPlaceMultiChunk(posLit.keySet(), 8))
                return;

            if (shapeData == null)
                shapeData = WGShapeData.ofStep(GenerationStep.Feature.VEGETAL_DECORATION, this.featureName);

            WGShapeHandler.encodeInformations(posLit.keySet(), shapeData, (chunkPosManager.getOffset()));

            for (Map.Entry<ChunkPos, LongOpenHashSet> posSet : posLit.entrySet()) {
                futures.add(CompletableFuture.runAsync(() -> {
                    manager.get(posSet.getValue())
                            .placeJson(posSet.getKey(), chunkPosManager.getOffset(), featureName);
                }, pool));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            placeWorldGenFiles();

        } else if (placeMoment == PlaceMoment.ANIMATED_OTHER) {
            if (animator == null) {
                animator = new StructurePlaceAnimator(world, new BlockSorter(BlockSorter.BlockSorterType.RANDOM), StructurePlaceAnimator.AnimatorTime.CONSTANT_TICKS);
            }
            animator.place(manager.get(posLit));
        } else {
            manager.get(posLit).placeAll(world);
        }
    }


    public void place(DividedBlockListManager manager) {
        ChunkPosManager chunkPosManager = new ChunkPosManager(world);
        if (placeMoment == PlaceMoment.WORLD_GEN &&
                chunkPosManager.isMultiChunk(manager.getChunkPos(), center)) {


            if (!chunkPosManager.canPlaceMultiChunk(manager.getChunkPos(), 8)) {
                return;
            }

            if (shapeData == null)
                shapeData = WGShapeData.ofStep(GenerationStep.Feature.VEGETAL_DECORATION, this.featureName);

            WGShapeHandler.encodeInformations(manager.getChunkPos(), shapeData, chunkPosManager.getOffset());

            manager.placeJson(this.featureName, chunkPosManager.getOffset());

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
        List<Path> path = LoadChunkShapeInfo.getWorldGenFiles(this.center);
        for (Path path1 : path) {
            world.setCurrentlyGeneratingStructureName(() -> "ewc multi-chunk feature generating: " + path1.getFileName());
            BlockListManager manager = BlockListHelper.fromJsonPath(world, path1);
            if (manager != null)
                manager.placeAllNDelete(world);
        }
    }

    public enum PlaceMoment {
        WORLD_GEN,
        ANIMATED_OTHER,
        OTHER
    }
}
