package net.rodofire.easierworldcreator.shapegen;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Easierworldcreator;
import net.rodofire.easierworldcreator.shapeutil.BlockLayer;
import net.rodofire.easierworldcreator.shapeutil.FillableShape;
import net.rodofire.easierworldcreator.shapeutil.Shape;
import net.rodofire.easierworldcreator.util.FastMaths;
import net.rodofire.easierworldcreator.worldgenutil.FastNoiseLite;
import net.rodofire.easierworldcreator.worldgenutil.WorldGenUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/*



                                ..:::::::::....
                      .-==-==-::==-:--==+=======----::-=-:.
                 .-=---=::::-=+=+-:::-=---===--=-::--:::==--::-=-:.
              =+=--==:--====-========-=---=++=+-===+=----:-==-:::-:::-+-
           :*====+*++++++++++=====--:-+*=-=+=**===*++==::==-::-+==:::-::::+*:
         =#=----=+-=====+*======*====++++===++******+**+=+++=-==-:---:::--:::-==:
       *##==+*+=+--------+::--=+#++++*+++++=++*##%@@@@@%#%++===::--:--+++==+-::::::-.
      *##*====-=++===++=-==::---=+=-====--====+=+++##*###*++=+*-::==----=::-:::..::-:::.
     *****==*+===+=-----=++=---:::-++=--:=+=--==-+-==*=+===-====-:-*-::.-+-....-:--:::..:=
    .***##=---====+++=------+====-:::==+-:::=+--=:.--=*=*==-:-:==--=::.-=-:...-=-.....:=::.:
    .*#****====++===+-:--=+==--=-:-+--:-==*-:::=:-=+--===++--=-==.::-::-=-..::---....:-::..::=
    .*++**+#+=--=-=-==*+--------=++-:::-=::::=#+-:-=::-=:.--::-:::=--==-...:=:.:-.=-.....-:...:=
     #*****+*+=====++-=-+=---==------++=-:::-=::::-::-=+==-=+===+:..:-.:.--:..---:....-::- ..::.::
     *@**+++++++++=-=====--+*::-----=------+#-::::+::::-:::::+:::::::.:=:..=+=.....-:...::::.  ..=.:
     :@@#++*++**++===========-=+=-==-------+:::-:=#==---*----==----+=:-==-:..:.-:.....-=- .. -:. :.::
      -@@%*++*+++++++==+=-------=+==++--:-=::::::-=:::::=:::::--:::::-::..:--:...::-.::...: .  ..-...:
       -%%@%*+=++++++**+=-=--=-=+-------+*=:--:::+::-::::*::::::=:::::--::...-::.::==-. ....-. ..-... .
         #@@%%*+++++*==+++++==+=--------==----=+=#===-:::=:::::::=::::::-::-=--==......-......::-   :.-
          .@%##%#++*++===++=+**+--------+--------+--------=:----=+=-:::::=:::::::=::.....=....::.:  . -
            -#####%*+====++=*+====+++=-+------------------+::::-::=-::::::-=:::::::-:..:..:--:....:  .+
              .*%######*=+=++=========*==+*+----=-----------::--:::+::::::::-:::::::-:::=-:::=......::=
                 =#########*=========+=========+#*+=--=---==::::::::+::::::::=::---====:::::::-.....-+-
                    *%######*##**====+==========+================--==+=--====-+-:::-----:::::::-::====
                      -@%#########*##+==========+============---==---=---------+--------=:::::-=*=--=:
                         .*@%#########*****+++===-=========+===-====-==--------==--------=-=++==*==:
                             :@@@%#*##**##************+====*----------*-------------=+*+++*+=++=*-
                                 :*%@@%#****#####**********#*******************++*++++++++*=+=-.
                                      .+#@%%%%##*+*********#************+++++++++*+++++++*-
                                             :+%%%%%%%%###*#************+++********#%%:
                                                     .:-=+##%%%%%%%%%%%%%%#***=:.
 */

/**
 * Class to generate Sphere related shapes
 * <p>
 * Since 2.1.0, the shape doesn't return a {@link List<BlockPos>} but it returns instead a {@link List< Set  <BlockPos>>}
 * Before 2.1.0, the BlockPos list was a simple list.
 * Starting from 2.1.0, the shapes returns a list of {@link ChunkPos} that has a set of {@link BlockPos}
 * The change from {@link List} to {@link Set} was done to avoid duplicates BlockPos wich resulted in unnecessary calculations.
 * this allow easy multithreading for the Block assignment done in the {@link Shape} which result in better performance;
 * </p>
 */
public class SphereGen extends FillableShape {
    private int radiusx;
    private int radiusy;
    private int radiusz;


    private SphereType halfSphere = SphereType.DEFAULT;

    private Direction direction = Direction.UP;


    //Used for performance test
    private long startTime;


