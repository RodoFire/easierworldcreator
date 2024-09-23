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
import net.rodofire.easierworldcreator.worldgenutil.BlockPlaceUtil;
import net.rodofire.easierworldcreator.worldgenutil.FastNoiseLite;
import net.rodofire.easierworldcreator.worldgenutil.WorldGenUtil;
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
public abstract class Shape {
    @NotNull
    private final StructureWorldAccess world;
    @NotNull
    private BlockPos pos;
    private BlockPos offset = new BlockPos(0, 0, 0);

    private List<BlockLayer> blockLayers;
    private List<ParticleLayer> particleLayers;

    private boolean force = false;
    private List<Block> blocksToForce;

    //These are rotations in degrees (0-360).
    //These 3 are used to represent every rotation possible in a 3d world
    private int xrotation = 0;
    private int yrotation = 0;
    private int secondxrotation = 0;

    //precalculated cos and sin table for every rotation
    private double cosx2 = 1;
    private double cosx = 1;
    private double cosy = 1;
    private double sinx = 0;
    private double sinx2 = 0;
    private double siny = 0;

    //enums to define how the structure is defined
    private LayersType layersType = LayersType.SURFACE;
    private PlaceType placeType = PlaceType.BLOCKS;

    //Only required if layerType == LayerType.ALONG_DIRECTION
    private Vec3d directionalLayerDirection = new Vec3d(0, 1, 0);

    //Center of the structure if PlaceType == Block
    private BlockPos radialCenterPos = this.getPos();

    //Center of the structure if PlaceType == Pearticle
    private Vec3d radialCenterVec3d;

    private LayerPlace layerPlace = LayerPlace.RANDOM;
    private FastNoiseLite noise = new FastNoiseLite();

    //int for the number of placed Blocks, used if layerPlace == LayerPlace.ORDER
    private int placedBlocks = 0;

    private PlaceMoment placeMoment;
    private String featureName;

    //boolean used to determine if we have to use the custom chunk building provided by the mod or not
    protected boolean biggerThanChunk = false;

    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();


    /**
     * init the shape gen
     *
     * @param world         the world the shape will be generated
     * @param pos           the pos of the structure center
     *                      -------------------------------------------------------------------------------------
     * @param layers        a list of layers that will be placed by the structure
     * @param force         force the blockPos if true (be careful if true, your structure will replace every block, you might destruct some constructions if it is bad used)
     * @param blocksToForce list of blocks that the structure can still replace if @param force = false
     */
    public Shape(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment, List<BlockLayer> layers, boolean force, List<Block> blocksToForce, int xrotation, int yrotation, int secondxrotation) {
        this.world = world;
        this.pos = pos;
        this.placeMoment = placeMoment;
        this.force = force;
        this.blocksToForce = new ArrayList<>();
        this.blocksToForce.addAll(blocksToForce);
        this.blockLayers = layers;
        this.radialCenterPos = this.getPos();
        this.radialCenterVec3d = this.getPos().toCenterPos();
        noise.SetSeed((int) this.world.getSeed());
        noise.SetFrequency(0.1f);
        getRotations(xrotation, yrotation, secondxrotation);
    }

    /**
     * init the shape gen
     *
     * @param world the world the shape will be generated
     * @param pos   the pos of the structure center
     */
    public Shape(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment) {
        this.world = world;
        this.pos = pos;
        this.placeMoment = placeMoment;
        this.radialCenterPos = this.getPos();
        this.radialCenterVec3d = this.getPos().toCenterPos();
        noise.SetSeed((int) this.world.getSeed());
        noise.SetFrequency(0.1f);
    }

    //calculate the cosinus and the sinus of the rotations
    public void getRotations(int xrotation, int yrotation, int secondxrotation) {
        this.xrotation = xrotation;
        this.yrotation = yrotation;
        this.secondxrotation = secondxrotation;
        this.cosx = FastMaths.getFastCos(xrotation);
        this.cosy = FastMaths.getFastCos(yrotation);
        this.sinx = FastMaths.getFastSin(xrotation);
        this.siny = FastMaths.getFastSin(yrotation);
        this.cosx2 = FastMaths.getFastCos(secondxrotation);
        this.sinx2 = FastMaths.getFastSin(secondxrotation);
        this.radialCenterPos = this.getPos();
        this.radialCenterVec3d = this.getPos().toCenterPos();
    }

    /**
     * used to get the layerType initialized
     *
     * @return the layer type of the shape
     */
    public LayersType getLayersType() {
        return layersType;
    }

    /**
     * used to change the layerType
     *
     * @param layersType the layer type that will replace the actual one
     */
    public void setLayersType(LayersType layersType) {
        this.layersType = layersType;
    }

    /*---------- Layers Related ----------*/

    /**
     * method to change the direction of the orthogonal vector used when {@code layerType = ALONG_DIRECTION}
     *
     * @param vect the vector that will be set
     */
    public void setLayerDirection(Vec3d vect) {
        this.directionalLayerDirection = vect.normalize();
    }

    /**
     * used to get the boolean force, to know if it is possible to force the pos of a block
     *
     * @return the boolean force
     */
    public boolean getForce() {
        return force;
    }

    /**
     * used to set the boolean force used when placing block
     *
     * @param force the boolean that will be set
     */
    public void setForce(boolean force) {
        this.force = force;
    }


