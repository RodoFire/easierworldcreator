package net.rodofire.easierworldcreator.worldgenutil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.util.FastMaths;

import java.util.List;

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


public class GenTore {
    public static class CircularTore {
        public static void generateFullTore(StructureWorldAccess world, BlockPos pos, boolean force, int innerRadius, int outerRadius, BlockState blocksToPlace) {
            generateFullTore(world, pos, force, innerRadius, outerRadius, 0, 0, null, List.of(blocksToPlace));
        }

        public static void generateFullTore(StructureWorldAccess world, BlockPos pos, boolean force, int innerRadius, int outerRadius, List<Block> blocksToForce, List<BlockState> blocksToPlace) {
            generateFullTore(world, pos, force, innerRadius, outerRadius, 0, 0, blocksToForce, blocksToPlace);
        }

        /**
         * @param world         the world that the structure wiil spawn in
         * @param pos           the center of the tore
         * @param force         force the block if true
         * @param innerRadius   the radius of the circle
         * @param outerRadius   the radius of the center of the circle
         * @param xrotation     the rotation in degrees relative to the x axis
         * @param yrotation     the rotation in degrees relative to the y axis
         * @param blocksToForce list of the blocks that can still be forced if froce = false
         * @param blocksToPlace list of blockstates that the structure will randomly place
         */
        public static void generateFullTore(StructureWorldAccess world, BlockPos pos, boolean force, int innerRadius, int outerRadius, int xrotation, int yrotation, List<Block> blocksToForce, List<BlockState> blocksToPlace) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            double cosx = FastMaths.getFastCos(xrotation);
            double cosy = FastMaths.getFastCos(yrotation);
            double sinx = FastMaths.getFastSin(xrotation);
            double siny = FastMaths.getFastSin(yrotation);

            //a rotation that can be divided by 180 will return the same shape(it allow us to avoid some unnecessary calculations)
            if (xrotation % 180 == 0 && yrotation % 180 == 0) {
                int outerRadiusSquared = outerRadius * outerRadius;
                int innerRadiusSquared = innerRadius * innerRadius;
                for (int x = -outerRadius - innerRadius; x <= outerRadius + innerRadius; x++) {
                    for (int z = -outerRadius - innerRadius; z <= outerRadius + innerRadius; z++) {
                        for (int y = -outerRadius - innerRadius; y <= outerRadius + innerRadius; y++) {
                            int xsquared = x * x;
                            int ysquared = y * y;
                            int zsquared = z * z;
                            int a = xsquared + ysquared + zsquared + outerRadiusSquared - innerRadiusSquared;
                            if ((a * a) - 4 * outerRadiusSquared * (xsquared + ysquared) <= 0) {
                                int x_rot = (int) (x * cosy - y * siny);
                                int y_rot = (int) (x * siny + y * cosy);

                                int x_rot2 = (int) (x_rot * cosx + z * sinx);
                                int z_rot = (int) (-x_rot * sinx + z * cosx);
                                mutable.set(pos, x_rot2, y_rot, z_rot);
                                WorldGenUtil.verifyBlock(world, force, blocksToForce, blocksToPlace, mutable);
                            }
                        }
                    }
                }
            } else {
                int outerRadiusSquared = outerRadius * outerRadius;
                int innerRadiusSquared = innerRadius * innerRadius;
                for (float x = -outerRadius - innerRadius; x <= outerRadius + innerRadius; x += 0.5f) {
                    for (float z = -outerRadius - innerRadius; z <= outerRadius + innerRadius; z += 0.5f) {
                        for (float y = -outerRadius - innerRadius; y <= outerRadius + innerRadius; y += 0.5f) {
                            float xsquared = x * x;
                            float ysquared = y * y;
                            float zsquared = z * z;
                            float a = xsquared + ysquared + zsquared + outerRadiusSquared - innerRadiusSquared;
                            if ((a * a) - 4 * outerRadiusSquared * (xsquared + ysquared) <= 0) {
                                float x_rot = (int) (x * cosy - y * siny);
                                float y_rot = (int) (x * siny + y * cosy);

                                float x_rot2 = (int) (x_rot * cosx + z * sinx);
                                float z_rot = (int) (-x_rot * sinx + z * cosx);
                                mutable.set(pos, (int) x_rot2, (int) y_rot, (int) z_rot);
                                WorldGenUtil.verifyBlock(world, force, blocksToForce, blocksToPlace, mutable);
                            }
                        }
                    }
                }
            }
        }


