package net.rodofire.easierworldcreator.shapeutil;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.util.FastMaths;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class ShapeRotation extends ShapeLayer {

    //These are rotations in degrees (0-360).
    //These 3 are used to represent every rotation possible in a 3d world
    private int xRotation = 0;
    private int yRotation = 0;
    private int secondXRotation = 0;

    //precalculated cos and sin table for every rotation
    private double cosX2 = 1;
    private double cosX = 1;
    private double cosY = 1;
    private double sinX = 0;
    private double sinX2 = 0;
    private double sinY = 0;

    /**
     * init the ShapeRotation
     *
     * @param world           the world the spiral will spawn in
     * @param pos             the center of the spiral
     * @param placeMoment     define the moment where the shape will be placed
     * @param layerPlace      how the {@code @BlockStates} inside of a {@link BlockLayer} will be placed
     * @param layersType      how the Layers will be placed
     * @param xRotation       first rotation around the x-axis
     * @param yRotation       second rotation around the y-axis
     * @param secondXRotation last rotation around the x-axis
     */
    public ShapeRotation(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment, LayerPlace layerPlace, LayersType layersType, int xRotation, int yRotation, int secondXRotation) {
        super(world, pos, placeMoment, layerPlace, layersType);
        getRotations(xRotation, yRotation, secondXRotation);
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
    public int getXRotation() {
        return xRotation;
    }

    public void setXRotation(int xRotation) {
        this.xRotation = xRotation;
        getRotations(this.xRotation, this.yRotation, this.secondXRotation);
    }

    public int getYRotation() {
        return yRotation;
    }

    public void setYRotation(int yRotation) {
        this.yRotation = yRotation;
        getRotations(this.xRotation, this.yRotation, this.secondXRotation);
    }

    public int getSecondXRotation() {
        return secondXRotation;
    }

    public void setSecondXRotation(int secondXRotation) {
        this.secondXRotation = secondXRotation;
        getRotations(this.xRotation, this.yRotation, this.secondXRotation);
    }

    public void addXRotation(int xRotation) {
        this.xRotation += xRotation;
        getRotations(this.xRotation, this.yRotation, this.secondXRotation);
    }

    public void addYRotation(int yRotation) {
        this.yRotation += yRotation;
        getRotations(this.xRotation, this.yRotation, this.secondXRotation);
    }

    public void addSecondXRotation(int secondXRotation) {
        this.secondXRotation += secondXRotation;
        getRotations(this.xRotation, this.yRotation, this.secondXRotation);
    }

    //calculate the cosines and the sinus of the rotations
    private void getRotations(int xRotation, int yRotation, int secondXRotation) {
        this.xRotation = xRotation;
        this.yRotation = yRotation;
        this.secondXRotation = secondXRotation;
        this.cosX = FastMaths.getFastCos(xRotation);
        this.cosY = FastMaths.getFastCos(yRotation);
        this.sinX = FastMaths.getFastSin(xRotation);
        this.sinY = FastMaths.getFastSin(yRotation);
        this.cosX2 = FastMaths.getFastCos(secondXRotation);
        this.sinX2 = FastMaths.getFastSin(secondXRotation);
        this.setRadialCenterPos(this.getPos());
        this.setRadialCenterVec3d(this.getPos().toCenterPos());
    }

    public BlockPos getCoordinatesRotation(Vec3d pos, BlockPos centerPos) {
        return getCoordinatesRotation((float) pos.getX(), (float) pos.getY(), (float) pos.getZ(), centerPos);
    }

    //return a BlockPos of a block after rotating it
    public BlockPos getCoordinatesRotation(float x, float y, float z, BlockPos pos) {
        // first x rotation
        float y_rot1 = (float) (y * cosX - z * sinX);
        float z_rot1 = (float) (y * sinX + z * cosX);

        // y rotation
        float x_rot_z = (float) (x * cosY - y_rot1 * sinY);
        float y_rot_z = (float) (x * sinY + y_rot1 * cosY);

        // second x rotation
        float y_rot2 = (float) (y_rot_z * cosX2 - z_rot1 * sinX2);
        float z_rot2 = (float) (y_rot_z * sinX2 + z_rot1 * cosX2);

        return new BlockPos(new BlockPos.Mutable().set(pos, (int) x_rot_z, (int) y_rot2, (int) z_rot2));
    }

    public List<BlockPos> getCoordinatesRotationList(List<Vec3d> poslist, BlockPos centerPos) {
        List<BlockPos> newposlist = new ArrayList<>();
        for (Vec3d pos : poslist) {
            newposlist.add(this.getCoordinatesRotation(pos, centerPos));
        }
        return newposlist;
    }

}
