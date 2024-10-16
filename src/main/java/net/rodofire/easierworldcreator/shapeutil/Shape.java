package net.rodofire.easierworldcreator.shapeutil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Easierworldcreator;
import net.rodofire.easierworldcreator.fileutil.FileUtil;
import net.rodofire.easierworldcreator.fileutil.LoadChunkShapeInfo;
import net.rodofire.easierworldcreator.fileutil.SaveChunkShapeInfo;
import net.rodofire.easierworldcreator.util.ChunkUtil;
import net.rodofire.easierworldcreator.util.FastMaths;
import net.rodofire.easierworldcreator.worldgenutil.FastNoiseLite;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class to create custom shapes
 * <p> - Since 2.1.0, the shape doesn't return a {@link List<BlockPos>} but it returns instead a {@code List<Set<BlockPos>>}
 * <p> - Before 2.1.0, the BlockPos list was a simple list.
 * <p> - Starting from 2.1.0, the shapes returns a list of {@link ChunkPos} that has a set of {@link BlockPos}
 * <p>The change from {@link List} to {@link Set} was done to avoid duplicates BlockPos wich resulted in unnecessary calculations.
 * <p>this allow easy multithreading for the Block assignment done in the {@link Shape} which result in better performance;
 * </p>
 */
public abstract class Shape extends ShapeRotation {
    private BlockPos offset = new BlockPos(0, 0, 0);

    private String featureName;

    /**
     * init the Shape
     * @param world           the world the spiral will spawn in
     * @param pos             the center of the spiral
     * @param placeMoment     define the moment where the shape will be placed
     * @param force           boolean to force the pos of the blocks
     * @param blocksToForce   a list of blocks that the blocks of the spiral can still force if force = false
     * @param layerPlace      how the {@code @BlockStates} inside of a {@link BlockLayer} will be placed
     * @param layersType      how the Layers will be placed
     * @param xrotation       first rotation around the x-axis
     * @param yrotation       second rotation around the y-axis
     * @param secondxrotation last rotation around the x-axis
     * @param featureName     the name of the feature
     */
    public Shape(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment, boolean force, List<Block> blocksToForce, LayerPlace layerPlace,  LayersType layersType, int xrotation, int yrotation, int secondxrotation, String featureName) {
        super(world, pos, placeMoment, force, blocksToForce, layerPlace,  layersType, xrotation, yrotation, secondxrotation);
        this.featureName = featureName;
    }

    /**
     * init the Shape
     *
     * @param world       the world of the shape
     * @param pos         the pos of the shape (usually the center of the structure)
     * @param placeMoment define the moment where the shape will be placed
     */
    public Shape(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment) {
        super(world, pos, placeMoment);
    }


    /**
     * This method allows you to place the structure in the world.
     * Any changes done after this moment will not be taken in count except if you place another shape later
     */
    public void place() throws IOException {
        long start = System.nanoTime();
        List<Set<BlockPos>> posList = this.getBlockPos();
        long end = System.nanoTime();
        long diff = end - start;
        Easierworldcreator.LOGGER.info("Shape coordinate calculations took : " + ((double) (diff / 1000)) / 1000 + "ms");
        place(posList);
    }

