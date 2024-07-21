package net.rodofire.easierworldcreator.worldgenutil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Easierworldcreator;
import net.rodofire.easierworldcreator.util.FastMaths;

import java.util.ArrayList;
import java.util.List;

public class GenSpiral {

    //all the methods in this classes will create a circular shape
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
    public static class circularSpiral {
        /**
         * @param radius        the radius of the spiral
         * @param height        the height of the spiral
         * @param turn          the number of turns that de spiral is going to do
         * @param world         the world
         * @param pos           the pos of the center of the spiral
         * @param force         force the blockpos (will put the block no matter what block is at the current pos)
         * @param k             the spiral offset in degrees
         * @param blocksToForce blocks that the generation will replace if force = false
         * @param blocksToPlace list of blocks that the generation will place randomly
         */

        public static void generateSpiral(int radius, int height, int turn, StructureWorldAccess world, BlockPos pos, boolean force, double k, List<Block> blocksToForce, List<BlockState> blocksToPlace) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            int blockstatelenght = blocksToPlace.size();
            if (turn <= 0) {
                Easierworldcreator.LOGGER.error("param turn can't be <= 0");
            }
            double f = (1.5 * turn);
            double a = (double) 360 / (1.5 * height);

            for (double i = 0; i < 1.5 * turn * height; i++) {
                int x = (int) (radius * FastMaths.getFastCos(a * i + k));
                int z = (int) (radius * FastMaths.getFastSin(a * i + k));
                int y = (int) (i / f);

                mutable.set(pos, x, y, z);
                BlockState state = world.getBlockState(mutable);
                if (state.getHardness(world, mutable) < 0) continue;
                if (!force) {
                    if (!state.isAir() && blocksToForce.stream().noneMatch(state.getBlock()::equals)) continue;
                }
                world.setBlockState(mutable, blocksToPlace.get(Random.create().nextBetween(0, blockstatelenght - 1)), 2);
            }
        }

