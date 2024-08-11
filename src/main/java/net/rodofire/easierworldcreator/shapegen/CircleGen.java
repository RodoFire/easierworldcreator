package net.rodofire.easierworldcreator.shapegen;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.shapeutil.BlockLayer;
import net.rodofire.easierworldcreator.shapeutil.FillableShape;
import net.rodofire.easierworldcreator.util.FastMaths;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
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
 * generate Circle
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
    public CircleGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull List<BlockLayer> layers, boolean force, List<Block> blocksToForce, int xrotation, int yrotation, int secondxrotation, int radiusx, int radiusz) {
        super(world, pos, layers, force, blocksToForce, xrotation, yrotation, secondxrotation);
        this.radiusx = radiusx;
        this.radiusz = radiusz;
    }

    public CircleGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, int radius) {
        super(world, pos);
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
    public List<BlockPos> getBlockPos() {
        if (this.getFillingType() == FillableShape.Type.HALF) {
            this.setCustomFill(0.5f);
        }
        if (this.getCustomFill() > 1f) this.setCustomFill(1f);
        if (this.getCustomFill() < 0f) this.setCustomFill(0f);

        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() == 0 && (this.getFillingType() == FillableShape.Type.FULL || this.getFillingType() == FillableShape.Type.EMPTY)) {
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

    public List<BlockPos> generateFullOval() {
        List<BlockPos> poslist = new ArrayList<>();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        int radiusxsquared = radiusx * radiusx;
        int radiuszsquared = radiusz * radiusz;
        float innerRadiusXSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusx * radiusx;
        float innerRadiusZSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusz * radiusz;
        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() % 180 == 0) {
            for (float x = -this.radiusx; x <= this.radiusx; x += 1) {
                float xsquared = x * x / radiusxsquared;
                for (float z = -this.radiusz; z <= this.radiusz; z += 1) {
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
                            poslist.add(new BlockPos((int) (this.getPos().getX() + x), this.getPos().getY(), (int) (this.getPos().getZ() + z)));
                        }
                    }
                }
            }
        } else {
            for (float x = -this.radiusx; x <= this.radiusx; x += 0.5f) {
                float xsquared = x * x / radiusxsquared;

                for (float z = -this.radiusz; z <= this.radiusz; z += 0.5f) {
                    if (xsquared + (z * z) / radiuszsquared <= 1) {
                        float innerXSquared = x * x / innerRadiusXSquared;
                        float innerZSquared = z * z / innerRadiusZSquared;
                        if (innerXSquared + innerZSquared > 1) {
                            poslist.add(this.getCoordinatesRotation(x, 0, z, this.getPos()));
                        }
                    }
                }
            }
        }

        return poslist;
    }

    public List<BlockPos> generateEmptyOval() {
        List<BlockPos> poslist = new ArrayList<>();
        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() == 0) {
            for (float u = 0; u < 360; u += (float) 45 / Math.max(this.radiusz, this.radiusx)) {
                float x = (float) (radiusx * FastMaths.getFastCos(u));
                float z = (float) (radiusz * FastMaths.getFastSin(u));
                poslist.add(new BlockPos((int) (this.getPos().getX() + x), this.getPos().getY(), (int) (this.getPos().getZ() + z)));
            }
        } else {
            for (float u = 0; u < 360; u += (float) 35 / Math.max(this.radiusz, this.radiusx)) {
                float x = (float) (radiusx * FastMaths.getFastCos(u));
                float z = (float) (radiusz * FastMaths.getFastSin(u));
                poslist.add(this.getCoordinatesRotation(x, 0, z, this.getPos()));
            }
        }
        return poslist;
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
    public List<BlockPos> generateEmptyOval(int centerX, int centerZ, int y) {
        int x = 0;
        int z = this.radiusz;
        int twoASquare = 2 * this.radiusx * this.radiusx;
        int twoBSquare = 2 * this.radiusz * this.radiusz;
        int decision1 = (int) (this.radiusz * this.radiusz - this.radiusx * this.radiusx * this.radiusz + 0.25 * this.radiusx * this.radiusx);
        int dx = twoBSquare * x;
        int dz = twoASquare * z;

        List<BlockPos> coordonates = new ArrayList<BlockPos>();

        // Region 1
        while (dx < dz) {
            if (this.getFillingType() != FillableShape.Type.FULL) {
                coordonates.addAll(this.getOvalBlocks(centerX, centerZ, x, y, z));
            } else {
                coordonates.addAll(placeFullOval(centerX, centerZ, x, y, z));
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

        // Region 2
        int decision2 = (int) (this.radiusz * this.radiusz * (x + 0.5) * (x + 0.5) + this.radiusx * this.radiusx * (z - 1) * (z - 1) - this.radiusx * this.radiusx * this.radiusz * this.radiusz);
        while (z >= 0) {
            if (this.getFillingType() != FillableShape.Type.FULL) {
                coordonates.addAll(this.getOvalBlocks(centerX, centerZ, x, y, z));
            } else {
                coordonates.addAll(placeFullOval(centerX, centerZ, x, y, z));
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
        return coordonates;
    }

    public List<BlockPos> getOvalBlocks(int centerX, int centerZ, int x, int y, int z) {
        BlockPos.Mutable pos = new BlockPos.Mutable();
        List<BlockPos> blockPosList = new ArrayList<>();

        if (this.getYrotation() % 180 == 0) {
            blockPosList.add(new BlockPos(centerX + x, y, centerZ + z));
            blockPosList.add(new BlockPos(centerX + x, y, centerZ - z));
            blockPosList.add(new BlockPos(centerX - x, y, centerZ + z));
            blockPosList.add(new BlockPos(centerX - x, y, centerZ - z));
        }
        return blockPosList;
    }

    //place lines between the blocks
    public List<BlockPos> placeFullOval(int centerX, int centerZ, int x, int y, int z) {
        BlockPos start1 = new BlockPos(centerX + x, y, centerZ + z);
        BlockPos start2 = new BlockPos(centerX - x, y, centerZ + z);

        List<BlockPos> blockPosList = new ArrayList<>();

        BlockPos.Mutable mutable = new BlockPos.Mutable();

        if (this.getYrotation() % 180 == 0) {
            for (int i = 0; i <= 2 * z; i++) {
                mutable.set(start1, 0, 0, -i);
                blockPosList.add(new BlockPos(mutable));
                mutable.set(start2, 0, 0, -i);
                blockPosList.add(new BlockPos(mutable));
            }
        }
        return blockPosList;
    }

    public List<BlockPos> getCircleWithRotation() {
        List<BlockPos> blockPosList = new ArrayList<>();
        for (float a = -180; a <= 180; a += (float) 25 / (Math.max(this.radiusx, this.radiusz))) {
            float x = (float) (this.radiusx * FastMaths.getFastCos(a));
            float z = (float) (this.radiusz * FastMaths.getFastCos(a));
            blockPosList.add(this.getCoordinatesRotation(x, 0, z, this.getPos()));
        }
        return blockPosList;
    }
}
