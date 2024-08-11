package net.rodofire.easierworldcreator.shapeutil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Easierworldcreator;
import net.rodofire.easierworldcreator.util.FastMaths;
import net.rodofire.easierworldcreator.worldgenutil.BlockPlaceUtil;
import net.rodofire.easierworldcreator.worldgenutil.FastNoiseLite;
import net.rodofire.easierworldcreator.worldgenutil.WorldGenUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * class to create custom shapes
 */
public abstract class Shape {
    @NotNull
    private StructureWorldAccess world;
    @NotNull
    private BlockPos pos;

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
    public Shape(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, List<BlockLayer> layers, boolean force, List<Block> blocksToForce, int xrotation, int yrotation, int secondxrotation) {
        this.world = world;
        this.pos = pos;
        this.force = force;
        this.blocksToForce = blocksToForce;
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
    public Shape(@NotNull StructureWorldAccess world, @NotNull BlockPos pos) {
        this.world = world;
        this.pos = pos;
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

    public LayersType getLayersType() {
        return layersType;
    }

    public void setLayersType(LayersType layersType) {
        this.layersType = layersType;
    }

    /*---------- Layers Related ----------*/
    public void setLayerDirection(Vec3d vect) {
        this.directionalLayerDirection = vect.normalize();
    }

    public boolean getForce() {
        return force;
    }

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
        this.blocksToForce = blocksToForce;
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
    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public void addPosOffset(BlockPos pos1) {
        this.pos.add(pos1);
    }

    public StructureWorldAccess getWorld() {
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

    public PlaceType getPlaceType() {
        return placeType;
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

    //Method to place the structure. Every change done after the method will not be taken in count
    public void place() {
        if (this.placeType == PlaceType.BLOCKS) {
            this.placeLayers(this.getBlockPos());
        } else {
        }
    }

    //returns a list of blockPos for every shape
    public abstract List<BlockPos> getBlockPos();

    //returns a list of Vec3d for every shape
    public abstract List<Vec3d> getVec3d();

    /**
     * place the layers of the structure starting from the first layer to the second to the third
     *
     * @param firstposlist list of BlockPos of the structure
     */
    public void placeLayers(List<BlockPos> firstposlist) {
        if (this.layersType == LayersType.SURFACE) {
            List<BlockPos> poslist = new ArrayList<>();
            poslist = this.placeFirstSurfaceBlockLayers(firstposlist);
            if (poslist == null) return;
            for (int i = 1; i < this.blockLayers.size(); ++i) {
                if (poslist.isEmpty()) return;
                //créer nouveau thread pour économiser du temps
                //pendant que les positions seront calculées, les blocs précédents seront mis
                List<List<BlockPos>> pos1 = this.placeSurfaceBlockLayers(poslist, i);
                poslist = pos1.get(1);
                firstposlist = pos1.get(0);


                //blocks qui seront poser sur le server thread
                for (BlockPos pos : firstposlist) {
                    this.placeBlocks(i - 1, pos);
                }
            }
            /*ExecutorService executor = Executors.newSingleThreadExecutor();
            for (int i = 1; i < this.blockLayers.size(); ++i) {
                if (poslist.isEmpty()) return;

                // Soumettre la tâche de calcul des positions à un nouveau thread
                List<BlockPos> finalPoslist = poslist;
                int finalI = i;
                Future<List<List<BlockPos>>> future = executor.submit(new Callable<List<List<BlockPos>>>() {
                    @Override
                    public List<List<BlockPos>> call() throws Exception {
                        return placeSurfaceBlockLayers(finalPoslist, finalI);
                    }
                });

                // Poser les blocs dans le thread principal pendant que le calcul est en cours
                for (BlockPos pos : firstposlist) {
                    this.placeBlocks(i - 1, pos);
                }

                try {
                    // Attendre que le calcul des positions soit terminé
                    List<List<BlockPos>> result = future.get();
                    poslist = result.get(1);
                    firstposlist = result.get(0);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            executor.shutdown();*/
        } else if (this.layersType == LayersType.RADIAL) {
            this.placeRadialBlocks(firstposlist);
        } else if (this.layersType == LayersType.CYLINDRICAL) {
            this.placeCylindricalBlocks(firstposlist);
        } else {
            this.placeDirectionalLayers(firstposlist);
        }
    }


    //place the first layer on the structure
    public List<BlockPos> placeFirstSurfaceBlockLayers(List<BlockPos> firstposlist) {
        List<BlockPos> newposlist = new ArrayList<BlockPos>();
        for (BlockPos pos : firstposlist) {
            this.setBlocksToForce(WorldGenUtil.addBlockStateListtoBlockList(this.blocksToForce, this.blockLayers.get(0).getBlockStates()));
            if (verifyBlocks(pos)) {
                newposlist.add(pos);
            }
        }
        return newposlist;
    }

    //place the other layers on the structure
    //for every blockpos, it verify if the block in the world at the pos + depth is still in the first layer
    public List<List<BlockPos>> placeSurfaceBlockLayers(List<BlockPos> poslist, int layerIndex) {
        List<BlockPos> newposlist = new ArrayList<>();

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
            placeBlocks(layerIndex, pos);
            newposlist.add(pos);
        }
        return List.of(poslist, newposlist);
    }

    public void placeCylindricalBlocks(List<BlockPos> posList) {
        for (BlockPos pos : posList) {
            //instead of a point in the world,
            //we use the Y coordinate of the pos
            //to get a straight line which creates a cylindrical shape instead of a circular shape
            float distance = WorldGenUtil.getDistance(new BlockPos(this.radialCenterPos.getX(), pos.getY(), this.radialCenterPos.getZ()), pos);
            int maxdist = this.blockLayers.get(0).getDepth();
            int mindist = 0;
            int a = 0;
            boolean bl = false;
            while (!(distance < maxdist && mindist < maxdist) || bl) {
                a++;
                if (a >= this.blockLayers.size()) {
                    BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blockLayers.get(this.blockLayers.size() - 1).getBlockStates(), pos);
                    bl = true;
                }
                mindist += maxdist;
                maxdist += this.blockLayers.get(a).getDepth();
            }
        }
    }

    //be careful when using layers with 1 block depth, that might do some weird things
    public void placeRadialBlocks(List<BlockPos> posList) {
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


    //TODO
    //known bug where the first layers is a little wider than what it is supposed to be
    private void placeDirectionalLayers(List<BlockPos> firstposlist) {
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
                    placeBlocksWithVerification(states,pos);
                } else {
                    u++;
                    a += this.blockLayers.get(u).getDepth();
                    h = (float) (a * g + 0.00002);
                    states = blockLayers.get(u).getBlockStates();
                    placeBlocksWithVerification(states,pos);

                }
            }
            //place the last layer on all the structure everything was placed
            else {
                placeBlocksWithVerification(u,pos);
            }
        }
    }


