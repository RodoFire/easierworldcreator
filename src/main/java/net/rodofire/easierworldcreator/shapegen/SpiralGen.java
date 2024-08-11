package net.rodofire.easierworldcreator.shapegen;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.shapeutil.BlockLayer;
import net.rodofire.easierworldcreator.shapeutil.Shape;
import net.rodofire.easierworldcreator.util.FastMaths;
import net.rodofire.easierworldcreator.worldgenutil.WorldGenUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

//helicoid shape
/*







                                                                           ......
                                                                       ...::::::..
                                                                   ....:::::::::::..
                                                                ...::::::::::::::::..
                                                              ..:::::::::::::::::::::.
                                                          ..::::::::::::::::::::::::::..
                                                      ...::::::::::::::::::::::::::::::..
                                                   ..:::::::::::::::::::::::::::::::::::..
                                               ...:::::::::::::::::::::::::::::::::::::::..
                                            ...:::::::::::::::::::::::::::::::::::::::::::..
                                        ....:::::::::::::::::::::::::::::::::::::::::::::::.
                                      ....::::::::::::::::::::::::::::::::::::::::::::::::::.
                                  ........::::::::::::::::::::::::::::::::::::::::::::::::::..
                               .........:::::::::::::::::::::::::::::::::::::::::::::::::::::.
                            ...........::::::::::::::::::::::::::::::::::::::::::::::::::::::..
                          ............::::::::::::::::::::::::::::::::::::::::::::::::::::::::.
                        .............:::::::::::::::::::::::::::::::::::::::::::::::::::::::::.
                       .............::::::::::::::::::::::::::::::::::::::::::::::::::::::::::..
                       ............:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::..
                        ...........:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::..
                          ..........:::::::::::::::::::::::::--------------:::::::::::::::::::..
                            .........:::::::::::::::::::-------------------------:::::::::::::..
                                .......:::::::::::::::--------------------------------::::::::.
                                   .....:::::::::::::--------------------------------------:::.
                                     ....:::::::::::----------------------------------------:..
                                       ....:::::::::----------------------------------------:.
                                         ...::::::::----------------------------------------..
                                          ...::::::::--------------------------------------:.
                                            ..:::::::--------------------------------------..
                                             ..::::::-------------------------------------:.
                                             ...::::::-----------------------------------:.
                                              ..::::::----------------------------------:.
                                               .::::::---------------------------------:.
                                               ..::::::-------------------------------:.
                                               ..:::::::-----------------------------:.
                                               ..:::::::---------------------------:..
                                               ..::::::::-------------------------:.
                                               .:::::::::-----------------------:..
                                              ..:::::::::----------------------:.
                                             ..:::::::::::-------------------:.
                                            ...:::::::::::-----------------:.
                                           ...:::::::::::::--------------:..
                                          ...::::::::::::::------------:.
                                        ...::::::::::::::::---------:..
                                      ....::::::::::::::::::-------:.
                                    ....:::::::::::::::::::------==-.
                 .............    ....:::::::::::::::::::----======-.
          ..........::::::::::::....::::::::::::::::::::--==========:
       ..................::.......::::::::::::::::::::-=============-
     ..........................::::::::::::::::::---================-.
     .......................::::::::::::::::-----===================-.
     ..................:::::::::::::::::::--------==================-
       ..........:::::::::::::........:::::-------==================:
               ..........             ..::::-------================-.
                                        ..:::------================-.
                                          .:::------==============-.
                                           .:::-----================:.
                                            ..::------================:.
                                             ..::-----==================:.
                                              .::------==================-.
                                               .::------===================:.
                                               .::-------===================-..
                                               .:::--------===================:
                                               .:::---------===================:.
                                               .:::--------=====================:.
                                               .:::-------=======================:.
                                              ..:::-----==========================:.
                                              .:::-----===========================-.
                                             .:::::--==============================:.
                                            .:::::-================================-.
                                          ..::::-===================================:.
                                         ..::--=====================================-.
                                      ....:---=======================================:.
                                    ...::------======================================-.
                                ....:::--------======================================-.
                            .....:::::------------===================================-.
                                  ..:::-------------=================================-.
                                     ..::------------================================-.
                                       ..::--------------============================-.
                                         ..:-------------------======================:.
                                            .:---------------------------------------:
                                              .:-------------------------------------.
                                                .-----------------------------------:.
                                                  .---------------------------------.
                                                    .------------------------------:.
                                                     ..---------------------------:.
                                                       .:-------------------------.
                                                         .:----------------------.
                                                           .:-------------------.
                                                             .:----------------:
                                                               ..-------------.
                                                                 .:---------:.
                                                                   ..------.
                                                                     .:--:.
                                                                       ...

     */

