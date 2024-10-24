package net.rodofire.easierworldcreator.worldgenutil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.shapegen.TorusGen;
import net.rodofire.easierworldcreator.util.FastMaths;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*

                                         =======%
                                 %%%#+*%%%%%%@%%%%%%%%%%%%
                            %%%%%%=*%@@%%%%%%%%%%%%@ %%%%%@%@%
                        %@%%%%%%+#%%%%%@%%%%%%%%%%%%%%%%%%%%%%%%%%%
                     %%%@%%%%%*+%%%%%%@%%%%%%%%%@%%%%%%%%%%%%%@%@%%%%@
                   %%%%%%%%%%#=%%%%%%%%%%@@%%%%%%%%%%%@%%%%%%%@%%%%%%%%%
                 %%%%%%@%%@%%=%%%%%%@%%%%%%@%%%%%%%@%% %%%%%%% % % @%%@%%%
               %%%%%%%%%%%%%%=%%%@%%%%%%%%%%@%% %%%%%%%% %%%%%%@%@%%%%%%%%%@
             %%%%%%%%%%=+==++===+=+=+%%%%@%%%%@%%%%% %%%%%%%@%%%%%%%@%%%%%%%%%
            %%%%%%%%==*#%%%%%=%@%%%%%#==*%%%%%%%%%%%%%@%%%%%% @%%%%@%%%%%%%%%%%
           %%%@%%==%%%%%%%%%%+=@%@%%%%%%%== % %%%%%% %% % %@%% %% % @%%%@%@@%%%%@
          %%%%%==%%%%%%%%%%@%%*=%@%%%%%%%@%+=%%%%@%%%%%%%%%@%%%%@@ %%%% %%%@%%%%%
        @@%%%+=%@%%%%%%%@%%%%%%%+*%%%%%%%@%%%++%%%%%%%% %%%% % %%@%@%%@%%%%%@%%%%%%
       %%%@#==%%%%%%@%%%%@%%%%%%%%**%%%%%%%%%@*=%%%%%%@% %@%%%%%%@ % %%%%%%%%%%%%%%%
      %%%%*=%% %%@%%%%@@%%%%%%%%%%%%%#+*%%%%%%%%=+%%% %@% % @ @%%%  @ % %%@@%@%%%%%%%
     @%%%#=@%%@%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%=+%%%%%%%%%@% %% %@%@%%%%%%%%%%%%%%%%
     %%%*=%%%@%%%%%%%%%@%@%%%%%%%%%%%%%%%%%%% %%%%+*%%%%@@%%%@%@%% % % %@%%@%@%%@@%@@%
    @%%*=%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%     @%%=*%%%%%%@%  %%%%%%%%@@ %%%@%%% @%%%%
   %%%%=#@%%@%%%%%%%%%@%%@%%%%%%@%%%%%%%%%       %@%=%@@@@%%%% %%%@ % %@%%%%%%%%%%%%@%%%
   %%%=+%%%%%%@%%%% %%%%%@@%%%%%@%@@%%%%%         @%%=%%% %%%@%@%%% %% % % %  %@%% %@%%%
  %@%*=%%%%%%@@%%%@%@%%%@%@%%%%%%%%@%%%%          %%%#=%%%%@% % %@@%@%%%%%%%%%@%%%%%%%@%%
  %%%=%%%@%@%%@%%%%%@%%%%%%%%@@%%%%%%%%%           %%%=#%%%%%%%%%% % @% %%% % %@% @%@%%%%
 %%%#+%%%%%%%%%%%%%%%% % %@%%%%%%%%%%%%             %%**%%%% @ %%%%%%%% % %%%@%%%%@%%%@%%%
 %%%= %%%%%@ % @%%%%%%%  %@% @@%% %@%%               @#+%%%@%@ @%%  % % %@@%%%%%%%%%%@%%%%
 %%%=%@%%%%%%% %%%%%%%%@%%%%%@%%%%%%%%               %@=*%%%%%@%%%% % %  %@@ % %@% %% %%%%
 %@++@%%%@%%%@@%%%%@%%%%%%%%%%%%%%%%%%               @%++%%%@ %%%@%%@%%%%%%%%%%%%%%@%%%%%%
 %%+%%%%@ % %%@@%%%%%%%%%%%%@%%%%%%%%%               %%#+%%%%%%%@ % %%%@ % % %%%@%%%%%%%%%
 %%= %@%% % % %%%%%%%%%%%%%%%%%@%%%%%%                %%=%@%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 %@=#%%%@ %%%%%%%%%%%%@%%%%%%%%%%%%%%@                %%=% %% % %%% @@ % % %  %@% %%%% %%%@
 %%=%%%%%%%%%%%%%%%%%%%%%%%%@%%%%%%%%@                %%=%%%%%%%%%%%%%%%%%%%@@%%%%%%%%%%%%@
 %%+%@%%%%%%%@%@%@%%@%%%@%%%%%%%%@%%%@                %@=@%%%%%@%%%%%%%%@% %  %%@ %%%@ %%%%
 %%= %%%%@%%%%%%%%@%@@ % @%% %%%% %%%%                %%=% %% % %%% %%%% % %  %%% %%%%@%%%@
 %%= %@%%%%%%@%%%%%% %%@ @%%%@@%@%%%%@                %*=% %%%%%%%%%%%%@%%%@@%%%%@%@%%%%%%@
 %%+*%@%%%@%%%@%%%@%%%%@%%%%%@@@@%%@%%               @%+*%%%%%%%% % %%%  % % @ %@% %%%%@%%
 %%%=@%%%%%@%%@%@%%@%@%%%@%%%%%%%%%%%%               @%+#%%%%%%%%%@%%%%%%%%%%%%%%%%%%%%%@%
 @%%=%%%%%@%%%%%%%% % %@@%%%%%%%%%%%%%               %%=# %%%%%%%%% % % @%%@ %@@ % @%%@%%%
 @%%==%%%%%%%@%%%%% @%@%%%%%%%%%%%%%%%%             %%*=%%@@@% % % %%%% % %%%%@%%%%@%% %%%
  %%%=%%%%@@@@%%%%@%%%%%%%%@%%%%%%%%%@%@           %%%=#%@%%%@ @%%@%@%%%%%%%% %%% %%%%%%%
  @%%#=%%%@@%%@%@%%%%%%@%%%%%%%%%@%%%%%%           %%#+%% @%%%%%%% @ % % %@%@%%%%%%%%%%%%
   %%%=*%%%%%%@@%%%%%%%@@%%%%%%%%@%%%%%%%         %%%=%%%%%%% %%% % %%%%%%%@%@@%%%%%%%%%
   %@%*=% @%%%%%%@ @%%% %%%% %%%%%%%%%%%%@       %%%=+%%%% % %%%%%% % %  % % @ % %%%@%%%
    %%%+=%%%%% %%%%%%%%%%%%@%%%%@%%%%%%%%%%     %%%+=%%@% %%%%%% @ @%@%%%%%%%%%%%%%%%%%
     %%%==%%%%%%%%%%%%%@%%%%%%%%%%%%%%%%%@%%% %%@%*=%%@@%%%%% %%%%%%%%%%%%% % @%@%%%%%
     @%%%+=%%%%%%%%%%%%%%%%%%%@%%%@%%%%%%%%%%%%@%*=% %%%@% %%@%@% % @% %%%@@@%%%%@%%%%
      %%@%#=%%%% %% %%%% %%%@%%%%@%%%%%%%%%%%%%@==%%%%% % %%%% % % %%%%%%%%%%@%%%%%%%
       %%%%#=#@%%%%%%@%%@%%%%%%%%%%%%%%%%%%%%%%=#%%%@@ %@%@%% %%%%%%%%@@%%%%%%%%%%%%
        %%%@%=+%%%%%%%%%%@%%%%%%%%%%%%%%%%%%%+=%%%%%%%%%@ %%%%%%@ % @%%%%%%%%%%%%%%
         @%%%%%==%%%%%%%%%%%%%%%%%@%%%%%%%@+=*%%%%%%%%%%%%%%% %%%@%%%%%%%@%%%%%%%%
          %%%%%%%=+@%%% @%%%@%%%% %%%%%%%==%%%%%%%%%  %@%  % %%%%@% @% % %%%@%%%%
            %%%%%%%+=+*%@%@%%%%%%%%%%+#=+%%%%%%%@%% %%%%%%%%@%@% % %%%%%%%%%%%%
             %%%%%%%@%#+=+==+=++=+===*%%%%%%@%@@%@%%%%% %%%%%%% %%%%%%%%%%%%%%
               %%%%@%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%@% %%%%%%%@%%%%@%%%%%%%%%
                 %@%%%@@ %%%%@%@%%%%% %%%%@%%%%% %@ %%%%@ %%%@%%%%%%%@%%%%
                   %%%%%%%@@%%%%@%%%%% %%@%%%%%%%@%%%%%@%%%@%@%%%%%%%%%@
                     %%%%%%%@@%%%%%%%@%@%%@%%@%@%%%%%%%@%%%%%%%%%%%%%%
                        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%@%@%%%%%%%%%%
                           %%%%%%%%%%%%%%%%%%@%%%%@%%@%%%%%%%%%
                                 %%%%%%%%%%%%%%%%%%%%%%%%%
                                        @@%%%%%%%@                                         */



