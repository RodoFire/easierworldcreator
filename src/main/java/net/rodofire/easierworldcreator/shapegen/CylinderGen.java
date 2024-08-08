package net.rodofire.easierworldcreator.shapegen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.shapeutil.FillableShape;
import net.rodofire.easierworldcreator.util.FastMaths;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * class to generate cylinder
 * this class use methods to generate circles with some modifications for the height
 */
public class CylinderGen extends FillableShape {
    private int radiusx;
    private int radiusz;
    private int height;


    public CylinderGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, int radius, int height) {
        super(world, pos);
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
    public List<BlockPos> getBlockPos() {
        return this.generateCylinder();
    }

    public List<BlockPos> generateCylinder() {
        long startTimeCartesian = System.nanoTime();
        List<Vec3d> veclist = new ArrayList<>();
        List<BlockPos> poslist = new ArrayList<>();
        this.setFill();



        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() == 0 && (this.getFillingType() == FillableShape.Type.FULL || this.getFillingType() == FillableShape.Type.EMPTY)) {
            for (int i = 0; i <= height; i++) {
                poslist.addAll(this.generateFatsOval());
                this.setPos(this.getPos().up());
            }
        } else if (this.getFillingType() == FillableShape.Type.EMPTY) {

            poslist.addAll(this.generateEmptyCylinder());
            this.setPos(this.getPos().up());

        } else {

            poslist.addAll(this.generateFullCylinder());
            this.setPos(this.getPos().up());

            poslist.addAll(this.getCoordinatesRotationList(veclist, this.getPos()));
        }
        this.getGenTime(startTimeCartesian, false);
        return poslist;
    }

    /**
     * this generates a full cylinder
     * @return blockStates list of the structures
     */
    public List<BlockPos> generateFullCylinder() {
        List<BlockPos> poslist = new ArrayList<>();
        int radiusxsquared = radiusx * radiusx;
        int radiuszsquared = radiusz * radiusz;
        float innerRadiusXSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusx * radiusx;
        float innerRadiusZSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusz * radiusz;
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
                                poslist.add(new BlockPos((int) (this.getPos().getX() + x), (int) (this.getPos().getY() + y), (int) (this.getPos().getZ() + z)));
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
                                poslist.add(this.getCoordinatesRotation(x, y, z, this.getPos()));
                            }
                        }
                    }
                }
            }
        }
        return poslist;
    }

    /**
     * this generates a full cylinder
     * @return blockStates list of the structures
     */
    public List<BlockPos> generateEmptyCylinder() {
        List<BlockPos> poslist = new ArrayList<>();

        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() == 0) {
            for (float u = 0; u < 360; u += (float) 45 / Math.max(this.radiusz, this.radiusx)) {
                float x = (float) (radiusx * FastMaths.getFastCos(u));
                float z = (float) (radiusz * FastMaths.getFastSin(u));
                for (float y = 0; y <= this.height; y += 1f) {
                    poslist.add(new BlockPos((int) (this.getPos().getX() + x), (int) (this.getPos().getY() + y), (int) (this.getPos().getZ() + z)));
                }
            }
        } else {
            for (float u = 0; u < 360; u += (float) 35 / Math.max(this.radiusz, this.radiusx)) {
                float x = (float) (radiusx * FastMaths.getFastCos(u));
                float z = (float) (radiusz * FastMaths.getFastSin(u));
                for (float y = 0; y <= this.height; y += 0.5f) {
                    poslist.add(this.getCoordinatesRotation(x, y, z, this.getPos()));
                }
            }
        }
        return poslist;
    }

    /*---------- Algorithm based on Bressen Algorithms for circle ----------*/
    public List<BlockPos> generateFatsOval() {
        int centerX = this.getPos().getX();
        int centerZ = this.getPos().getZ();
        int y = this.getPos().getY();

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
        } /*else {
            blockPosList.add(this.getCoordinatesRotation(centerX + x, y, centerZ + z, new BlockPos(0, 0, 0)));
            blockPosList.add(this.getCoordinatesRotation(centerX + x, y, centerZ - z, new BlockPos(0, 0, 0)));
            blockPosList.add(this.getCoordinatesRotation(centerX - x, y, centerZ + z, new BlockPos(0, 0, 0)));
            blockPosList.add(this.getCoordinatesRotation(centerX - x, y, centerZ - z, new BlockPos(0, 0, 0)));
        }*/
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
        } /*else {
            for (int i = 0; i <= 2 * z; i++) {
                mutable.set(start1, 0, 0, -i);
                blockPosList.add(this.getCoordinatesRotation(x, 0, z - i, new BlockPos(centerX, y, centerZ)));
                mutable.set(start2, 0, 0, -i);
                blockPosList.add(this.getCoordinatesRotation(-x, 0, z - i, new BlockPos(centerX, y, centerZ)));
            }
        }*/
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


    @Override
    public List<Vec3d> getVec3d() {
        return List.of();
    }
}