/**
 * class to generates spiral related shapes
 */
public class SpiralGen extends Shape {
    //radius on the x-axis
    private int radiusx;
    //radius on the z-axis
    private int radiusz;
    //height of the spiral
    private int height;
    //offset of the spiral in degrees, set to 0 by default, but you can change it
    private int offset = 0;
    //the number of turn that the spiral do between the start and the end, by default, this value is set to 1
    private float turnNumber = 1;
    //set the type of spiral
    private SpiralType spiralType = SpiralType.DEFAULT;
    private float spiralFilling = 1f;
    //set the radius of the outline on the x-axis
    private int outlineRadiusx = 1;
    //set the radius of the outline on the z-axis
    private int outlineRadiusz = 1;

    //the angle of the side
    private int helicoidAngle = 0;

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
     * @param height          the height of the spiral
     * @param turnNumber      the number of turns that the structure will do before reaching the top
     */
    public SpiralGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, List<BlockLayer> layers, boolean force, List<Block> blocksToForce, int xrotation, int yrotation, int secondxrotation, int radiusx, int radiusz, int height, float turnNumber) {
        super(world, pos, layers, force, blocksToForce, xrotation, yrotation, secondxrotation);
        this.radiusx = radiusx;
        this.radiusz = radiusz;
        this.height = height;
        this.turnNumber = turnNumber;
    }

    /**
     * @param world  the world the spiral will spawn in
     * @param pos    the center of the spiral
     * @param radius the radius of the spiral
     * @param height the height of the spiral
     */
    public SpiralGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, int radius, int height) {
        super(world, pos);
        this.radiusx = radius;
        this.radiusz = radius;
        this.height = height;
    }

    public int getOutlineRadiusz() {
        return outlineRadiusz;
    }

    public void setOutlineRadiusz(int outlineRadiusz) {
        this.outlineRadiusz = outlineRadiusz;
    }

    public int getOutlineRadiusx() {
        return outlineRadiusx;
    }

    public void setOutlineRadiusx(int outlineRadiusx) {
        this.outlineRadiusx = outlineRadiusx;
    }

    public float getSpiralFilling() {
        return spiralFilling;
    }

    public void setSpiralFilling(float spiralFilling) {
        this.spiralFilling = spiralFilling;
    }

    public SpiralType getSpiralType() {
        return spiralType;
    }

    public void setSpiralType(SpiralType spiralType) {
        this.spiralType = spiralType;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getRadiusz() {
        return radiusz;
    }

    public void setRadiusz(int radiusz) {
        this.radiusz = radiusz;
    }

    public int getRadiusx() {
        return radiusx;
    }

    public void setRadiusx(int radiusx) {
        this.radiusx = radiusx;
    }

    public float getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(float turnNumber) {
        this.turnNumber = turnNumber;
    }

    public int getOffset() {
        return offset;
    }

    /**
     *
     * @param offset the offset of the start of the spiral
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getHelicoidAngle() {
        return helicoidAngle;
    }

    /**
     *
     * @param helicoidAngle the angle of the blocks on the side
     */
    public void setHelicoidAngle(int helicoidAngle) {
        this.helicoidAngle = helicoidAngle;
    }


    @Override
    public List<BlockPos> getBlockPos() {
        this.getFilling();
        List<BlockPos> posList = new ArrayList<>();
        if (this.spiralType == SpiralType.DEFAULT) {
            return this.generateElipsoidSpiral(this.getPos());
        }
        if (this.spiralType == SpiralType.HELICOID || this.spiralType == SpiralType.HALF_HELICOID || this.spiralType == SpiralType.CUSTOM_HELICOOID) {
            return this.generateHelicoid();
        }
        if (this.spiralType == SpiralType.LARGE_OUTLINE) {
            return this.generateLargeOutlineSpiral();
        }
        if (this.spiralType == SpiralType.DOUBLE_HELICOID || this.spiralType == SpiralType.HALF_DOUBLE_HELICOID || this.spiralType == SpiralType.CUSTOM_DOUBLE_HELICOID) {
            posList.addAll(this.generateHelicoid());
            this.setOffset(180);
            posList.addAll(this.generateHelicoid());
            return posList;
        }
        return this.generateHelicoid();
    }

    @Override
    public List<Vec3d> getVec3d() {
        return List.of();
    }

    /**
     * generates a simple spiral
     * @param pos the center of the spiral. This can be changed to match certain needing like when generating a large outline
     * @return a list of blockPos that will be used to place the structure
     */
    public List<BlockPos> generateElipsoidSpiral(BlockPos pos) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        List<BlockPos> poslist = new ArrayList<BlockPos>();
        /*if (this.turnNumber <= 0) {
            Easierworldcreator.LOGGER.error("param turn can't be <= 0");
        }*/
        int maxlarge = Math.max(radiusx, radiusz);
        double f = (this.turnNumber * maxlarge);
        double a = (double) 360 / (height * maxlarge);
        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() == 0) {
            for (double i = 0; i < maxlarge * this.turnNumber * height; i++) {
                int x = (int) (radiusx * FastMaths.getFastCos(a * i + offset));
                int z = (int) (radiusz * FastMaths.getFastSin(a * i + offset));
                int y = (int) (i / f);
                poslist.add(new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z));
            }
        } else {
            for (double i = 0; i < maxlarge * this.turnNumber * height; i += 0.5) {
                float x = (float) (radiusx * FastMaths.getFastCos(a * i + offset));
                float z = (float) (radiusz * FastMaths.getFastSin(a * i + offset));
                float y = (float) (i / f);
                poslist.add(this.getCoordinatesRotation(x, y, z, pos));
            }
        }
        return poslist;
    }


    /**
     * this allow the generation of a large outline spiral.
     *
     * @return BlockPos list
     */
    public List<BlockPos> generateLargeOutlineSpiral() {
        float angle = (float) Math.atan(height / turnNumber);
        int degangle = (int) Math.toDegrees(angle);
        Vec3d vec = new Vec3d(FastMaths.getFastCos(degangle), FastMaths.getFastSin(degangle), 0).normalize();
        double cosy = FastMaths.getFastCos(degangle);
        double siny = FastMaths.getFastSin(degangle);
        List<BlockPos> posList = new ArrayList<>();

        // Trouver une base orthonormée pour le plan orthogonal à direction
        Vec3d u = new Vec3d(-vec.y, vec.x, 0).normalize();
        Vec3d v = vec.crossProduct(u).normalize();
        int maxlarge = Math.max(outlineRadiusx, outlineRadiusz);
        // Générer les points du cercle
        for (int i = 0; i < 360; i += 45 / maxlarge) { // Ajuster l'angle selon la densité souhaitée
            double x = outlineRadiusx * FastMaths.getFastCos(i);
            double z = outlineRadiusz * FastMaths.getFastSin(i);
            BlockPos pos = WorldGenUtil.getCoordinatesRotation((float) x, (float) 0, (float) z, 1, 0, cosy, siny, 1, 0, this.getPos());
            /*Vec3d point = this.getPos().toCenterPos().add(
                    u.x * x + v.x * z,
                    u.y * x + v.y * z,
                    u.z * x + v.z * z
            );*/

            posList.addAll(this.generateElipsoidSpiral(pos));
        }
        return posList;
    }

    /**
     * generates an helicoid if the {@link SpiralType} is set to HELICOID or double helicoid with their variants
     *
     * @return
     */
    public List<BlockPos> generateHelicoid() {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        List<BlockPos> poslist = new ArrayList<BlockPos>();
        /*if (this.turnNumber <= 0) {
            Easierworldcreator.LOGGER.error("param turn can't be <= 0");
        }*/
        int maxlarge = Math.max(radiusx, radiusz);
        double f = (this.turnNumber * maxlarge);
        double a = (double) 360 / (height * maxlarge);
        float innerRadiusX = (1 - this.spiralFilling) * radiusx;
        float innerRadiusZ = (1 - this.spiralFilling) * radiusz;
        float innerRadiusXSquared = innerRadiusX * innerRadiusX;
        float innerRadiusZSquared = innerRadiusZ * innerRadiusZ;


        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() == 0 && this.helicoidAngle < 45 && this.helicoidAngle > -45) {
            for (double j = 0; j <= maxlarge; j++) {
                float gainx = (float) (radiusx * j / maxlarge);
                float gainz = (float) (radiusz * j / maxlarge);
                for (double i = 0; i < maxlarge * this.turnNumber * height; i++) {
                    int x = (int) (gainx * FastMaths.getFastCos(a * i + offset));
                    int z = (int) (gainz * FastMaths.getFastSin(a * i + offset));
                    double distance = FastMaths.getLength(x, z);
                    if (distance >= Math.abs(FastMaths.getFastCos(i) * innerRadiusX) + Math.abs(FastMaths.getFastSin(i) * innerRadiusZ)) {
                        int y = (int) ((int) (i / f) + distance * FastMaths.getFastSin(helicoidAngle));
                        poslist.add(new BlockPos(this.getPos().getX() + x, this.getPos().getY() + y, this.getPos().getZ() + z));
                    }
                    boolean bl = true;
                    if (innerRadiusXSquared != 0) {
                        float innerXSquared = x * x / innerRadiusXSquared;
                        float innerZSquared = z * z / innerRadiusZSquared;
                        if (innerXSquared + innerZSquared <= 1f) { // pas dans l'ovale intérieur
                            bl = false;
                        }
                    }
                    if (bl) {
                        int y = (int) ((int) (i / f) + distance * FastMaths.getFastSin(helicoidAngle));
                        poslist.add(new BlockPos((int) (this.getPos().getX() + x), (int) (this.getPos().getY() + y), (int) (this.getPos().getZ() + z)));
                    }
                }
            }
        } else {
            for (double j = 0; j <= maxlarge; j++) {
                float gainx = (float) (radiusx * j / maxlarge);
                float gainz = (float) (radiusz * j / maxlarge);
                for (double i = 0; i < maxlarge * this.turnNumber * height; i += 0.5) {
                    float x = (float) (gainx * FastMaths.getFastCos(a * i + offset));
                    float z = (float) (gainz * FastMaths.getFastSin(a * i + offset));
                    double distance = FastMaths.getLength(x, z);
                    boolean bl = true;
                    if (innerRadiusXSquared != 0) {
                        float innerXSquared = x * x / innerRadiusXSquared;
                        float innerZSquared = z * z / innerRadiusZSquared;
                        if (innerXSquared + innerZSquared <= 1f) { // pas dans l'ovale intérieur
                            bl = false;
                        }
                    }
                    if (bl) {
                        int y = (int) ((int) (i / f) + distance * FastMaths.getFastSin(helicoidAngle));
                        poslist.add(this.getCoordinatesRotation(x, y, z, this.getPos()));
                    }
                }
            }
        }
        return poslist;
    }

    /**
     * change the filling of the spiral
     */
    private void getFilling() {
        if (spiralType == SpiralType.HELICOID || spiralType == SpiralType.DOUBLE_HELICOID) {
            this.spiralFilling = 1f;
        } else if (spiralType == SpiralType.HALF_HELICOID || spiralType == SpiralType.HALF_DOUBLE_HELICOID) {
            this.spiralFilling = 0.5f;
        }
    }


    /**
     * set every possible spiral shape
     */
    public enum SpiralType {
        //default shape
        DEFAULT,
        //helicoid shape, blocks are posed between the center axis and the outline
        HELICOID,
        //helicoid shape, blocks are posed between the center of the axis and the outline
        HALF_HELICOID,
        CUSTOM_HELICOOID,
        //helicoid shape,this generates 2 helicoidswith an opposite direction
        DOUBLE_HELICOID,
        HALF_DOUBLE_HELICOID,
        CUSTOM_DOUBLE_HELICOID,
        LARGE_OUTLINE
    }
}
