package net.rodofire.easierworldcreator.shapeutil;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import org.jetbrains.annotations.NotNull;

/**
 * class to change the filling of the structure
 * since that all structure may not need or can't have a custom filling like the line generation, it is not implemented in the ShapeGen class
 */
@SuppressWarnings("unused")
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
     * init the ShapeFilling
     *
     * @param world           the world the spiral will spawn in
     * @param pos             the center of the spiral
     * @param placeMoment     define the moment where the shape will be placed
     * @param layerPlace      how the {@code BlockStates} inside of a {@link BlockLayer} will be placed
     * @param layersType      how the Layers will be placed
     * @param xRotation       first rotation around the x-axis
     * @param yRotation       second rotation around the y-axis
     * @param secondXRotation last rotation around the x-axis
     * @param featureName     the name of the feature
     */
    public FillableShape(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment, LayerPlace layerPlace, LayersType layersType, int xRotation, int yRotation, int secondXRotation, String featureName) {
        super(world, pos, placeMoment, layerPlace, layersType, xRotation, yRotation, secondXRotation, featureName);
    }

    /**
     * init the ShapeFilling
     *
     * @param world       the world of the shape
     * @param pos         the pos of the shape (usually the center of the structure)
     * @param placeMoment define the moment where the shape will be placed
     */
    public FillableShape(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment) {
        super(world, pos, placeMoment);
    }

    /**
     * change how the structure is filled
     */
    public enum Type {
        /**
         * will only generate the outline
         */
        EMPTY,
        /**
         * will generate an outline large of 50% the radius
         */
        HALF,
        /**
         * will entirely fill the circle
         */
        FULL,
        /**
         * Set custom filling type. It must be associated with a customFill float.
         */
        CUSTOM
    }

    /*----------- FillingType Related -----------*/

    /**
     * method to get the filling Type
     *
     * @return the filling type
     */
    public FillableShape.Type getFillingType() {
        return fillingType;
    }

    /**
     * method to change the filling Type
     *
     * @param fillingType change the fillingType
     */
    public void setFillingType(FillableShape.Type fillingType) {
        this.fillingType = fillingType;
    }

    /**
     * method to get the custom fill
     *
     * @return the float of the custom fill
     */
    public float getCustomFill() {
        return customFill;
    }

    /**
     * method to set the custom fill
     *
     * @param customFill change the custom fill used to change the percentage of the radius that will be filled
     */
    public void setCustomFill(float customFill) {
        this.customFill = customFill;
    }

    /**
     * method to set the custom fill
     * set the filling value depending on the filling type
     */
    protected void setFill() {
        if (this.fillingType == FillableShape.Type.HALF) {
            this.customFill = 0.5f;
        }
        if (this.fillingType == FillableShape.Type.FULL) {
            this.customFill = 1.0f;
        }
        if (this.getCustomFill() > 1f) this.customFill = 1f;
        if (this.getCustomFill() < 0f) this.customFill = 0f;
    }
}
