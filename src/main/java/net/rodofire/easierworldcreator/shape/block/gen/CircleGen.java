package net.rodofire.easierworldcreator.shape.block.gen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayer;
import net.rodofire.easierworldcreator.maths.FastMaths;
import net.rodofire.easierworldcreator.shape.block.instanciator.AbstractBlockShape;
import net.rodofire.easierworldcreator.shape.block.instanciator.AbstractFillableBlockShape;
import net.rodofire.easierworldcreator.util.WorldGenUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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
 * Since 2.1.0, the shape doesn't return a {@link List<BlockPos>} but it returns instead a {@code List<Set<BlockPos>>}
 * Before 2.1.0, the BlockPos list was a simple list.
 * Starting from 2.1.0, the shapes return a list of {@link ChunkPos} that has a set of {@link BlockPos}
 * The change from {@link List} to {@link Set} was done to avoid duplicates BlockPos which resulted in unnecessary calculations.
 * This allows easy multithreading for the Block assignment done in the {@link AbstractBlockShape} which result in better performance;
 * </p>
 */
@SuppressWarnings("unused")
public class CircleGen extends AbstractFillableBlockShape {
    private int radiusX;
    private int radiusZ;


    /**
     * init the Circle Shape
     *
     * @param world           the world the spiral will spawn in
     * @param pos             the center of the spiral
     * @param placeMoment     define the moment where the shape will be placed
     * @param layerPlace      how the {@code @BlockStates} inside of a {@link BlockLayer} will be placed
     * @param layersType      how the Layers will be placed
     * @param yRotation       first rotation around the y-axis
     * @param zRotation       second rotation around the z-axis
     * @param secondYRotation last rotation around the y-axis
     * @param featureName     the name of the feature
     * @param radiusX         the radius of the x-axis
     * @param radiusZ         the radius of the z-axis
     */
    public CircleGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment,
                     LayerPlace layerPlace, LayersType layersType,
                     int yRotation, int zRotation, int secondYRotation, String featureName,
                     int radiusX, int radiusZ) {
        super(world, pos, placeMoment, layerPlace, layersType, yRotation, zRotation, secondYRotation, featureName);
        this.radiusX = radiusX;
        this.radiusZ = radiusZ;
    }

    /**
     * init a circle generator
     *
     * @param world       the world the spiral will spawn in
     * @param pos         the center of the spiral
     * @param placeMoment define the moment where the shape will be placed
     * @param radius      the radius of the x-axis
     */
    public CircleGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment, int radius) {
        super(world, pos, placeMoment);
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
    public Map<ChunkPos, Set<BlockPos>> getBlockPos() {
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
    public Map<ChunkPos, Set<BlockPos>> generateFullOval() {
        Map<ChunkPos, Set<BlockPos>> chunkMap = new HashMap<>();

        int radiusXSquared = radiusX * radiusX;
        int radiusZSquared = radiusZ * radiusZ;
        float innerRadiusXSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusX * radiusX;
        float innerRadiusZSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusZ * radiusZ;

        //Rotating a shape requires more blocks.
        //This verification is there to avoid some unnecessary calculations when the rotations don't have any impact on the number of blocks
        if (this.getYRotation() % 180 == 0 && this.getZRotation() % 180 == 0 && this.getSecondYRotation() % 180 == 0) {
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
                            BlockPos pos = new BlockPos((int) (this.getPos().getX() + x), this.getPos().getY(), (int) (this.getPos().getZ() + z));
                            if (!this.multiChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                                this.multiChunk = true;
                            WorldGenUtil.modifyChunkMap(pos, chunkMap);
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
                            BlockPos pos = this.getCoordinatesRotation(x, 0, z, this.getPos());
                            if (!this.multiChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                                this.multiChunk = true;
                            WorldGenUtil.modifyChunkMap(pos, chunkMap);
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
    public Map<ChunkPos, Set<BlockPos>> generateEmptyOval() {
        Map<ChunkPos, Set<BlockPos>> chunkMap = new HashMap<>();

        //Rotating a shape requires more blocks.
        //This verification is there to avoid some unnecessary calculations when the rotations don't have any impact on the number of blocks
        if (this.getYRotation() % 180 == 0 && this.getZRotation() % 180 == 0 && this.getSecondYRotation() == 0) {
            for (float u = 0; u < 360; u += (float) 45 / Math.max(this.radiusZ, this.radiusX)) {
                float x = radiusX * FastMaths.getFastCos(u);
                float z = radiusZ * FastMaths.getFastSin(u);
                BlockPos pos = new BlockPos((int) (this.getPos().getX() + x), this.getPos().getY(), (int) (this.getPos().getZ() + z));
                if (!this.multiChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                    this.multiChunk = true;
                WorldGenUtil.modifyChunkMap(pos, chunkMap);
            }
        } else {
            for (float u = 0; u < 360; u += (float) 35 / Math.max(this.radiusZ, this.radiusX)) {
                float x = radiusX * FastMaths.getFastCos(u);
                float z = radiusZ * FastMaths.getFastSin(u);
                BlockPos pos = this.getCoordinatesRotation(x, 0, z, this.getPos());
                if (!this.multiChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                    this.multiChunk = true;
                WorldGenUtil.modifyChunkMap(pos, chunkMap);
            }
        }
        return chunkMap;
    }

    /*---------- Algorithm based on Bressan Algorithms for circle ----------*/
    //TODO fix method

    /**
     * This class is used when no rotation is present.
     * This allows fast coordinates generation but doesn't work with rotations
     *
     * @param centerX the x coordinate of the center of the circle
     * @param centerZ the z coordinate of the center of the circle
     * @param y       the height of the circle
     * @return a map of chunkPos represented by a set of BlockPos
     */
    public Map<ChunkPos, Set<BlockPos>> generateEmptyOval(int centerX, int centerZ, int y) {
        int x = 0;
        int z = this.radiusZ;
        int twoASquare = 2 * this.radiusX * this.radiusX;
        int twoBSquare = 2 * this.radiusZ * this.radiusZ;
        int decision1 = (int) (this.radiusZ * this.radiusZ - this.radiusX * this.radiusX * this.radiusZ + 0.25 * this.radiusX * this.radiusX);
        int dx = 0;
        int dz = twoASquare * z;

        Map<ChunkPos, Set<BlockPos>> chunkMap = new HashMap<>();

        // Région 1
        while (dx < dz) {
            if (this.getFillingType() == Type.FULL) {
                placeFullOval(centerX, centerZ, x, y, z, chunkMap);
            } else {
                addOvalBlocks(centerX, centerZ, x, y, z, chunkMap);
            }
            x++;
            if (decision1 < 0) {
                dx = dx + twoBSquare;
                decision1 = decision1 + dx + this.radiusZ * this.radiusZ;
            } else {
                z--;
                dx = dx + twoBSquare;
                dz = dz - twoASquare;
                decision1 = decision1 + dx - dz + this.radiusZ * this.radiusZ;
            }
        }

        // Région 2
        int decision2 = (int) (this.radiusZ * this.radiusZ * (x + 0.5) * (x + 0.5) + this.radiusX * this.radiusX * (z - 1) * (z - 1) - this.radiusX * this.radiusX * this.radiusZ * this.radiusZ);
        while (z >= 0) {
            if (this.getFillingType() == Type.FULL) {
                placeFullOval(centerX, centerZ, x, y, z, chunkMap);
            } else {
                addOvalBlocks(centerX, centerZ, x, y, z, chunkMap);
            }
            z--;
            if (decision2 > 0) {
                dz = dz - twoASquare;
                decision2 = decision2 + this.radiusX * this.radiusX - dz;
            } else {
                x++;
                dx = dx + twoBSquare;
                dz = dz - twoASquare;
                decision2 = decision2 + dx - dz + this.radiusX * this.radiusX;
            }
        }
        return chunkMap;
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
        if (this.getYRotation() % 180 == 0) {
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

        if (this.getYRotation() % 180 == 0) {
            for (int i = 0; i <= 2 * z; i++) {
                BlockPos pos1 = new BlockPos(start1.getX(), start1.getY(), start1.getZ() - i);
                BlockPos pos2 = new BlockPos(start2.getX(), start2.getY(), start2.getZ() - i);
                WorldGenUtil.modifyChunkMap(pos1, chunkMap);
                WorldGenUtil.modifyChunkMap(pos2, chunkMap);
            }
        }
    }

}
