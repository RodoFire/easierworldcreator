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
import java.util.List;
import java.util.Map;
import java.util.Set;

/*

                                        @@@@@@@@@@@@@@@@@@@@
                                    @@@@@#*++++++++++++++*#@@@@@
                                 @@@%#++++++++++++++++++++++++*%@@@
                                @@#++++++++++++++++++++++++++++++#@@@
                              @@@++++++++++++++++++++++++++++++++++%@@
                              @@++++++++++++++++++++++++++++++++++++%@@
                             @@%++++++++++++++++++++++++++++++++++++*@@
                             @@@++++++++++++++++++++++++++++++++++++%@@
                             @@@@++++++++++++++++++++++++++++++++++%@@@
                             @@@@@#++++++++++++++++++++++++++++++*@@%@@
                             @@@#%@@@*+++++++++++++++++++++++++%@@%#%@@
                             @@%####%@@@%#*++++++++++++++**%@@@%####%@@
                             @@%########%%%@@@@@@@@@@@@@@@%%########%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@@####################################@@
                              @@@##################################@@@
                               @@@%##############################%@@
                                 @@@@%########################%@@@@
                                    @@@@@%################%@@@@@
                                        @@@@@@@@@@@@@@@@@@@@
 */

/**
 * Class to generate Sphere related shapes
 * the methods in this class basically stack multiple circles to generate a cylinder
 * <p> - Since 2.1.0, the shape doesn't return a {@link List<BlockPos>} but it returns instead a {@code List<Set<BlockPos>>}
 * <p> - Before 2.1.0, the BlockPos list was a simple list.
 * <p> - Starting from 2.1.0, the shapes return a list of {@link ChunkPos} that has a set of {@link BlockPos}
 * <p>The change from {@link List} to {@link Set} was done to avoid duplicates BlockPos,
 * which resulted in unnecessary calculations.
 * <p>this allows easy multithreading for the Block assignment
 * done in the {@link AbstractBlockShape} which result in better performance;
 * </p>
 */
@SuppressWarnings("unused")
public class CylinderGen extends AbstractFillableBlockShape {
    private int radiusX;
    private int radiusZ;
    private int height;

    /**
     * init a Cylinder object
     *
     * @param pos     the center of the spiral
     * @param radiusX the radius of the cylinder on the x-axis
     * @param radiusZ the radius of the cylinder on the z-axis
     * @param height  the height of the cylinder
     */
    public CylinderGen(@NotNull BlockPos pos, Rotator rotator, int radiusX, int radiusZ, int height) {
        super(pos, rotator);
        this.radiusX = radiusX;
        this.radiusZ = radiusZ;
        this.height = height;
    }

    /**
     * init a cylinder object
     *
     * @param pos    the center of the spiral
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     */
    public CylinderGen(@NotNull BlockPos pos, int radius, int height) {
        super(pos);
        this.radiusX = radius;
        this.radiusZ = radius;
        this.height = height;
    }


    /**
     * Sets the height of the cylinder.
     *
     * <p>The height defines the vertical size of the cylinder.</p>
     *
     * @param height the height of the cylinder, in units.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Gets the height of the cylinder.
     *
     * <p>The height represents the vertical size of the cylinder.</p>
     *
     * @return the height of the cylinder, in units.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the radius of the cylinder along the X-axis.
     *
     * <p>The radius along the X-axis determines the horizontal size of the cylinder in the X direction.</p>
     *
     * @param radius the radius of the cylinder along the X-axis, in units.
     */
    public void setRadiusX(int radius) {
        this.radiusX = radius;
    }

    /**
     * Gets the radius of the cylinder along the X-axis.
     *
     * <p>The radius along the X-axis determines the horizontal size of the cylinder in the X direction.</p>
     *
     * @return the radius of the cylinder along the X-axis, in units.
     */
    public int getRadiusX() {
        return radiusX;
    }

    /**
     * Sets the radius of the cylinder along the Z-axis.
     *
     * <p>The radius along the Z-axis determines the horizontal size of the cylinder in the Z direction.</p>
     *
     * @param radius the radius of the cylinder along the Z-axis, in units.
     */
    public void setRadiusZ(int radius) {
        this.radiusZ = radius;
    }

    /**
     * Gets the radius of the cylinder along the Z-axis.
     *
     * <p>The radius along the Z-axis determines the horizontal size of the cylinder in the Z direction.</p>
     *
     * @return the radius of the cylinder along the Z-axis, in units.
     */
    public int getRadiusZ() {
        return radiusZ;
    }

