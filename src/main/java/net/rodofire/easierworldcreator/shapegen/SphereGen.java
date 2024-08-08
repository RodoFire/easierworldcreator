package net.rodofire.easierworldcreator.shapegen;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Easierworldcreator;
import net.rodofire.easierworldcreator.shapeutil.BlockLayer;
import net.rodofire.easierworldcreator.shapeutil.FillableShape;
import net.rodofire.easierworldcreator.util.FastMaths;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to generate 3d spheres / Ellipsoid
 */

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

public class SphereGen extends FillableShape {
    private int radiusx;
    private int radiusy;
    private int radiusz;


    private boolean halfSphere = false;
    private Direction direction = Direction.UP;


    //Used for performance test
    private long startTime;


    /**
     * init the shape generation
     *
     * @param world     the world the shape will be generated
     * @param pos       the pos of the structure center
     * @param radiusx   the radius along the x-axis
     * @param radiusy   the radius along the y-axis
     * @param radiusz   the radius along the z axis
     * @param xrotation the rotation along the x-axis
     * @param yrotation the rotation along the y-axis
     */
    public SphereGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, int radiusx, List<BlockLayer> layers, int radiusy, int radiusz, int xrotation, int yrotation, int seconxrotation, boolean force, List<Block> blockToForce, boolean halfSphere, Direction direction) {
        super(world, pos, layers, force, blockToForce, xrotation, yrotation, seconxrotation);
        this.radiusx = radiusx;
        this.radiusy = radiusy;
        this.radiusz = radiusz;
        this.halfSphere = halfSphere;
        this.direction = direction;
    }


    /**
     * init the shape generation
     *
     * @param world  the world the shape will be generated
     * @param pos    the pos of the structure center
     * @param radius the radius of the sphere
     */
    public SphereGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, int radius) {
        super(world, pos);
        this.radiusx = radius;
        this.radiusy = radius;
        this.radiusz = radius;
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
    public List<BlockPos> getBlockPos() {
        return this.getStructureCoordinates();
    }

    @Override
    public List<Vec3d> getVec3d() {
        return List.of();
    }

    //calculate and return the coordinates
    public List<BlockPos> getStructureCoordinates() {
        this.startTime = System.nanoTime();

        //verify if the rotations == 0 to avoid some unnecessary calculations
        if (this.getFillingType() == Type.EMPTY) {
            if (halfSphere) {
                return this.generateHalfEmptyElipsoid();
            } else {
                return this.generateEmptyEllipsoid();
            }
        } else {
            if (halfSphere) {
                return this.generateHalfFullElipsoid();
            } else {
                return this.generateFullEllipsoid();
            }
        }
    }


    public List<BlockPos> generateHalfEmptyElipsoid() {
        if (direction == Direction.UP) {
            return generateEmptyEllipsoid(180, 180, 0, 90);
        }
        if (direction == Direction.DOWN) {
            return generateEmptyEllipsoid(180, 180, -90, 0);
        }
        if (direction == Direction.WEST) {
            return generateEmptyEllipsoid(0, 180, -90, 90);
        }
        if (direction == Direction.EAST) {
            return generateEmptyEllipsoid(-180, 0, -90, 90);
        }
        if (direction == Direction.NORTH) {
            return generateEmptyEllipsoid(-90, 90, -90, 90);
        }
        return generateEmptyEllipsoid(90, 270, -90, 90);
    }

    public List<BlockPos> generateEmptyEllipsoid() {
        return this.generateEmptyEllipsoid(-180, 180, -90, 90);
    }

    public List<BlockPos> generateEmptyEllipsoid(int minlarge, int maxlarge, int minheight, int maxheight) {
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
                    mutable.set(this.getPos(), x, y, z);
                    poslist.add(new BlockPos(mutable));
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


                    poslist.add(this.getCoordinatesRotation(x, y, z, this.getPos()));
                }
            }
        }
        this.getGenTime(this.startTime, false);
        return poslist;
    }


    public List<BlockPos> generateHalfFullElipsoid() {
        if (direction == Direction.UP) {
            return this.generateFullEllipsoid(-radiusx, radiusx, 0, radiusy, -radiusz, radiusz);
        }
        if (direction == Direction.DOWN) {
            return this.generateFullEllipsoid(-radiusx, radiusx, -radiusy, 0, -radiusz, radiusz);
        }
        if (direction == Direction.WEST) {
            return this.generateFullEllipsoid(0, radiusx, -radiusy, radiusy, -radiusz, radiusz);
        }
        if (direction == Direction.EAST) {
            return this.generateFullEllipsoid(-radiusx, 0, -radiusy, radiusy, -radiusz, radiusz);
        }
        if (direction == Direction.NORTH) {
            return this.generateFullEllipsoid(-radiusx, radiusx, -radiusy, radiusy, -radiusz, 0);
        }
        return this.generateFullEllipsoid(-radiusx, radiusx, -radiusy, radiusy, 0, radiusz);
    }

    public List<BlockPos> generateFullEllipsoid() {
        return this.generateFullEllipsoid(-radiusx, radiusx, -radiusy, radiusy, -radiusz, radiusz);
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
    public List<BlockPos> generateFullEllipsoid(int minx, int maxx, int miny, int maxy, int minz, int maxz) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        this.setFill();
        int largexsquared = radiusx * radiusx;
        int largeysquared = radiusy * radiusy;
        int largezsquared = radiusz * radiusz;

        float innerRadiusXSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusx * radiusx;
        float innerRadiusYSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusy * radiusy;
        float innerRadiusZSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusz * radiusz;



        if (radiusx > 32 || radiusy > 32 || radiusz > 32) {
            Easierworldcreator.LOGGER.warn("generating huge sphere (diameter > 64)");
        }
        List<BlockPos> poslist = new ArrayList<BlockPos>();
        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() % 180 == 0) {
            for (float x = minx; x <= maxx; x++) {
                float xs = x * x / largexsquared;

                for (float y = miny; y <= maxy; y++) {
                    float ys = y * y / largeysquared + xs;

                    for (float z = minz; z <= maxz; z++) {
                        if (ys + (z * z) / (largezsquared) <= 1) {
                            boolean bl = true;
                            if (innerRadiusXSquared != 0) {
                                float innerXSquared = x * x / innerRadiusXSquared;
                                float innerYSquared = y * y / innerRadiusYSquared;
                                float innerZSquared = z * z / innerRadiusZSquared;
                                if (innerXSquared + innerZSquared + innerYSquared <= 1f) { // pas dans l'ovale intérieur
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
            for (float x = minx; x <= maxx; x += 0.5f) {
                float xs = x * x / largexsquared;

                for (float y = miny; y <= maxy; y += 0.5f) {
                    float ys = y * y / largeysquared + xs;

                    for (float z = minz; z <= maxz; z += 0.5f) {
                        if (ys + (z * z) / (largezsquared) <= 1) {
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
        this.getGenTime(this.startTime, false);
        return poslist;
    }


}
