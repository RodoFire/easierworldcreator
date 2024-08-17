package net.rodofire.easierworldcreator.shapegen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.shapeutil.FillableShape;
import net.rodofire.easierworldcreator.shapeutil.Shape;
import net.rodofire.easierworldcreator.util.FastMaths;
import net.rodofire.easierworldcreator.worldgenutil.WorldGenUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/*

Characters

100
Brightness

100%
Contrast

100%
Saturation

100%
Hue

0°
Grayscale

0%
Sepia

0%
Invert Colors

0%
 Thresholding

128
 Sharpness

9
 Edge Detection

1
ASCII gradient

Normal
Space Density

1
Quality Enhancements

None
Transparent frame

0px

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
 * <p> - Starting from 2.1.0, the shapes returns a list of {@link ChunkPos} that has a set of {@link BlockPos}
 * <p>The change from {@link List} to {@link Set} was done to avoid duplicates BlockPos wich resulted in unnecessary calculations.
 * <p>this allow easy multithreading for the Block assignment done in the {@link Shape} which result in better performance;
 * </p>
 */
public class CylinderGen extends FillableShape {
    private int radiusx;
    private int radiusz;
    private int height;


    public CylinderGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, int radius, int height, PlaceMoment placeMoment) {
        super(world, pos, placeMoment);
        this.radiusx = radius;
        this.radiusz = radius;
        this.height = height;
    }


    /*---------- Height Related ----------*/
    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    /*---------- Radius Related ---------*/
    public void setRadiusX(int radius) {
        this.radiusx = radius;
    }

    public int getRadiusX() {
        return radiusx;
    }

    public void setRadiusZ(int radius) {
        this.radiusz = radius;
    }

    public int getRadiusZ() {
        return radiusz;
    }

    @Override
    public List<Set<BlockPos>> getBlockPos() {
        return this.generateCylinder();
    }

    public List<Set<BlockPos>> generateCylinder() {
        long startTimeCartesian = System.nanoTime();
        Map<ChunkPos, Set<BlockPos>> chunkMap = new HashMap<>();
        this.setFill();

        //Rotating a shape requires more blocks.
        //This verification is there to avoid some unnecessary calculations when the rotations don't have any impact on the number of blocks
        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() == 0 && (this.getFillingType() == FillableShape.Type.FULL || this.getFillingType() == FillableShape.Type.EMPTY)) {
            for (int i = 0; i <= height; i++) {
                this.generateFatsOval(this.getPos().getX(), this.getPos().getZ(), this.getPos().getY(), chunkMap);
                this.setPos(this.getPos().up());
            }
        } else if (this.getFillingType() == FillableShape.Type.EMPTY) {

            this.generateEmptyCylinder(chunkMap);
            this.setPos(this.getPos().up());

        } else {

            this.generateFullCylinder(chunkMap);
            //this.setPos(this.getPos().up());

            //poslist.addAll(this.getCoordinatesRotationList(veclist, this.getPos()));
        }
        this.getGenTime(startTimeCartesian, false);
        return new ArrayList<>(chunkMap.values());
    }

    /**
     * this generates a full cylinder
     */
    public void generateFullCylinder(Map<ChunkPos, Set<BlockPos>> chunkMap) {
        int radiusxsquared = radiusx * radiusx;
        int radiuszsquared = radiusz * radiusz;
        float innerRadiusXSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusx * radiusx;
        float innerRadiusZSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusz * radiusz;

        //Rotating a shape requires more blocks.
        //This verification is there to avoid some unnecessary calculations when the rotations don't have any impact on the number of blocks
        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() % 180 == 0) {
            for (float y = 0; y <= this.height; y += 1f) {
                for (float x = -this.radiusx; x <= this.radiusx; x += 1f) {
                    float xsquared = x * x / radiusxsquared;
                    for (float z = -this.radiusz; z <= this.radiusz; z += 1f) {
                        if (xsquared + (z * z) / radiuszsquared <= 1) {
                            boolean bl = true;
                            if (innerRadiusXSquared != 0) {
                                float innerXSquared = x * x / innerRadiusXSquared;
                                float innerZSquared = z * z / innerRadiusZSquared;
                                if (innerXSquared + innerZSquared <= 1f) { // pas dans l'ovale intérieur
                                    bl = false;
                                }
                            }
                            if (bl) {
                                BlockPos pos = new BlockPos((int) (this.getPos().getX() + x), this.getPos().getY(), (int) (this.getPos().getZ() + z));
                                WorldGenUtil.modifyChunkMap(pos, chunkMap);
                            }
                        }
                    }
                }
            }
        } else {
            for (float y = 0; y <= this.height; y += 0.5f) {
                for (float x = -this.radiusx; x <= this.radiusx; x += 0.5f) {
                    float xsquared = x * x / radiusxsquared;
                    for (float z = -this.radiusz; z <= this.radiusz; z += 0.5f) {
                        if (xsquared + (z * z) / radiuszsquared <= 1) {
                            boolean bl = true;
                            if (innerRadiusXSquared != 0) {
                                float innerXSquared = x * x / innerRadiusXSquared;
                                float innerZSquared = z * z / innerRadiusZSquared;
                                if (innerXSquared + innerZSquared <= 1f) { // pas dans l'ovale intérieur
                                    bl = false;
                                }
                            }
                            if (bl) {
                                BlockPos pos = this.getCoordinatesRotation(x, y, z, this.getPos());
                                WorldGenUtil.modifyChunkMap(pos, chunkMap);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * this generates a full cylinder
     */
    public void generateEmptyCylinder(Map<ChunkPos, Set<BlockPos>> chunkMap) {
        //Rotating a shape requires more blocks.
        //This verification is there to avoid some unnecessary calculations when the rotations don't have any impact on the number of blocks
        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() == 0) {
            for (float u = 0; u < 360; u += (float) 45 / Math.max(this.radiusz, this.radiusx)) {
                float x = (float) (radiusx * FastMaths.getFastCos(u));
                float z = (float) (radiusz * FastMaths.getFastSin(u));
                for (float y = 0; y <= this.height; y += 1f) {
                    BlockPos pos = new BlockPos((int) (this.getPos().getX() + x), this.getPos().getY(), (int) (this.getPos().getZ() + z));
                    WorldGenUtil.modifyChunkMap(pos, chunkMap);
                }
            }
        } else {
            for (float u = 0; u < 360; u += (float) 35 / Math.max(this.radiusz, this.radiusx)) {
                float x = (float) (radiusx * FastMaths.getFastCos(u));
                float z = (float) (radiusz * FastMaths.getFastSin(u));
                for (float y = 0; y <= this.height; y += 0.5f) {
                    BlockPos pos = this.getCoordinatesRotation(x, 0, z, this.getPos());
                    WorldGenUtil.modifyChunkMap(pos, chunkMap);
                }
            }
        }
    }


    /*---------- Algorithm based on Bressen Algorithms for circle ----------*/

    /**
     * this class is used when no rotation is present. This allow fast coordinates generation but don't work with rotations
     *
     * @param centerX  the x coordinate of the center of the circle
     * @param centerZ  the z coordinate of the center of the circle
     * @param y        the height of the circle
     * @param chunkMap the Map used to return the positions
     */
    public void generateFatsOval(int centerX, int centerZ, int y, Map<ChunkPos, Set<BlockPos>> chunkMap) {
        int x = 0;
        int z = this.radiusz;
        int twoASquare = 2 * this.radiusx * this.radiusx;
        int twoBSquare = 2 * this.radiusz * this.radiusz;
        int decision1 = (int) (this.radiusz * this.radiusz - this.radiusx * this.radiusx * this.radiusz + 0.25 * this.radiusx * this.radiusx);
        int dx = twoBSquare * x;
        int dz = twoASquare * z;


        // Région 1
        while (dx < dz) {
            if (this.getFillingType() == Type.FULL) {
                placeFullOval(centerX, centerZ, y, x, z, chunkMap);
            } else {
                addOvalBlocks(centerX, centerZ, x, y, z, chunkMap);
            }
            if (decision1 < 0) {
                x++;
                dx = dx + twoBSquare;
                decision1 = decision1 + dx + this.radiusz * this.radiusz;
            } else {
                x++;
                z--;
                dx = dx + twoBSquare;
                dz = dz - twoASquare;
                decision1 = decision1 + dx - dz + this.radiusz * this.radiusz;
            }
        }

        // Région 2
        int decision2 = (int) (this.radiusz * this.radiusz * (x + 0.5) * (x + 0.5) + this.radiusx * this.radiusx * (z - 1) * (z - 1) - this.radiusx * this.radiusx * this.radiusz * this.radiusz);
        while (z >= 0) {
            if (this.getFillingType() == Type.FULL) {
                placeFullOval(centerX, centerZ, y, x, z, chunkMap);
            } else {
                addOvalBlocks(centerX, centerZ, x, y, z, chunkMap);
            }
            if (decision2 > 0) {
                z--;
                dz = dz - twoASquare;
                decision2 = decision2 + this.radiusx * this.radiusx - dz;
            } else {
                z--;
                x++;
                dx = dx + twoBSquare;
                dz = dz - twoASquare;
                decision2 = decision2 + dx - dz + this.radiusx * this.radiusx;
            }
        }
    }

    /**
     * Adds block positions to the chunkMap based on the given coordinates.
     *
     * @param centerX  The x-coordinate of the center of the oval
     * @param centerZ  The z-coordinate of the center of the oval
     * @param x        The x-coordinate in the context of the Bresenham algorithm
     * @param y        The height of the oval
     * @param z        The z-coordinate in the context of the Bresenham algorithm
     * @param chunkMap The map of chunks with the block positions
     */
    public void addOvalBlocks(int centerX, int centerZ, int x, int y, int z, Map<ChunkPos, Set<BlockPos>> chunkMap) {
        if (this.getYrotation() % 180 == 0) {
            BlockPos[] positions = {
                    new BlockPos(centerX + x, y, centerZ + z),
                    new BlockPos(centerX + x, y, centerZ - z),
                    new BlockPos(centerX - x, y, centerZ + z),
                    new BlockPos(centerX - x, y, centerZ - z)
            };
            for (BlockPos pos : positions) {
                WorldGenUtil.modifyChunkMap(pos, chunkMap);
            }
        }
    }

    /**
     * Fills in the lines between the blocks for a complete oval.
     *
     * @param centerX  The x-coordinate of the center of the oval
     * @param centerZ  The z-coordinate of the center of the oval
     * @param x        The x-coordinate in the context of the Bresenham algorithm
     * @param y        The height of the oval
     * @param z        The z-coordinate in the context of the Bresenham algorithm
     * @param chunkMap The map of chunks with the block positions
     */
    public void placeFullOval(int centerX, int centerZ, int x, int y, int z, Map<ChunkPos, Set<BlockPos>> chunkMap) {
        BlockPos start1 = new BlockPos(centerX + x, y, centerZ + z);
        BlockPos start2 = new BlockPos(centerX - x, y, centerZ + z);

        if (this.getYrotation() % 180 == 0) {
            for (int i = 0; i <= 2 * z; i++) {
                BlockPos pos1 = new BlockPos(start1.getX(), start1.getY(), start1.getZ() - i);
                BlockPos pos2 = new BlockPos(start2.getX(), start2.getY(), start2.getZ() - i);
                WorldGenUtil.modifyChunkMap(pos1, chunkMap);
                WorldGenUtil.modifyChunkMap(pos2, chunkMap);
            }
        }
    }

    @Override
    public List<Vec3d> getVec3d() {
        return List.of();
    }
}