    /*---------- LayerPlace Related ----------*/
    public void setLayerPlace(LayerPlace layerPlace) {
        this.layerPlace = layerPlace;
    }

    public LayerPlace getLayerPlace() {
        return layerPlace;
    }
    /*---------- Force Related ----------*/

    public List<Block> getBlocksToForce() {
        return blocksToForce;
    }

    public void setBlocksToForce(List<Block> blocksToForce) {
        this.blocksToForce = new ArrayList<>();
        this.blocksToForce.addAll(blocksToForce);
    }

    public void addBlocksToForce(Block block) {
        this.blocksToForce.add(block);
    }

    public void addBlocksToForce(List<Block> blocks) {
        this.blocksToForce.addAll(blocks);
    }

    /*---------- Rotation related ----------*/
    public int getXrotation() {
        return xrotation;
    }

    public void setXrotation(int xrotation) {
        this.xrotation = xrotation;
        getRotations(this.xrotation, this.yrotation, this.secondxrotation);
    }

    public int getYrotation() {
        return yrotation;
    }

    public void setYrotation(int yrotation) {
        this.yrotation = yrotation;
        getRotations(this.xrotation, this.yrotation, this.secondxrotation);
    }

    public int getSecondXrotation() {
        return secondxrotation;
    }

    public void setSecondxrotation(int secondxrotation) {
        this.secondxrotation = secondxrotation;
        getRotations(this.xrotation, this.yrotation, this.secondxrotation);
    }

    public void addXrotation(int xrotation) {
        this.xrotation += xrotation;
        getRotations(this.xrotation, this.yrotation, this.secondxrotation);
    }

    public void addYrotation(int yrotation) {
        this.yrotation += yrotation;
        getRotations(this.xrotation, this.yrotation, this.secondxrotation);
    }

    public void addSecondxrotation(int secondxrotation) {
        this.secondxrotation += secondxrotation;
        getRotations(this.xrotation, this.yrotation, this.secondxrotation);
    }

    /*----------- Pos Related ----------*/
    public @NotNull BlockPos getPos() {
        return pos;
    }

    public void setPos(@NotNull BlockPos pos) {
        this.pos = pos;
    }

    public void addPosOffset(BlockPos pos1) {
        this.pos.add(pos1);
    }

    public @NotNull StructureWorldAccess getWorld() {
        return world;
    }




    /*---------- Layers Related ----------*/

    public void addBlockLayers(List<BlockLayer> blockLayers) {
        this.blockLayers.addAll(blockLayers);
    }

    public void addBlockLayer(BlockLayer blockLayer) {
        this.blockLayers.add(blockLayer);
    }

    public void removeBlockLayer(List<BlockLayer> blockLayers) {
        this.blockLayers.removeAll(blockLayers);
    }

    public void removeBlockLayer(BlockLayer blockLayer) {
        this.blockLayers.remove(blockLayer);
    }

