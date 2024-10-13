package net.rodofire.easierworldcreator.shapegen;

import net.minecraft.block.Block;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.shapeutil.BlockLayer;
import net.rodofire.easierworldcreator.shapeutil.Shape;
import net.rodofire.easierworldcreator.util.FastMaths;
import net.rodofire.easierworldcreator.worldgenutil.FastNoiseLite;
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
 * <p>The change from {@link List} to {@link Set} was done to avoid duplicates BlockPos wich resulted in unnecessary calculations.
 * <p>this allow easy multithreading for the Block assignment done in the {@link Shape} which result in better performance;
 */
public class SpiralGen extends Shape {
    //radius on the x-axis
    private Pair<Integer, Integer> radiusx = new Pair<>(0, 0);
    //radius on the z-axis
    private Pair<Integer, Integer> radiusz = new Pair<>(0, 0);
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
     * @param force           boolean to force the pos of the blocks
     * @param blocksToForce   a list of blocks that the blocks of the spiral can still force if force = false
     * @param layerPlace      how the {@code @BlockStates} inside of a {@link BlockLayer} will be placed
     * @param layersType      how the Layers will be placed
     * @param xrotation       first rotation around the x-axis
     * @param yrotation       second rotation around the y-axis
     * @param secondxrotation last rotation around the x-axis
     * @param featureName     the name of the feature
     * @param radiusx         the radius on the x-axis. The first value corresponding to the radius at the base of the spiral, the second, corresponding to the radius at the top of the spiral
     * @param radiusz         the radius on the z-axis. The first value corresponding to the radius at the base of the spiral, the second, corresponding to the radius at the top of the spiral
     * @param height          the height of the spiral
     * @param turnNumber      the number of turn that the spiral will do (ex: 1 -> 1 turn, 3.5 -> 3.5 turn)
     */
    public SpiralGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment, boolean force, List<Block> blocksToForce, LayerPlace layerPlace, LayersType layersType, int xrotation, int yrotation, int secondxrotation, String featureName, Pair<Integer, Integer> radiusx, Pair<Integer, Integer> radiusz, int height, float turnNumber) {
        super(world, pos, placeMoment, force, blocksToForce, layerPlace, layersType, xrotation, yrotation, secondxrotation, featureName);
        this.radiusx = radiusx;
        this.radiusz = radiusz;
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
        this.radiusx = new Pair<>(radius, radius);
        this.radiusz = new Pair<>(radius, radius);
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

    public Pair<Integer, Integer> getRadiusz() {
        return radiusz;
    }

    public int getStartRadiusZ() {
        return radiusz.getLeft();
    }

    public int getStartRadiusX() {
        return radiusx.getLeft();
    }

    public int getEndRadiusZ() {
        return radiusz.getRight();
    }

    public int getEndRadiusX() {
        return radiusx.getRight();
    }

    public void setRadiusz(Pair<Integer, Integer> radiusz) {
        this.radiusz = radiusz;
    }

    public Pair<Integer, Integer> getRadiusx() {
        return radiusx;
    }

    public void setRadiusx(Pair<Integer, Integer> radiusx) {
        this.radiusx = radiusx;
    }

    public void setEndRadiusX(int endRadiusX) {
        this.radiusx = new Pair<>(radiusx.getLeft(), endRadiusX);
    }

    public void setEndRadiusZ(int endRadiusZ) {
        this.radiusz = new Pair<>(radiusz.getLeft(), endRadiusZ);
    }

    public void setStartRadiusX(int startRadiusX) {
        this.radiusx = new Pair<>(startRadiusX, radiusx.getRight());
    }

    public void setStartRadiusZ(int startRadiusZ) {
        this.radiusz = new Pair<>(startRadiusZ, radiusz.getRight());
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
            this.generateElipsoidSpiral(this.getPos(), chunkMap);
        } else if (this.spiralType == SpiralType.HELICOID || this.spiralType == SpiralType.HALF_HELICOID || this.spiralType == SpiralType.CUSTOM_HELICOOID) {
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
    public void generateElipsoidSpiral(BlockPos pos, Map<ChunkPos, Set<BlockPos>> chunkMap) {
        /*if (this.turnNumber <= 0) {
            Easierworldcreator.LOGGER.error("param turn can't be <= 0");
        }*/
        int maxlarge = Math.max(Math.max(radiusx.getLeft(), radiusx.getRight()), Math.max(radiusz.getLeft(), radiusz.getRight()));
        double f = (this.turnNumber * maxlarge);
        double a = (double) 360 / (height * maxlarge);
        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() == 0) {
            for (double i = 0; i < maxlarge * this.turnNumber * height; i++) {
                float percentage = (float) i / (maxlarge * this.turnNumber * height);
                float radiusx = this.getXradius(percentage);
                float radiusz = this.getZradius(percentage);
                int x = (int) (radiusx * FastMaths.getFastCos(a * i + offset));
                int z = (int) (radiusz * FastMaths.getFastSin(a * i + offset));
                int y = (int) (i / f);
                BlockPos pos1 = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos1, this.getPos()))
                    this.biggerThanChunk = true;
                WorldGenUtil.modifyChunkMap(pos1, chunkMap);
            }
        } else {
            for (double i = 0; i < maxlarge * this.turnNumber * height; i += 0.5) {
                float percentage = (float) i / (maxlarge * this.turnNumber * height);
                float radiusx = this.getXradius(percentage);
                float radiusz = this.getZradius(percentage);
                float x = (float) (radiusx * FastMaths.getFastCos(a * i + offset));
                float z = (float) (radiusz * FastMaths.getFastSin(a * i + offset));
                float y = (float) (i / f);
                BlockPos pos2 = this.getCoordinatesRotation(x, y, z, pos);
                if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos2, this.getPos()))
                    this.biggerThanChunk = true;
                WorldGenUtil.modifyChunkMap(pos2, chunkMap);
            }
        }
    }


    /**
     * this allow the generation of a large outline spiral.
     */
    public void generateLargeOutlineSpiral(Map<ChunkPos, Set<BlockPos>> chunkMap) {
        float angle = (float) Math.atan(height / turnNumber);
        int degangle = (int) Math.toDegrees(angle);
        Vec3d vec = new Vec3d(FastMaths.getFastCos(degangle), FastMaths.getFastSin(degangle), 0).normalize();
        double cosy = FastMaths.getFastCos(degangle);
        double siny = FastMaths.getFastSin(degangle);

        int maxlarge = Math.max(outlineRadiusx, outlineRadiusz);
        // Générer les points du cercle
        for (int i = 0; i < 360; i += 45 / maxlarge) { // Ajuster l'angle selon la densité souhaitée
            double x = outlineRadiusx * FastMaths.getFastCos(i);
            double z = outlineRadiusz * FastMaths.getFastSin(i);
            BlockPos pos = WorldGenUtil.getCoordinatesRotation((float) x, (float) 0, (float) z, 1, 0, cosy, siny, 1, 0, this.getPos());
            if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos())) this.biggerThanChunk = true;
            this.generateElipsoidSpiral(pos, chunkMap);
        }
    }

    /**
     * generates an helicoid if the {@link SpiralType} is set to {@code HELICOID} or {@code DOUBLE_HELICOID} with their variants
     */
    public void generateHelicoid(Map<ChunkPos, Set<BlockPos>> chunkMap) {
        /*if (this.turnNumber <= 0) {
            Easierworldcreator.LOGGER.error("param turn can't be <= 0");
        }*/
        int maxlarge = Math.max(Math.max(radiusx.getLeft(), radiusx.getRight()), Math.max(radiusz.getLeft(), radiusz.getRight()));
        double f = (this.turnNumber * maxlarge);
        double a = (double) 360 / (height * maxlarge);


        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() == 0 && this.helicoidAngle.getLeft() < 45 && this.helicoidAngle.getLeft() > -45 && this.helicoidAngle.getRight() < 45 && this.helicoidAngle.getRight() > -45) {
            for (double i = 0; i < maxlarge * this.turnNumber * height; i++) {

                float percentage = (float) i / (maxlarge * this.turnNumber * height);
                float radiusx = this.getXradius(percentage);
                float radiusz = this.getZradius(percentage);
                float gainx = (float) (radiusx / maxlarge);
                float gainz = (float) (radiusz / maxlarge);

                float innerRadiusX = (1 - this.spiralFilling) * radiusx;
                float innerRadiusZ = (1 - this.spiralFilling) * radiusz;
                float innerRadiusXSquared = innerRadiusX * innerRadiusX;
                float innerRadiusZSquared = innerRadiusZ * innerRadiusZ;

                int helicoidAngle = getAngle(percentage);

                for (double j = 0; j <= maxlarge; j++) {

                    int x = (int) (gainx * j * FastMaths.getFastCos(a * i + offset));
                    int z = (int) (gainz * j * FastMaths.getFastSin(a * i + offset));
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
                        BlockPos pos = new BlockPos((int) (this.getPos().getX() + x), this.getPos().getY() + y, (int) (this.getPos().getZ() + z));
                        if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                            this.biggerThanChunk = true;
                        WorldGenUtil.modifyChunkMap(pos, chunkMap);
                    }
                }
            }
        } else {
            for (double i = 0; i < maxlarge * this.turnNumber * height; i += 0.25) {

                float percentage = (float) i / (maxlarge * this.turnNumber * height);
                float radiusx = this.getXradius(percentage);
                float radiusz = this.getZradius(percentage);
                float gainx = (float) (radiusx / maxlarge);
                float gainz = (float) (radiusz / maxlarge);

                float innerRadiusX = (1 - this.spiralFilling) * radiusx;
                float innerRadiusZ = (1 - this.spiralFilling) * radiusz;
                float innerRadiusXSquared = innerRadiusX * innerRadiusX;
                float innerRadiusZSquared = innerRadiusZ * innerRadiusZ;

                int helicoidAngle = getAngle(percentage);

                for (double j = 0; j <= maxlarge; j++) {

                    int x = (int) (gainx * j * FastMaths.getFastCos(a * i + offset));
                    int z = (int) (gainz * j * FastMaths.getFastSin(a * i + offset));

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
        CUSTOM_HELICOOID,
        //helicoid shape,this generates 2 helicoidswith an opposite direction
        DOUBLE_HELICOID,
        HALF_DOUBLE_HELICOID,
        CUSTOM_DOUBLE_HELICOID,
        LARGE_OUTLINE
    }

    /**
     * this method returns the {@code xradius} depending of the height we are at
     *
     * @param percentage the percentage of the height we are at
     * @return the x radius of the spiral
     */
    public float getXradius(float percentage) {
        return (int) (radiusx.getLeft() * (1 - percentage) + radiusz.getRight() * percentage);
    }

    /**
     * this method returns the {@code zradius} depending of the height we are at
     *
     * @param percentage the percentage of the height we are at
     * @return the x radius of the spiral
     */
    public float getZradius(float percentage) {
        return (int) (radiusz.getLeft() * (1 - percentage) + radiusz.getRight() * percentage);
    }

    /**
     * this method returns the {@code helicoidAngle} depending of the height we are at
     *
     * @param percentage the percentage of the height we are at
     * @return the angle of the spiral
     */
    public int getAngle(float percentage) {
        return (int) (helicoidAngle.getLeft() * (1 - percentage) + helicoidAngle.getRight() * percentage);
    }
}
