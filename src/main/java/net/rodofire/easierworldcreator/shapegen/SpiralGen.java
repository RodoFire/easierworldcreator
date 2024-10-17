package net.rodofire.easierworldcreator.shapegen;

import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.shapeutil.BlockLayer;
import net.rodofire.easierworldcreator.shapeutil.Shape;
import net.rodofire.easierworldcreator.util.FastMaths;
import net.rodofire.easierworldcreator.worldgenutil.WorldGenUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
 * Class to generate Spiral related shapes
 * <p>Since 2.1.0, the shape doesn't return a {@code List<BlockPos>} but it returns instead a {@code List<Set<BlockPos>>}
 * <p>Before 2.1.0, the BlockPos list was a simple list.
 * <p>Starting from 2.1.0, the shapes returns a list of {@link ChunkPos} that has a set of {@link BlockPos}
 * <p>The change from {@link List} to {@link Set} was done to avoid duplicates BlockPos which resulted in unnecessary calculations.
 * <p>this allow easy multithreading for the Block assignment done in the {@link Shape} which result in better performance;
 */
@SuppressWarnings("unused")
public class SpiralGen extends Shape {
    //radius on the x-axis
    private Pair<Integer, Integer> radiusX;
    //radius on the z-axis
    private Pair<Integer, Integer> radiusZ;
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
    private int outlineRadiusX = 1;
    //set the radius of the outline on the z-axis
    private int outlineRadiusZ = 1;

    /**
     * The angle of the side.
     * <p>It can change during the generation.
     * <p>To change the angle you can do as following : {@code new Pair<>{startAngle, endAngle}}
     */
    private Pair<Integer, Integer> helicoidAngle = new Pair<>(0, 0);

