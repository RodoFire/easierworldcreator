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
 * <p>The change from {@link List} to {@link Set} was done to avoid duplicates BlockPos which resulted in unnecessary calculations.
 * <p>this allow easy multithreading for the Block assignment done in the {@link Shape} which result in better performance;
 * </p>
 */
@SuppressWarnings("unused")
public class CylinderGen extends FillableShape {
    private int radiusX;
    private int radiusZ;
    private int height;

    /**
     * @param world           the world the spiral will spawn in
     * @param pos             the center of the spiral
     * @param placeMoment     define the moment where the shape will be placed
     * @param layerPlace      how the {@code @BlockStates} inside of a {@code BlockLayer} will be placed
     * @param layersType      how the Layers will be placed
     * @param xRotation       first rotation around the x-axis
     * @param yRotation       second rotation around the y-axis
     * @param secondXRotation last rotation around the x-axis
     * @param featureName     the name of the feature
     * @param radiusX         the radius of the cylinder on the x-axis
     * @param radiusZ         the radius of the cylinder on the z-axis
     * @param height          the height of the cylinder
     */
    public CylinderGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment, LayerPlace layerPlace, LayersType layersType, int xRotation, int yRotation, int secondXRotation, String featureName, int radiusX, int radiusZ, int height) {
        super(world, pos, placeMoment, layerPlace, layersType, xRotation, yRotation, secondXRotation, featureName);
        this.radiusX = radiusX;
        this.radiusZ = radiusZ;
        this.height = height;
    }

    /**
     * @param world       the world the spiral will spawn in
     * @param pos         the center of the spiral
     * @param placeMoment define the moment where the shape will be placed
     * @param radius      the radius of the cylinder
     * @param height      the height of the cylinder
     */
    public CylinderGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment, int radius, int height) {
        super(world, pos, placeMoment);
        this.radiusX = radius;
        this.radiusZ = radius;
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
        this.radiusX = radius;
    }

    public int getRadiusX() {
        return radiusX;
    }

    public void setRadiusZ(int radius) {
        this.radiusZ = radius;
    }

    public int getRadiusZ() {
        return radiusZ;
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
        int radiusXSquared = radiusX * radiusX;
        int radiusZSquared = radiusZ * radiusZ;
        float innerRadiusXSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusX * radiusX;
        float innerRadiusZSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusZ * radiusZ;

        //Rotating a shape requires more blocks.
        //This verification is there to avoid some unnecessary calculations when the rotations don't have any impact on the number of blocks
        if (this.getXRotation() % 180 == 0 && this.getYRotation() % 180 == 0 && this.getSecondXRotation() % 180 == 0) {

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
        if (this.getXRotation() % 180 == 0 && this.getYRotation() % 180 == 0 && this.getSecondXRotation() == 0) {
            for (float u = 0; u < 360; u += (float) 45 / Math.max(this.radiusZ, this.radiusX)) {
                float x = (float) (radiusX * FastMaths.getFastCos(u));
                float z = (float) (radiusZ * FastMaths.getFastSin(u));
                for (float y = 0; y <= this.height; y += 1f) {
                    BlockPos pos = new BlockPos((int) (this.getPos().getX() + x), this.getPos().getY(), (int) (this.getPos().getZ() + z));
                    if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                        this.biggerThanChunk = true;
                    WorldGenUtil.modifyChunkMap(pos, chunkMap);
                }
            }
        } else {
            for (float u = 0; u < 360; u += (float) 35 / Math.max(this.radiusZ, this.radiusX)) {
                float x = (float) (radiusX * FastMaths.getFastCos(u));
                float z = (float) (radiusZ * FastMaths.getFastSin(u));
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
