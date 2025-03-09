package net.rodofire.easierworldcreator.shape.block.gen;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;
import net.rodofire.easierworldcreator.maths.FastMaths;
import net.rodofire.easierworldcreator.shape.block.instanciator.AbstractBlockShape;
import net.rodofire.easierworldcreator.shape.block.instanciator.AbstractFillableBlockShape;
import net.rodofire.easierworldcreator.shape.block.layer.LayerManager;
import net.rodofire.easierworldcreator.shape.block.placer.ShapePlacer;
import net.rodofire.easierworldcreator.shape.block.rotations.Rotator;
import net.rodofire.easierworldcreator.util.LongPosHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
/*



                                             .:::::::..
                                         .::............::.
                                       ::..................::
                                     .:......................:.
                                    :..........................:
                                   -............................:
                                   ..............................
                                  -..............................:
                                  =..............................-
                                  =..............................-
                                  -..............................:
                                   .............................:
                                   -............................:
                                    :..........................:
                                     .:......................:.
                                       ::..................::
                                          ::............::
       */

/**
 * Class to generate circle related shapes
 * <br>
 * The Main purpose of this class is to generate the coordinates based on a shape.
 * The coordinates are organized depending on a {@code Map<ChunkPos, LongOpenHashSet>}.
 * <p>It emply some things:
 * <ul>
 *     <li>The coordinates are divided in chunk</li>
 *     <li>It uses {@link LongOpenHashSet} for several reasons.
 *     <ul>
 *     <li>First, We use a set to avoid doing unnecessary calculations on the shape. It ensures that no duplicate is present.
 *     <li>Second, it compresses the BlockPos: The {@link BlockPos} are saved under long using {@link LongPosHelper}.
 *     It saves some memory since that we save four bytes of data for each {@link BlockPos},
 *     and there should not have overhead since that we use primitive data type.
 *     <li>Third, since that we use primitive data types and that they take less memory,
 *     coordinate generation, accession or deletion is much faster than using a {@code Set<BlockPos>}.
 *     Encoding and decoding blockPos and then adding it into {@link LongOpenHashSet}is extremely faster
 *     compared to only adding a {@link BlockPos}.
 *     ~60- 70% facter.
 *     </ul>
 *     </li>
 * </ul>
 * <p>Dividing Coordinates into Chunk has some advantages :
 * <ul>
 *     <li> allow a multithreaded block assignement when using {@link LayerManager}
 *     <li> allow to be used during WG, when using {@link DividedBlockListManager} or when placing using {@link ShapePlacer}
 * </ul>
 */
@SuppressWarnings("unused")
public class CircleGen extends AbstractFillableBlockShape {
    private int radiusX;
    private int radiusZ;


    /**
     * init the Circle Shape
     *
     * @param pos     the center of the spiral
     * @param radiusX the radius of the x-axis
     * @param radiusZ the radius of the z-axis
     */
    public CircleGen(@NotNull BlockPos pos, Rotator rotator, int radiusX, int radiusZ) {
        super(pos, rotator);
        this.radiusX = radiusX;
        this.radiusZ = radiusZ;
    }

    /**
     * init a circle generator
     *
     * @param pos    the center of the spiral
     * @param radius the radius of the x-axis
     */
    public CircleGen(@NotNull BlockPos pos, int radius) {
        super(pos);
        this.radiusX = radius;
        this.radiusZ = radius;
    }


    /*---------- Radius Related ----------*/

    /**
     * method to get the radius of the circle
     *
     * @return the radius of the circle on the x-axis
     */
    public int getRadiusX() {
        return radiusX;
    }

    /**
     * method to set the radius of the circle
     *
     * @param radiusX the radius that will be set on the x-axis
     */
    public void setRadiusX(int radiusX) {
        this.radiusX = radiusX;
    }

    /**
     * method to get the radius of the circle
     *
     * @return the radius of the circle on the z-axis
     */
    public int getRadiusZ() {
        return radiusZ;
    }

    /**
     * method to set the radius of the circle
     *
     * @param radiusZ the radius that will be set on the z-axis
     */
    public void setRadiusZ(int radiusZ) {
        this.radiusZ = radiusZ;
    }