/**
 * switch to new generation
 * @see TorusGen
 *this class will not be updated anymore and won't receive any support
 */
@Deprecated
@SuppressWarnings("unused")
public class GenTore {
    public static class CircularTore {
        public static void generateFullTore(StructureWorldAccess world, BlockPos pos, boolean force, int innerRadius, int outerRadius, BlockState blocksToPlace) {
            generateFullTore(world, pos, force, innerRadius, outerRadius, 0, 0, null, List.of(blocksToPlace));
        }

        public static void generateFullTore(StructureWorldAccess world, BlockPos pos, boolean force, int innerRadius, int outerRadius, Set<Block> blocksToForce, List<BlockState> blocksToPlace) {
            generateFullTore(world, pos, force, innerRadius, outerRadius, 0, 0, blocksToForce, blocksToPlace);
        }

        /**
         * @param world         the world that the structure will spawn in
         * @param pos           the center of the torus
         * @param force         force the block if true
         * @param innerRadius   the radius of the circle
         * @param outerRadius   the radius of the center of the circle
         * @param xRotation     the rotation in degrees relative to the x-axis
         * @param yRotation     the rotation in degrees relative to the y-axis
         * @param blocksToForce list of the blocks that can still be forced if force = false
         * @param blocksToPlace list of blockStates that the structure will randomly place
         */
        public static void generateFullTore(StructureWorldAccess world, BlockPos pos, boolean force, int innerRadius, int outerRadius, int xRotation, int yRotation, Set<Block> blocksToForce, List<BlockState> blocksToPlace) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            double cosX = FastMaths.getFastCos(xRotation);
            double cosY = FastMaths.getFastCos(yRotation);
            double sinX = FastMaths.getFastSin(xRotation);
            double sinY = FastMaths.getFastSin(yRotation);

