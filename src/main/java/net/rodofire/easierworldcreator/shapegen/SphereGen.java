package net.rodofire.easierworldcreator.shapegen;

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
 * Since 2.1.0, the shape doesn't return a {@link List<BlockPos>} but it returns instead a {@code List<Set <BlockPos>>}
 * Before 2.1.0, the BlockPos list was a simple list.
 * Starting from 2.1.0, the shapes returns a list of {@link ChunkPos} that has a set of {@link BlockPos}
 * The change from {@link List} to {@link Set} was done to avoid duplicates BlockPos which resulted in unnecessary calculations.
 * this allow easy multithreading for the Block assignment done in the {@link Shape} which result in better performance;
 * </p>
 */
@SuppressWarnings("unused")
public class SphereGen extends FillableShape {
    private int radiusX;
    private int radiusY;
    private int radiusZ;


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
     * @param layerPlace      how the {@code @BlockStates} inside of a {@link BlockLayer} will be placed
     * @param layersType      how the Layers will be placed
     * @param xRotation       first rotation around the x-axis
     * @param yRotation       second rotation around the y-axis
     * @param secondXRotation last rotation around the x-axis
     * @param featureName     the name of the feature
     * @param radiusX         the radius on the x-axis
     * @param radiusY         the radius on the y-axis
     * @param radiusZ         the radius on the z-axis
     * @param halfSphere      determines if the sphere is half or not
     */
    public SphereGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment, LayerPlace layerPlace, LayersType layersType, int xRotation, int yRotation, int secondXRotation, String featureName, int radiusX, int radiusY, int radiusZ, SphereType halfSphere) {
        super(world, pos, placeMoment, layerPlace, layersType, xRotation, yRotation, secondXRotation, featureName);
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.radiusZ = radiusZ;
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
        this.radiusX = radius;
        this.radiusY = radius;
        this.radiusZ = radius;
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

    public int getRadiusX() {
        return radiusX;
    }

    public void setRadiusX(int radiusX) {
        this.radiusX = radiusX;
    }

    public int getRadiusY() {
        return radiusY;
    }

    public void setRadiusY(int radiusY) {
        this.radiusY = radiusY;
    }

    public int getRadiusZ() {
        return radiusZ;
    }

    public void setRadiusZ(int radiusZ) {
        this.radiusZ = radiusZ;
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
                this.generateHalfEmptyEllipsoid(chunkMap);
            } else {
                this.generateEmptyEllipsoid(chunkMap);
            }
        } else {
            if (this.halfSphere == SphereType.HALF) {
                this.generateHalfFullEllipsoid(chunkMap);
            } else {
                this.generateFullEllipsoid(chunkMap);
            }
        }
        return new ArrayList<>(chunkMap.values());
    }


    public void generateHalfEmptyEllipsoid(Map<ChunkPos, Set<BlockPos>> chunkMap) {
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

    public void generateEmptyEllipsoid(int minLarge, int maxLarge, int minHeight, int maxHeight, Map<ChunkPos, Set<BlockPos>> chunkMap) {
        int maxLarge1 = Math.max(radiusZ, Math.max(radiusX, radiusY));

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        List<BlockPos> poslist = new ArrayList<>();
        if (this.getXRotation() % 180 == 0 && this.getYRotation() % 180 == 0 && this.getSecondXRotation() % 180 == 0) {
            for (float theta = minLarge; theta <= maxLarge; theta += (float) 45 / maxLarge1) {

                double xCosTheta = radiusX * FastMaths.getFastCos(theta);
                double zSinTheta = radiusZ * FastMaths.getFastSin(theta);


                for (float phi = minHeight; phi <= maxHeight; phi += (float) 45 / maxLarge1) {
                    double cosPhi = FastMaths.getFastCos(phi);
                    int x = (int) (xCosTheta * cosPhi);
                    int y = (int) (radiusY * FastMaths.getFastSin(phi));
                    int z = (int) (zSinTheta * cosPhi);
                    BlockPos pos = new BlockPos(this.getPos().getX() + x, this.getPos().getY() + y, this.getPos().getZ() + z);
                    if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                        this.biggerThanChunk = true;
                    WorldGenUtil.modifyChunkMap(pos, chunkMap);
                    System.out.println(x + " " + y + " " + z);
                }
            }
        } else {
            for (float theta = minLarge; theta <= maxLarge; theta += (float) 45 / maxLarge1) {

                float xCosTheta = radiusX * FastMaths.getFastCos(theta);
                float zSinTheta = radiusZ * FastMaths.getFastSin(theta);

                for (float phi = minHeight; phi <= maxHeight; phi += (float) 45 / maxLarge1) {
                    float cosPhi = FastMaths.getFastCos(phi);

                    float x = xCosTheta * cosPhi;
                    float y = (radiusY * FastMaths.getFastSin(phi));
                    float z = zSinTheta * cosPhi;
                    BlockPos pos = this.getCoordinatesRotation(x, y, z, this.getPos());
                    if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                        this.biggerThanChunk = true;
                    WorldGenUtil.modifyChunkMap(pos, chunkMap);
                }
            }
        }
    }


    public void generateHalfFullEllipsoid(Map<ChunkPos, Set<BlockPos>> chunkMap) {
        if (direction == Direction.UP) {
            this.generateFullEllipsoid(-radiusX, radiusX, 0, radiusY, -radiusZ, radiusZ, chunkMap);
        } else if (direction == Direction.DOWN) {
            this.generateFullEllipsoid(-radiusX, radiusX, -radiusY, 0, -radiusZ, radiusZ, chunkMap);
        } else if (direction == Direction.WEST) {
            this.generateFullEllipsoid(0, radiusX, -radiusY, radiusY, -radiusZ, radiusZ, chunkMap);
        } else if (direction == Direction.EAST) {
            this.generateFullEllipsoid(-radiusX, 0, -radiusY, radiusY, -radiusZ, radiusZ, chunkMap);
        } else if (direction == Direction.NORTH) {
            this.generateFullEllipsoid(-radiusX, radiusX, -radiusY, radiusY, -radiusZ, 0, chunkMap);
        } else {
            this.generateFullEllipsoid(-radiusX, radiusX, -radiusY, radiusY, 0, radiusZ, chunkMap);
        }
    }

    public void generateFullEllipsoid(Map<ChunkPos, Set<BlockPos>> chunkMap) {
        this.generateFullEllipsoid(-radiusX, radiusX, -radiusY, radiusY, -radiusZ, radiusZ, chunkMap);
    }

    //Using cartesian coordinates because it has better performance than using trigonometry

    /**
     * allow you to generate a full ellipsoid
     *
     * @param minX the start of the circle on the x-axis
     * @param maxX the end of the circle on the x-axis
     * @param minY the start of the circle on the y-axis
     * @param maxY the end of the circle on the y-axis
     * @param minZ the start of the circle on the z-axis
     * @param maxZ the end of the circle on the z-axis
     */
    public void generateFullEllipsoid(int minX, int maxX, int minY, int maxY, int minZ, int maxZ, Map<ChunkPos, Set<BlockPos>> chunkMap) {
        this.setFill();
        int largeXSquared = radiusX * radiusX;
        int largeYSquared = radiusY * radiusY;
        int largeZSquared = radiusZ * radiusZ;

        float innerRadiusXSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * largeXSquared;
        float innerRadiusYSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * largeYSquared;
        float innerRadiusZSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * largeZSquared;


        if (radiusX > 32 || radiusY > 32 || radiusZ > 32) {
            Easierworldcreator.LOGGER.warn("generating huge sphere (diameter > 64)");
        }
        if (this.getXRotation() % 180 == 0 && this.getYRotation() % 180 == 0 && this.getSecondXRotation() % 180 == 0) {
            for (float x = minX; x <= maxX; x++) {
                float xx = x * x;
                float xs = xx / largeXSquared;

                for (float y = minY; y <= maxY; y++) {
                    float yy = y * y;
                    float ys = yy / largeYSquared + xs;

                    for (float z = minZ; z <= maxZ; z++) {
                        float zz = z * z;
                        if (ys + (zz) / (largeZSquared) <= 1) {
                            boolean bl = true;
                            if (innerRadiusXSquared != 0) {
                                float innerXSquared = xx / innerRadiusXSquared;
                                float innerYSquared = yy / innerRadiusYSquared;
                                float innerZSquared = zz / innerRadiusZSquared;
                                if (innerXSquared + innerZSquared + innerYSquared <= 1f) {
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
            for (float x = minX; x <= maxX; x += 0.5f) {
                float xx = x * x;
                float xs = xx / largeXSquared;

                for (float y = minY; y <= maxY; y += 0.5f) {
                    float yy = y * y;
                    float ys = yy / largeYSquared + xs;

                    for (float z = minZ; z <= maxZ; z += 0.5f) {
                        float zz = z * z;
                        if (ys + (zz) / (largeZSquared) <= 1) {
                            boolean bl = true;
                            if (innerRadiusXSquared != 0) {
                                float innerXSquared = xx / innerRadiusXSquared;
                                float innerYSquared = yy / innerRadiusYSquared;
                                float innerZSquared = zz / innerRadiusZSquared;
                                if (innerXSquared + innerZSquared + innerYSquared <= 1f) {
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