    public void removeBlockLayer(int index) {
        if (index >= this.blockLayers.size()) {
            Easierworldcreator.LOGGER.error("int index >= blocklayer size");
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.blockLayers.size());
        }
        this.removeBlockLayer(index);
    }

    public List<BlockLayer> getBlockLayers() {
        return this.blockLayers;
    }

    public void setBlockLayers(List<BlockLayer> blockLayers) {
        this.blockLayers = blockLayers;
    }

    public void setBlockLayers(BlockLayer... layers) {
        this.blockLayers = List.of(layers);
    }

    public BlockLayer getBlockLayer(int index) {
        if (index >= this.blockLayers.size()) {
            Easierworldcreator.LOGGER.error("int index >= blocklayer size");
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.blockLayers.size());
        }
        return this.blockLayers.get(index);
    }


    /*---------- Noise Related ----------*/
    public void setNoise(FastNoiseLite noise) {
        this.noise = noise;
    }

    public FastNoiseLite getNoise() {
        return this.noise;
    }


    /*---------- Place Related ----------*/
    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
    }

    public PlaceType getPlaceType() {
        return placeType;
    }

    public PlaceMoment getPlaceMoment() {
        return placeMoment;
    }

    /**
     * allow you to set and change the place moment
     *
     * @param placeMoment the changed parameter
     */
    public void setPlaceMoment(PlaceMoment placeMoment) {
        this.placeMoment = placeMoment;
    }

    /**
     * This method allows you to place the structure in the world.
     * Any changes done after this moment will not be taken in count except if you place another shape later
     */
    public void place() throws IOException {
        place(this.getBlockPos());
    }

    /**
     * This method is the method to place the related Blocks
     *
     * @param posList the {@link List} of {@link Set} of {@link BlockPos} calculated before, that will be placed
     */
    public void place(List<Set<BlockPos>> posList) throws IOException {
        Easierworldcreator.LOGGER.info("placing structure");
        if (this.placeType == PlaceType.BLOCKS) {

            //verify if the shape is larger than a chunk
            //if yes, we have to divide the structure into chunks
            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
            if (this.placeMoment == PlaceMoment.WORLD_GEN && this.biggerThanChunk) {

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
                            SaveChunkShapeInfo.saveChunkWorldGen(blockList, world, "false_" + featureName, offset);

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
                Path generatedPath = Objects.requireNonNull(world.getServer()).getSavePath(WorldSavePath.GENERATED).resolve(Easierworldcreator.MOD_ID).resolve("structures").normalize();


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


                List<Path> path = LoadChunkShapeInfo.verifyFiles(world, this.getPos());
                for (Path path1 : path) {
                    List<BlockList> blockLists = LoadChunkShapeInfo.loadFromJson(world, path1);
                    LoadChunkShapeInfo.placeStructure(world, blockLists);
                }
            }
            //In the case our structure isn't place during world gen or it is less than a chunk large
            else {
                for (Set<BlockPos> pos : posList) {
                    Easierworldcreator.LOGGER.info("structure smaller than chunk");
                    //executorService.submit(() -> {
                    this.placeLayers(pos);
                    //});
                }

            }
        } else {
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

    /**
     * place the layers of the structure starting from the first layer to the second to the third
     *
     * @param firstposlist list of BlockPos of the structure
     */
    public Set<BlockList> getLayers(Set<BlockPos> firstposlist) {
        Set<BlockList> blockLists = new HashSet<>();
        switch (layersType) {
            case SURFACE -> {
                Set<BlockPos> poslist = firstposlist; // Use a copy here

                if (poslist == null) {
                    List<BlockState> states = this.blockLayers.get(0).getBlockStates();
                    for (BlockPos pos : firstposlist) {
                        verifyForBlockLayer(pos, states, blockLists);
                    }
                    return blockLists;
                }

                for (int i = 1; i < this.blockLayers.size(); ++i) {
                    if (poslist.isEmpty()) return blockLists;
                    List<Set<BlockPos>> pos1 = this.placeSurfaceBlockLayers(new HashSet<>(poslist), i);
                    poslist = pos1.get(1);
                    firstposlist = pos1.get(0);
                    List<BlockState> states = this.blockLayers.get(i - 1).getBlockStates();

                    // Create a copy for safe iteration
                    Set<BlockPos> firstposlistCopy = new HashSet<>(firstposlist);
                    for (BlockPos pos : firstposlistCopy) {
                        verifyForBlockLayer(pos, states, blockLists);
                    }
                }
                List<BlockState> states = this.blockLayers.get(this.blockLayers.size() - 1).getBlockStates();

                // Create a copy for safe iteration
                Set<BlockPos> poslistCopy = new HashSet<>(poslist);
                for (BlockPos pos : poslistCopy) {
                    verifyForBlockLayer(pos, states, blockLists);
                }
            }
            case INNER_RADIAL -> blockLists.addAll(this.getInnerRadialBlocks(firstposlist));
            case OUTER_RADIAL -> blockLists.addAll(this.getOuterRadialBlocks(firstposlist));
            case INNER_CYLINDRICAL -> blockLists.addAll(this.getInnerCylindricalBlocks(firstposlist));
            case OUTER_CYLINDRICAL -> blockLists.addAll(this.getOuterCylindricalBlocks(firstposlist));
            case ALONG_DIRECTION -> blockLists.addAll(this.getDirectionalLayers(firstposlist));
            default -> throw new IllegalStateException("Unexpected value: " + layersType);
        }
        return blockLists;
    }

    /**
     * place the layers of the structure depending on the {@link LayersType}
     *
     * @param firstposlist list of BlockPos of the structure
     */
    public void placeLayers(Set<BlockPos> firstposlist) {

        switch (layersType) {
            case SURFACE -> {
                Set<BlockPos> poslist = new HashSet<>();
                poslist = this.placeFirstSurfaceBlockLayers(firstposlist);

                if (poslist == null) return;

                for (int i = 1; i < this.blockLayers.size(); ++i) {
                    if (poslist.isEmpty()) return;
                    List<Set<BlockPos>> pos1 = this.placeSurfaceBlockLayers(poslist, i);
                    poslist = pos1.get(1);
                    firstposlist = pos1.get(0);

                    List<BlockState> states = this.blockLayers.get(i - 1).getBlockStates();

                    for (BlockPos pos : firstposlist) {
                        this.placeBlocks(states, pos);
                    }
                }
                List<BlockState> states = this.blockLayers.get(this.blockLayers.size() - 1).getBlockStates();

                for (BlockPos pos : poslist) {
                    this.placeBlocks(states, pos);
                }
            }
            case INNER_RADIAL -> this.placeInnerRadialBlocks(firstposlist);
            case OUTER_RADIAL -> this.placeOuterRadialBlocks(firstposlist);
            case INNER_CYLINDRICAL -> this.placeInnerCylindricalBlocks(firstposlist);
            case OUTER_CYLINDRICAL -> this.placeOuterCylindricalBlocks(firstposlist);
            case ALONG_DIRECTION -> this.placeDirectionalLayers(firstposlist);
            default -> throw new IllegalStateException("Unexpected value: " + layersType);
        }
    }


    /**
     * This method returns a temporary blockPos list of the first layer after verification. The first layer will the be placed in {@link #placeSurfaceBlockLayers(Set, int)}
     *
     * @param firstposlist the list of BlockPos to verify at first
     * @return the list of verified BlockPos
     */
    public Set<BlockPos> placeFirstSurfaceBlockLayers(Set<BlockPos> firstposlist) {
        Set<BlockPos> newposlist = new HashSet<>();
        for (BlockPos pos : firstposlist) {
            this.setBlocksToForce(WorldGenUtil.addBlockStateListtoBlockList(this.blocksToForce, this.blockLayers.get(0).getBlockStates()));
            if (verifyBlocks(pos)) {
                newposlist.add(pos);
            }
        }
        return newposlist;
    }

    /**
     * <p>This method is used to determine and place the layers of the structure
     * <p>It determines for every {@link BlockPos} provided if the pos can be replaced by the new Layer.
     * <p>To determine it, it gets the depth of the previous layer.
     * It verifies if there is in the {@link List} the actual {@link  BlockPos} with an offset of the depth
     * <p>  -if there is,
     * it adds the {@link BlockPos} in a new {@link List}
     * and remove the {@link BlockPos} from the existing list.
     * <p>  -If not, it does nothing.
     *
     * @param poslist    the list of {@link BlockPos} of the precedent Layer
     * @param layerIndex the index to get the depth
     * @return two {@link List}.
     * One corresponding to the final {@code List<BlockPos>} of the previous layer.
     * The other one, the rest of the {@code List<BlockPos>} of the structure that will be used by the next iteration.
     */
    public List<Set<BlockPos>> placeSurfaceBlockLayers(Set<BlockPos> poslist, int layerIndex) {
        Set<BlockPos> newposlist = new HashSet<>();

        // Precompute the depth of the previous layer if it exists
        int previousLayerDepth = this.blockLayers.get(layerIndex - 1).getDepth();

        // Convert poslist to a set for faster lookups
        Set<BlockPos> posSet = new HashSet<>(poslist);
        Iterator<BlockPos> iterator = poslist.iterator();

        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            BlockPos posUp = pos.up(previousLayerDepth);
            if (!posSet.contains(posUp)) {
                continue;
            }
            iterator.remove();
            //placeBlocks(layerIndex, pos);
            newposlist.add(pos);
        }
        return List.of(poslist, newposlist);
    }

    public void placeInnerCylindricalBlocks(Set<BlockPos> posList) {
        List<Integer> layerDistance = new ArrayList<Integer>();
        layerDistance.add(this.blockLayers.get(0).getDepth());

        for (int i = 1; i < this.blockLayers.size(); i++) {
            layerDistance.add(this.blockLayers.get(i).getDepth() + layerDistance.get(i - 1));
        }

        int layerDistanceSize = layerDistance.size();
        for (BlockPos pos : posList) {
            boolean bl = false;
            float distance = WorldGenUtil.getDistance(new BlockPos(this.radialCenterPos.getX(), pos.getY(), this.radialCenterPos.getZ()), pos);
            for (int i = 0; i < layerDistanceSize - 1; i++) {
                if (distance < layerDistance.get(i)) {
                    placeBlocksWithVerification(i, pos);
                    bl = true;
                }
            }
            if (!bl) {
                placeBlocksWithVerification(layerDistanceSize - 1, pos);
            }
        }
    }

    public void placeOuterCylindricalBlocks(Set<BlockPos> posList) {
        List<Integer> layerDistance = new ArrayList<Integer>();
        layerDistance.add(this.blockLayers.get(0).getDepth());

        float maxDistance = 0;
        for (BlockPos pos : posList) {
            float distance = WorldGenUtil.getDistance(new BlockPos(this.radialCenterPos.getX(), pos.getY(), this.radialCenterPos.getZ()), pos);
            maxDistance = Math.max(distance, maxDistance);
        }
        for (int i = 1; i < this.blockLayers.size(); i++) {
            layerDistance.add(this.blockLayers.get(i).getDepth() + layerDistance.get(i - 1));
        }

        int layerDistanceSize = layerDistance.size();
        for (BlockPos pos : posList) {
            boolean bl = false;
            float distance = WorldGenUtil.getDistance(new BlockPos(this.radialCenterPos.getX(), pos.getY(), this.radialCenterPos.getZ()), pos);
            for (int i = 0; i < layerDistanceSize - 1; i++) {
                if (distance > maxDistance - layerDistance.get(i)) {
                    placeBlocksWithVerification(i, pos);
                    bl = true;
                }
            }
            if (!bl) {
                placeBlocksWithVerification(layerDistanceSize-1, pos);
            }
        }
    }

    //be careful when using layers with 1 block depth, that might do some weird things
    public void placeInnerRadialBlocks(Set<BlockPos> posList) {
        for (BlockPos pos : posList) {
            float distance = WorldGenUtil.getDistance(radialCenterPos, pos);
            int maxdist = this.blockLayers.get(0).getDepth();
            int mindist = 0;
            int a = 0;
            boolean bl = false;
            while (!(distance <= maxdist && distance >= mindist)) {
                if (a >= this.blockLayers.size()) {
                    placeBlocks(a--, pos);
                    bl = true;
                    continue;
                }
                mindist = maxdist;
                maxdist += this.blockLayers.get(a).getDepth();
                a++;
            }
        }
    }

    public void placeOuterRadialBlocks(Set<BlockPos> posList) {
        List<Integer> layerDistance = new ArrayList<Integer>();
        layerDistance.add(this.blockLayers.get(0).getDepth());

        float maxDistance = 0;
        for (BlockPos pos : posList) {
            float distance = WorldGenUtil.getDistance(this.radialCenterPos, pos);
            maxDistance = Math.max(distance, maxDistance);
        }
        for (int i = 1; i < this.blockLayers.size(); i++) {
            layerDistance.add(this.blockLayers.get(i).getDepth() + layerDistance.get(i - 1));
        }

        int layerDistanceSize = layerDistance.size();
        for (BlockPos pos : posList) {
            boolean bl = false;
            float distance = WorldGenUtil.getDistance(radialCenterPos, pos);
            for (int i = 0; i < layerDistanceSize - 1; i++) {
                if (distance > maxDistance - layerDistance.get(i)) {
                    placeBlocksWithVerification(i, pos);
                    bl = true;
                }
            }
            if (!bl) {
                placeBlocksWithVerification(layerDistanceSize-1, pos);
            }
        }
    }


    //TODO
    //known bug where the first layers is a little wider than what it is supposed to be

    /**
     * This method place the {@link Block} in a normal plan of the directional {@link Vec3d}
     * <p>This method assign for every {@link BlockPos} in the list a {@link Block} depending on the {@link LayerPlace} provided
     * and then place it.
     * <p>- To determine the {@link BlockLayer} to place,
     * <p> First, it sorts the list depending on the {@link Vec3d}.
     * <p> Then for every {@link Block}, it calculates the distance between the actual {@link Block} and the first one of the sorted list.
     * It will then place the Block corresponding to the actual depth, determined distance / first distance
     *
     * @param firstposlist the list of the BlockPos
     */
    private void placeDirectionalLayers(Set<BlockPos> firstposlist) {
        Vec3d direction = this.directionalLayerDirection.normalize();
        List<BlockPos> poslist = new ArrayList<>(firstposlist);

        // Sort positions according to the directional vector
        poslist.sort(Comparator.comparingDouble(pos -> -pos.getX() * direction.x - pos.getY() * direction.y - pos.getZ() * direction.z));

        BlockPos firstPoint = poslist.get(0);

        int u = 0;
        int a = this.blockLayers.get(u).getDepth();
        float b;
        float g = 0;
        float h = 0;
        List<BlockState> states = blockLayers.get(u).getBlockStates();
        int size = this.blockLayers.size() - 1;
        for (BlockPos pos : poslist) {

            if (u != size) {
                b = WorldGenUtil.getDistanceFromPointToPlane(direction, firstPoint.toCenterPos(), pos.toCenterPos());
                if (g == 0 && b > 2.0E-4) {
                    g = (float) (b);
                    h = (float) ((a) * g + 0.00002);
                }

                if (b <= h) {
                    placeBlocksWithVerification(states, pos);
                } else {
                    u++;
                    a += this.blockLayers.get(u).getDepth();
                    h = (float) (a * g + 0.00002);
                    states = blockLayers.get(u).getBlockStates();
                    placeBlocksWithVerification(states, pos);

                }
            }
            //place the last layer on all the structure everything was placed
            else {
                placeBlocksWithVerification(u, pos);
            }
        }
    }




    /*-----------------------------------------------------------------------------------------------------------------------------*/


    /**
     * this is basically the same as previous except that it can't verify if it can place the Block.
     *
     * @param firstposlist the first {@link List}
     * @return
     */
    public Set<BlockPos> getFirstSurfaceBlockLayers(Set<BlockPos> firstposlist) {
        Set<BlockPos> newposlist = new HashSet<>();
        for (BlockPos pos : firstposlist) {
            this.setBlocksToForce(WorldGenUtil.addBlockStateListtoBlockList(this.blocksToForce, this.blockLayers.get(0).getBlockStates()));
            newposlist.add(pos);

        }
        return newposlist;
    }

    public Set<BlockList> getInnerCylindricalBlocks(Set<BlockPos> posList) {
        Set<BlockList> blockLists = new HashSet<>();
        List<Integer> layerDistance = new ArrayList<Integer>();
        layerDistance.add(this.blockLayers.get(0).getDepth());

        for (int i = 1; i < this.blockLayers.size(); i++) {
            layerDistance.add(this.blockLayers.get(i).getDepth() + layerDistance.get(i - 1));
        }

        int layerDistanceSize = layerDistance.size();
        for (BlockPos pos : posList) {
            boolean bl = false;
            float distance = WorldGenUtil.getDistance(new BlockPos(this.radialCenterPos.getX(), pos.getY(), this.radialCenterPos.getZ()), pos);
            for (int i = 0; i < layerDistanceSize - 1; i++) {
                if (distance < layerDistance.get(i)) {
                    verifyForBlockLayer(pos, this.blockLayers.get(i).getBlockStates(), blockLists);
                    bl = true;
                }
            }
            if (!bl) {
                verifyForBlockLayer(pos, this.blockLayers.get(layerDistanceSize - 1).getBlockStates(), blockLists);
            }
        }
        return blockLists;
    }

    public Set<BlockList> getOuterCylindricalBlocks(Set<BlockPos> posList) {
        Set<BlockList> blockLists = new HashSet<>();
        List<Integer> layerDistance = new ArrayList<Integer>();

        //get the max distance of the list
        float maxDistance = 0;
        for (BlockPos pos : posList) {
            float distance = WorldGenUtil.getDistance(new BlockPos(this.radialCenterPos.getX(), pos.getY(), this.radialCenterPos.getZ()), pos);
            maxDistance = Math.max(distance, maxDistance);
        }

        layerDistance.add(this.blockLayers.get(0).getDepth());

        for (int i = 1; i < this.blockLayers.size(); i++) {
            layerDistance.add(this.blockLayers.get(i).getDepth() + layerDistance.get(i - 1));
        }

        int layerDistanceSize = layerDistance.size();
        for (BlockPos pos : posList) {
            boolean bl = false;
            float distance = WorldGenUtil.getDistance(new BlockPos(this.radialCenterPos.getX(), pos.getY(), this.radialCenterPos.getZ()), pos);
            for (int i = 0; i < layerDistanceSize - 1; i++) {
                if (distance > maxDistance - layerDistance.get(i)) {
                    verifyForBlockLayer(pos, this.blockLayers.get(i).getBlockStates(), blockLists);
                    bl = true;
                }
            }
            if (!bl) {
                verifyForBlockLayer(pos, this.blockLayers.get(layerDistanceSize - 1).getBlockStates(), blockLists);
            }
        }
        return blockLists;
    }

    //be careful when using layers with 1 block depth, that might do some weird things
    public Set<BlockList> getInnerRadialBlocks(Set<BlockPos> posList) {
        Set<BlockList> blockLists = new HashSet<>();
        List<Integer> layerDistance = new ArrayList<Integer>();
        layerDistance.add(this.blockLayers.get(0).getDepth());

        for (int i = 1; i < this.blockLayers.size(); i++) {
            layerDistance.add(this.blockLayers.get(i).getDepth() + layerDistance.get(i - 1));
        }

        int layerDistanceSize = layerDistance.size();
        for (BlockPos pos : posList) {
            boolean bl = false;
            float distance = WorldGenUtil.getDistance(radialCenterPos, pos);
            for (int i = 0; i < layerDistanceSize - 1; i++) {
                if (distance < layerDistance.get(i)) {
                    verifyForBlockLayer(pos, this.blockLayers.get(i).getBlockStates(), blockLists);
                    bl = true;
                }
            }
            if (!bl) {
                verifyForBlockLayer(pos, this.blockLayers.get(layerDistanceSize - 1).getBlockStates(), blockLists);
            }
        }
        return blockLists;
    }

    public Set<BlockList> getOuterRadialBlocks(Set<BlockPos> posList) {
        Set<BlockList> blockLists = new HashSet<>();
        List<Integer> layerDistance = new ArrayList<Integer>();
        layerDistance.add(this.blockLayers.get(0).getDepth());

        float maxDistance = 0;
        for (BlockPos pos : posList) {
            float distance = WorldGenUtil.getDistance(this.radialCenterPos, pos);
            maxDistance = Math.max(distance, maxDistance);
        }
        for (int i = 1; i < this.blockLayers.size(); i++) {
            layerDistance.add(this.blockLayers.get(i).getDepth() + layerDistance.get(i - 1));
        }

        int layerDistanceSize = layerDistance.size();
        for (BlockPos pos : posList) {
            boolean bl = false;
            float distance = WorldGenUtil.getDistance(radialCenterPos, pos);
            for (int i = 0; i < layerDistanceSize - 1; i++) {
                if (distance > maxDistance - layerDistance.get(i)) {
                    verifyForBlockLayer(pos, this.blockLayers.get(i).getBlockStates(), blockLists);
                    bl = true;
                }
            }
            if (!bl) {
                verifyForBlockLayer(pos, this.blockLayers.get(layerDistanceSize - 1).getBlockStates(), blockLists);
            }
        }
        return blockLists;
    }


    //TODO
    //known bug where the first layers is a little wider than what it is supposed to be
    private Set<BlockList> getDirectionalLayers(Set<BlockPos> firstposlist) {
        Vec3d direction = this.directionalLayerDirection.normalize();
        List<BlockPos> poslist = new ArrayList<>(firstposlist);
        Set<BlockList> blockLists = new HashSet<>();

        // Sort positions according to the directional vector
        poslist.sort(Comparator.comparingDouble(pos -> -pos.getX() * direction.x - pos.getY() * direction.y - pos.getZ() * direction.z));

        BlockPos firstPoint = poslist.get(0);

        int layerIndex = 0;
        int cumulativeDepth = this.blockLayers.get(layerIndex).getDepth();
        float initialDistance = 0;
        float maxDistance = 0;

        List<BlockState> currentStates = this.blockLayers.get(layerIndex).getBlockStates();
        int totalLayers = this.blockLayers.size() - 1;

        for (BlockPos pos : poslist) {
            float currentDistance = WorldGenUtil.getDistanceFromPointToPlane(direction, firstPoint.toCenterPos(), pos.toCenterPos());

            if (initialDistance == 0 && currentDistance > 2.0E-4) {
                initialDistance = currentDistance;
                maxDistance = cumulativeDepth * initialDistance + 0.00002f;
            }

            // Move to the next layer if needed
            while (layerIndex < totalLayers && currentDistance > maxDistance) {
                layerIndex++;
                cumulativeDepth += this.blockLayers.get(layerIndex).getDepth();
                maxDistance = cumulativeDepth * initialDistance + 0.00002f;
                currentStates = this.blockLayers.get(layerIndex).getBlockStates();
            }

            // Add the block position to the appropriate layer
            verifyForBlockLayer(pos, currentStates, blockLists);
        }

        return blockLists;
    }

    private void verifyForBlockLayer(BlockPos pos, List<BlockState> states, Set<BlockList> blockLists) {
        BlockState state = getBlockToPlace(states, pos);
        Iterator<BlockList> iterator = blockLists.iterator();

        while (iterator.hasNext()) {
            BlockList blockList = iterator.next();
            if (blockList.getBlockstate().equals(state)) {
                blockList.addBlockPos(pos);
                return;
            }
        }
        blockLists.add(new BlockList(List.of(pos), state));
    }


    /*---------- Place Structure ----------*/

    /**
     * verify for a set of chunks if every chunks wasn't generated.
     *
     * @param chunks
     * @return
     */
    private boolean canPos(Set<ChunkPos> chunks) {
        for (ChunkPos chunk : chunks) {
            if (ChunkUtil.isChunkGenerated(chunk, world)) {
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
        int maxOffset = 3;
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
                            for (ChunkPos chunk : coveredChunks) {
                                ChunkUtil.protectChunk(chunk);
                            }
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


    public BlockPos getCoordinatesRotation(Vec3d pos, BlockPos centerPos) {
        return getCoordinatesRotation((float) pos.getX(), (float) pos.getY(), (float) pos.getZ(), centerPos);
    }

    //return a BlockPos of a block after rotating it
    public BlockPos getCoordinatesRotation(float x, float y, float z, BlockPos pos) {
        // first x rotation
        float y_rot1 = (float) (y * cosx - z * sinx);
        float z_rot1 = (float) (y * sinx + z * cosx);

        // y rotation
        float x_rot_z = (float) (x * cosy - y_rot1 * siny);
        float y_rot_z = (float) (x * siny + y_rot1 * cosy);

        // second x rotation
        float y_rot2 = (float) (y_rot_z * cosx2 - z_rot1 * sinx2);
        float z_rot2 = (float) (y_rot_z * sinx2 + z_rot1 * cosx2);

        return new BlockPos(new BlockPos.Mutable().set((Vec3i) pos, (int) x_rot_z, (int) y_rot2, (int) z_rot2));
    }

    public List<BlockPos> getCoordinatesRotationList(List<Vec3d> poslist, BlockPos centerPos) {
        List<BlockPos> newposlist = new ArrayList<>();
        for (Vec3d pos : poslist) {
            newposlist.add(this.getCoordinatesRotation(pos, centerPos));
        }
        return newposlist;
    }

    public void getGenTime(long startTime, boolean place) {
        long endTime = (System.nanoTime());
        long duration = (endTime - startTime) / 1000000;
        if (place) {
            Easierworldcreator.LOGGER.info("structure placing took : {} ms", duration);
        } else {
            Easierworldcreator.LOGGER.info("structure coordinates calculations took : {} ms", duration);
        }
    }

    //place blocks without verification
    public void placeBlocks(int index, BlockPos pos) {
        switch (this.layerPlace) {
            case RANDOM:
                BlockPlaceUtil.placeRandomBlock(world, this.blockLayers.get(index).getBlockStates(), pos);
                break;
            case NOISE2D:
                BlockPlaceUtil.placeBlockWith2DNoise(world, this.blockLayers.get(index).getBlockStates(), pos, this.noise);
                break;
            case NOISE3D:
                BlockPlaceUtil.placeBlockWith3DNoise(world, this.blockLayers.get(index).getBlockStates(), pos, this.noise);
                break;
            default:
                BlockPlaceUtil.placeBlockWithOrder(world, this.blockLayers.get(index).getBlockStates(), pos, this.placedBlocks);
                this.placedBlocks = this.placedBlocks % (this.blockLayers.size() - 1);
                break;
        }
    }

    //Place blocks without verification. Used for precomputed List<BlockStates> instead of searching it on the BlockLayer
    public void placeBlocks(List<BlockState> states, BlockPos pos) {
        switch (this.layerPlace) {
            case RANDOM:
                BlockPlaceUtil.placeRandomBlock(world, states, pos);
                break;
            case NOISE2D:
                BlockPlaceUtil.placeBlockWith2DNoise(world, states, pos, this.noise);
                break;
            case NOISE3D:
                BlockPlaceUtil.placeBlockWith3DNoise(world, states, pos, this.noise);
                break;
            default:
                BlockPlaceUtil.placeBlockWithOrder(world, states, pos, this.placedBlocks);
                this.placedBlocks = this.placedBlocks % (this.blockLayers.size() - 1);
                break;
        }
    }

    /**
     * place a block in the world at the pos if it is able to
     *
     * @param index the index of the the {@link  BlockLayer}
     * @param pos   the pos of the block
     * @return boolean if the block was placed
     */
    public boolean placeBlocksWithVerification(int index, BlockPos pos) {
        switch (this.layerPlace) {
            case RANDOM:
                return BlockPlaceUtil.setRandomBlockWithVerification(world, this.force, this.blocksToForce, this.blockLayers.get(index).getBlockStates(), pos);
            case NOISE2D:
                return BlockPlaceUtil.set2dNoiseBlockWithVerification(world, this.force, this.blocksToForce, this.blockLayers.get(index).getBlockStates(), pos, this.noise);
            case NOISE3D:
                return BlockPlaceUtil.set3dNoiseBlockWithVerification(world, this.force, this.blocksToForce, this.blockLayers.get(index).getBlockStates(), pos, this.noise);
            default:
                boolean bl = BlockPlaceUtil.setBlockWithOrderWithVerification(world, this.force, this.blocksToForce, this.blockLayers.get(index).getBlockStates(), pos, this.placedBlocks);
                this.placedBlocks = (this.placedBlocks + 1) % (this.blockLayers.size() - 1);
                return bl;
        }
    }

    /**
     * place a block in the world at the pos if it is able to
     * precomputed list for little performance improvement
     *
     * @param states the states that will be choosed
     * @param pos    the pos of the block
     * @return boolean if the block was placed
     */
    public boolean placeBlocksWithVerification(List<BlockState> states, BlockPos pos) {
        switch (this.layerPlace) {
            case RANDOM:
                return BlockPlaceUtil.setRandomBlockWithVerification(world, this.force, this.blocksToForce, states, pos);
            case NOISE2D:
                return BlockPlaceUtil.set2dNoiseBlockWithVerification(world, this.force, this.blocksToForce, states, pos, this.noise);
            case NOISE3D:
                return BlockPlaceUtil.set3dNoiseBlockWithVerification(world, this.force, this.blocksToForce, states, pos, this.noise);
            default:
                boolean bl = BlockPlaceUtil.setBlockWithOrderWithVerification(world, this.force, this.blocksToForce, states, pos, this.placedBlocks);
                this.placedBlocks = (this.placedBlocks + 1) % (this.blockLayers.size() - 1);
                return bl;
        }

    }

    //Used to get the blocksState notably used during world gen, this doesn't place anything
    public BlockState getBlockToPlace(int index, BlockPos pos) {
        switch (this.layerPlace) {
            case RANDOM:
                return BlockPlaceUtil.getRandomBlock(this.blockLayers.get(index).getBlockStates());
            case NOISE2D:
                return BlockPlaceUtil.getBlockWith2DNoise(this.blockLayers.get(index).getBlockStates(), pos, this.noise);
            case NOISE3D:
                return BlockPlaceUtil.getBlockWith3DNoise(this.blockLayers.get(index).getBlockStates(), pos, this.noise);
            default:
                BlockState blockState = BlockPlaceUtil.getBlockWithOrder(this.blockLayers.get(index).getBlockStates(), this.placedBlocks);
                this.placedBlocks = (this.placedBlocks + 1) % (this.blockLayers.size() - 1);
                return blockState;
        }
    }

    //Used to get the blocksState notably used during world gen, this doesn't place anything
    //Used for precomputed BlockState list
    public BlockState getBlockToPlace(List<BlockState> states, BlockPos pos) {
        switch (this.layerPlace) {
            case RANDOM:
                return BlockPlaceUtil.getRandomBlock(states);
            case NOISE2D:
                return BlockPlaceUtil.getBlockWith2DNoise(states, pos, this.noise);
            case NOISE3D:
                return BlockPlaceUtil.getBlockWith3DNoise(states, pos, this.noise);
            default:
                BlockState blockState = BlockPlaceUtil.getBlockWithOrder(states, this.placedBlocks);
                this.placedBlocks = (this.placedBlocks + 1) % (this.blockLayers.size() - 1);
                return blockState;
        }
    }

    public boolean verifyBlocks(BlockPos pos) {
        return BlockPlaceUtil.verifyBlock(this.world, this.force, blocksToForce, pos);
    }


    /**
     * change how the blocks are put
     */
    public enum LayersType {
        /**
         * for a natural aspect
         * Put the first BlockStates on top of the structure for a coordinate x and z,
         * and until it reaches the depth of the layer
         */
        SURFACE,
        //place the blocks in a sphere shape, first layer being placed at the center
        INNER_RADIAL,
        //place the blocks in a sphere shape, last layer being placed at the center
        OUTER_RADIAL,
        //place the blocks in a cylindrical shape, first layer being placed at the center
        INNER_CYLINDRICAL,
        //place the blocks in a cylindrical shape, last layer being placed near the center
        OUTER_CYLINDRICAL,
        //place the blocks on a plan
        //the plan is defined by the vector "directionalLayerDirection"
        ALONG_DIRECTION
    }

    /**
     * set the type of objects that will be placed
     */
    public enum PlaceType {
        //place blocks
        BLOCKS,
        //place particles
        //particles are not implemented for the moment
        PARTICLE
    }

    /**
     * set how the blocks/particles will be chosen inside a layer
     */
    public enum LayerPlace {
        //will choose random Block/Particle in the layer
        RANDOM,
        //will place the first Block/particle in the layer, then the second, then the third, in the order
        ORDER,
        //will place the Block/Particle according to a 2d noise
        NOISE2D,
        //will place the Block/Particle according to a 3d noise
        NOISE3D
    }

    //Define the moment the shape will be placed.
    // It is really important that this does match what you want or you will run into issues or even crash
    public enum PlaceMoment {
        //used during the world generation
        WORLD_GEN,
        //used for any other moment than world gen
        OTHER
    }
}