            //a rotation that can be divided by 180 will return the same shape
            // (it allows us to avoid some unnecessary calculations)
            int outerRadiusSquared = outerRadius * outerRadius;
            int innerRadiusSquared = innerRadius * innerRadius;
            if (xRotation % 180 == 0 && yRotation % 180 == 0) {
                for (int x = -outerRadius - innerRadius; x <= outerRadius + innerRadius; x++) {
                    int xSquared = x * x;
                    for (int z = -outerRadius - innerRadius; z <= outerRadius + innerRadius; z++) {
                        int zSquared = z * z;
                        for (int y = -outerRadius - innerRadius; y <= outerRadius + innerRadius; y++) {
                            int ySquared = y * y;
                            int a = xSquared + ySquared + zSquared + outerRadiusSquared - innerRadiusSquared;
                            if ((a * a) - 4 * outerRadiusSquared * (xSquared + ySquared) <= 0) {
                                int x_rot = (int) (x * cosY - y * sinY);
                                int y_rot = (int) (x * sinY + y * cosY);

                                int x_rot2 = (int) (x_rot * cosX + z * sinX);
                                int z_rot = (int) (-x_rot * sinX + z * cosX);
                                mutable.set(pos, x_rot2, y_rot, z_rot);
                                BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
                            }
                        }
                    }
                }
            } else {
                for (float x = -outerRadius - innerRadius; x <= outerRadius + innerRadius; x += 0.5f) {
                    float xSquared = x * x;
                    for (float z = -outerRadius - innerRadius; z <= outerRadius + innerRadius; z += 0.5f) {
                        float zSquared = z * z;
                        for (float y = -outerRadius - innerRadius; y <= outerRadius + innerRadius; y += 0.5f) {
                            float ySquared = y * y;
                            float a = xSquared + ySquared + zSquared + outerRadiusSquared - innerRadiusSquared;
                            if ((a * a) - 4 * outerRadiusSquared * (xSquared + ySquared) <= 0) {
                                float x_rot = (int) (x * cosY - y * sinY);
                                float y_rot = (int) (x * sinY + y * cosY);

                                float x_rot2 = (int) (x_rot * cosX + z * sinX);
                                float z_rot = (int) (-x_rot * sinX + z * cosX);
                                mutable.set(pos, (int) x_rot2, (int) y_rot, (int) z_rot);
                                BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
                            }
                        }
                    }
                }
            }
        }


        /** generate a natural torus with the default blocks (grass [1 block], dirt [3 blocks], Stone[x blocks])
         */
        public static void generateNaturalFullTore(StructureWorldAccess world, BlockPos pos, boolean force, int innerRadius, int outerRadius, int xRotation, int yRotation, Set<Block> blocksToForce) {
            List<BlockState> grasslist = List.of(Blocks.GRASS_BLOCK.getDefaultState());
            List<BlockState> dirtlist = List.of(Blocks.DIRT.getDefaultState());
            List<BlockState> stonelist = List.of(Blocks.STONE.getDefaultState());
            generateNaturalFullTore(world, pos, force, innerRadius, outerRadius, xRotation, yRotation, blocksToForce, grasslist, dirtlist, stonelist, 1,3);
        }