    /**
     * method to add the radius of the circle
     *
     * @param radiusX the radius that will be added on the x-axis
     */
    public void addRadiusX(int radiusX) {
        this.radiusX += radiusX;
    }

    /**
     * method to add the radius of the circle
     *
     * @param radiusZ the radius that will be added on the z-axis
     */
    public void addRadiusY(int radiusZ) {
        this.radiusZ += radiusZ;
    }

    /*---------- Place Structure ----------*/

    /**
     * method to get all the pos of the circle
     *
     * @return the blockPos of the circle. The List is divided into chunkPos, allowing for parallel modification
     */
    @Override
    public Map<ChunkPos, LongOpenHashSet> getShapeCoordinates() {
        if (this.getFillingType() == AbstractFillableBlockShape.Type.HALF) {
            this.setCustomFill(0.5f);
        }
        if (this.getCustomFill() > 1f) this.setCustomFill(1f);
        if (this.getCustomFill() < 0f) this.setCustomFill(0f);

        if (this.getFillingType() == AbstractFillableBlockShape.Type.EMPTY) {
            this.generateEmptyOval();
        }else{
            this.generateFullOval();
        }
        return chunkMap;
    }

    /**
     * method to create a full oval/ with custom filling
     *
     */
    public void generateFullOval() {

        int radiusXSquared = radiusX * radiusX;
        int radiusZSquared = radiusZ * radiusZ;
        float innerRadiusXSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusX * radiusX;
        float innerRadiusZSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusZ * radiusZ;

        //Rotating a shape requires more blocks.
        //This verification is there to avoid some unnecessary calculations when the rotations don't have any impact on the number of blocks
        if (rotator == null) {
            for (float x = -this.radiusX; x <= this.radiusX; x += 1) {
                float x2 = x * x;
                float xSquared = x * x / radiusXSquared;
                for (float z = -this.radiusZ; z <= this.radiusZ; z += 1) {
                    float z2 = z * z;

                    if (xSquared + (z2) / radiusZSquared <= 1) {
                        boolean bl = true;
                        if (innerRadiusXSquared != 0) {
                            float innerXSquared = x2 / innerRadiusXSquared;
                            float innerZSquared = z2 / innerRadiusZSquared;
                            if (innerXSquared + innerZSquared <= 1f) {
                                bl = false;
                            }
                        }
                        if (bl) {
                            modifyChunkMap(LongPosHelper.encodeBlockPos((int) (centerX + x), centerY, (int) (centerZ + z)));
                        }
                    }
                }
            }
        } else {
            for (float x = -this.radiusX; x <= this.radiusX; x += 0.5f) {
                float x2 = x * x;
                float xSquared = x2 / radiusXSquared;

                for (float z = -this.radiusZ; z <= this.radiusZ; z += 0.5f) {
                    float z2 = z * z;
                    if (xSquared + (z2) / radiusZSquared <= 1) {
                        boolean bl = true;
                        if (innerRadiusXSquared != 0) {
                            float innerXSquared = x2 / innerRadiusXSquared;
                            float innerZSquared = z2 / innerRadiusZSquared;
                            if (innerXSquared + innerZSquared <= 1f) {
                                bl = false;
                            }
                        }
                        if (bl) {
                            modifyChunkMap(rotator.get(x, 0, z));
                        }
                    }
                }
            }
        }
    }

    /**
     * method to create an empty oval with rotations
     */
    public void generateEmptyOval() {
        //Rotating a shape requires more blocks.
        //This verification is there to avoid some unnecessary calculations when the rotations don't have any impact on the number of blocks
        if (rotator == null) {
            for (float u = 0; u < 360; u += (float) 45 / Math.max(this.radiusZ, this.radiusX)) {
                float x = radiusX * FastMaths.getFastCos(u);
                float z = radiusZ * FastMaths.getFastSin(u);
                modifyChunkMap(LongPosHelper.encodeBlockPos((int) (x + centerX),  centerY, (int) (z+ centerZ)));
            }
        } else {
            for (float u = 0; u < 360; u += (float) 35 / Math.max(this.radiusZ, this.radiusX)) {
                float x = radiusX * FastMaths.getFastCos(u);
                float z = radiusZ * FastMaths.getFastSin(u);

                modifyChunkMap(rotator.get(x, 0, z));
            }
        }
    }
}