    /*---------- Place Structure ----------*/
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
        if (this.layerPlace == LayerPlace.RANDOM) {
            BlockPlaceUtil.placeRandomBlock(world, this.blockLayers.get(index).getBlockStates(), pos);
        } else if (this.layerPlace == LayerPlace.NOISE2D) {
            BlockPlaceUtil.placeBlockWith2DNoise(world, this.blockLayers.get(index).getBlockStates(), pos, this.noise);
        } else if (this.layerPlace == LayerPlace.NOISE3D) {
            BlockPlaceUtil.placeBlockWith3DNoise(world, this.blockLayers.get(index).getBlockStates(), pos, this.noise);
        } else {
            BlockPlaceUtil.placeBlockWithOrder(world, this.blockLayers.get(index).getBlockStates(), pos, this.placedBlocks);
            this.placedBlocks = this.placedBlocks % (this.blockLayers.size() - 1);
        }
    }

    //Place blocks without verification. Used for precomputed List<BlockStates> instead of searching it on the BlockLayer
    public void placeBlocks(List<BlockState> states, BlockPos pos) {
        if (this.layerPlace == LayerPlace.RANDOM) {
            BlockPlaceUtil.placeRandomBlock(world, states, pos);
        } else if (this.layerPlace == LayerPlace.NOISE2D) {
            BlockPlaceUtil.placeBlockWith2DNoise(world, states, pos, this.noise);
        } else if (this.layerPlace == LayerPlace.NOISE3D) {
            BlockPlaceUtil.placeBlockWith3DNoise(world, states, pos, this.noise);
        } else {
            BlockPlaceUtil.placeBlockWithOrder(world, states, pos, this.placedBlocks);
            this.placedBlocks = this.placedBlocks % (this.blockLayers.size() - 1);
        }
    }

    //Place blocks with verification depending on the layer place
    public boolean placeBlocksWithVerification(int index, BlockPos pos) {
        if (this.layerPlace == LayerPlace.RANDOM) {
            return BlockPlaceUtil.setRandomBlockWithVerification(world, this.force, this.blocksToForce, this.blockLayers.get(index).getBlockStates(), pos);
        } else if (this.layerPlace == LayerPlace.NOISE2D) {
            return BlockPlaceUtil.set2dNoiseBlockWithVerification(world, this.force, this.blocksToForce, this.blockLayers.get(index).getBlockStates(), pos, this.noise);
        } else if (this.layerPlace == LayerPlace.NOISE3D) {
            return BlockPlaceUtil.set3dNoiseBlockWithVerification(world, this.force, this.blocksToForce, this.blockLayers.get(index).getBlockStates(), pos, this.noise);
        } else {
            boolean bl = BlockPlaceUtil.setBlockWithOrderWithVerification(world, this.force, this.blocksToForce, this.blockLayers.get(index).getBlockStates(), pos, this.placedBlocks);
            this.placedBlocks = (this.placedBlocks + 1) % (this.blockLayers.size() - 1);
            return bl;
        }
    }

    //precomputed list for little performance improvement
    public boolean placeBlocksWithVerification(List<BlockState> states, BlockPos pos) {
        if (this.layerPlace == LayerPlace.RANDOM) {
            return BlockPlaceUtil.setRandomBlockWithVerification(world, this.force, this.blocksToForce, states, pos);
        } else if (this.layerPlace == LayerPlace.NOISE2D) {
            return BlockPlaceUtil.set2dNoiseBlockWithVerification(world, this.force, this.blocksToForce, states, pos, this.noise);
        } else if (this.layerPlace == LayerPlace.NOISE3D) {
            return BlockPlaceUtil.set3dNoiseBlockWithVerification(world, this.force, this.blocksToForce, states, pos, this.noise);
        } else {
            boolean bl = BlockPlaceUtil.setBlockWithOrderWithVerification(world, this.force, this.blocksToForce, states, pos, this.placedBlocks);
            this.placedBlocks = (this.placedBlocks + 1) % (this.blockLayers.size() - 1);
            return bl;
        }
    }

    public boolean verifyBlocks(BlockPos pos) {
        return BlockPlaceUtil.verifyBlock(this.world, this.force, blocksToForce, pos);
    }


    /**
     * change how the blocks are put
     */
    public enum LayersType {
        //more for a natural aspect
        //put the first BlockStates on top of the structure for a coordinate x and z,
        // and until it reaches the depth of the layer
        SURFACE,
        //place the blocks in a sphere shape
        RADIAL,
        //place the blocks in a cylindrical shape
        CYLINDRICAL,
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


}