        /** Generate a torus with a natural aspect.
         * The three lists should not contain any block in common, or you will run into issues
         * @param grass list of blockState that will be placed on top of the structure
         * @param dirt list of blockState that will be placed under the grass at a depth of three blocks
         * @param stone list of blockStates that will be placed under the dirt
         */
        public static void generateNaturalFullTore(StructureWorldAccess world, BlockPos pos, boolean force, int innerRadius, int outerRadius, int xRotation, int yRotation, Set<Block> blocksToForce, List<BlockState> grass, List<BlockState> dirt, List<BlockState> stone, int firstLayer, int secondLayer ) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            double cosX = FastMaths.getFastCos(xRotation);
            double cosY = FastMaths.getFastCos(yRotation);
            double sinX = FastMaths.getFastSin(xRotation);
            double sinY = FastMaths.getFastSin(yRotation);

            List<BlockPos> posList = new ArrayList<>();

            //a rotation that can be divided by 180 will return the same shape
            // (it allows us to avoid some unnecessary calculations)
            //the method returns a list that will be used for later
            int outerRadiusSquared = outerRadius * outerRadius;
            int innerRadiusSquared = innerRadius * innerRadius;
            if (xRotation % 180 == 0 && yRotation % 180 == 0) {
                for (int x = -outerRadius - innerRadius; x <= outerRadius + innerRadius; x++) {
                    int xSquared = x * x;
                    for (int z = -outerRadius - innerRadius; z <= outerRadius + innerRadius; z++) {
                        int zSquared = z * z;
                        for (int y = -outerRadius - innerRadius; y <= outerRadius + innerRadius; y++) {
                            int ySquared = y * y;
                            int a = xSquared + ySquared + zSquared + outerRadiusSquared - innerRadiusSquared;
                            if ((a * a) - 4 * outerRadiusSquared * (xSquared + ySquared) <= 0) {
                                int x_rot = (int) (x * cosY - y * sinY);
                                int y_rot = (int) (x * sinY + y * cosY);

                                int x_rot2 = (int) (x_rot * cosX + z * sinX);
                                int z_rot = (int) (-x_rot * sinX + z * cosX);
                                mutable.set(pos, x_rot2, y_rot, z_rot);
                                posList.add(mutable);
                            }
                        }
                    }
                }
            } else {
                for (float x = -outerRadius - innerRadius; x <= outerRadius + innerRadius; x += 0.5f) {
                    float xSquared = x * x;
                    for (float z = -outerRadius - innerRadius; z <= outerRadius + innerRadius; z += 0.5f) {
                        float zSquared = z * z;
                        for (float y = -outerRadius - innerRadius; y <= outerRadius + innerRadius; y += 0.5f) {
                            float ySquared = y * y;
                            float a = xSquared + ySquared + zSquared + outerRadiusSquared - innerRadiusSquared;
                            if ((a * a) - 4 * outerRadiusSquared * (xSquared + ySquared) <= 0) {
                                float x_rot = (int) (x * cosY - y * sinY);
                                float y_rot = (int) (x * sinY + y * cosY);

                                float x_rot2 = (int) (x_rot * cosX + z * sinX);
                                float z_rot = (int) (-x_rot * sinX + z * cosX);
                                mutable.set(pos, (int) x_rot2, (int) y_rot, (int) z_rot);
                                posList.add(mutable);
                            }
                        }
                    }
                }
            }

