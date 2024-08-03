package net.rodofire.easierworldcreator.shapegen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Easierworldcreator;
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

public class SphereGen extends ShapeGen {
    private int xradius;
    private int yradius;
    private int zradius;


    private boolean halfSphere=false;
    private Direction direction=Direction.UP;

    private boolean empty=false;

    //Used for performance test
    private long startTime;


    /**
     * init the shape generation
     *
     * @param world            the world the shape will be generated
     * @param pos              the pos of the structure center
     *                         -------------------------------------------------------------------------------------
     * @param firstlayer       the list of blockstates that will be placed on top of the structure
     * @param secondlayer      the list of blockstates that will be placed in the second layer of the structure
     * @param thirdlayer       the list of blockstates tha will be placed in the third layer of the structure
     *                         these list shouldn't have blocks in common, or you might run into generation problems
     * @param firstlayerdepth  int that represents the depth of the blockstates in the first layer
     * @param secondlayerdepth int that represents the depth of the blockstates in the second layer
     * @param xradius          the radius along the x-axis
     * @param yradius          the radius along the y-axis
     * @param zradius          the radius along the z axis
     * @param xrotation        the rotation along the x-axis
     * @param yrotation        the rotation along the y-axis
     */
    public SphereGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, List<BlockState> firstlayer, List<BlockState> secondlayer, List<BlockState> thirdlayer, int firstlayerdepth, int secondlayerdepth, int xradius, int yradius, int zradius, int xrotation, int yrotation,int seconxrotation, boolean force, List<Block> blockToForce, boolean halfSphere, Direction direction, boolean empty) {
        super(world, pos, firstlayer, secondlayer, thirdlayer, firstlayerdepth, secondlayerdepth, force, blockToForce, xrotation, yrotation, seconxrotation);
        this.xradius = xradius;
        this.yradius = yradius;
        this.zradius = zradius;
        this.halfSphere = halfSphere;
        this.direction = direction;
        this.empty = empty;
    }


    /**
     * init the shape generation
     *
     * @param world  the world the shape will be generated
     * @param pos    the pos of the structure center
     *               -------------------------------------------------------------------------------------
     * @param states list of blockstates that will be placed on all the structure
     * @param radius the radius of the sphere
     */
    public SphereGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, List<BlockState> states, int radius) {
        super(world, pos, states);
        this.xradius = radius;
        this.yradius = radius;
        this.zradius = radius;
        this.empty = false;
    }

    /*---------- Empty Related ----------*/

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public int getXradius() {
        return xradius;
    }

    /*---------- Radius Related ----------*/
    public void setXradius(int xradius) {
        this.xradius = xradius;
    }

    public int getYradius() {
        return yradius;
    }

    public void setYradius(int yradius) {
        this.yradius = yradius;
    }

    public int getZradius() {
        return zradius;
    }

    public void setZradius(int zradius) {
        this.zradius = zradius;
    }

    /*---------- Place Structure ----------*/
    @Override
    public void placeBlocks() {
        this.placeLayers(this.getStructureCoordinates());
        this.getGenTime(this.startTime, true);
    }

    //calculate and return the coordinates
    public List<BlockPos> getStructureCoordinates() {
        this.startTime = System.nanoTime();

        //verify if the rotations == 0 to avoid some unnecessary calculations
        if (empty) {
            if (halfSphere) {
                return this.generateHalfEmptyElipsoid(xradius, yradius, zradius, this.getPos(), direction);
            } else {
                return this.generateEmptyEllipsoid(xradius, yradius, zradius);
            }
        } else {
            if (halfSphere) {
                return this.generateHalfFullElipsoid(xradius, yradius, zradius, this.getPos(), direction);
            } else {
                return this.generateFullEllipsoid(xradius, yradius, zradius, this.getPos());
            }
        }
    }


    public List<BlockPos> generateHalfEmptyElipsoid(int xradius, int yradius, int zradius, BlockPos pos, Direction direction) {
        if (direction == Direction.UP) {
            return generateEmptyEllipsoid(xradius, yradius, zradius, pos, 180, 180, 0, 90);
        }
        if (direction == Direction.DOWN) {
            return generateEmptyEllipsoid(xradius, yradius, zradius, pos, 180, 180, -90, 0);
        }
        if (direction == Direction.WEST) {
            return generateEmptyEllipsoid(xradius, yradius, zradius, pos, 0, 180, -90, 90);
        }
        if (direction == Direction.EAST) {
            return generateEmptyEllipsoid(xradius, yradius, zradius, pos, -180, 0, -90, 90);
        }
        if (direction == Direction.NORTH) {
            return generateEmptyEllipsoid(xradius, yradius, zradius, pos, -90, 90, -90, 90);
        }
        return generateEmptyEllipsoid(xradius, yradius, zradius, pos, 90, 270, -90, 90);
    }

    public List<BlockPos> generateEmptyEllipsoid(int xradius, int yradius, int zradius) {
        return this.generateEmptyEllipsoid(xradius, yradius, zradius, this.getPos(), -180, 180, -90, 90);
    }

    public List<BlockPos> generateEmptyEllipsoid(int xradius, int yradius, int zradius, BlockPos pos, int minlarge, int maxlarge, int minheight, int maxheight) {
        int maxlarge1 = Math.max(zradius, Math.max(xradius, yradius));

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        List<BlockPos> poslist = new ArrayList<>();
        if (this.getYrotation() % 180 == 0) {
            for (double theta = minlarge; theta <= maxlarge; theta += (double) 45 / maxlarge1) {

                double xcostheta = xradius * FastMaths.getFastCos(theta);
                double zsinkheta = zradius * FastMaths.getFastSin(theta);

                for (double phi = minheight; phi <= maxheight; phi += (double) 45 / maxlarge1) {
                    double cosphi = FastMaths.getFastCos(phi);
                    int x = (int) (xcostheta * cosphi);
                    int y = (int) (yradius * FastMaths.getFastSin(phi));
                    int z = (int) (zsinkheta * cosphi);
                    mutable.set(pos, x, y, z);
                    poslist.add(new BlockPos(mutable));
                }
            }
        } else {
            for (double theta = minlarge; theta <= maxlarge; theta += (double) 45 / maxlarge1) {

                double xcostheta = xradius * FastMaths.getFastCos(theta);
                double zsinkheta = zradius * FastMaths.getFastSin(theta);

                for (double phi = minheight; phi <= maxheight; phi += (double) 45 / maxlarge1) {
                    double cosphi = FastMaths.getFastCos(phi);
                    float x = (float) (xcostheta * cosphi);
                    float y = (float) (yradius * FastMaths.getFastSin(phi));
                    float z = (float) (zsinkheta * cosphi);


                    poslist.add(this.getCoordinatesRotation(x, y, z, pos));
                }
            }
        }
        this.getGenTime(this.startTime, false);
        return poslist;
    }


    public List<BlockPos> generateHalfFullElipsoid(int xradius, int yradius, int zradius, BlockPos pos, Direction direction) {
        if (direction == Direction.UP) {
            return this.generateFullEllipsoid(xradius, yradius, zradius, pos, -xradius, xradius, 0, yradius, -zradius, zradius);
        }
        if (direction == Direction.DOWN) {
            return this.generateFullEllipsoid(xradius, yradius, zradius, pos, -xradius, xradius, -yradius, 0, -zradius, zradius);
        }
        if (direction == Direction.WEST) {
            return this.generateFullEllipsoid(xradius, yradius, zradius, pos, 0, xradius, -yradius, yradius, -zradius, zradius);
        }
        if (direction == Direction.EAST) {
            return this.generateFullEllipsoid(xradius, yradius, zradius, pos, -xradius, 0, -yradius, yradius, -zradius, zradius);
        }
        if (direction == Direction.NORTH) {
            return this.generateFullEllipsoid(xradius, yradius, zradius, pos, -xradius, xradius, -yradius, yradius, -zradius, 0);
        }
        return this.generateFullEllipsoid(xradius, yradius, zradius, pos, -xradius, xradius, -yradius, yradius, 0, zradius);
    }

    public List<BlockPos> generateFullEllipsoid(int xradius, int yradius, int zradius, BlockPos pos) {
        return this.generateFullEllipsoid(xradius, yradius, zradius, pos, -xradius, xradius, -yradius, yradius, -zradius, zradius);
    }

    //Using carthesian coordinates beacause it have better performance than using trigonometry

    /**
     * allow you to generate a full elipsoid
     *
     * @param xradius the x axis radius
     * @param yradius the y aris radius
     * @param zradius the z axis radius
     * @param pos     the center of the ellipsoid
     * @param minx    the start of the circle on the x axis
     * @param maxx    the end of the circle on the x axis
     * @param miny    the start of the circle on the y axis
     * @param maxy    the end of the circle on the y axis
     * @param minz    the start of the circle on the z axis
     * @param maxz    the end of the circle on the z axis
     */
    public List<BlockPos> generateFullEllipsoid(int xradius, int yradius, int zradius, BlockPos pos, int minx, int maxx, int miny, int maxy, int minz, int maxz) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        int largexsquared = xradius * xradius;
        int largeysquared = yradius * yradius;
        int largezsquared = zradius * zradius;


        if (xradius > 32 || yradius > 32 || zradius > 32) {
            Easierworldcreator.LOGGER.warn("generating huge sphere (diameter > 64)");
        }
        List<BlockPos> poslist = new ArrayList<BlockPos>();
        if (this.getYrotation() % 180 == 0) {
            for (float x = minx; x <= maxx; x++) {
                float xs = x * x / largexsquared;

                for (float y = miny; y <= maxy; y++) {
                    float ys = y * y / largeysquared + xs;

                    for (float z = minz; z <= maxz; z++) {
                        if (ys + (z * z) / (largezsquared) <= 1) {
                            mutable.set(pos, (int) x, (int) y, (int) z);
                            poslist.add(new BlockPos(mutable));
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
                            poslist.add(getCoordinatesRotation(x, y, z, pos));
                        }
                    }
                }
            }
        }
        this.getGenTime(this.startTime, false);
        return poslist;
    }
}
