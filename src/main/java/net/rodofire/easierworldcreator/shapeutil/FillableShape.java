package net.rodofire.easierworldcreator.shapeutil;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**class to change the filling of the structure
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

    public FillableShape(@NotNull StructureWorldAccess world, @NotNull BlockPos pos) {
        super(world, pos);
    }

    public FillableShape(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, List<BlockLayer> layers, boolean force, List<Block> blocksToForce, int xrotation, int yrotation, int secondxrotation) {
        super(world, pos, layers, force, blocksToForce, xrotation, yrotation, secondxrotation);
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
     *
     * @return the filling type
     */
    public FillableShape.Type getFillingType() {
        return fillingType;
    }

    /**
     *
     * @param fillingType change the fillingtype
     */
    public void setFillingType(FillableShape.Type fillingType) {
        this.fillingType = fillingType;
    }

    /**
     *
     * @return the float of the custom fill
     */
    public float getCustomFill() {
        return customFill;
    }

    /**
     *
     * @param customFill change the custom fill used to change the percentage of the radius that will be filled
     */
    public void setCustomFill(float customFill) {
        this.customFill = customFill;
    }
}