        /**
         * Generate a spiral that goes around the center point
         *
         * @param large      the radius of the blocks
         * @param radius     the radius of the spiral
         * @param height     the height of the spiral
         * @param turn       the number of turn that the spiral is going to do until it reach the wanted height
         * @param blockState the BlockStates that will randomly be placed
         * @param world      the world in wich the spiral is going to spawn
         * @param pos        the pos of the center of the spiral
         */
        public static void generateCircleSpiral(int large, int radius, int height, int turn, StructureWorldAccess world, BlockPos pos, List<BlockState> blockState) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            int largesquared = large * large;
            for (int i = 0; i <= large; i++) {
                for (int j = 0; j <= large; j++) {
                    if (i * i + j * j <= largesquared) {
                        mutable.set(pos, i, 0, j);
                        generateSpiral(radius, height, turn, world, mutable, false, 0, null, blockState);
                    }
                }
            }
        }

        /**
         * this generates a Spiral that fill blocks in a straight line starting from the center to the separated from the radius
         *
         * @param start the start of the filling. To start from the center, start should be equal to 0. A value equal to -radius will generate a double full spiral
         * @param end   the end of the filling. To finish it at a distance from the center equal to the radius, it should be equal to the radius
         */
        public static void generateFullSpiral(int height, int turn, int start, int end, StructureWorldAccess world, BlockPos pos, boolean force, List<BlockState> blockState) {
            for (int i = start; i <= end; i++) {
                generateSpiral(i, height, turn, world, pos, force, 0, null, blockState);
            }
        }

        //other methods to avoid some parameters

        //generates a default full spiral, starting from the center to the radius block
        public static void generateFullSpiral(int radius, int height, int turn, StructureWorldAccess world, BlockPos pos, List<BlockState> blockState) {
            generateFullSpiral(height, turn, 0, radius, world, pos, false, blockState);
        }


        public static void generateSpiral(int radius, int height, int turn, List<BlockState> blocks, StructureWorldAccess world, BlockPos pos) {
            generateSpiral(radius, height, turn, world, pos, false, 0, null, blocks);
        }

        //Generate Spiral if you only need one Block
        public static void generateSpiral(int radius, int height, int turn, BlockState block, StructureWorldAccess world, BlockPos pos) {
            List<BlockState> state = new ArrayList<>();
            state.add(block);
            generateSpiral(radius, height, turn, world, pos, false, 0, null, state);
        }

        //Generate a spiral if you only need one turn
        public static void generateSpiral(int radius, int height, BlockState block, StructureWorldAccess world, BlockPos pos) {
            List<BlockState> state = new ArrayList<>();
            state.add(block);
            generateSpiral(radius, height, 1, world, pos, false, 0, null, state);
        }
    }

    //Class to generate a Spiral that have an elispoid form
    /*




                                                                            ....
                                                                     ...::::::::::.
                                                               ..:::::::::::::::::::::
                                                        ..::::::::::::::::::::::::::::::.
                                                 ...::::::::::::::::::::::::::::::::::::::..
                                           ...:::::::::::::::::::::::::::::::::::::::::::::::.
                                    ......::::::::::::::::::::::::::::::::::::::::::::::::::::.
                               .........:::::::::::::::::::::::::::::::::::::::::::::::::::::::.
                           ..........:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::.
                         ..........::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
                         .........:::::::::::::::::::::::::::::::-----:-----:::::::::::::::::::::
                           ........:::::::::::::::::::-=-----------------------------::::::::::::.
                               .......:::::::::::::---=@@------------------------------------:::.
                                     ....::::::::----------------------------------------------.
                                         ...:::::---------------------------------------------:
                                            ..:::-------------------------------------------:.
                                              ..::-----------------------------------------.
                                                .::--------------------------------------.
                                                .::-----------------------------------.
                                                .:::-------------------------------.
                                               ..::::--------------------------:..
                                              ..:::::----------------------:.
                                            ..::::::::-----------------:..
                                         ..:::::::::::::-----------==.
                                      ..::::::::::::::::-----=========.
                                ....:::::::::::::::::--===============:
       ........................:::::::::::::::----=====================
     ....................::::::::::::::::--------=====================:
         ...............           ...::::-------=====================:
                                        ..::-----====================:
                                           ..:----===================..
                                              .:---======================:.
                                                .--=========================-.
                                                .:--===========================-.
                                                 :---=============================.
                                                .:-----=============================.
                                               .::-----==============================-
                                             .::::-====================================
    .                                     ..::-=========================================.
     .                                 .::-----=========================================.
                               ...:::::----------=======================================-
                                 ..::::---------------===================================.
                                       ..:--------------------==============------------.
                                             .:-----------------------------------------
                                                 ..------------------------------------.
  .                                                   .:------------------------------
                                                           .:-----------------------.
                                                                .:----------------.
                                                                     .----------.
                                                                         ..--.

     */
    public static class ElipsoidSpiral {

        /**
         * @param xradius       the radius of the x axis
         * @param zradius       the radius of the z axis
         * @param height        the height of the spiral
         * @param turn          the number of turns that the spiral will do
         * @param world         the world where the spiral will spawn
         * @param pos           the pos of the center fo the spiral
         * @param force         force the pos of the blocks
         * @param k             the spiral offset in degrees
         * @param blocksToForce blocks that the generation will replace if force = false
         * @param blocksToPlace list of blocks that the generation will place randomly
         */
        public static void generateElipsoidSpiral(int xradius, int zradius, int height, int turn, StructureWorldAccess world, BlockPos pos, boolean force, double k, List<Block> blocksToForce, List<BlockState> blocksToPlace) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            System.out.println("ok  " + blocksToPlace + "  " + blocksToForce);
            int blockstatelenght = blocksToPlace.size();
            if (turn <= 0) {
                Easierworldcreator.LOGGER.error("param turn can't be <= 0");
            }
            int maxlarge = Math.max(xradius, zradius);
            double f = (turn * maxlarge);
            double a = (double) 360 / ( height * maxlarge);

            for (double i = 0; i < maxlarge * turn * height; i++) {
                int x = (int) (xradius * FastMaths.getFastCos(a * i + k));
                int z = (int) (zradius * FastMaths.getFastSin(a * i + k));
                int y = (int) (i / f);

                mutable.set(pos, x, y, z);
                BlockState state = world.getBlockState(mutable);
                if (state.getHardness(world, mutable) < 0) continue;
                if (!force) {
                    if (!state.isAir() && blocksToForce.stream().noneMatch(state.getBlock()::equals)) continue;
                }
                world.setBlockState(mutable, blocksToPlace.get(Random.create().nextBetween(0, blockstatelenght - 1)), 2);
            }
        }

        public static void generateElipsoidFullSpiral(int xradius, int zradius, int height, int turn, int start, int end, StructureWorldAccess world, BlockPos pos, boolean force, List<BlockState> blockState) {
            //we have to take the largest value in order to don't have any hole in the feature
            if (xradius >= zradius) {
                for (int i = start; i <= end; i++) {
                    generateElipsoidSpiral(i, i * zradius / xradius, height, turn, world, pos, force, 0, null, blockState);
                }
            }
        }

        public static void generateCircleElipsoidSpiral(int xradius, int zradius, int height, int turn, int radius, StructureWorldAccess world, BlockPos pos, boolean force, List<BlockState> blockState) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            int squaredradius = radius * radius;
            for (int x = -radius; x <= radius; x++) {
                int xs = x*x;
                for (int z = -radius; z <= radius; z++) {
                    if (xs + z * z <= squaredradius) {
                        mutable.set(pos, x, 0, z);
                        generateElipsoidSpiral(xradius, zradius, height, turn, world, mutable, force, 0, null, blockState);
                    }
                }
            }
        }
    }
}
