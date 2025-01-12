package net.rodofire.easierworldcreator.shape.block.instanciator;

import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Ewc;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.DefaultBlockList;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.comparator.DefaultBlockListComparator;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayer;
import net.rodofire.easierworldcreator.blockdata.sorter.BlockSorter;
import net.rodofire.easierworldcreator.config.ewc.EwcConfig;
import net.rodofire.easierworldcreator.fileutil.LoadChunkShapeInfo;
import net.rodofire.easierworldcreator.fileutil.SaveChunkShapeInfo;
import net.rodofire.easierworldcreator.placer.blocks.animator.StructurePlaceAnimator;
import net.rodofire.easierworldcreator.placer.blocks.util.BlockStateUtil;
import net.rodofire.easierworldcreator.util.WorldGenUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

/**
 * Class to create custom shapes
 * <p> - Since 2.1.0, the shape doesn't return a {@link List<BlockPos>} but it returns instead a {@code List<Set<BlockPos>>}
 * <p> - Before 2.1.0, the BlockPos list was a simple list.
 * <p> - Starting from 2.1.0, the shapes return a list of {@link ChunkPos} that has a set of {@link BlockPos}
 * <p>The change from {@link List} to {@link Set} was done to avoid duplicates BlockPos, which resulted in unnecessary calculations.
 * <p>this allows easy multithreading for the Block assignment done in the {@link AbstractBlockShape} which result in better performance;
 * </p>
 */
@SuppressWarnings("unused")
public abstract class AbstractBlockShape extends AbstractBlockShapeRotation {
    private String featureName;

    private StructurePlaceAnimator animator;

    /**
     * init the Shape
     *
     * @param world           the world the spiral will spawn in
     * @param pos             the center of the spiral
     * @param placeMoment     define the moment where the shape will be placed
     * @param layerPlace      how the {@code @BlockStates} inside of a {@link BlockLayer} will be placed
     * @param layersType      how the Layers will be placed
     * @param yRotation       first rotation around the y-axis
     * @param zRotation       second rotation around the z-axis
     * @param secondYRotation last rotation around the y-axis
     * @param featureName     the name of the feature
     */
    public AbstractBlockShape(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment, LayerPlace layerPlace, LayersType layersType, int yRotation, int zRotation, int secondYRotation, String featureName) {
        super(world, pos, placeMoment, layerPlace, layersType, yRotation, zRotation, secondYRotation);
        this.featureName = featureName;
    }

    /**
     * init the Shape
     *
     * @param world       the world of the shape
     * @param pos         the pos of the shape (usually the center of the structure)
     * @param placeMoment define the moment where the shape will be placed
     */
    public AbstractBlockShape(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment) {
        super(world, pos, placeMoment);
    }

    public StructurePlaceAnimator getAnimator() {
        return animator;
    }

    /**
     * Method to set the animator. It is required when {@code PlaceMoment} is defined on {@code ANIMATED_OTHER}
     *
     * @param animator the animator that will be played
     */
    public void setAnimator(StructurePlaceAnimator animator) {
        this.animator = animator;
    }

    public String getFeatureName() {
        return featureName;
    }

    /**
     * <p>Method to set a custom name for the structure.
     * <p>It is optional.
     * <p>If no name is provided, one random name will be generated.
     * <p>It allows better readibility in the generated files
     *
     * @param featureName the name of the structure
     */
    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    /**
     * This method allows you to place the structure in the world.
     * Any changes done after this moment will not be taken in count except if you place another shape later
     */
    public void place() {
        if (this.getBlockLayer() == null || this.getBlockLayer().isEmpty()) {
            Ewc.LOGGER.warn("shape not placed, no BlockLayer present");
            return;
        }
        long start = System.nanoTime();
        Map<ChunkPos, Set<BlockPos>> posList = this.getBlockPos();
        long end = System.nanoTime();
        long diff = end - start;
        if (EwcConfig.getLogPerformanceInfo()) {
            Ewc.LOGGER.info("Shape coordinate calculations took : {}ms", ((double) (diff / 1000)) / 1000);
        }
        place(posList);
    }

