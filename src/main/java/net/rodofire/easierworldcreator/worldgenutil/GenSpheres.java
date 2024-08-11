package net.rodofire.easierworldcreator.worldgenutil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Easierworldcreator;
import net.rodofire.easierworldcreator.util.FastMaths;
import net.rodofire.easierworldcreator.shapegen.SphereGen;

import java.util.List;

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
@Deprecated(forRemoval = false)
/**
 * switch to new generation
 * @see SphereGen
 * this class will not be updated anymore and won't receive any support
 */
public class GenSpheres {
    public static void generateHalfFullSphere(StructureWorldAccess world, int radius, BlockPos pos, Direction direction, BlockState state) {
        generateHalfFullElipsoid(world, radius, radius, radius, pos, direction, List.of(state));
    }

    public static void generateHalfFullSphere(StructureWorldAccess world, int radius, BlockPos pos, Direction direction, List<BlockState> state) {
        generateHalfFullElipsoid(world, radius, radius, radius, pos, direction, state);
    }


    public static void generateHalfFullElipsoid(StructureWorldAccess world, int largex, int largey, int largez, BlockPos pos, Direction direction, BlockState state) {
        generateHalfFullElipsoid(world, largex, largey, largez, pos, direction, List.of(state));
    }

    public static void generateHalfFullElipsoid(StructureWorldAccess world, int largex, int largey, int largez, BlockPos pos, Direction direction, List<BlockState> state) {
        if (direction == Direction.UP) {
            generateFullEllipsoid(world, largex, largey, largez, pos, false, -largex, largex, 0, largey, -largez, largez, null, state);
            return;
        }
        if (direction == Direction.DOWN) {
            generateFullEllipsoid(world, largex, largey, largez, pos, false, -largex, largex, -largey, 0, -largez, largez, null, state);
            return;
        }
        if (direction == Direction.WEST) {
            generateFullEllipsoid(world, largex, largey, largez, pos, false, 0, largex, -largey, largey, -largez, largez, null, state);
            return;
        }
        if (direction == Direction.EAST) {
            generateFullEllipsoid(world, largex, largey, largez, pos, false, -largex, 0, -largey, largey, -largez, largez, null, state);
            return;
        }
        if (direction == Direction.NORTH) {
            generateFullEllipsoid(world, largex, largey, largez, pos, false, -largex, largex, -largey, largey, -largez, 0, null, state);
            return;
        }
        generateFullEllipsoid(world, largex, largey, largez, pos, false, -largex, largex, -largey, largey, 0, largez, null, state);
    }


    //Using carthesian coordinates beacause it have better performance than using trigonometry

