package net.rodofire.easierworldcreator.shape.block.gen;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.rodofire.easierworldcreator.maths.FastMaths;
import net.rodofire.easierworldcreator.shape.block.instanciator.AbstractBlockShape;
import net.rodofire.easierworldcreator.shape.block.instanciator.AbstractFillableBlockShape;
import net.rodofire.easierworldcreator.shape.block.rotations.Rotator;
import net.rodofire.easierworldcreator.util.LongPosHelper;
import net.rodofire.easierworldcreator.util.WorldGenUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
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
 * Class to generate Circle related shapes
 * <p>
 * Coordinates are organized as {@code Map<ChunkPos, LongOpenHashSet>}.
 * The {@link LongOpenHashSet} contains the compressed {@link BlockPos}, allowing for better performance and less memory usage.
 * The coordinates are also divided by ChunkPos, allowing easy multithreading for the Block assignment done in the {@link AbstractBlockShape} which result in better performance;
 * </p>
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
            return this.generateEmptyOval();
        }
        return this.generateFullOval();
    }

    /**
     * method to create a full oval/ with custom filling
     *
     * @return {@code List<Set<BlockPos>>} : set of BlockPos divided into a list of chunks
     */
    public Map<ChunkPos, LongOpenHashSet> generateFullOval() {
        Map<ChunkPos, LongOpenHashSet> chunkMap = new HashMap<>();

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
                            WorldGenUtil.modifyChunkMap(LongPosHelper.encodeBlockPos((int) (centerX + x), centerY, (int) (centerZ + z)), chunkMap);
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
                            WorldGenUtil.modifyChunkMap(rotator.get(x, 0, z), chunkMap);
                        }
                    }
                }
            }
        }

        return chunkMap;
    }

    /**
     * method to create an empty oval with rotations
     *
     * @return {@code List<Set<BlockPos>>} : set of BlockPos divided into a list of chunks
     */
    public Map<ChunkPos, LongOpenHashSet> generateEmptyOval() {
        Map<ChunkPos, LongOpenHashSet> chunkMap = new HashMap<>();

        //Rotating a shape requires more blocks.
        //This verification is there to avoid some unnecessary calculations when the rotations don't have any impact on the number of blocks
        if (rotator == null) {
            for (float u = 0; u < 360; u += (float) 45 / Math.max(this.radiusZ, this.radiusX)) {
                float x = radiusX * FastMaths.getFastCos(u);
                float z = radiusZ * FastMaths.getFastSin(u);
                WorldGenUtil.modifyChunkMap(LongPosHelper.encodeBlockPos((int) (x + centerX),  centerY, (int) (z+ centerZ)), chunkMap);
            }
        } else {
            for (float u = 0; u < 360; u += (float) 35 / Math.max(this.radiusZ, this.radiusX)) {
                float x = radiusX * FastMaths.getFastCos(u);
                float z = radiusZ * FastMaths.getFastSin(u);

                WorldGenUtil.modifyChunkMap(
                        rotator.get(x, 0, z),
                        chunkMap)
                ;
            }
        }
        return chunkMap;
    }
}
