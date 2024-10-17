package net.rodofire.easierworldcreator.shapegen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.shapeutil.BlockLayer;
import net.rodofire.easierworldcreator.shapeutil.FillableShape;
import net.rodofire.easierworldcreator.shapeutil.Shape;
import net.rodofire.easierworldcreator.util.FastMaths;
import net.rodofire.easierworldcreator.worldgenutil.WorldGenUtil;
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
 * Starting from 2.1.0, the shapes returns a list of {@link ChunkPos} that has a set of {@link BlockPos}
 * The change from {@link List} to {@link Set} was done to avoid duplicates BlockPos which resulted in unnecessary calculations.
 * this allow easy multithreading for the Block assignment done in the {@link Shape} which result in better performance;
 * </p>
 */
@SuppressWarnings("unused")
public class CircleGen extends FillableShape {
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
     * @param xRotation       first rotation around the x-axis
     * @param yRotation       second rotation around the y-axis
     * @param secondXRotation last rotation around the x-axis
     * @param featureName     the name of the feature
     * @param radiusX         the radius of the x-axis
     * @param radiusZ         the radius of the z-axis
     */
    public CircleGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment,
                     LayerPlace layerPlace, LayersType layersType,
                     int xRotation, int yRotation, int secondXRotation, String featureName,
                     int radiusX, int radiusZ) {
        super(world, pos, placeMoment, layerPlace, layersType, xRotation, yRotation, secondXRotation, featureName);
        this.radiusX = radiusX;
        this.radiusZ = radiusZ;
    }

    /**
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
    public int getRadiusX() {
        return radiusX;
    }

    public void setRadiusX(int radiusX) {
        this.radiusX = radiusX;
    }

    public int getRadiusZ() {
        return radiusZ;
    }

    public void setRadiusZ(int radiusZ) {
        this.radiusZ = radiusZ;
    }

    public void addRadiusX(int radiusX) {
        this.radiusX += radiusX;
    }

    public void addRadiusY(int radiusY) {
        this.radiusZ += radiusY;
    }

    /*---------- Place Structure ----------*/
    @Override
    public List<Set<BlockPos>> getBlockPos() {
        if (this.getFillingType() == FillableShape.Type.HALF) {
            this.setCustomFill(0.5f);
        }
        if (this.getCustomFill() > 1f) this.setCustomFill(1f);
        if (this.getCustomFill() < 0f) this.setCustomFill(0f);

        if (this.getFillingType() == FillableShape.Type.EMPTY) {
            return this.generateEmptyOval();
        }
        return this.generateFullOval();
    }

    @Override
    public List<Vec3d> getVec3d() {
        return null;
    }

    /**
     * method to create a full oval/ with custom filling
     *
     * @return {@code List<Set<BlockPos>>} : set of BlockPos divided into a list of chunks
     */
    public List<Set<BlockPos>> generateFullOval() {
        Map<ChunkPos, Set<BlockPos>> chunkMap = new HashMap<>();

        int radiusXSquared = radiusX * radiusX;
        int radiusZSquared = radiusZ * radiusZ;
        float innerRadiusXSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusX * radiusX;
        float innerRadiusZSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusZ * radiusZ;

        //Rotating a shape requires more blocks.
        //This verification is there to avoid some unnecessary calculations when the rotations don't have any impact on the number of blocks
        if (this.getXRotation() % 180 == 0 && this.getYRotation() % 180 == 0 && this.getSecondXRotation() % 180 == 0) {
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
                            if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                                this.biggerThanChunk = true;
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
                            if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                                this.biggerThanChunk = true;
                            WorldGenUtil.modifyChunkMap(pos, chunkMap);
                        }
                    }
                }
            }
        }

        return new ArrayList<>(chunkMap.values());
    }

    /**
     * method to create an empty oval with rotations
     *
     * @return {@code List<Set<BlockPos>>} : set of BlockPos divided into a list of chunks
     */
    public List<Set<BlockPos>> generateEmptyOval() {
        Map<ChunkPos, Set<BlockPos>> chunkMap = new HashMap<>();

        //Rotating a shape requires more blocks.
        //This verification is there to avoid some unnecessary calculations when the rotations don't have any impact on the number of blocks
        if (this.getXRotation() % 180 == 0 && this.getYRotation() % 180 == 0 && this.getSecondXRotation() == 0) {
            for (float u = 0; u < 360; u += (float) 45 / Math.max(this.radiusZ, this.radiusX)) {
                float x = (float) (radiusX * FastMaths.getFastCos(u));
                float z = (float) (radiusZ * FastMaths.getFastSin(u));
                BlockPos pos = new BlockPos((int) (this.getPos().getX() + x), this.getPos().getY(), (int) (this.getPos().getZ() + z));
                if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                    this.biggerThanChunk = true;
                WorldGenUtil.modifyChunkMap(pos, chunkMap);
            }
        } else {
            for (float u = 0; u < 360; u += (float) 35 / Math.max(this.radiusZ, this.radiusX)) {
                float x = (float) (radiusX * FastMaths.getFastCos(u));
                float z = (float) (radiusZ * FastMaths.getFastSin(u));
                BlockPos pos = this.getCoordinatesRotation(x, 0, z, this.getPos());
                if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                    this.biggerThanChunk = true;
                WorldGenUtil.modifyChunkMap(pos, chunkMap);
            }
        }
        return new ArrayList<>(chunkMap.values());
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
     * @return a list of chunk represented by a set of BlockPos
     */
    public List<Set<BlockPos>> generateEmptyOval(int centerX, int centerZ, int y) {
        int x = 0;
        int z = this.radiusZ;
        int twoASquare = 2 * this.radiusX * this.radiusX;
        int twoBSquare = 2 * this.radiusZ * this.radiusZ;
        int decision1 = (int) (this.radiusZ * this.radiusZ - this.radiusX * this.radiusX * this.radiusZ + 0.25 * this.radiusX * this.radiusX);
        int dx = twoBSquare * x;
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
        return new ArrayList<>(chunkMap.values());
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