    /**
     * allow you to generate a full elipsoid
     *
     * @param world         the world it will spawn in
     * @param largex        the x axis radius
     * @param largey        the y aris radius
     * @param largez        the z axis radius
     * @param pos           the center of the ellipsoid
     * @param force         force the putting of the blocks
     * @param minx          the start of the circle on the x axis
     * @param maxx          the end of the circle on the x axis
     * @param miny          the start of the circle on the y axis
     * @param maxy          the end of the circle on the y axis
     * @param minz          the start of the circle on the z axis
     * @param maxz          the end of the circle on the z axis
     * @param blocksToForce list of blocks that the structure can still force if force = true
     * @param blocksToPlace list of blockState that will be placed randomly (if only one blockstate is given, the block will be the only block put
     */
    public static void generateFullEllipsoid(StructureWorldAccess world, int largex, int largey, int largez, BlockPos pos, boolean force, int minx, int maxx, int miny, int maxy, int minz, int maxz, List<Block> blocksToForce, List<BlockState> blocksToPlace) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int length = blocksToPlace.size() - 1;
        int largexsquared = largex * largex;
        int largeysquared = largey * largey;
        int largezsquared = largez * largez;
        if (largex > 32 || largey > 32 || largez > 32) {
            Easierworldcreator.LOGGER.warn("generating huge sphere (diameter > 64)");
        }
        for (float x = minx; x <= maxx; x++) {
            float xs = x*x/largexsquared;
            for (float y = miny; y <= maxy; y++) {
                float ys = y*y/largeysquared + xs;
                for (float z = minz; z <= maxz; z++) {
                    if (ys + (z * z) / (largezsquared) <= 1) {
                        mutable.set(pos, (int) x, (int) y, (int) z);
                        BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            }
        }
    }

    public static void generateSphere(StructureWorldAccess world, int radius, BlockPos pos, BlockState state) {
        generateFullEllipsoid(world, radius, radius, radius, pos, false, -radius, radius, -radius, radius, -radius, radius, null, List.of(state));
    }

    public static void generateSphere(StructureWorldAccess world, int radius, BlockPos pos, BlockState state, boolean force) {
        generateFullEllipsoid(world, radius, radius, radius, pos, force, -radius, radius, -radius, radius, -radius, radius, null, List.of(state));
    }

    public static void generateFullElipsoid(StructureWorldAccess world, int largex, int largey, int largez, BlockPos pos, BlockState state) {
        generateFullEllipsoid(world, largex, largey, largez, pos, false, -largex, largex, -largey, largey, -largez, largez, null, List.of(state));
    }

    public static void generateFullElipsoid(StructureWorldAccess world, int largex, int largey, int largez, BlockPos pos, BlockState state, boolean force) {
        generateFullEllipsoid(world, largex, largey, largez, pos, force, -largex, largex, -largey, largey, -largez, largez, null, List.of(state));
    }


    //better performance when generating an empty sphere

    /**
     * generate an empty elipsoid
     *
     * @param world         the world the structure will spawn in
     * @param largex        the x radius
     * @param largey        the y radius
     * @param largez        the z radius
     * @param pos           the center of the elipsoid
     * @param force         force the spawn of the structure by replacing already existing blocks
     * @param minlarge      the start of the horizontal radius (for a full elipsoid put 0 - 360 or -180 - 180)
     * @param maxlarge      the end of the horizontal radius
     * @param minheight     the start of the vertical radius (for a full elipsoid put -90 to 90
     * @param maxheight     the end of the vertical radius
     * @param blocksToForce the list of blocks that the structure can still force if force = true
     * @param blocksToPlace the list of blockstate that the structure will pose in a random order
     */
    public static void generateEmptyEllipsoid(StructureWorldAccess world, int largex, int largey, int largez, BlockPos pos, boolean force, int minlarge, int maxlarge, int minheight, int maxheight, List<Block> blocksToForce, List<BlockState> blocksToPlace) {
        int maxlarge1 = Math.max(largez, Math.max(largex, largey));
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int length = blocksToPlace.size() - 1;
        for (double theta = minlarge; theta <= maxlarge; theta += (double) 45 / maxlarge1) {
            double xcostheta = largex * FastMaths.getFastCos(theta);
            double zsinkheta = largez * FastMaths.getFastSin(theta);
            for (double phi = minheight; phi <= maxheight; phi += (double) 45 / maxlarge1) {
                double cosphi = FastMaths.getFastCos(phi);
                int x = (int) (xcostheta * cosphi);
                int y = (int) (largey * FastMaths.getFastSin(phi));
                int z = (int) (zsinkheta * cosphi);
                mutable.set(pos, x, y, z);

                BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
            }
        }
    }

    public static void generateHalfEmptySphere(StructureWorldAccess world, int radius, BlockPos pos, Direction direction, List<BlockState> state) {
        generateHalfEmptyElipsoid(world, radius, radius, radius, pos, direction, state);
    }

    public static void generateHalfEmptySphere(StructureWorldAccess world, int radius, BlockPos pos, Direction direction, BlockState state) {
        generateHalfEmptyElipsoid(world, radius, radius, radius, pos, direction, List.of(state));
    }

    public static void generateHalfEmptyElipsoid(StructureWorldAccess world, int largex, int largey, int largez, BlockPos pos, Direction direction, BlockState state) {
        generateHalfEmptyElipsoid(world, largex, largey, largez, pos, direction, List.of(state));
    }

    public static void generateHalfEmptyElipsoid(StructureWorldAccess world, int largex, int largey, int largez, BlockPos pos, Direction direction, List<BlockState> state) {
        if (direction == Direction.UP) {
            generateEmptyEllipsoid(world, largex, largey, largez, pos, false, 180, 180, 0, 90, null, state);
            return;
        }
        if (direction == Direction.DOWN) {
            generateEmptyEllipsoid(world, largex, largey, largez, pos, false, 180, 180, -90, 0, null, state);
            return;
        }
        if (direction == Direction.WEST) {
            generateEmptyEllipsoid(world, largex, largey, largez, pos, false, 0, 180, -90, 90, null, state);
            return;
        }
        if (direction == Direction.EAST) {
            generateEmptyEllipsoid(world, largex, largey, largez, pos, false, -180, 0, -90, 90, null, state);
            return;
        }
        if (direction == Direction.NORTH) {
            generateEmptyEllipsoid(world, largex, largey, largez, pos, false, -90, 90, -90, 90, null, state);
            return;
        }
        generateEmptyEllipsoid(world, largex, largey, largez, pos, false, 90, 270, -90, 90, null, state);
    }

    public static void generateEmptySphere(StructureWorldAccess world, int radius, BlockPos pos, BlockState state) {
        generateEmptyEllipsoid(world, radius, radius, radius, pos, false, -180, 180, -90, 90, null, List.of(state));
    }

    public static void generateEmptySphere(StructureWorldAccess world, int radius, BlockPos pos, BlockState state, boolean force) {
        generateEmptyEllipsoid(world, radius, radius, radius, pos, force, -180, 180, -90, 90, null, List.of(state));
    }


}
