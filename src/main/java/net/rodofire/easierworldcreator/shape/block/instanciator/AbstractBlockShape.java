package net.rodofire.easierworldcreator.shape.block.instanciator;

import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.EasierWorldCreator;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.DefaultBlockList;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.comparator.DefaultBlockListComparator;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayer;
import net.rodofire.easierworldcreator.blockdata.sorter.BlockSorter;
import net.rodofire.easierworldcreator.config.ewc.EwcConfig;
import net.rodofire.easierworldcreator.fileutil.FileUtil;
import net.rodofire.easierworldcreator.fileutil.LoadChunkShapeInfo;
import net.rodofire.easierworldcreator.fileutil.SaveChunkShapeInfo;
import net.rodofire.easierworldcreator.placer.blocks.animator.StructurePlaceAnimator;
import net.rodofire.easierworldcreator.placer.blocks.util.BlockStateUtil;
import net.rodofire.easierworldcreator.util.ChunkUtil;
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
    private BlockPos offset = new BlockPos(0, 0, 0);

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

    public BlockPos getOffset() {
        return offset;
    }

    /**
     * method to set an offset
     *
     * @param offset the offset of the entire structure
     */
    public void setOffset(BlockPos offset) {
        this.offset = offset;
    }


    /**
     * This method allows you to place the structure in the world.
     * Any changes done after this moment will not be taken in count except if you place another shape later
     */
    public void place() {
        if (this.getBlockLayer() == null || this.getBlockLayer().isEmpty()) {
            EasierWorldCreator.LOGGER.warn("shape not placed, no BlockLayer present");
            return;
        }
        long start = System.nanoTime();
        Map<ChunkPos, Set<BlockPos>> posList = this.getBlockPos();
        long end = System.nanoTime();
        long diff = end - start;
        if (EwcConfig.getLogWarns()) {
            EasierWorldCreator.LOGGER.info("Shape coordinate calculations took : {}ms", ((double) (diff / 1000)) / 1000);
        }
        place(posList);
    }

    /**
     * This method is the method to place the related Blocks
     *
     * @param posList the {@link List} of {@link Set} of {@link BlockPos} calculated before, that will be placed
     */
    public void place(Map<ChunkPos, Set<BlockPos>> posList) {
        boolean logWarns = EwcConfig.getLogWarns();
        if (posList == null || posList.isEmpty()) {
            if (logWarns)
                EasierWorldCreator.LOGGER.warn("shape not placed, no BlockPos present");
            return;
        }
        if (logWarns)
            EasierWorldCreator.LOGGER.info("placing structure");

        //avoid issue where the method to place the block would not take the last block
        if (this.getLayerPlace() == LayerPlace.NOISE2D || this.getLayerPlace() == LayerPlace.NOISE3D) {
            for (BlockLayer layers : this.getBlockLayer().getLayers()) {
                layers.addBlockState(layers.getBlockStates().get(layers.getBlockStates().size() - 1));
            }
        }

        //verify if the shape is larger than a chunk
        //if yes, we have to divide the structure into chunks

        if (this.getPlaceMoment() == PlaceMoment.WORLD_GEN && this.multiChunk && EwcConfig.getMultiChunkFeatures()) {

            if (!tryPlaceStructure(posList)) {
                if (logWarns) {
                    EasierWorldCreator.LOGGER.info("cannot place structure due to too much chunks generated around the original Pos");
                }
                return;
            }
            if (logWarns)
                EasierWorldCreator.LOGGER.info("structure bigger than chunk");

            long randomLong = Random.create().nextLong();
            featureName = "custom_feature_" + randomLong;

            ChunkPos chunk = new ChunkPos(this.getPos());
            chunk.getStartPos();

            List<Future<?>> creationTasks = new ArrayList<>();

            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
            for (Set<BlockPos> pos : posList.values()) {
                Future<?> future = executorService.submit(() -> {
                    Optional<BlockPos> opt = pos.stream().findFirst();
                    if (opt.isPresent()) {
                        DefaultBlockListComparator comparator = this.getLayers(pos);
                        Path generatedPath = SaveChunkShapeInfo.getMultiChunkPath(getWorld(), new ChunkPos(opt.get()));
                        comparator.toJson(generatedPath, offset);
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

            Set<ChunkPos> chunSet = posList.keySet();
            //tell the garbage collector that it can free the list of pos
            posList.clear();

            if (!moveChunks(chunSet, 5)) return;
            Path generatedPath = Objects.requireNonNull(getWorld().getServer()).getSavePath(WorldSavePath.GENERATED).resolve(EasierWorldCreator.MOD_ID).resolve("structures").normalize();

            for (ChunkPos chunkPos : chunSet) {
                executorService.submit(() -> {
                    FileUtil.renameFile(
                            generatedPath.resolve("chunk_" + chunkPos.x + "_" + chunkPos.z).resolve("false_" + featureName + ".json"),
                            generatedPath.resolve("chunk_" + (chunkPos.x + offset.getX() / 16) + "_" + (chunkPos.z + offset.getZ() / 16)).resolve("[" + offset.getX() + "," + offset.getZ() + "]_" + featureName + ".json"));
                });
            }
            executorService.shutdown();


            List<Path> path = LoadChunkShapeInfo.verifyFiles(getWorld(), this.getPos());
            for (Path path1 : path) {
                List<DefaultBlockList> defaultBlockLists = LoadChunkShapeInfo.loadFromJson(getWorld(), path1);
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

        if (!canPos(posList.keySet())) {
            if (EwcConfig.getLogWarns())
                EasierWorldCreator.LOGGER.info("cannot place structure due to too much chunks generated around the original Pos");
            return;
        }

        for (Set<DefaultBlockList> defaultBlockList : posList.values()) {
            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
            Future<?> future = executorService.submit(() -> {
                try {
                    SaveChunkShapeInfo.saveChunkWorldGen(defaultBlockList, getWorld(), "false_" + featureName, offset);

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
        if (!moveChunks(chunkPosList, 5)) return;
        Path generatedPath = Objects.requireNonNull(getWorld().getServer()).getSavePath(WorldSavePath.GENERATED).resolve(EasierWorldCreator.MOD_ID).resolve("structures").normalize();


        for (ChunkPos chunkPos : chunkPosList) {
            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
            executorService.submit(() -> {
                FileUtil.renameFile(
                        generatedPath.resolve("chunk_" + chunkPos.x + "_" + chunkPos.z).resolve("false_" + featureName + ".json"),
                        generatedPath.resolve("chunk_" + (chunkPos.x + offset.getX() / 16) + "_" + (chunkPos.z + offset.getZ() / 16)).resolve("[" + offset.getX() + "," + offset.getZ() + "]_" + featureName + ".json"));

            });
            executorService.shutdown();

        }


        List<Path> path = LoadChunkShapeInfo.verifyFiles(getWorld(), this.getPos());
        for (Path path1 : path) {
            List<DefaultBlockList> defaultBlockLists = LoadChunkShapeInfo.loadFromJson(getWorld(), path1);
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

    /**
     * verify for a set of chunks if every chunk wasn't generated.
     *
     * @param chunks the list of chunks that would be placed
     * @return true if it can pos, false if it can't
     */
    private boolean canPos(Set<ChunkPos> chunks) {
        for (ChunkPos chunk : chunks) {
            if (ChunkUtil.isChunkGenerated(getWorld(), chunk)) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Method to try placing the structure.
     * <p>It will try to see if the structure can be placed in the near chunks.
     * <p>If yes, it will set an offset that will be used when creating the files.
     *
     * @return {@link Boolean} that determines if the shape can be placed
     */
    private boolean tryPlaceStructure(Map<ChunkPos, Set<BlockPos>> posList) {
        int maxOffset = 5;
        List<ChunkPos> chunkList = new ArrayList<>();

        if (moveChunks(posList.keySet(), maxOffset)) return true;

        if (EwcConfig.getLogWarns())
            EasierWorldCreator.LOGGER.info("can't place the structure");
        return false;
    }

    private boolean moveChunks(Set<ChunkPos> chunkList, int maxStep) {
        for (int step = 0; step <= maxStep; step++) {
            for (int xOffset = -step; xOffset <= step; xOffset++) {
                for (int zOffset = -step; zOffset <= step; zOffset++) {

                    if (Math.abs(xOffset) + Math.abs(zOffset) == step) {

                        BlockPos newPos = new BlockPos(xOffset * 16, 0, zOffset * 16);
                        Set<ChunkPos> coveredChunks = this.getChunkCovered(newPos, chunkList);


                        if (canPos(coveredChunks)) {
                            List<ChunkPos> region = new ArrayList<>();
                            this.offset = newPos;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

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

    /**
     * Method to get a chunk list to know if the structure can be placed during worldGen;
     *
     * @param pos the center pos
     */
    protected Set<ChunkPos> getChunkCovered(BlockPos pos, Set<ChunkPos> chunks) {
        Set<ChunkPos> newChunks = new HashSet<>();
        for (ChunkPos chunk : chunks) {
            newChunks.add(new ChunkPos(chunk.getStartPos().add(pos)));
        }
        return newChunks;
    }
}