        public static void generateEmptyTore(StructureWorldAccess world, BlockPos pos, boolean force, int innerRadius, int outerRadius, List<Block> blocksToForce, List<BlockState> blocksToPlace) {
            generateEmptyTore(world, pos, force, innerRadius, outerRadius, 0, 0, blocksToForce, blocksToPlace);
        }

        public static void generateEmptyTore(StructureWorldAccess world, BlockPos pos, int innerRadius, int outerRadius, BlockState blocksToPlace) {
            generateEmptyTore(world, pos, false, innerRadius, outerRadius, 0, 0, null, List.of(blocksToPlace));
        }

        /**
         * @param world         the world that the structure wiil spawn in
         * @param pos           the center of the tore
         * @param force         force the block if true
         * @param innerRadius   the radius of the circle
         * @param outerRadius   the radius of the center of the circle
         * @param xrotation     the rotation in degrees relative to the x axis
         * @param yrotation     the rotation in degrees relative to the y axis
         * @param blocksToForce list of the blocks that can still be forced if froce = false
         * @param blocksToPlace list of blockstates that the structure will randomly place
         */
        public static void generateEmptyTore(StructureWorldAccess world, BlockPos pos, boolean force, int innerRadius, int outerRadius, int xrotation, int yrotation, List<Block> blocksToForce, List<BlockState> blocksToPlace) {

            BlockPos.Mutable mutable = new BlockPos.Mutable();
            double cosx = FastMaths.getFastCos(xrotation);
            double cosy = FastMaths.getFastCos(yrotation);
            double sinx = FastMaths.getFastSin(xrotation);
            double siny = FastMaths.getFastSin(yrotation);

            //many if statement to avoid doing multiple if in the loops
            if (xrotation % 360 == 0 && yrotation % 360 == 0) {
                for (int u = 0; u <= 360; u += 40 / outerRadius) {
                    for (int v = 0; v <= 360; v += 45 / innerRadius) {
                        Vec3d pos2 = getToreCoordinates(u, v, innerRadius, outerRadius);
                        mutable.set((Vec3i) pos, (int) pos2.getX(), (int) pos2.getY(), (int) pos2.getZ());
                        WorldGenUtil.verifyBlock(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else if (xrotation % 90 == 0 && yrotation % 360 == 0) {
                for (int u = 0; u <= 360; u += 40 / outerRadius) {
                    for (int v = 0; v <= 360; v += 45 / innerRadius) {
                        Vec3d pos2 = getToreCoordinates(u, v, innerRadius, outerRadius);
                        mutable.set((Vec3i) pos, (int) pos2.getZ(), (int) pos2.getY(), (int) pos2.getX());
                        WorldGenUtil.verifyBlock(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else if (yrotation % 360 == 0) {
                for (int u = 0; u <= 360; u += 30 / outerRadius) {
                    for (int v = 0; v <= 360; v += 35 / innerRadius) {
                        Vec3d pos2 = getToreCoordinates(u, v, innerRadius, outerRadius);

                        int x_rot = (int) (pos2.getX() * cosx + pos2.getZ() * sinx);
                        int z_rot = (int) (-pos2.getX() * sinx + pos2.getZ() * cosx);

                        mutable.set((Vec3i) pos, x_rot, (int) pos2.getY(), z_rot);
                        WorldGenUtil.verifyBlock(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else if (xrotation % 360 == 0 && yrotation % 90 == 0) {
                for (int u = 0; u <= 360; u += 30 / outerRadius) {
                    for (int v = 0; v <= 360; v += 35 / innerRadius) {
                        Vec3d pos2 = getToreCoordinates(u, v, innerRadius, outerRadius);
                        mutable.set((Vec3i) pos, (int) pos2.getY(), (int) pos2.getX(), (int) pos2.getZ());
                        WorldGenUtil.verifyBlock(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else if (xrotation % 360 == 0) {
                for (int u = 0; u <= 360; u += 40 / outerRadius) {
                    for (int v = 0; v <= 360; v += 35 / innerRadius) {
                        Vec3d pos2 = getToreCoordinates(u, v, innerRadius, outerRadius);

                        int x_rot = (int) (pos.getX() * cosy - pos2.getY() * siny);
                        int y_rot = (int) (pos.getX() * siny + pos2.getY() * cosy);

                        mutable.set((Vec3i) pos, x_rot, y_rot, (int) pos2.getZ());
                        WorldGenUtil.verifyBlock(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else if (yrotation % 90 == 0) {
                for (int u = 0; u <= 3600; u += 1) {
                    for (int v = 0; v <= 3600; v += 1) {
                        Vec3d pos2 = getPreciseToreCoordinates(u, v, innerRadius, outerRadius);


                        int x_rot2 = (int) (pos2.getX() * cosx + pos2.getZ() * sinx);
                        int z_rot = (int) (-pos2.getX() * sinx + pos2.getZ() * cosx);


                        mutable.set((Vec3i) pos, x_rot2, z_rot, (int) pos2.getY());
                        WorldGenUtil.verifyBlock(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else {
                for (int u = 0; u <= 360; u += 45 / (outerRadius + 2 * innerRadius)) {
                    for (int v = 0; v <= 360; v += 45 / innerRadius) {
                        Vec3d pos2 = getToreCoordinates(u, v, innerRadius, outerRadius);

                        float x_rot = (float) (pos2.getX() * cosy - pos2.getY() * siny);
                        float y_rot = (float) (pos2.getX() * siny + pos2.getY() * cosy);

                        float x_rot2 = (float) (x_rot * cosx + pos2.getZ() * sinx);
                        float z_rot = (float) (-x_rot * sinx + pos2.getZ() * cosx);


                        mutable.set(pos, (int) x_rot2, (int) y_rot, (int) z_rot);
                        WorldGenUtil.verifyBlock(world, force, blocksToForce, blocksToPlace, mutable);
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


    //generate tore with radius that change
    public static class EllipsoidTore {
        public static void generateFullTore(StructureWorldAccess world, BlockPos pos, boolean force, int xinnerRadius, int xouterRadius, int zinnerRadius, int zouterRadius, BlockState blocksToPlace) {
            generateFullTore(world, pos, force, xinnerRadius, xouterRadius, zinnerRadius, zouterRadius, 0, 0, null, List.of(blocksToPlace));
        }

        public static void generateFullTore(StructureWorldAccess world, BlockPos pos, boolean force, int xinnerRadius, int xouterRadius, int zinnerRadius, int zouterRadius, List<Block> blocksToForce, List<BlockState> blocksToPlace) {
            generateFullTore(world, pos, force, xinnerRadius, xouterRadius, zinnerRadius, zouterRadius, 0, 0, blocksToForce, blocksToPlace);
        }

        /**
         * @param xinnerRadius  the inner radius relative to the x axis
         * @param xouterRadius  the outer radius relative to the x axis
         * @param zinnerRadius  the inner radius relative to the z axis
         * @param zouterRadius  the outer radius relative to the z axis
         * @param world         the world that the structure wiil spawn in
         * @param pos           the center of the tore
         * @param force         force the block if true
         * @param xrotation     the rotation in degrees relative to the x axis
         * @param yrotation     the rotation in degrees relative to the y axis
         * @param blocksToForce list of the blocks that can still be forced if froce = false
         * @param blocksToPlace list of blockstates that the structure will randomly place
         */
        public static void generateFullTore(StructureWorldAccess world, BlockPos pos, boolean force, int xinnerRadius, int xouterRadius, int zinnerRadius, int zouterRadius, int xrotation, int yrotation, List<Block> blocksToForce, List<BlockState> blocksToPlace) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            double cosx = FastMaths.getFastCos(xrotation);
            double cosy = FastMaths.getFastCos(yrotation);
            double sinx = FastMaths.getFastSin(xrotation);
            double siny = FastMaths.getFastSin(yrotation);

            int maxinnerRadius = Math.max(xinnerRadius, zinnerRadius);
            int maxouterRadius = Math.max(xouterRadius, zouterRadius);

            //a rotation that can be divided by 180 will return the same shape(it allow us to avoid some unnecessary calculations)
            if (xrotation % 180 == 0 && yrotation % 180 == 0) {
                for (int x = -maxouterRadius - maxinnerRadius; x <= maxouterRadius + maxinnerRadius; x++) {
                    for (int z = -maxouterRadius - maxinnerRadius; z <= maxouterRadius + maxinnerRadius; z++) {
                        for (int y = -maxouterRadius - maxinnerRadius; y <= maxouterRadius + maxinnerRadius; y++) {
                            int xsquared = x * x;
                            int ysquared = y * y;
                            int zsquared = z * z;

                            int angle = (int) Math.toDegrees(Math.atan2(y, x));
                            double outerRadius = getOuterRadius(xouterRadius, zouterRadius, angle);
                            double innerRadius = getInnerRadius(xinnerRadius, zinnerRadius, angle);

                            int outerRadiusSquared = (int) (outerRadius * outerRadius);
                            int innerRadiusSquared = (int) (innerRadius * innerRadius);
                            int a = xsquared + ysquared + zsquared + outerRadiusSquared - innerRadiusSquared;


                            if ((a * a) - 4 * outerRadiusSquared * (xsquared + ysquared) <= 0) {
                                mutable.set(pos, x, y, z);
                                WorldGenUtil.verifyBlock(world, force, blocksToForce, blocksToPlace, mutable);
                            }

                        }
                    }
                }
            } else {
                for (float x = -maxouterRadius - maxinnerRadius; x <= maxouterRadius + maxinnerRadius; x += 0.5f) {
                    for (float z = -maxouterRadius - maxinnerRadius; z <= maxouterRadius + maxinnerRadius; z += 0.5f) {
                        for (float y = -maxouterRadius - maxinnerRadius; y <= maxouterRadius + maxinnerRadius; y += 0.5f) {
                            float xsquared = x * x;
                            float ysquared = y * y;
                            float zsquared = z * z;

                            int angle = (int) Math.toDegrees(Math.atan2(y, x));
                            double outerRadius = getOuterRadius(xouterRadius, zouterRadius, angle);
                            double innerRadius = getInnerRadius(xinnerRadius, zinnerRadius, angle);

                            int outerRadiusSquared = (int) (outerRadius * outerRadius);
                            int innerRadiusSquared = (int) (innerRadius * innerRadius);
                            float a = xsquared + ysquared + zsquared + outerRadiusSquared - innerRadiusSquared;

                            if ((a * a) - 4 * outerRadiusSquared * (xsquared + ysquared) <= 0) {
                                float x_rot = (int) (x * cosy - y * siny);
                                float y_rot = (int) (x * siny + y * cosy);

                                float x_rot2 = (int) (x_rot * cosx + z * sinx);
                                float z_rot = (int) (-x_rot * sinx + z * cosx);
                                mutable.set(pos, (int) x_rot2, (int) y_rot, (int) z_rot);
                                WorldGenUtil.verifyBlock(world, force, blocksToForce, blocksToPlace, mutable);
                            }
                        }
                    }
                }
            }
        }

        public static double getInnerRadius(int xinnerRadius, int zinnerRadius, int angle) {
            return xinnerRadius + (zinnerRadius - xinnerRadius) * Math.abs(FastMaths.getFastSin(angle));

        }

        public static double getOuterRadius(int xouterRadius, int zouterRadius, int angle) {
            return xouterRadius + (zouterRadius - xouterRadius) * Math.abs(FastMaths.getFastSin(angle));
        }


        public static void generateEmptyTore(StructureWorldAccess world, BlockPos pos, boolean force, int xinnerRadius, int xouterRadius, int zinnerRadius, int zouterRadius, List<Block> blocksToForce, List<BlockState> blocksToPlace) {
            generateEmptyTore(world, pos, force, xinnerRadius, xouterRadius, zinnerRadius, zouterRadius, 0, 0, blocksToForce, blocksToPlace);
        }

        public static void generateEmptyTore(StructureWorldAccess world, BlockPos pos, int xinnerRadius, int xouterRadius, int zinnerRadius, int zouterRadius, BlockState blocksToPlace) {
            generateEmptyTore(world, pos, false, xinnerRadius, xouterRadius, zinnerRadius, zouterRadius,  0, 0, null, List.of(blocksToPlace));
        }

        /**
         * @param xinnerRadius  the inner radius relative to the x axis
         * @param xouterRadius  the outer radius relative to the x axis
         * @param zinnerRadius  the inner radius relative to the z axis
         * @param zouterRadius  the outer radius relative to the z axis
         * @param world         the world that the structure wiil spawn in
         * @param pos           the center of the tore
         * @param force         force the block if true
         * @param xrotation     the rotation in degrees relative to the x axis
         * @param yrotation     the rotation in degrees relative to the y axis
         * @param blocksToForce list of the blocks that can still be forced if froce = false
         * @param blocksToPlace list of blockstates that the structure will randomly place
         */
        public static void generateEmptyTore(StructureWorldAccess world, BlockPos pos, boolean force, int xinnerRadius, int xouterRadius, int zinnerRadius, int zouterRadius, int xrotation, int yrotation, List<Block> blocksToForce, List<BlockState> blocksToPlace) {

            BlockPos.Mutable mutable = new BlockPos.Mutable();
            double cosx = FastMaths.getFastCos(xrotation);
            double cosy = FastMaths.getFastCos(yrotation);
            double sinx = FastMaths.getFastSin(xrotation);
            double siny = FastMaths.getFastSin(yrotation);

            int maxouterRadius = Math.max(xouterRadius, zouterRadius);
            int maxinnerRadius = Math.max(xinnerRadius, zinnerRadius);

            //many if statement to avoid doing multiple if in the loops
            if (xrotation % 360 == 0 && yrotation % 360 == 0) {
                for (int u = 0; u <= 360; u += 40 / maxouterRadius) {
                    for (int v = 0; v <= 360; v += 45 / maxinnerRadius) {
                        Vec3d pos2 = getEllipsoidalToreCoordinates(u, v, xinnerRadius, xouterRadius, zinnerRadius, zouterRadius);
                        mutable.set((Vec3i) pos, (int) pos2.getX(), (int) pos2.getY(), (int) pos2.getZ());
                        WorldGenUtil.verifyBlock(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else if (xrotation % 90 == 0 && yrotation % 360 == 0) {
                for (int u = 0; u <= 360; u += 40 / maxouterRadius) {
                    for (int v = 0; v <= 360; v += 45 / maxinnerRadius) {
                        Vec3d pos2 = getEllipsoidalToreCoordinates(u, v, xinnerRadius, xouterRadius, zinnerRadius, zouterRadius);
                        mutable.set((Vec3i) pos, (int) pos2.getZ(), (int) pos2.getY(), (int) pos2.getX());
                        WorldGenUtil.verifyBlock(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else if (yrotation % 360 == 0) {
                for (int u = 0; u <= 360; u += 30 / maxouterRadius) {
                    for (int v = 0; v <= 360; v += 35 / maxinnerRadius) {
                        Vec3d pos2 = getEllipsoidalToreCoordinates(u, v, xinnerRadius, xouterRadius, zinnerRadius, zouterRadius);

                        int x_rot = (int) (pos2.getX() * cosx + pos2.getZ() * sinx);
                        int z_rot = (int) (-pos2.getX() * sinx + pos2.getZ() * cosx);

                        mutable.set((Vec3i) pos, x_rot, (int) pos2.getY(), z_rot);
                        WorldGenUtil.verifyBlock(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else if (xrotation % 360 == 0 && yrotation % 90 == 0) {
                for (int u = 0; u <= 360; u += 30 / maxouterRadius) {
                    for (int v = 0; v <= 360; v += 35 / maxinnerRadius) {
                        Vec3d pos2 = getEllipsoidalToreCoordinates(u, v, xinnerRadius, xouterRadius, zinnerRadius, zouterRadius);
                        mutable.set((Vec3i) pos, (int) pos2.getY(), (int) pos2.getX(), (int) pos2.getZ());
                        WorldGenUtil.verifyBlock(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else if (xrotation % 360 == 0) {
                for (int u = 0; u <= 360; u += 40 / maxouterRadius) {
                    for (int v = 0; v <= 360; v += 35 / maxinnerRadius) {
                        Vec3d pos2 = getEllipsoidalToreCoordinates(u, v, xinnerRadius, xouterRadius, zinnerRadius, zouterRadius);

                        int x_rot = (int) (pos.getX() * cosy - pos2.getY() * siny);
                        int y_rot = (int) (pos.getX() * siny + pos2.getY() * cosy);

                        mutable.set((Vec3i) pos, x_rot, y_rot, (int) pos2.getZ());
                        WorldGenUtil.verifyBlock(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else if (yrotation % 90 == 0) {
                for (int u = 0; u <= 3600; u += 1) {
                    for (int v = 0; v <= 3600; v += 1) {
                        Vec3d pos2 = getEllipsoidalToreCoordinates(u, v, xinnerRadius, xouterRadius, zinnerRadius, zouterRadius);


                        int x_rot2 = (int) (pos2.getX() * cosx + pos2.getZ() * sinx);
                        int z_rot = (int) (-pos2.getX() * sinx + pos2.getZ() * cosx);


                        mutable.set((Vec3i) pos, x_rot2, z_rot, (int) pos2.getY());
                        WorldGenUtil.verifyBlock(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            } else {
                for (int u = 0; u <= 360; u += 45 / maxouterRadius) {
                    for (int v = 0; v <= 360; v += 45 / maxinnerRadius) {
                        Vec3d pos2 = getEllipsoidalToreCoordinates(u, v, xinnerRadius, xouterRadius, zinnerRadius, zouterRadius);

                        float x_rot = (float) (pos2.getX() * cosy - pos2.getY() * siny);
                        float y_rot = (float) (pos2.getX() * siny + pos2.getY() * cosy);

                        float x_rot2 = (float) (x_rot * cosx + pos2.getZ() * sinx);
                        float z_rot = (float) (-x_rot * sinx + pos2.getZ() * cosx);


                        mutable.set(pos, (int) x_rot2, (int) y_rot, (int) z_rot);
                        WorldGenUtil.verifyBlock(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            }
        }

        public static Vec3d getEllipsoidalToreCoordinates(int u, int v, int xinnerRadius, int xouterRadius, int zinnerRadius, int zouterRadius) {

            // Interpolating the radii based on the angle
            double R = xouterRadius + (zouterRadius - xouterRadius) * Math.abs(FastMaths.getFastSin(u));
            double r = xinnerRadius + (zinnerRadius - xinnerRadius) * Math.abs(FastMaths.getFastSin(u));

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