    /**
     * Method to get the BlockPos of the shape
     *
     * @return the blockPos divided into chunkPos
     */
    @Override
    public Map<ChunkPos, LongOpenHashSet> getShapeCoordinates() {
        Map<ChunkPos, LongOpenHashSet> chunkMap = new HashMap<>();
        this.setFill();

        if (this.getFillingType() == Type.EMPTY) {
            this.generateEmptyCylinder(chunkMap);
        } else {
            this.generateFullCylinder(chunkMap);
        }

        return chunkMap;
    }

    /**
     * this generates a full cylinder
     *
     * @param chunkMap the Map of ChunkPos that will be converted into {@code List<Set<BlockPos>>}
     */
    public void generateFullCylinder(Map<ChunkPos, LongOpenHashSet> chunkMap) {
        int radiusXSquared = radiusX * radiusX;
        int radiusZSquared = radiusZ * radiusZ;
        float innerRadiusXSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusX * radiusX;
        float innerRadiusZSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusZ * radiusZ;

        //Rotating a shape requires more blocks.
        //This verification is there to avoid some unnecessary calculations when the rotations don't have any impact on the number of blocks
        if (rotator == null) {

            for (float x = -this.radiusX; x <= this.radiusX; x += 1f) {
                float x2 = x * x;
                float xSquared = x2 / radiusXSquared;

                for (float z = -this.radiusZ; z <= this.radiusZ; z += 1f) {
                    float z2 = z * z;
                    float zSquared = z2 / radiusZSquared;
                    if (xSquared + zSquared <= 1) {
                        boolean bl = true;
                        if (innerRadiusXSquared != 0) {
                            float innerXSquared = x2 / innerRadiusXSquared;
                            float innerZSquared = z2 / innerRadiusZSquared;
                            if (innerXSquared + innerZSquared <= 1f) {
                                bl = false;
                            }
                        }
                        if (bl) {
                            for (float y = 0; y <= this.height; y += 1f) {
                                WorldGenUtil.modifyChunkMap(LongPosHelper.encodeBlockPos((int) x, (int) y, (int) z), chunkMap);
                            }
                        }
                    }
                }
            }
        } else {
            for (float x = -this.radiusX; x <= this.radiusX; x += 0.5f) {
                float x2 = x * x;
                float xSquared = x * x / radiusXSquared;
                for (float z = -this.radiusZ; z <= this.radiusZ; z += 0.5f) {
                    float z2 = z * z;
                    boolean bl = true;
                    if (innerRadiusXSquared != 0) {
                        float innerXSquared = x2 / innerRadiusXSquared;
                        float innerZSquared = z2 / innerRadiusZSquared;
                        if (innerXSquared + innerZSquared <= 1f) {
                            bl = false;
                        }
                    }
                    if (bl) {
                        for (float y = 0; y <= this.height; y += 0.5f) {
                            if (xSquared + (z * z) / radiusZSquared <= 1) {
                                WorldGenUtil.modifyChunkMap(rotator.get(x, y, z), chunkMap);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * this generates a full cylinder.
     */
    public void generateEmptyCylinder(Map<ChunkPos, LongOpenHashSet> chunkMap) {
        //Rotating a shape requires more blocks.
        //This verification is there to avoid some unnecessary calculations when the rotations don't have any impact on the number of blocks
        if (rotator == null) {
            for (float u = 0; u < 360; u += (float) 45 / Math.max(this.radiusZ, this.radiusX)) {
                float x = radiusX * FastMaths.getFastCos(u);
                float z = radiusZ * FastMaths.getFastSin(u);
                for (float y = 0; y <= this.height; y += 1f) {
                    WorldGenUtil.modifyChunkMap(LongPosHelper.encodeBlockPos((int) (centerX + x), (int) (centerY + y), (int) (centerZ + z)), chunkMap);
                }
            }
        } else {
            for (float u = 0; u < 360; u += (float) 35 / Math.max(this.radiusZ, this.radiusX)) {
                float x = radiusX * FastMaths.getFastCos(u);
                float z = radiusZ * FastMaths.getFastSin(u);
                for (float y = 0; y <= this.height; y += 0.5f) {
                    WorldGenUtil.modifyChunkMap(rotator.get(x, y, z), chunkMap);
                }
            }
        }
    }


}