    /**
     * This method is the method to place the related Blocks
     *
     * @param posList the {@link List} of {@link Set} of {@link BlockPos} calculated before, that will be placed
     */
    public void place(List<Set<BlockPos>> posList) throws IOException {
        Easierworldcreator.LOGGER.info("placing structure");
        if(this.getLayerPlace() == LayerPlace.NOISE2D || this.getLayerPlace() == LayerPlace.NOISE3D) {
            for (BlockLayer layers : this.getBlockLayers()) {
                layers.addBlockState(layers.getBlockStates().get(layers.getBlockStates().size() - 1));
            }
        }
        if (this.getPlaceType() == PlaceType.BLOCKS) {

            //verify if the shape is larger than a chunk
            //if yes, we have to divide the structure into chunks
            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
            if (this.getPlaceMoment() == PlaceMoment.WORLD_GEN && this.biggerThanChunk) {

                if (!tryPlaceStructure(posList)) {
                    Easierworldcreator.LOGGER.info("cannot place structure due to too much chunks generated around the original Pos");
                    return;
                }
                Easierworldcreator.LOGGER.info("structure bigger than chunk");
                long randomLong = Random.create().nextLong();
                featureName = "custom_feature_" + randomLong;

                ChunkPos chunk = new ChunkPos(this.getPos());
                chunk.getStartPos();

                List<Future<?>> creationTasks = new ArrayList<>();

                for (Set<BlockPos> pos : posList) {
                    Future<?> future = executorService.submit(() -> {
                        try {
                            Set<BlockList> blockList = this.getLayers(pos);
                            SaveChunkShapeInfo.saveChunkWorldGen(blockList, getWorld(), "false_" + featureName, offset);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    creationTasks.add(future);
                }

                for (Future<?> future : creationTasks) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                List<ChunkPos> chunkPosList = new ArrayList<>();
                for (Set<BlockPos> pos : posList) {
                    pos.stream().findFirst().ifPresent(blockPos -> chunkPosList.add(new ChunkPos(blockPos.add(offset))));
                }

                if (!moveChunks(chunkPosList)) return;
                Path generatedPath = Objects.requireNonNull(getWorld().getServer()).getSavePath(WorldSavePath.GENERATED).resolve(Easierworldcreator.MOD_ID).resolve("structures").normalize();


                for (ChunkPos chunkPos : chunkPosList) {
                    //executorService.submit(() -> {
                    try {
                        FileUtil.renameFile(
                                generatedPath.resolve("chunk_" + chunkPos.x + "_" + chunkPos.z).resolve("false_" + featureName + ".json"),
                                generatedPath.resolve("chunk_" + (chunkPos.x + offset.getX() / 16) + "_" + (chunkPos.z + offset.getZ() / 16)).resolve("[" + offset.getX() + "," + offset.getZ() + "]_" + featureName + ".json"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //});
                }


                List<Path> path = LoadChunkShapeInfo.verifyFiles(getWorld(), this.getPos());
                for (Path path1 : path) {
                    List<BlockList> blockLists = LoadChunkShapeInfo.loadFromJson(getWorld(), path1);
                    LoadChunkShapeInfo.placeStructure(getWorld(), blockLists);
                }
            }
            //In the case our structure isn't place during world gen or it is less than a chunk large
            else {
                Easierworldcreator.LOGGER.info("structure smaller than chunk");
                for (Set<BlockPos> pos : posList) {
                    //executorService.submit(() -> {
                    this.placeLayers(pos);
                    //});
                }

            }
        } else {
        }
    }

    public void placeWBlockList(List<Set<BlockList>> posList) throws IOException {
        List<Set<BlockPos>> convertedList = new ArrayList<>();
        for(Set<BlockList> set : posList) {
            Set<BlockPos> convertedSet = new HashSet<>();
            set.stream().findFirst().ifPresent(blockList -> convertedSet.add(blockList.getPoslist().get(0)));
            convertedList.add(convertedSet);
        }
        List<Future<?>> creationTasks = new ArrayList<>();

        if (!tryPlaceStructure(convertedList)) {
            Easierworldcreator.LOGGER.info("cannot place structure due to too much chunks generated around the original Pos");
            return;
        }

        for (Set<BlockList> blockList : posList) {
            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
            Future<?> future = executorService.submit(() -> {
                try {
                    SaveChunkShapeInfo.saveChunkWorldGen(blockList, getWorld(), "false_" + featureName, offset);

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
                e.printStackTrace();
            }
        }
        List<ChunkPos> chunkPosList = new ArrayList<>();
        for (Set<BlockPos> pos : convertedList) {
            pos.stream().findFirst().ifPresent(blockPos -> chunkPosList.add(new ChunkPos(blockPos.add(offset))));
        }

        if (!moveChunks(chunkPosList)) return;
        Path generatedPath = Objects.requireNonNull(getWorld().getServer()).getSavePath(WorldSavePath.GENERATED).resolve(Easierworldcreator.MOD_ID).resolve("structures").normalize();


        for (ChunkPos chunkPos : chunkPosList) {
            //executorService.submit(() -> {
            try {
                FileUtil.renameFile(
                        generatedPath.resolve("chunk_" + chunkPos.x + "_" + chunkPos.z).resolve("false_" + featureName + ".json"),
                        generatedPath.resolve("chunk_" + (chunkPos.x + offset.getX() / 16) + "_" + (chunkPos.z + offset.getZ() / 16)).resolve("[" + offset.getX() + "," + offset.getZ() + "]_" + featureName + ".json"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //});
        }


        List<Path> path = LoadChunkShapeInfo.verifyFiles(getWorld(), this.getPos());
        for (Path path1 : path) {
            List<BlockList> blockLists = LoadChunkShapeInfo.loadFromJson(getWorld(), path1);
            LoadChunkShapeInfo.placeStructure(getWorld(), blockLists);
        }
    }

    /**
     * method to get the coordinates that will be placed later
     *
     * @return a list of blockPos for every shape
     */
    public abstract List<Set<BlockPos>> getBlockPos();

    /**
     * method to get the Vec3d that will be placed later
     *
     * @return a list of Vec3d for every shape
     */
    public abstract List<Vec3d> getVec3d();






    /*---------- Place Structure ----------*/

    /**
     * verify for a set of chunks if every chunks wasn't generated.
     *
     * @param chunks
     * @return
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
    private boolean tryPlaceStructure(List<Set<BlockPos>> posList) {
        int maxOffset = 5;
        List<ChunkPos> chunkList = new ArrayList<>();
        for (Set<BlockPos> pos : posList) {
            Optional<BlockPos> pos1 = pos.stream().findFirst();
            pos1.ifPresent(blockPos -> chunkList.add(new ChunkPos(blockPos)));
        }
        if (chunkList.isEmpty()) {
            return false;
        }

        for (int step = 0; step <= maxOffset; step++) {
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
        Easierworldcreator.LOGGER.info("can't place the structure");
        return false;
    }

    private boolean moveChunks(List<ChunkPos> chunkList) {
        for (int step = 0; step <= 5; step++) {
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

    /**
     * Method to get a chunk list to kwnow if the structure can be placed during worldgen;
     *
     * @param pos the center pos
     */
    protected Set<ChunkPos> getChunkCovered(BlockPos pos, List<ChunkPos> chunks) {
        Set<ChunkPos> newchunks = new HashSet<>();
        for (ChunkPos chunk : chunks) {
            newchunks.add(new ChunkPos(chunk.getStartPos().add(pos)));
        }
        return newchunks;
    }
}