    /**
     * init the Spiral Shape
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
     * @param radiusX         the radius on the x-axis. The first value corresponding to the radius at the base of the spiral, the second, corresponding to the radius at the top of the spiral
     * @param radiusZ         the radius on the z-axis. The first value corresponding to the radius at the base of the spiral, the second, corresponding to the radius at the top of the spiral
     * @param height          the height of the spiral
     * @param turnNumber      the number of turn that the spiral will do (ex: 1 -> 1 turn, 3.5 -> 3.5 turn)
     */
    public SpiralGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment, LayerPlace layerPlace, LayersType layersType, int xRotation, int yRotation, int secondXRotation, String featureName, Pair<Integer, Integer> radiusX, Pair<Integer, Integer> radiusZ, int height, float turnNumber) {
        super(world, pos, placeMoment, layerPlace, layersType, xRotation, yRotation, secondXRotation, featureName);
        this.radiusX = radiusX;
        this.radiusZ = radiusZ;
        this.height = height;
        this.turnNumber = turnNumber;
    }

    /**
     * @param world       the world the spiral will spawn in
     * @param pos         the center of the spiral
     * @param placeMoment define the moment where the shape will be placed
     * @param radius      the radius of the spiral
     * @param height      the height of the spiral
     */
    public SpiralGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment, int radius, int height) {
        super(world, pos, placeMoment);
        this.radiusX = new Pair<>(radius, radius);
        this.radiusZ = new Pair<>(radius, radius);
        this.height = height;
    }

    public int getOutlineRadiusZ() {
        return outlineRadiusZ;
    }

    public void setOutlineRadiusZ(int outlineRadiusZ) {
        this.outlineRadiusZ = outlineRadiusZ;
    }

    public int getOutlineRadiusX() {
        return outlineRadiusX;
    }

    public void setOutlineRadiusX(int outlineRadiusX) {
        this.outlineRadiusX = outlineRadiusX;
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

    public Pair<Integer, Integer> getRadiusZ() {
        return radiusZ;
    }

    public int getStartRadiusZ() {
        return radiusZ.getLeft();
    }

    public int getStartRadiusX() {
        return radiusX.getLeft();
    }

    public int getEndRadiusZ() {
        return radiusZ.getRight();
    }

    public int getEndRadiusX() {
        return radiusX.getRight();
    }

    public void setRadiusZ(Pair<Integer, Integer> radiusZ) {
        this.radiusZ = radiusZ;
    }

    public Pair<Integer, Integer> getRadiusX() {
        return radiusX;
    }

    public void setRadiusX(Pair<Integer, Integer> radiusX) {
        this.radiusX = radiusX;
    }

    public void setEndRadiusX(int endRadiusX) {
        this.radiusX = new Pair<>(radiusX.getLeft(), endRadiusX);
    }

    public void setEndRadiusZ(int endRadiusZ) {
        this.radiusZ = new Pair<>(radiusZ.getLeft(), endRadiusZ);
    }

    public void setStartRadiusX(int startRadiusX) {
        this.radiusX = new Pair<>(startRadiusX, radiusX.getRight());
    }

    public void setStartRadiusZ(int startRadiusZ) {
        this.radiusZ = new Pair<>(startRadiusZ, radiusZ.getRight());
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
     * @param offset the offset of the start of the spiral
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    public Pair<Integer, Integer> getHelicoidAngle() {
        return helicoidAngle;
    }

    /**
     * @param helicoidAngle the start and the end angle of the blocks on the side
     */
    public void setHelicoidAngle(Pair<Integer, Integer> helicoidAngle) {
        this.helicoidAngle = helicoidAngle;
    }


    @Override
    public List<Set<BlockPos>> getBlockPos() {
        this.getFilling();
        Map<ChunkPos, Set<BlockPos>> chunkMap = new HashMap<>();

        if (this.spiralType == SpiralType.DEFAULT) {
            this.generateEllipsoidSpiral(this.getPos(), chunkMap);
        } else if (this.spiralType == SpiralType.HELICOID || this.spiralType == SpiralType.HALF_HELICOID || this.spiralType == SpiralType.CUSTOM_HELICOID) {
            this.generateHelicoid(chunkMap);
        } else if (this.spiralType == SpiralType.LARGE_OUTLINE) {
            this.generateLargeOutlineSpiral(chunkMap);
        } else if (this.spiralType == SpiralType.DOUBLE_HELICOID || this.spiralType == SpiralType.HALF_DOUBLE_HELICOID || this.spiralType == SpiralType.CUSTOM_DOUBLE_HELICOID) {
            this.generateHelicoid(chunkMap);
            this.setOffset(180);
            this.generateHelicoid(chunkMap);

        } else {
            this.generateHelicoid(chunkMap);
        }
        return new ArrayList<>(chunkMap.values());
    }

    @Override
    public List<Vec3d> getVec3d() {
        return List.of();
    }

    /**
     * generates a simple spiral
     *
     * @param pos the center of the spiral. This can be changed to match certain needing like when generating a large outline
     */
    public void generateEllipsoidSpiral(BlockPos pos, Map<ChunkPos, Set<BlockPos>> chunkMap) {
        /*if (this.turnNumber <= 0) {
            Easierworldcreator.LOGGER.error("param turn can't be <= 0");
        }*/
        int maxLarge = Math.max(Math.max(radiusX.getLeft(), radiusX.getRight()), Math.max(radiusZ.getLeft(), radiusZ.getRight()));
        double f = (this.turnNumber * maxLarge);
        double a = (double) 360 / (height * maxLarge);
        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() == 0) {
            for (double i = 0; i < maxLarge * this.turnNumber * height; i++) {
                float percentage = (float) i / (maxLarge * this.turnNumber * height);
                float radiusX = this.getXRadius(percentage);
                float radiusZ = this.getZRadius(percentage);
                int x = (int) (radiusX * FastMaths.getFastCos(a * i + offset));
                int z = (int) (radiusZ * FastMaths.getFastSin(a * i + offset));
                int y = (int) (i / f);
                BlockPos pos1 = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos1, this.getPos()))
                    this.biggerThanChunk = true;
                WorldGenUtil.modifyChunkMap(pos1, chunkMap);
            }
        } else {
            for (double i = 0; i < maxLarge * this.turnNumber * height; i += 0.5) {
                float percentage = (float) i / (maxLarge * this.turnNumber * height);
                float radiusX = this.getXRadius(percentage);
                float radiusZ = this.getZRadius(percentage);
                float x = (float) (radiusX * FastMaths.getFastCos(a * i + offset));
                float z = (float) (radiusZ * FastMaths.getFastSin(a * i + offset));
                float y = (float) (i / f);
                BlockPos pos2 = this.getCoordinatesRotation(x, y, z, pos);
                if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos2, this.getPos()))
                    this.biggerThanChunk = true;
                WorldGenUtil.modifyChunkMap(pos2, chunkMap);
            }
        }
    }


    /**
     * this allows the generation of a large outline spiral.
     */
    public void generateLargeOutlineSpiral(Map<ChunkPos, Set<BlockPos>> chunkMap) {
        float angle = (float) Math.atan(height / turnNumber);
        int degAngle = (int) Math.toDegrees(angle);
        Vec3d vec = new Vec3d(FastMaths.getFastCos(degAngle), FastMaths.getFastSin(degAngle), 0).normalize();
        double cosY = FastMaths.getFastCos(degAngle);
        double sinY = FastMaths.getFastSin(degAngle);

        int maxLarge = Math.max(outlineRadiusX, outlineRadiusZ);
        for (int i = 0; i < 360; i += 45 / maxLarge) {
            double x = outlineRadiusX * FastMaths.getFastCos(i);
            double z = outlineRadiusZ * FastMaths.getFastSin(i);
            BlockPos pos = WorldGenUtil.getCoordinatesRotation((float) x, (float) 0, (float) z, 1, 0, cosY, sinY, 1, 0, this.getPos());
            if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos())) this.biggerThanChunk = true;
            this.generateEllipsoidSpiral(pos, chunkMap);
        }
    }

    /**
     * generates a helicoid if the {@link SpiralType} is set to {@code HELICOID} or {@code DOUBLE_HELICOID} with their variants
     */
    public void generateHelicoid(Map<ChunkPos, Set<BlockPos>> chunkMap) {
        /*if (this.turnNumber <= 0) {
            Easierworldcreator.LOGGER.error("param turn can't be <= 0");
        }*/
        int maxLarge = Math.max(Math.max(radiusX.getLeft(), radiusX.getRight()), Math.max(radiusZ.getLeft(), radiusZ.getRight()));
        double f = (this.turnNumber * maxLarge);
        double a = (double) 360 / (height * maxLarge);


        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() == 0 && this.helicoidAngle.getLeft() < 45 && this.helicoidAngle.getLeft() > -45 && this.helicoidAngle.getRight() < 45 && this.helicoidAngle.getRight() > -45) {
            for (double i = 0; i < maxLarge * this.turnNumber * height; i++) {

                float percentage = (float) i / (maxLarge * this.turnNumber * height);
                float radiusX = this.getXRadius(percentage);
                float radiusZ = this.getZRadius(percentage);
                float gainX = radiusX / maxLarge;
                float gainZ = radiusZ / maxLarge;

                float innerRadiusX = (1 - this.spiralFilling) * radiusX;
                float innerRadiusZ = (1 - this.spiralFilling) * radiusZ;
                float innerRadiusXSquared = innerRadiusX * innerRadiusX;
                float innerRadiusZSquared = innerRadiusZ * innerRadiusZ;

                int helicoidAngle = getAngle(percentage);

                for (double j = 0; j <= maxLarge; j++) {

                    int x = (int) (gainX * j * FastMaths.getFastCos(a * i + offset));
                    int z = (int) (gainZ * j * FastMaths.getFastSin(a * i + offset));
                    double distance = FastMaths.getLength(x, z);


                    boolean bl = true;
                    if (innerRadiusXSquared != 0) {
                        float innerXSquared = x * x / innerRadiusXSquared;
                        float innerZSquared = z * z / innerRadiusZSquared;
                        if (innerXSquared + innerZSquared <= 1f) {
                            bl = false;
                        }
                    }
                    if (bl) {
                        int y = (int) ((int) (i / f) + distance * FastMaths.getFastSin(helicoidAngle));
                        BlockPos pos = new BlockPos(this.getPos().getX() + x, this.getPos().getY() + y, this.getPos().getZ() + z);
                        if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                            this.biggerThanChunk = true;
                        WorldGenUtil.modifyChunkMap(pos, chunkMap);
                    }
                }
            }
        } else {
            for (double i = 0; i < maxLarge * this.turnNumber * height; i += 0.25) {

                float percentage = (float) i / (maxLarge * this.turnNumber * height);
                float radiusX = this.getXRadius(percentage);
                float radiusZ = this.getZRadius(percentage);
                float gainX = radiusX / maxLarge;
                float gainZ = radiusZ / maxLarge;

                float innerRadiusX = (1 - this.spiralFilling) * radiusX;
                float innerRadiusZ = (1 - this.spiralFilling) * radiusZ;
                float innerRadiusXSquared = innerRadiusX * innerRadiusX;
                float innerRadiusZSquared = innerRadiusZ * innerRadiusZ;

                int helicoidAngle = getAngle(percentage);

                for (double j = 0; j <= maxLarge; j++) {

                    int x = (int) (gainX * j * FastMaths.getFastCos(a * i + offset));
                    int z = (int) (gainZ * j * FastMaths.getFastSin(a * i + offset));

                    double distance = FastMaths.getLength(x, z);


                    boolean bl = true;
                    if (innerRadiusXSquared != 0) {
                        float innerXSquared = x * x / innerRadiusXSquared;
                        float innerZSquared = z * z / innerRadiusZSquared;
                        if (innerXSquared + innerZSquared <= 1f) {
                            bl = false;
                        }
                    }
                    if (bl) {
                        int y = (int) ((int) (i / f) + distance * FastMaths.getFastSin(helicoidAngle));
                        BlockPos pos = this.getCoordinatesRotation(x, y, z, this.getPos());
                        if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                            this.biggerThanChunk = true;
                        WorldGenUtil.modifyChunkMap(pos, chunkMap);
                    }
                }
            }
        }
    }

    /**
     * change the filling of the spiral to avoid some possible issues if the value is badly set
     */
    private void getFilling() {
        if (spiralType == SpiralType.HELICOID || spiralType == SpiralType.DOUBLE_HELICOID) {
            this.spiralFilling = 1f;
        } else if (spiralType == SpiralType.HALF_HELICOID || spiralType == SpiralType.HALF_DOUBLE_HELICOID) {
            this.spiralFilling = 0.5f;
        }
    }


    /**
     * set every possible spiral shape of the mod
     */
    public enum SpiralType {
        //default shape
        DEFAULT,
        //helicoid shape, blocks are posed between the center axis and the outline
        HELICOID,
        //helicoid shape, blocks are posed between the center of the axis and the outline
        HALF_HELICOID,
        CUSTOM_HELICOID,
        //helicoid shape,this generates 2 helicoid with an opposite direction
        DOUBLE_HELICOID,
        HALF_DOUBLE_HELICOID,
        CUSTOM_DOUBLE_HELICOID,
        LARGE_OUTLINE
    }

    /**
     * this method returns the {@code xRadius} depending on the height we are at
     *
     * @param percentage the percentage of the height we are at
     * @return the x radius of the spiral
     */
    public float getXRadius(float percentage) {
        return (int) (radiusX.getLeft() * (1 - percentage) + radiusZ.getRight() * percentage);
    }

    /**
     * this method returns the {@code zRadius} depending on the height we are at
     *
     * @param percentage the percentage of the height we are at
     * @return the x radius of the spiral
     */
    public float getZRadius(float percentage) {
        return (int) (radiusZ.getLeft() * (1 - percentage) + radiusZ.getRight() * percentage);
    }

    /**
     * this method returns the {@code helicoïdAngle} depending on the height we are at
     *
     * @param percentage the percentage of the height we are at
     * @return the angle of the spiral
     */
    public int getAngle(float percentage) {
        return (int) (helicoidAngle.getLeft() * (1 - percentage) + helicoidAngle.getRight() * percentage);
    }
}