    /**
     * init the Sphere Shape
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
     * @param radiusx         the radius on the x-axis
     * @param radiusy         the radius on the y-axis
     * @param radiusz         the radius on the z-axis
     * @param halfSphere      determines if the sphere is half or not
     */
    public SphereGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment, boolean force, List<Block> blocksToForce, LayerPlace layerPlace, LayersType layersType, int xrotation, int yrotation, int secondxrotation, String featureName, int radiusx, int radiusy, int radiusz, SphereType halfSphere) {
        super(world, pos, placeMoment, force, blocksToForce, layerPlace, layersType, xrotation, yrotation, secondxrotation, featureName);
        this.radiusx = radiusx;
        this.radiusy = radiusy;
        this.radiusz = radiusz;
        this.halfSphere = halfSphere;
    }

    /**
     * init the shape generation
     *
     * @param world       the world the shape will be generated
     * @param pos         the pos of the structure center
     * @param placeMoment define the moment where the shape will be placed
     * @param radius      the radius of the sphere
     */
    public SphereGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment, int radius) {
        super(world, pos, placeMoment);
        this.radiusx = radius;
        this.radiusy = radius;
        this.radiusz = radius;
    }

    public Direction getHalfSphereDirection() {
        return direction;
    }

    public void setHalfSphereDirection(Direction direction) {
        this.direction = direction;
    }

    public SphereType isHalfSphere() {
        return halfSphere;
    }

    public void setHalfSphere(SphereType halfSphere) {
        this.halfSphere = halfSphere;
    }


    /*---------- Radius Related ----------*/

    public int getRadiusx() {
        return radiusx;
    }

    public void setRadiusx(int radiusx) {
        this.radiusx = radiusx;
    }

    public int getRadiusy() {
        return radiusy;
    }

    public void setRadiusy(int radiusy) {
        this.radiusy = radiusy;
    }

    public int getRadiusz() {
        return radiusz;
    }

    public void setRadiusz(int radiusz) {
        this.radiusz = radiusz;
    }

    @Override
    public List<Set<BlockPos>> getBlockPos() {
        return this.getSphereCoordinates();
    }

    @Override
    public List<Vec3d> getVec3d() {
        return List.of();
    }

    //calculate and return the coordinates
    public List<Set<BlockPos>> getSphereCoordinates() {
        Map<ChunkPos, Set<BlockPos>> chunkMap = new HashMap<>();

        //verify if the rotations == 0 to avoid some unnecessary calculations
        if (this.getFillingType() == Type.EMPTY) {
            if (this.halfSphere == SphereType.HALF) {
                this.generateHalfEmptyElipsoid(chunkMap);
            } else {
                this.generateEmptyEllipsoid(chunkMap);
            }
        } else {
            if (this.halfSphere == SphereType.HALF) {
                this.generateHalfFullElipsoid(chunkMap);
            } else {
                this.generateFullEllipsoid(chunkMap);
            }
        }
        return new ArrayList<>(chunkMap.values());
    }


    public void generateHalfEmptyElipsoid(Map<ChunkPos, Set<BlockPos>> chunkMap) {
        if (direction == Direction.UP) {
            generateEmptyEllipsoid(-180, 180, 0, 90, chunkMap);
        } else if (direction == Direction.DOWN) {
            generateEmptyEllipsoid(-180, 180, -90, 0, chunkMap);
        } else if (direction == Direction.WEST) {
            generateEmptyEllipsoid(0, 180, -90, 90, chunkMap);
        } else if (direction == Direction.EAST) {
            generateEmptyEllipsoid(-180, 0, -90, 90, chunkMap);
        } else if (direction == Direction.NORTH) {
            generateEmptyEllipsoid(-90, 90, -90, 90, chunkMap);
        } else {
            generateEmptyEllipsoid(90, 270, -90, 90, chunkMap);
        }
    }

    public void generateEmptyEllipsoid(Map<ChunkPos, Set<BlockPos>> chunkMap) {
        this.generateEmptyEllipsoid(-180, 180, -90, 90, chunkMap);
    }

    public void generateEmptyEllipsoid(int minlarge, int maxlarge, int minheight, int maxheight, Map<ChunkPos, Set<BlockPos>> chunkMap) {
        int maxlarge1 = Math.max(radiusz, Math.max(radiusx, radiusy));

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        List<BlockPos> poslist = new ArrayList<>();
        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() % 180 == 0) {
            for (double theta = minlarge; theta <= maxlarge; theta += (double) 45 / maxlarge1) {

                double xcostheta = radiusx * FastMaths.getFastCos(theta);
                double zsinkheta = radiusz * FastMaths.getFastSin(theta);

                for (double phi = minheight; phi <= maxheight; phi += (double) 45 / maxlarge1) {
                    double cosphi = FastMaths.getFastCos(phi);
                    int x = (int) (xcostheta * cosphi);
                    int y = (int) (radiusy * FastMaths.getFastSin(phi));
                    int z = (int) (zsinkheta * cosphi);
                    BlockPos pos = new BlockPos(this.getPos().getX() + x, this.getPos().getY() + y, this.getPos().getZ() + z);
                    if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                        this.biggerThanChunk = true;
                    WorldGenUtil.modifyChunkMap(pos, chunkMap);
                    System.out.println(x + " " + y + " " + z);
                }
            }
        } else {
            for (double theta = minlarge; theta <= maxlarge; theta += (double) 45 / maxlarge1) {

                double xcostheta = radiusx * FastMaths.getFastCos(theta);
                double zsinkheta = radiusz * FastMaths.getFastSin(theta);

                for (double phi = minheight; phi <= maxheight; phi += (double) 45 / maxlarge1) {
                    double cosphi = FastMaths.getFastCos(phi);

                    float x = (float) (xcostheta * cosphi);
                    float y = (float) (radiusy * FastMaths.getFastSin(phi));
                    float z = (float) (zsinkheta * cosphi);
                    BlockPos pos = this.getCoordinatesRotation(x, y, z, this.getPos());
                    if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                        this.biggerThanChunk = true;
                    WorldGenUtil.modifyChunkMap(pos, chunkMap);
                }
            }
        }
    }


    public void generateHalfFullElipsoid(Map<ChunkPos, Set<BlockPos>> chunkMap) {
        if (direction == Direction.UP) {
            this.generateFullEllipsoid(-radiusx, radiusx, 0, radiusy, -radiusz, radiusz, chunkMap);
        } else if (direction == Direction.DOWN) {
            this.generateFullEllipsoid(-radiusx, radiusx, -radiusy, 0, -radiusz, radiusz, chunkMap);
        } else if (direction == Direction.WEST) {
            this.generateFullEllipsoid(0, radiusx, -radiusy, radiusy, -radiusz, radiusz, chunkMap);
        } else if (direction == Direction.EAST) {
            this.generateFullEllipsoid(-radiusx, 0, -radiusy, radiusy, -radiusz, radiusz, chunkMap);
        } else if (direction == Direction.NORTH) {
            this.generateFullEllipsoid(-radiusx, radiusx, -radiusy, radiusy, -radiusz, 0, chunkMap);
        } else {
            this.generateFullEllipsoid(-radiusx, radiusx, -radiusy, radiusy, 0, radiusz, chunkMap);
        }
    }

    public void generateFullEllipsoid(Map<ChunkPos, Set<BlockPos>> chunkMap) {
        this.generateFullEllipsoid(-radiusx, radiusx, -radiusy, radiusy, -radiusz, radiusz, chunkMap);
    }

    //Using carthesian coordinates beacause it have better performance than using trigonometry

    /**
     * allow you to generate a full elipsoid
     *
     * @param minx the start of the circle on the x axis
     * @param maxx the end of the circle on the x axis
     * @param miny the start of the circle on the y axis
     * @param maxy the end of the circle on the y axis
     * @param minz the start of the circle on the z axis
     * @param maxz the end of the circle on the z axis
     */
    public void generateFullEllipsoid(int minx, int maxx, int miny, int maxy, int minz, int maxz, Map<ChunkPos, Set<BlockPos>> chunkMap) {
        this.setFill();
        int largexsquared = radiusx * radiusx;
        int largeysquared = radiusy * radiusy;
        int largezsquared = radiusz * radiusz;

        float innerRadiusXSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * largexsquared;
        float innerRadiusYSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * largeysquared;
        float innerRadiusZSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * largezsquared;


        if (radiusx > 32 || radiusy > 32 || radiusz > 32) {
            Easierworldcreator.LOGGER.warn("generating huge sphere (diameter > 64)");
        }
        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() % 180 == 0) {
            for (float x = minx; x <= maxx; x++) {
                float xx = x * x;
                float xs = xx / largexsquared;

                for (float y = miny; y <= maxy; y++) {
                    float yy = y * y;
                    float ys = yy / largeysquared + xs;

                    for (float z = minz; z <= maxz; z++) {
                        float zz = z * z;
                        if (ys + (zz) / (largezsquared) <= 1) {
                            boolean bl = true;
                            if (innerRadiusXSquared != 0) {
                                float innerXSquared = xx / innerRadiusXSquared;
                                float innerYSquared = yy / innerRadiusYSquared;
                                float innerZSquared = zz / innerRadiusZSquared;
                                if (innerXSquared + innerZSquared + innerYSquared <= 1f) { // pas dans l'ovale intérieur
                                    bl = false;
                                }
                            }
                            if (bl) {
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
            for (float x = minx; x <= maxx; x += 0.5f) {
                float xx = x * x;
                float xs = xx / largexsquared;

                for (float y = miny; y <= maxy; y += 0.5f) {
                    float yy = y * y;
                    float ys = yy / largeysquared + xs;

                    for (float z = minz; z <= maxz; z += 0.5f) {
                        float zz = z * z;
                        if (ys + (zz) / (largezsquared) <= 1) {
                            boolean bl = true;
                            if (innerRadiusXSquared != 0) {
                                float innerXSquared = xx / innerRadiusXSquared;
                                float innerYSquared = yy / innerRadiusYSquared;
                                float innerZSquared = zz / innerRadiusZSquared;
                                if (innerXSquared + innerZSquared + innerYSquared <= 1f) { // pas dans l'ovale intérieur
                                    bl = false;
                                }
                            }
                            if (bl) {
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

    public enum SphereType {
        HALF,
        DEFAULT
    }


}
