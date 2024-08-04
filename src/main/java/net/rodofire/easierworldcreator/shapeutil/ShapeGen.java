package net.rodofire.easierworldcreator.shapeutil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Easierworldcreator;
import net.rodofire.easierworldcreator.util.FastMaths;
import net.rodofire.easierworldcreator.worldgenutil.WorldGenUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class ShapeGen {
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
    private double cosx = 1;
    private double cosx2 = 1;
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
    private Vec3d radialCenterVec3d = this.getPos().toCenterPos();

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
    public ShapeGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, List<BlockLayer> layers, boolean force, List<Block> blocksToForce, int xrotation, int yrotation, int secondxrotation) {
        this.world = world;
        this.pos = pos;
        this.force = force;
        this.blocksToForce = blocksToForce;
        getRotations(xrotation, yrotation, secondxrotation);
    }

    /**
     * init the shape gen
     *
     * @param world the world the shape will be generated
     * @param pos   the pos of the structure center
     */
    public ShapeGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

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
    }

    public LayersType getLayersType() {
        return layersType;
    }

    public void setLayersType(LayersType layersType) {
        this.layersType = layersType;
    }

    /*---------- Layers Related ----------*/

    public void setLayerDirection(Vec3d vect) {
        this.directionalLayerDirection = vect;
    }

    public boolean getForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
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
            List<BlockPos> poslist = firstposlist;
            for (int i = 0; i < this.blockLayers.size(); ++i) {
                if (poslist.isEmpty()) return;
                poslist = this.placeSurfaceBlockLayers(poslist, i);
            }
        } else if (this.layersType == LayersType.RADIAL) {
            this.placeRadialBlocks(firstposlist);
        } else if (this.layersType == LayersType.CYLINDRICAL){
            this.placeCylindricalBlocks(firstposlist);
        }
    }

    //place the layers on the structure
    public List<BlockPos> placeSurfaceBlockLayers(List<BlockPos> poslist, int layerIndex) {
        /*List<BlockPos> newposlist = new ArrayList<BlockPos>();
        for (BlockPos pos : poslist) {
            //verify if the block on top is still in the previous Block Layer
            if(i>0){
                BlockState state = world.getBlockState(pos.up(this.blockLayers.get(i-1).getDepth()));
                if (!WorldGenUtil.isBlockStateInBlockStateList(state, this.blockLayers.get(i-1).getBlockStates())){
                    continue;
                }
            }
            if(WorldGenUtil.verifyBlock(world,force,blocksToForce,blockLayers.get(i).getBlockStates(),pos)){
                newposlist.add(pos);
            }
        }
        return newposlist;*/
        List<BlockPos> newposlist = new ArrayList<>();
        BlockLayer currentLayer = this.blockLayers.get(layerIndex);

        // Precompute the depth and block states of the previous layer if it exists
        int previousLayerDepth = layerIndex > 0 ? this.blockLayers.get(layerIndex - 1).getDepth() : 0;
        Set<BlockState> previousLayerBlockStates = layerIndex > 0 ? new HashSet<>(this.blockLayers.get(layerIndex - 1).getBlockStates()) : Collections.emptySet();

        for (BlockPos pos : poslist) {
            if (layerIndex > 0) {
                BlockState state = world.getBlockState(pos.up(previousLayerDepth));
                if (!previousLayerBlockStates.contains(state)) {
                    continue;
                }
            }

            if (WorldGenUtil.verifyBlock(world, force, blocksToForce, currentLayer.getBlockStates(), pos)) {
                newposlist.add(pos);
            }
        }

        return newposlist;
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
                    WorldGenUtil.verifyBlock(world, force, blocksToForce, blockLayers.get(this.blockLayers.size() - 1).getBlockStates(), pos);
                    bl = true;
                }
                mindist += maxdist;
                maxdist += this.blockLayers.get(a).getDepth();
            }
        }
    }

    public void placeRadialBlocks(List<BlockPos> posList) {
        for (BlockPos pos : posList) {
            float distance = WorldGenUtil.getDistance(radialCenterPos, pos);
            int maxdist = this.blockLayers.get(0).getDepth();
            int mindist = 0;
            int a = 0;
            boolean bl = false;
            while (!(distance < maxdist && mindist < maxdist) || bl) {
                a++;
                if (a >= this.blockLayers.size()) {
                    WorldGenUtil.verifyBlock(world, force, blocksToForce, blockLayers.get(this.blockLayers.size() - 1).getBlockStates(), pos);
                    bl = true;
                }
                mindist += maxdist;
                maxdist += this.blockLayers.get(a).getDepth();
            }
        }
    }

    /*private void placeDirectionalLayers(List<BlockPos> firstposlist) {
        Vec3d direction = this.directionalLayerDirection.normalize();
        List<BlockPos> poslist = new ArrayList<>(firstposlist);

        for (int i = 0; i < this.blockLayers.size(); ++i) {
            if (poslist.isEmpty()) return;
            BlockLayer layer = this.blockLayers.get(i);

            poslist = this.placeDirectionalBlockLayer(poslist, direction, layer.getDepth(), layer.getBlockStates());
        }
    }

    private List<BlockPos> placeDirectionalBlockLayer(List<BlockPos> poslist, Vec3d direction, int depth, List<BlockState> blockStates) {
        List<BlockPos> newposlist = new ArrayList<>();

        for (BlockPos pos : poslist) {
            for (int d = 0; d < depth; ++d) {
                BlockPos targetPos = pos.add(direction.scale(d));
                if (WorldGenUtil.verifyBlock(world, force, blocksToForce, blockStates, targetPos)) {
                    newposlist.add(targetPos);
                }
            }
        }

        return newposlist;
    }*/

    private void placeDirectionalLayers(List<BlockPos> firstposlist) {
        /*Vec3d direction = this.directionalLayerDirection.normalize();
        List<BlockPos> poslist = new ArrayList<>(firstposlist);

        // Trier les positions selon la direction du vecteur
        poslist.sort((pos1, pos2) -> Double.compare(
                pos2.getX() * direction.x + pos2.getY() * direction.y + pos2.getZ() * direction.z,
                pos1.getX() * direction.x + pos1.getY() * direction.y + pos1.getZ() * direction.z
        ));

        int currentLayerIndex = 0;
        int remainingDepth = this.blockLayers.get(currentLayerIndex).getDepth();

        for (BlockPos pos : poslist) {
            if (WorldGenUtil.verifyBlock(world, force, blocksToForce, this.blockLayers.get(currentLayerIndex).getBlockStates(), pos)) {
                remainingDepth--;
                if (remainingDepth <= 0) {
                    currentLayerIndex++;
                    if (currentLayerIndex >= this.blockLayers.size()) {
                        break;
                    }
                    remainingDepth = this.blockLayers.get(currentLayerIndex).getDepth();
                }
            }
        }*/

        Vec3d direction = this.directionalLayerDirection/*.normalize()*/;
        List<BlockPos> poslist = new ArrayList<>(firstposlist);

        // Sort positions according to the directional vector
        poslist.sort(Comparator.comparingDouble(pos -> -pos.getX() * direction.x - pos.getY() * direction.y - pos.getZ() * direction.z));

        BlockPos firstPoint = poslist.get(0);

        int u = 0;
        int a = this.blockLayers.get(u).getDepth();
        for (BlockPos pos : poslist) {
            if (WorldGenUtil.getDistanceFromPointToPlane(direction, firstPoint, pos) < a ) {
                WorldGenUtil.verifyBlock(world, force, blocksToForce, blockLayers.get(u).getBlockStates(), pos);
            }else {
                u++;
                a+=this.blockLayers.get(u).getDepth();
                WorldGenUtil.verifyBlock(world, force, blocksToForce, blockLayers.get(u).getBlockStates(), pos);
            }
        }
    }


    /*---------- Place Structure ----------*/

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

    public void getGenTime(long startTime, boolean place) {
        long endTime = (System.nanoTime());
        long duration = (endTime - startTime) / 1000000;
        if (place) {
            Easierworldcreator.LOGGER.info("structure placing took : {} ms", duration);
        } else {
            Easierworldcreator.LOGGER.info("structure coordinates calculations took : {} ms", duration);
        }
    }

    //change how the blocks are put
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

    public enum PlaceType {
        //place blocks
        BLOCKS,
        //place particles
        PARTICLE
    }


}