            //grass step
            for (BlockPos position : posList) {
                //put grass on all the torus
                BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, grass, position);
            }

            //dirt step
            for (BlockPos position : posList) {
                //get the block on top
                Block block = world.getBlockState(position.up()).getBlock();

                //replace the actual block if the block on top is in the grass list
                if (WorldGenUtil.isBlockInBlockStateList(block, grass) || WorldGenUtil.isBlockInBlockStateList(block, dirt)) {
                    BlockPlaceUtil.setRandomBlockWithVerification(world, force, WorldGenUtil.addBlockStateListToBlockList(blocksToForce, grass), dirt, position);
                }
            }

            //stone step
            for (BlockPos position : posList) {
                //get the block on top
                Block block = world.getBlockState(position).getBlock();
                Block block1 = world.getBlockState(position.up()).getBlock();

                //if block on top is grass, it can't place the stone
                if(WorldGenUtil.isBlockInBlockStateList(block, grass)) {
                    continue;
                }

                //can't replace the actual block if the block on top is in the grass list
                if (WorldGenUtil.isBlockInBlockStateList(block1, grass)) {
                    continue;
                }
                if (WorldGenUtil.isBlockInBlockStateList(block1, stone)) {
                    BlockPlaceUtil.setRandomBlockWithVerification(world, force, WorldGenUtil.addBlockStateListToBlockList(blocksToForce, dirt), stone, position);
                    continue;
                }
                int a = 0;
                for (int i = 1; i <= 3; i++) {
                    if (WorldGenUtil.isBlockInBlockStateList(world.getBlockState(position.up(i)).getBlock(), dirt)) {
                        a++;
                    }
                }
                if(a==3){
                    BlockPlaceUtil.setRandomBlockWithVerification(world, force, WorldGenUtil.addBlockStateListToBlockList(blocksToForce, dirt), stone, position);
                }
            }
        }


        public static void generateEmptyTore(StructureWorldAccess world, BlockPos pos, boolean force, int innerRadius, int outerRadius, Set<Block> blocksToForce, List<BlockState> blocksToPlace) {
            generateEmptyTore(world, pos, force, innerRadius, outerRadius, 0, 0, blocksToForce, blocksToPlace);
        }

        public static void generateEmptyTore(StructureWorldAccess world, BlockPos pos, int innerRadius, int outerRadius, BlockState blocksToPlace) {
            generateEmptyTore(world, pos, false, innerRadius, outerRadius, 0, 0, null, List.of(blocksToPlace));
        }

        /**
         * @param world         the world that the structure will spawn in
         * @param pos           the center of the torus
         * @param force         force the block if true
         * @param innerRadius   the radius of the circle
         * @param outerRadius   the radius of the center of the circle
         * @param xRotation     the rotation in degrees relative to the x-axis
         * @param yRotation     the rotation in degrees relative to the y-axis
         * @param blocksToForce list of the blocks that can still be forced if force = false
         * @param blocksToPlace list of blockStates that the structure will randomly place
         */
        public static void generateEmptyTore(StructureWorldAccess world, BlockPos pos, boolean force, int innerRadius, int outerRadius, int xRotation, int yRotation, Set<Block> blocksToForce, List<BlockState> blocksToPlace) {

            BlockPos.Mutable mutable = new BlockPos.Mutable();
            double cosX = FastMaths.getFastCos(xRotation);
            double cosY = FastMaths.getFastCos(yRotation);
            double sinX = FastMaths.getFastSin(xRotation);
            double sinY = FastMaths.getFastSin(yRotation);

            //many if statement to avoid doing multiple if in the loops
            if (xRotation % 360 == 0 && yRotation % 360 == 0) {
                for (int u = 0; u <= 360; u += 40 / outerRadius) {
                    for (int v = 0; v <= 360; v += 45 / innerRadius) {
                        Vec3d pos2 = getToreCoordinates(u, v, innerRadius, outerRadius);
                        mutable.set(pos, (int) pos2.getX(), (int) pos2.getY(), (int) pos2.getZ());
                        BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else if (xRotation % 90 == 0 && yRotation % 360 == 0) {
                for (int u = 0; u <= 360; u += 40 / outerRadius) {
                    for (int v = 0; v <= 360; v += 45 / innerRadius) {
                        Vec3d pos2 = getToreCoordinates(u, v, innerRadius, outerRadius);
                        mutable.set(pos, (int) pos2.getZ(), (int) pos2.getY(), (int) pos2.getX());
                        BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else if (yRotation % 360 == 0) {
                for (int u = 0; u <= 360; u += 30 / outerRadius) {
                    for (int v = 0; v <= 360; v += 35 / innerRadius) {
                        Vec3d pos2 = getToreCoordinates(u, v, innerRadius, outerRadius);

                        int x_rot = (int) (pos2.getX() * cosX + pos2.getZ() * sinX);
                        int z_rot = (int) (-pos2.getX() * sinX + pos2.getZ() * cosX);

                        mutable.set(pos, x_rot, (int) pos2.getY(), z_rot);
                        BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else if (xRotation % 360 == 0 && yRotation % 90 == 0) {
                for (int u = 0; u <= 360; u += 30 / outerRadius) {
                    for (int v = 0; v <= 360; v += 35 / innerRadius) {
                        Vec3d pos2 = getToreCoordinates(u, v, innerRadius, outerRadius);
                        mutable.set(pos, (int) pos2.getY(), (int) pos2.getX(), (int) pos2.getZ());
                        BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else if (xRotation % 360 == 0) {
                for (int u = 0; u <= 360; u += 40 / outerRadius) {
                    for (int v = 0; v <= 360; v += 35 / innerRadius) {
                        Vec3d pos2 = getToreCoordinates(u, v, innerRadius, outerRadius);

                        int x_rot = (int) (pos.getX() * cosY - pos2.getY() * sinY);
                        int y_rot = (int) (pos.getX() * sinY + pos2.getY() * cosY);

                        mutable.set(pos, x_rot, y_rot, (int) pos2.getZ());
                        BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else if (yRotation % 90 == 0) {
                for (int u = 0; u <= 3600; u += 1) {
                    for (int v = 0; v <= 3600; v += 1) {
                        Vec3d pos2 = getPreciseToreCoordinates(u, v, innerRadius, outerRadius);


                        int x_rot2 = (int) (pos2.getX() * cosX + pos2.getZ() * sinX);
                        int z_rot = (int) (-pos2.getX() * sinX + pos2.getZ() * cosX);


                        mutable.set(pos, x_rot2, z_rot, (int) pos2.getY());
                        BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else {
                for (int u = 0; u <= 360; u += 45 / (outerRadius + 2 * innerRadius)) {
                    for (int v = 0; v <= 360; v += 45 / innerRadius) {
                        Vec3d pos2 = getToreCoordinates(u, v, innerRadius, outerRadius);

                        float x_rot = (float) (pos2.getX() * cosY - pos2.getY() * sinY);
                        float y_rot = (float) (pos2.getX() * sinY + pos2.getY() * cosY);

                        float x_rot2 = (float) (x_rot * cosX + pos2.getZ() * sinX);
                        float z_rot = (float) (-x_rot * sinX + pos2.getZ() * cosX);


                        mutable.set(pos, (int) x_rot2, (int) y_rot, (int) z_rot);
                        BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            }
        }

        public static Vec3d getToreCoordinates(int u, int v, int innerRadius, int outerRadius) {
            double a = outerRadius + innerRadius * FastMaths.getFastCos(v);
            double x = (a * FastMaths.getFastCos(u));
            double z = (a * FastMaths.getFastSin(u));
            double y = (innerRadius * FastMaths.getFastSin(v));
            return new Vec3d(x, y, z);
        }

        public static Vec3d getPreciseToreCoordinates(int u, int v, int innerRadius, int outerRadius) {
            double a = outerRadius + innerRadius * FastMaths.getPreciseCos(v);
            double x = (a * FastMaths.getPreciseCos(u));
            double z = (a * FastMaths.getPreciseSin(u));
            double y = (innerRadius * FastMaths.getPreciseSin(v));
            return new Vec3d(x, y, z);
        }
    }


    //generate torus with radius that changes
    public static class EllipsoidTore {
        public static void generateFullTore(StructureWorldAccess world, BlockPos pos, boolean force, int xInnerRadius, int xOuterRadius, int zInnerRadius, int zOuterRadius, BlockState blocksToPlace) {
            generateFullTore(world, pos, force, xInnerRadius, xOuterRadius, zInnerRadius, zOuterRadius, 0, 0, null, List.of(blocksToPlace));
        }

        public static void generateFullTore(StructureWorldAccess world, BlockPos pos, boolean force, int xInnerRadius, int xOuterRadius, int zInnerRadius, int zOuterRadius, Set<Block> blocksToForce, List<BlockState> blocksToPlace) {
            generateFullTore(world, pos, force, xInnerRadius, xOuterRadius, zInnerRadius, zOuterRadius, 0, 0, blocksToForce, blocksToPlace);
        }

        /**
         * @param xInnerRadius  the inner radius relative to the x-axis
         * @param xOuterRadius  the outer radius relative to the x-axis
         * @param zInnerRadius  the inner radius relative to the z axis
         * @param zOuterRadius  the outer radius relative to the z axis
         * @param world         the world that the structure will spawn in
         * @param pos           the center of the torus
         * @param force         force the block if true
         * @param xRotation     the rotation in degrees relative to the x-axis
         * @param yRotation     the rotation in degrees relative to the y-axis
         * @param blocksToForce list of the blocks that can still be forced if force = false
         * @param blocksToPlace list of blockStates that the structure will randomly place
         */
        public static void generateFullTore(StructureWorldAccess world, BlockPos pos, boolean force, int xInnerRadius, int xOuterRadius, int zInnerRadius, int zOuterRadius, int xRotation, int yRotation, Set<Block> blocksToForce, List<BlockState> blocksToPlace) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            double cosX = FastMaths.getFastCos(xRotation);
            double cosY = FastMaths.getFastCos(yRotation);
            double sinX = FastMaths.getFastSin(xRotation);
            double sinY = FastMaths.getFastSin(yRotation);

            int maxInnerRadius = Math.max(xInnerRadius, zInnerRadius);
            int maxOuterRadius = Math.max(xOuterRadius, zOuterRadius);

            //a rotation that can be divided by 180 will return the same shape (it allows us
            // to avoid some unnecessary calculations)
            if (xRotation % 180 == 0 && yRotation % 180 == 0) {
                for (int x = -maxOuterRadius - maxInnerRadius; x <= maxOuterRadius + maxInnerRadius; x++) {
                    for (int z = -maxOuterRadius - maxInnerRadius; z <= maxOuterRadius + maxInnerRadius; z++) {
                        for (int y = -maxOuterRadius - maxInnerRadius; y <= maxOuterRadius + maxInnerRadius; y++) {
                            int xSquared = x * x;
                            int ySquared = y * y;
                            int zSquared = z * z;

                            int angle = (int) Math.toDegrees(Math.atan2(y, x));
                            double outerRadius = getOuterRadius(xOuterRadius, zOuterRadius, angle);
                            double innerRadius = getInnerRadius(xInnerRadius, zInnerRadius, angle);

                            int outerRadiusSquared = (int) (outerRadius * outerRadius);
                            int innerRadiusSquared = (int) (innerRadius * innerRadius);
                            int a = xSquared + ySquared + zSquared + outerRadiusSquared - innerRadiusSquared;


                            if ((a * a) - 4 * outerRadiusSquared * (xSquared + ySquared) <= 0) {
                                mutable.set(pos, x, y, z);
                                BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
                            }

                        }
                    }
                }
            } else {
                for (float x = -maxOuterRadius - maxInnerRadius; x <= maxOuterRadius + maxInnerRadius; x += 0.5f) {
                    for (float z = -maxOuterRadius - maxInnerRadius; z <= maxOuterRadius + maxInnerRadius; z += 0.5f) {
                        for (float y = -maxOuterRadius - maxInnerRadius; y <= maxOuterRadius + maxInnerRadius; y += 0.5f) {
                            float xSquared = x * x;
                            float ySquared = y * y;
                            float zSquared = z * z;

                            int angle = (int) Math.toDegrees(Math.atan2(y, x));
                            double outerRadius = getOuterRadius(xOuterRadius, zOuterRadius, angle);
                            double innerRadius = getInnerRadius(xInnerRadius, zInnerRadius, angle);

                            int outerRadiusSquared = (int) (outerRadius * outerRadius);
                            int innerRadiusSquared = (int) (innerRadius * innerRadius);
                            float a = xSquared + ySquared + zSquared + outerRadiusSquared - innerRadiusSquared;

                            if ((a * a) - 4 * outerRadiusSquared * (xSquared + ySquared) <= 0) {
                                float x_rot = (int) (x * cosY - y * sinY);
                                float y_rot = (int) (x * sinY + y * cosY);

                                float x_rot2 = (int) (x_rot * cosX + z * sinX);
                                float z_rot = (int) (-x_rot * sinX + z * cosX);
                                mutable.set(pos, (int) x_rot2, (int) y_rot, (int) z_rot);
                                BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
                            }
                        }
                    }
                }
            }
        }

        public static double getInnerRadius(int xInnerRadius, int zInnerRadius, int angle) {
            return xInnerRadius + (zInnerRadius - xInnerRadius) * Math.abs(FastMaths.getFastSin(angle));

        }

        public static double getOuterRadius(int xOuterRadius, int zOuterRadius, int angle) {
            return xOuterRadius + (zOuterRadius - xOuterRadius) * Math.abs(FastMaths.getFastSin(angle));
        }


        public static void generateEmptyTore(StructureWorldAccess world, BlockPos pos, boolean force, int xInnerRadius, int xOuterRadius, int zInnerRadius, int zOuterRadius, Set<Block> blocksToForce, List<BlockState> blocksToPlace) {
            generateEmptyTore(world, pos, force, xInnerRadius, xOuterRadius, zInnerRadius, zOuterRadius, 0, 0, blocksToForce, blocksToPlace);
        }

        public static void generateEmptyTore(StructureWorldAccess world, BlockPos pos, int xInnerRadius, int xOuterRadius, int zInnerRadius, int zOuterRadius, BlockState blocksToPlace) {
            generateEmptyTore(world, pos, false, xInnerRadius, xOuterRadius, zInnerRadius, zOuterRadius, 0, 0, null, List.of(blocksToPlace));
        }

        /**
         * @param xInnerRadius  the inner radius relative to the x-axis
         * @param xOuterRadius  the outer radius relative to the x-axis
         * @param zInnerRadius  the inner radius relative to the z axis
         * @param zOuterRadius  the outer radius relative to the z axis
         * @param world         the world that the structure will spawn in
         * @param pos           the center of the torus
         * @param force         force the block if true
         * @param xRotation     the rotation in degrees relative to the x-axis
         * @param yRotation     the rotation in degrees relative to the y-axis
         * @param blocksToForce list of the blocks that can still be forced if force = false
         * @param blocksToPlace list of blockStates that the structure will randomly place
         */
        public static void generateEmptyTore(StructureWorldAccess world, BlockPos pos, boolean force, int xInnerRadius, int xOuterRadius, int zInnerRadius, int zOuterRadius, int xRotation, int yRotation, Set<Block> blocksToForce, List<BlockState> blocksToPlace) {

            BlockPos.Mutable mutable = new BlockPos.Mutable();
            double cosX = FastMaths.getFastCos(xRotation);
            double cosY = FastMaths.getFastCos(yRotation);
            double sinX = FastMaths.getFastSin(xRotation);
            double sinY = FastMaths.getFastSin(yRotation);

            int maxOuterRadius = Math.max(xOuterRadius, zOuterRadius);
            int maxInnerRadius = Math.max(xInnerRadius, zInnerRadius);

            //many if statement to avoid doing multiple if in the loops
            if (xRotation % 360 == 0 && yRotation % 360 == 0) {
                for (int u = 0; u <= 360; u += 40 / maxOuterRadius) {
                    for (int v = 0; v <= 360; v += 45 / maxInnerRadius) {
                        Vec3d pos2 = getEllipsoidalToreCoordinates(u, v, xInnerRadius, xOuterRadius, zInnerRadius, zOuterRadius);
                        mutable.set(pos, (int) pos2.getX(), (int) pos2.getY(), (int) pos2.getZ());
                        BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else if (xRotation % 90 == 0 && yRotation % 360 == 0) {
                for (int u = 0; u <= 360; u += 40 / maxOuterRadius) {
                    for (int v = 0; v <= 360; v += 45 / maxInnerRadius) {
                        Vec3d pos2 = getEllipsoidalToreCoordinates(u, v, xInnerRadius, xOuterRadius, zInnerRadius, zOuterRadius);
                        mutable.set(pos, (int) pos2.getZ(), (int) pos2.getY(), (int) pos2.getX());
                        BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else if (yRotation % 360 == 0) {
                for (int u = 0; u <= 360; u += 30 / maxOuterRadius) {
                    for (int v = 0; v <= 360; v += 35 / maxInnerRadius) {
                        Vec3d pos2 = getEllipsoidalToreCoordinates(u, v, xInnerRadius, xOuterRadius, zInnerRadius, zOuterRadius);

                        int x_rot = (int) (pos2.getX() * cosX + pos2.getZ() * sinX);
                        int z_rot = (int) (-pos2.getX() * sinX + pos2.getZ() * cosX);

                        mutable.set(pos, x_rot, (int) pos2.getY(), z_rot);
                        BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else if (xRotation % 360 == 0 && yRotation % 90 == 0) {
                for (int u = 0; u <= 360; u += 30 / maxOuterRadius) {
                    for (int v = 0; v <= 360; v += 35 / maxInnerRadius) {
                        Vec3d pos2 = getEllipsoidalToreCoordinates(u, v, xInnerRadius, xOuterRadius, zInnerRadius, zOuterRadius);
                        mutable.set(pos, (int) pos2.getY(), (int) pos2.getX(), (int) pos2.getZ());
                        BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else if (xRotation % 360 == 0) {
                for (int u = 0; u <= 360; u += 40 / maxOuterRadius) {
                    for (int v = 0; v <= 360; v += 35 / maxInnerRadius) {
                        Vec3d pos2 = getEllipsoidalToreCoordinates(u, v, xInnerRadius, xOuterRadius, zInnerRadius, zOuterRadius);

                        int x_rot = (int) (pos.getX() * cosY - pos2.getY() * sinY);
                        int y_rot = (int) (pos.getX() * sinY + pos2.getY() * cosY);

                        mutable.set(pos, x_rot, y_rot, (int) pos2.getZ());
                        BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else if (yRotation % 90 == 0) {
                for (int u = 0; u <= 3600; u += 1) {
                    for (int v = 0; v <= 3600; v += 1) {
                        Vec3d pos2 = getEllipsoidalToreCoordinates(u, v, xInnerRadius, xOuterRadius, zInnerRadius, zOuterRadius);


                        int x_rot2 = (int) (pos2.getX() * cosX + pos2.getZ() * sinX);
                        int z_rot = (int) (-pos2.getX() * sinX + pos2.getZ() * cosX);


                        mutable.set(pos, x_rot2, z_rot, (int) pos2.getY());
                        BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else {
                for (int u = 0; u <= 360; u += 45 / maxOuterRadius) {
                    for (int v = 0; v <= 360; v += 45 / maxInnerRadius) {
                        Vec3d pos2 = getEllipsoidalToreCoordinates(u, v, xInnerRadius, xOuterRadius, zInnerRadius, zOuterRadius);

                        float x_rot = (float) (pos2.getX() * cosY - pos2.getY() * sinY);
                        float y_rot = (float) (pos2.getX() * sinY + pos2.getY() * cosY);

                        float x_rot2 = (float) (x_rot * cosX + pos2.getZ() * sinX);
                        float z_rot = (float) (-x_rot * sinX + pos2.getZ() * cosX);


                        mutable.set(pos, (int) x_rot2, (int) y_rot, (int) z_rot);
                        BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            }
        }

        public static Vec3d getEllipsoidalToreCoordinates(int u, int v, int xInnerRadius, int xOuterRadius, int zInnerRadius, int zOuterRadius) {

            // Interpolating the radii based on the angle
            double R = xOuterRadius + (zOuterRadius - xOuterRadius) * Math.abs(FastMaths.getFastSin(u));
            double r = xInnerRadius + (zInnerRadius - xInnerRadius) * Math.abs(FastMaths.getFastSin(u));

            double a = R + r * FastMaths.getFastCos(v);
            double x = (a * FastMaths.getFastCos(u));
            double z = (a * FastMaths.getFastSin(u));
            double y = (r * FastMaths.getFastSin(v));

            return new Vec3d(x, y, z);
        }

        public static Vec3d getPreciseToreCoordinates(int u, int v, int innerRadius, int outerRadius) {
            double a = outerRadius + innerRadius * FastMaths.getPreciseCos(v);
            double x = (a * FastMaths.getPreciseCos(u));
            double z = (a * FastMaths.getPreciseSin(u));
            double y = (innerRadius * FastMaths.getPreciseSin(v));
            return new Vec3d(x, y, z);
        }
    }
}
