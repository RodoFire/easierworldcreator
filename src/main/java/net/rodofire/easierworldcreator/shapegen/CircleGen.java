package net.rodofire.easierworldcreator.shapegen;

import net.minecraft.block.Block;
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
 * Since 2.1.0, the shape doesn't return a {@link List<BlockPos>} but it returns instead a {@link List<Set <BlockPos>>}
 * Before 2.1.0, the BlockPos list was a simple list.
 * Starting from 2.1.0, the shapes returns a list of {@link ChunkPos} that has a set of {@link BlockPos}
 * The change from {@link List} to {@link Set} was done to avoid duplicates BlockPos wich resulted in unnecessary calculations.
 * this allow easy multithreading for the Block assignment done in the {@link Shape} which result in better performance;
 * </p>
 */
public class CircleGen extends FillableShape {
    private int radiusx;
    private int radiusz;


    /**
     * @param world           the world the spiral will spawn in
     * @param pos             the center of the spiral
     * @param layers          a list of layers that will be used for the structure
     * @param force           boolean to force the pos of the blocks
     * @param blocksToForce   a list of blocks that the blocks of the spiral can still force if force = false
     * @param xrotation       first rotation around the x-axis
     * @param yrotation       second rotation around the y-axis
     * @param secondxrotation last rotation around the x-axis
     * @param radiusx         the radius of the x-axis
     * @param radiusz         the radius of the z-axis
     */
    public CircleGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment, @NotNull List<BlockLayer> layers, boolean force, List<Block> blocksToForce, int xrotation, int yrotation, int secondxrotation, int radiusx, int radiusz) {
        super(world, pos,placeMoment, layers, force, blocksToForce, xrotation, yrotation, secondxrotation);
        this.radiusx = radiusx;
        this.radiusz = radiusz;
    }

    public CircleGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, int radius, PlaceMoment placeMoment) {
        super(world, pos, placeMoment);
        this.radiusx = radius;
        this.radiusz = radius;
    }


    /*---------- Radius Related ----------*/
    public int getRadiusx() {
        return radiusx;
    }

    public void setRadiusx(int radiusx) {
        this.radiusx = radiusx;
    }

    public int getRadiusz() {
        return radiusz;
    }

    public void setRadiusz(int radiusz) {
        this.radiusz = radiusz;
    }

    public void addRadiusx(int radiusx) {
        this.radiusx += radiusx;
    }

    public void addRadiusy(int radiusy) {
        this.radiusz += radiusy;
    }

    /*---------- Place Structure ----------*/
    @Override
    public List<Set<BlockPos>> getBlockPos() {
        if (this.getFillingType() == FillableShape.Type.HALF) {
            this.setCustomFill(0.5f);
        }
        if (this.getCustomFill() > 1f) this.setCustomFill(1f);
        if (this.getCustomFill() < 0f) this.setCustomFill(0f);

        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() == 0 && (this.getFillingType() == FillableShape.Type.FULL || this.getFillingType() == FillableShape.Type.EMPTY)) {
            if(this.radiusz > 16 || this.radiusx > 16) this.biggerThanChunk = true;
            return this.generateEmptyOval(this.getPos().getX(), this.getPos().getZ(), this.getPos().getY());
        } else if (this.getFillingType() == FillableShape.Type.EMPTY) {
            return this.generateEmptyOval();
        }
        return this.generateFullOval();
    }

    @Override
    public List<Vec3d> getVec3d() {
        return null;
    }

    public List<Set<BlockPos>> generateFullOval() {
        Map<ChunkPos, Set<BlockPos>> chunkMap = new HashMap<>();

        int radiusxsquared = radiusx * radiusx;
        int radiuszsquared = radiusz * radiusz;
        float innerRadiusXSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusx * radiusx;
        float innerRadiusZSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusz * radiusz;

        //Rotating a shape requires more blocks.
        //This verification is there to avoid some unnecessary calculations when the rotations don't have any impact on the number of blocks
        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() % 180 == 0) {
            for (float x = -this.radiusx; x <= this.radiusx; x += 1) {
                float xsquared = x * x / radiusxsquared;
                for (float z = -this.radiusz; z <= this.radiusz; z += 1) {
                    if (xsquared + (z * z) / radiuszsquared <= 1) {
                        boolean bl = true;
                        if (innerRadiusXSquared != 0) {
                            float innerXSquared = x * x / innerRadiusXSquared;
                            float innerZSquared = z * z / innerRadiusZSquared;
                            if (innerXSquared + innerZSquared <= 1f) {
                                bl = false;
                            }
                        }
                        if (bl) {
                            BlockPos pos = new BlockPos((int) (this.getPos().getX() + x), this.getPos().getY(), (int) (this.getPos().getZ() + z));
                            if(!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos,this.getPos())) this.biggerThanChunk = true;
                            WorldGenUtil.modifyChunkMap(pos, chunkMap);
                        }
                    }
                }
            }
        } else {
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
                            BlockPos pos = this.getCoordinatesRotation(x, 0, z, this.getPos());
                            if(!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos,this.getPos())) this.biggerThanChunk = true;
                            WorldGenUtil.modifyChunkMap(pos, chunkMap);
                        }
                    }
                }
            }
        }

        return new ArrayList<>(chunkMap.values());
    }

    public List<Set<BlockPos>> generateEmptyOval() {
        Map<ChunkPos, Set<BlockPos>> chunkMap = new HashMap<>();

        //Rotating a shape requires more blocks.
        //This verification is there to avoid some unnecessary calculations when the rotations don't have any impact on the number of blocks
        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() == 0) {
            for (float u = 0; u < 360; u += (float) 45 / Math.max(this.radiusz, this.radiusx)) {
                float x = (float) (radiusx * FastMaths.getFastCos(u));
                float z = (float) (radiusz * FastMaths.getFastSin(u));
                BlockPos pos = new BlockPos((int) (this.getPos().getX() + x), this.getPos().getY(), (int) (this.getPos().getZ() + z));
                if(!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos,this.getPos())) this.biggerThanChunk = true;
                WorldGenUtil.modifyChunkMap(pos, chunkMap);
            }
        } else {
            for (float u = 0; u < 360; u += (float) 35 / Math.max(this.radiusz, this.radiusx)) {
                float x = (float) (radiusx * FastMaths.getFastCos(u));
                float z = (float) (radiusz * FastMaths.getFastSin(u));
                BlockPos pos = this.getCoordinatesRotation(x, 0, z, this.getPos());
                if(!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos,this.getPos())) this.biggerThanChunk = true;
                WorldGenUtil.modifyChunkMap(pos, chunkMap);
            }
        }
        return new ArrayList<>(chunkMap.values());
    }

    /*---------- Algorithm based on Bressen Algorithms for circle ----------*/

    /**
     * this class is used when no rotation is present. This allow fast coordinates generation but don't work with rotations
     *
     * @param centerX the x coordinate of the center of the circle
     * @param centerZ the z coordinate of the center of the circle
     * @param y       the height of the circle
     * @return
     */
    public List<Set<BlockPos>> generateEmptyOval(int centerX, int centerZ, int y) {
        int x = 0;
        int z = this.radiusz;
        int twoASquare = 2 * this.radiusx * this.radiusx;
        int twoBSquare = 2 * this.radiusz * this.radiusz;
        int decision1 = (int) (this.radiusz * this.radiusz - this.radiusx * this.radiusx * this.radiusz + 0.25 * this.radiusx * this.radiusx);
        int dx = twoBSquare * x;
        int dz = twoASquare * z;

        Map<ChunkPos, Set<BlockPos>> chunkMap = new HashMap<>();

        // Région 1
        while (dx < dz) {
            if(this.getFillingType() == Type.FULL){
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
            if(this.getFillingType() == Type.FULL){
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
        return new ArrayList<>(chunkMap.values());
    }

    /**
     * Adds block positions to the chunkMap based on the given coordinates.
     * @param centerX The x-coordinate of the center of the oval
     * @param centerZ The z-coordinate of the center of the oval
     * @param x The x-coordinate in the context of the Bresenham algorithm
     * @param y The height of the oval
     * @param z The z-coordinate in the context of the Bresenham algorithm
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
     * @param centerX The x-coordinate of the center of the oval
     * @param centerZ The z-coordinate of the center of the oval
     * @param x The x-coordinate in the context of the Bresenham algorithm
     * @param y The height of the oval
     * @param z The z-coordinate in the context of the Bresenham algorithm
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
}
