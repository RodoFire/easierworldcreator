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

    public CylinderGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment, int radius, int height) {
        super(world, pos, placeMoment);
        this.radiusx = radius;
        this.radiusz = radius;
        this.height = height;
    }

    @Deprecated(forRemoval = true)
    /**
     * will be removed and replaced by a more consistent way of placing placemoment
     */
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

        Map<ChunkPos, Set<BlockPos>> chunkMap = new HashMap<>();
        this.setFill();

        //Rotating a shape requires more blocks.
        //This verification is there to avoid some unnecessary calculations when the rotations don't have any impact on the number of blocks
        if (this.getFillingType() == Type.EMPTY) {

            this.generateEmptyCylinder(chunkMap);
            this.setPos(this.getPos().up());

        } else {
            this.generateFullCylinder(chunkMap);
        }

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

            for (float x = -this.radiusx; x <= this.radiusx; x += 1f) {
                float x2 = x * x;
                float xsquared = x2 / radiusxsquared;

                for (float z = -this.radiusz; z <= this.radiusz; z += 1f) {
                    float z2 = z * z;
                    float zsquared = z2 / radiuszsquared;
                    if (xsquared + zsquared <= 1) {
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

                                BlockPos pos = new BlockPos((int) (this.getPos().getX() + x), (int) (this.getPos().getY() + y), (int) (this.getPos().getZ() + z));
                                if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                                    this.biggerThanChunk = true;
                                WorldGenUtil.modifyChunkMap(pos, chunkMap);
                            }
                        }
                    }
                }
            }
        } else {

            for (float x = -this.radiusx; x <= this.radiusx; x += 0.5f) {
                float x2 = x * x;
                float xsquared = x * x / radiusxsquared;
                for (float z = -this.radiusz; z <= this.radiusz; z += 0.5f) {
                    float z2 = z * z;
                    boolean bl = true;
                    if (innerRadiusXSquared != 0) {
                        float innerXSquared = x2 / innerRadiusXSquared;
                        float innerZSquared = z2 / innerRadiusZSquared;
                        if (innerXSquared + innerZSquared <= 1f) { // pas dans l'ovale intérieur
                            bl = false;
                        }
                    }
                    if (bl) {
                        for (float y = 0; y <= this.height; y += 0.5f) {
                            if (xsquared + (z * z) / radiuszsquared <= 1) {

                                BlockPos pos = this.getCoordinatesRotation(x, y, z, this.getPos());
                                if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                                    this.biggerThanChunk = true;
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
                    if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                        this.biggerThanChunk = true;
                    WorldGenUtil.modifyChunkMap(pos, chunkMap);
                }
            }
        } else {
            for (float u = 0; u < 360; u += (float) 35 / Math.max(this.radiusz, this.radiusx)) {
                float x = (float) (radiusx * FastMaths.getFastCos(u));
                float z = (float) (radiusz * FastMaths.getFastSin(u));
                for (float y = 0; y <= this.height; y += 0.5f) {
                    BlockPos pos = this.getCoordinatesRotation(x, 0, z, this.getPos());
                    if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                        this.biggerThanChunk = true;
                    WorldGenUtil.modifyChunkMap(pos, chunkMap);
                }
            }
        }
    }

    @Override
    public List<Vec3d> getVec3d() {
        return List.of();
    }

}
