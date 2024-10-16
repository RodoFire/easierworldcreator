package net.rodofire.easierworldcreator.shapeutil;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.util.FastMaths;
import net.rodofire.easierworldcreator.worldgenutil.FastNoiseLite;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class ShapeRotation extends ShapeLayer {

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

    /**
     * init the ShapeRotation
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
     */
    public ShapeRotation(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment, boolean force, List<Block> blocksToForce, LayerPlace layerPlace, LayersType layersType, int xrotation, int yrotation, int secondxrotation) {
        super(world, pos, placeMoment, force, blocksToForce, layerPlace, layersType);
        getRotations(xrotation, yrotation, secondxrotation);
    }

    /**
     * init the ShapeRotation
     *
     * @param world       the world of the shape
     * @param pos         the pos of the shape (usually the center of the structure)
     * @param placeMoment define the moment where the shape will be placed
     */
    public ShapeRotation(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment) {
        super(world, pos, placeMoment);
        getRotations(0, 0, 0);
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

    //calculate the cosinus and the sinus of the rotations
    private void getRotations(int xrotation, int yrotation, int secondxrotation) {
        this.xrotation = xrotation;
        this.yrotation = yrotation;
        this.secondxrotation = secondxrotation;
        this.cosx = FastMaths.getFastCos(xrotation);
        this.cosy = FastMaths.getFastCos(yrotation);
        this.sinx = FastMaths.getFastSin(xrotation);
        this.siny = FastMaths.getFastSin(yrotation);
        this.cosx2 = FastMaths.getFastCos(secondxrotation);
        this.sinx2 = FastMaths.getFastSin(secondxrotation);
        this.setRadialCenterPos(this.getPos());
        this.setRadialCenterVec3d(this.getPos().toCenterPos());
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

}
