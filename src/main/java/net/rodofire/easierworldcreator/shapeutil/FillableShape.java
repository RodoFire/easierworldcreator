package net.rodofire.easierworldcreator.shapeutil;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * class to change the filling of the structure
 * since that all structure may not need or can't have a custom filling like the line generation, it is not implemented in the ShapeGen class
 */
public abstract class FillableShape extends Shape {
    /**
     * if ==0, there will be no circle
     * if ==1f, it will be a full circle
     * Don't need to care if
     *
     * @see FillableShape is not set on CUSTOM
     **/
    float customFill = 1f;

    /**
     * set the default filling type
     */
    FillableShape.Type fillingType = FillableShape.Type.FULL;

    /**
     * @param world the world of the shape
     * @param pos   the pos of the shape (usually the center of the structure)
     */
    public FillableShape(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment) {
        super(world, pos, placeMoment);
    }

    /**
     * @param world           the world of the shape
     * @param pos             ths pos of the shape (usually the center of the structure)
     * @param layers          list of blockLayers that will be placed in the world
     * @param force           boolean to force or not the pos of the blocks
     * @param blocksToForce   list of block that the shape can still replace when force = false
     * @param xrotation       first rotation around the x-axis
     * @param yrotation       second rotation around the y-axis
     * @param secondxrotation last rotation around the x-axis
     */
    public FillableShape(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment, List<BlockLayer> layers, boolean force, List<Block> blocksToForce, int xrotation, int yrotation, int secondxrotation) {
        super(world, pos, placeMoment, layers, force, blocksToForce, xrotation, yrotation, secondxrotation);
    }

    /**
     * change how the structure is filled
     */
    public enum Type {
        //will only generate the outline
        EMPTY,
        //will generate an outline large of 50% the radius
        HALF,
        //will entirely fill the circle
        FULL,
        //Set custom filling type. It must be associated with a customfill float.
        CUSTOM
    }

    /*----------- FillingType Related -----------*/

    /**
     * @return the filling type
     */
    public FillableShape.Type getFillingType() {
        return fillingType;
    }

    /**
     * @param fillingType change the fillingtype
     */
    public void setFillingType(FillableShape.Type fillingType) {
        this.fillingType = fillingType;
    }

    /**
     * @return the float of the custom fill
     */
    public float getCustomFill() {
        return customFill;
    }

    /**
     * @param customFill change the custom fill used to change the percentage of the radius that will be filled
     */
    public void setCustomFill(float customFill) {
        this.customFill = customFill;
    }

    /**
     * set the filling value depending on the filling type
     */
    protected void setFill() {
        if (this.getFillingType() == FillableShape.Type.HALF) {
            this.setCustomFill(0.5f);
        }
        if (this.getFillingType() == Type.FULL) {
            this.setCustomFill(1.0f);
        }
        if (this.getCustomFill() > 1f) this.setCustomFill(1f);
        if (this.getCustomFill() < 0f) this.setCustomFill(0f);
    }
}