    /**
     * This method is the method to place the related Blocks
     *
     * @param posList the {@link List} of {@link Set} of {@link BlockPos} calculated before, that will be placed
     */
    public void place(Map<ChunkPos, Set<BlockPos>> posList) {
        if(this.isMultiChunk(posList) && !EwcConfig.getMultiChunkFeatures() && this.getPlaceMoment() == PlaceMoment.WORLD_GEN){
            return;
        }
        boolean logWarns = EwcConfig.getLogWarns();
        if (posList == null || posList.isEmpty()) {
            if (logWarns)
                Ewc.LOGGER.warn("shape not placed, no BlockPos present");
            return;
        }
        if (logWarns)
            Ewc.LOGGER.info("placing structure");

        //avoid issue where the method to place the block would not take the last block
        if (this.getLayerPlace() == LayerPlace.NOISE2D || this.getLayerPlace() == LayerPlace.NOISE3D) {
            for (BlockLayer layers : this.getBlockLayer().getLayers()) {
                layers.addBlockState(layers.getBlockStates().get(layers.getBlockStates().size() - 1));
            }
        }

        //verify if the shape is larger than a chunk
        //if yes, we have to divide the structure into chunks
        if (this.getPlaceMoment() == PlaceMoment.WORLD_GEN && this.isMultiChunk(posList) && EwcConfig.getMultiChunkFeatures()) {
            if (!canPlaceMultiChunk(posList.keySet())) {
                if (logWarns) {
                    Ewc.LOGGER.info("cannot place structure due to too much chunks generated around the original Pos");
                }
                return;
            }
            if (logWarns)
                Ewc.LOGGER.info("structure bigger than chunk");
            long randomLong = Random.create().nextLong();
            featureName = "custom_feature_" + randomLong;

            ChunkPos chunk = new ChunkPos(this.getPos());
            chunk.getStartPos();

            List<Future<?>> creationTasks = new ArrayList<>();

            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
            for (Map.Entry<ChunkPos, Set<BlockPos>> pos : posList.entrySet()) {
                Future<?> future = executorService.submit(() -> {
                    DefaultBlockListComparator comparator = this.getLayers(pos.getValue());
                    Path generatedPath = SaveChunkShapeInfo.getMultiChunkPath(getWorld(), WorldGenUtil.addChunkPos(pos.getKey(), this.getOffset()));
                     if (generatedPath != null) {
                        comparator.toJson(generatedPath.resolve(this.featureName + ".json"), this.getOffset());
                    }
                });
                creationTasks.add(future);
            }

            for (Future<?> future : creationTasks) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.fillInStackTrace();
                }
            }
            //tell the garbage collector that it can free the list of pos
            posList.clear();
            executorService.shutdown();
            List<Path> path = LoadChunkShapeInfo.getWorldGenFiles(getWorld(), this.getPos());
            for (Path path1 : path) {
                DefaultBlockListComparator defaultBlockLists = LoadChunkShapeInfo.loadFromJson(getWorld(), path1);
                LoadChunkShapeInfo.placeStructure(getWorld(), defaultBlockLists);
            }
        } else if (this.getPlaceMoment() == PlaceMoment.ANIMATED_OTHER) {
            if (animator == null) {
                animator = new StructurePlaceAnimator(this.getWorld(), new BlockSorter(BlockSorter.BlockSorterType.RANDOM), StructurePlaceAnimator.AnimatorTime.CONSTANT_BLOCKS_PER_TICK);
                animator.setBlocksPerTick(100);
            }
            if (this.multiChunk && EwcConfig.getChatWarns()) {
                MinecraftServer server = getWorld().getServer();
                if (server != null) {
                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        player.sendMessage(Text.translatable("ewc.chat_warn"), false);
                    }
                }
            }
            DefaultBlockListComparator comparator = getBlockListWithVerification(posList.values().stream().toList());
            animator.placeFromBlockList(comparator);
        }
        //In the case our structure isn't place during world gen, or it is less than a chunk large
        else {
            for (Set<BlockPos> pos : posList.values()) {
                this.placeLayers(pos);
            }

        }
    }

    public void placeWBlockList(Map<ChunkPos, Set<DefaultBlockList>> posList) {
        List<Future<?>> creationTasks = new ArrayList<>();

        if (!canPlaceMultiChunk(posList.keySet())) {
            if (EwcConfig.getLogWarns())
                Ewc.LOGGER.info("cannot place structure due to too much chunks generated around the original Pos");
            return;
        }

        for (Set<DefaultBlockList> defaultBlockList : posList.values()) {
            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
            Future<?> future = executorService.submit(() -> {
                try {
                    SaveChunkShapeInfo.saveChunkWorldGen(defaultBlockList, getWorld(), featureName, this.getOffset());

                } catch (IOException e) {
                    e.fillInStackTrace();
                }
            });
            creationTasks.add(future);
            executorService.shutdown();

        }

        for (Future<?> future : creationTasks) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.fillInStackTrace();
            }
        }

        Set<ChunkPos> chunkPosList = posList.keySet();
        posList.clear();

        List<Path> path = LoadChunkShapeInfo.getWorldGenFiles(getWorld(), this.getPos());
        for (Path path1 : path) {
            DefaultBlockListComparator defaultBlockLists = LoadChunkShapeInfo.loadFromJson(getWorld(), path1);
            LoadChunkShapeInfo.placeStructure(getWorld(), defaultBlockLists);
        }
    }

    /**
     * method to get the coordinates that will be placed later
     *
     * @return a map of ChunkPos of blockPos for every shape
     */
    public abstract Map<ChunkPos, Set<BlockPos>> getBlockPos();


    /*---------- Place Structure ----------*/
    public DefaultBlockListComparator getBlockListWithVerification(List<Set<BlockPos>> posList) {
        DefaultBlockListComparator blockList = new DefaultBlockListComparator();
        Map<BlockPos, BlockState> blockStateMap = new HashMap<>();
        BlockStateUtil.getBlockStatesFromWorld(posList, blockStateMap, getWorld());


        ExecutorService finalExecutorService = Executors.newFixedThreadPool(Math.min(posList.size(), Runtime.getRuntime().availableProcessors()));
        List<CompletableFuture<DefaultBlockListComparator>> result =
                posList.stream()
                        .map(set -> CompletableFuture.supplyAsync(() -> this.getLayersWithVerification(set, blockStateMap), finalExecutorService))
                        .toList();

        result.forEach(future -> {
            try {
                DefaultBlockListComparator comp = future.get();
                blockList.put(comp.get());
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        });
        finalExecutorService.shutdown();

        return blockList;
    }
}