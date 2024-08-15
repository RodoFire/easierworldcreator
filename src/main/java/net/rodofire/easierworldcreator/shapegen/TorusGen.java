package net.rodofire.easierworldcreator.shapegen;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.shapeutil.BlockLayer;
import net.rodofire.easierworldcreator.shapeutil.FillableShape;
import net.rodofire.easierworldcreator.shapeutil.Shape;
import net.rodofire.easierworldcreator.util.FastMaths;
import net.rodofire.easierworldcreator.worldgenutil.WorldGenUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
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
 * Class to generate Torus related shapes
 * <p>
 * <p>Since 2.1.0, the shape doesn't return a {@link List<BlockPos>} but it returns instead a {@code List<Set<BlockPos>>}
 * <p>Before 2.1.0, the BlockPos list was a simple list.
 * <p>Starting from 2.1.0, the shapes returns a list of {@link ChunkPos} that has a set of {@link BlockPos}
 * <p>The change from {@link List} to {@link Set} was done to avoid duplicates BlockPos wich resulted in unnecessary calculations.
 * <p>this allow easy multithreading for the Block assignment done in the {@link Shape} which result in better performance;
 * </p>
 */
public class TorusGen extends FillableShape {
    private int innerRadiusx;
    private int outerRadiusx;
    private int innerRadiusz;
    private int outerRadiusz;

    //set the shape of the torus
    private TorusType torusType = TorusType.FULL;
    //float that determines how much of the structure is filled along y-axis
    private float verticalTorus = 1f;
    //float that determines how much of the structure is filled along x-axis
    private float horizontalTorus = 1f;


    public TorusGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment, int innerRadius, int outerRadius) {
        super(world, pos, placeMoment);
        this.innerRadiusx = innerRadius;
        this.outerRadiusx = outerRadius;
        this.innerRadiusz = innerRadius;
        this.outerRadiusz = outerRadius;
    }

    public TorusGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment, List<BlockLayer> layers, boolean force, List<Block> blocksToForce, int xrotation, int yrotation, int secondxrotation, int innerRadius, int outerRadius) {
        super(world, pos, placeMoment, layers, force, blocksToForce, xrotation, yrotation, secondxrotation);
        this.innerRadiusx = innerRadius;
        this.outerRadiusx = outerRadius;
        this.innerRadiusz = innerRadius;
        this.outerRadiusz = outerRadius;
    }

    public int getInnerRadiusx() {
        return innerRadiusx;
    }

    public void setInnerRadiusx(int innerRadiusx) {
        this.innerRadiusx = innerRadiusx;
    }

    public int getOuterRadiusx() {
        return outerRadiusx;
    }

    public void setOuterRadiusx(int outerRadiusx) {
        this.outerRadiusx = outerRadiusx;
    }

    public int getInnerRadiusz() {
        return innerRadiusz;
    }

    public void setInnerRadiusz(int innerRadiusz) {
        this.innerRadiusz = innerRadiusz;
    }

    public int getOuterRadiusz() {
        return outerRadiusz;
    }

    public void setOuterRadiusz(int outerRadiusz) {
        this.outerRadiusz = outerRadiusz;
    }


    public TorusType getTorusType() {
        return torusType;
    }

    public void setTorusType(TorusType torusType) {
        this.torusType = torusType;
    }

    public float getVerticalTorus() {
        return verticalTorus;
    }

    public void setVerticalTorus(float verticalTorus) {
        verticalTorus = verticalTorus;
    }

    public float getHorizontalTorus() {
        return horizontalTorus;
    }

    public void setHorizontalTorus(float horizontalTorus) {
        horizontalTorus = horizontalTorus;
    }

    @Override
    public List<Set<BlockPos>> getBlockPos() {
        setTorusFill();
        Map<ChunkPos, Set<BlockPos>> chunkMap = new HashMap<>();
        if (this.getFillingType() == Type.EMPTY) {
            this.generateEmptyTore(chunkMap);
        } else {
            this.generateFullTore(chunkMap);
        }
        return new ArrayList<>(chunkMap.values());
    }

    @Override
    public List<Vec3d> getVec3d() {
        return List.of();
    }


    /**
     * generates a full tore / tore with custom filling
     * the shape with the torus might be different from the empty one if you're using custom torus filling
     */
    public void generateFullTore(Map<ChunkPos, Set<BlockPos>> chunkMap) {
        this.setFill();
        List<BlockPos> poslist = new ArrayList<>();

        //TODO fix
        //float innerRadiusXSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusx * radiusx;
        //float innerRadiusZSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusz * radiusz;


        int mainnerRadiusx = Math.max(innerRadiusx, innerRadiusz);
        int maouterRadiusx = Math.max(outerRadiusx, outerRadiusz);

        int b = maouterRadiusx + mainnerRadiusx;

        if ((verticalTorus == 1f && horizontalTorus == 1f && this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() % 180 == 0)
                || (verticalTorus == 1f && this.getYrotation() % 180 == 0) || (horizontalTorus == 1f && this.getXrotation() % 180 == 0 && this.getSecondXrotation() % 180 == 0)) {

            for (int x = (-b); x <= 2 * b * this.horizontalTorus - b; x++) {
                int xsquared = x * x;
                for (int z = -b; z <= b; z++) {
                    int zsquared = z * z;
                    for (int y = -b; y <= 2 * b * this.verticalTorus - b; y++) {
                        int ysquared = y * y;

                        int angle = (int) Math.toDegrees(Math.atan2(z, x));
                        double outerRadius = getOuterRadius(angle);
                        double innerRadius = getInnerRadius(angle);

                        int outerRadiusSquared = (int) (outerRadius * outerRadius);
                        int innerRadiusSquared = (int) (innerRadius * innerRadius);
                        int a = xsquared + ysquared + zsquared + outerRadiusSquared - innerRadiusSquared;


                        if ((a * a) - 4 * outerRadiusSquared * (xsquared + zsquared) <= 0) {
                            boolean bl = true;
                            /*if (innerRadiusXSquared != 0) {
                                float innerXSquared = x * x / innerRadiusXSquared;
                                float innerZSquared = z * z / innerRadiusZSquared;
                                if (innerXSquared + innerZSquared <= 1f) { // pas dans l'ovale intérieur
                                    bl = false;
                                }
                            }*/
                            if (bl) {
                                BlockPos pos = new BlockPos((int) (this.getPos().getX() + x), (int) (this.getPos().getY() + y), (int) (this.getPos().getZ() + z));
                                WorldGenUtil.modifyChunkMap(pos, chunkMap);
                            }
                        }

                    }
                }
            }
        } else {
            for (float x = -b; x <= 2 * b * this.horizontalTorus - b; x += 0.5f) {
                for (float z = -b; z <= b; z += 0.5f) {
                    for (float y = -b; y <= 2 * b * this.verticalTorus - b; y += 0.5f) {
                        float xsquared = x * x;
                        float ysquared = y * y;
                        float zsquared = z * z;

                        int angle = (int) Math.toDegrees(Math.atan2(z, x));
                        double outerRadius = getOuterRadius(angle);
                        double innerRadius = getInnerRadius(angle);

                        int outerRadiusSquared = (int) (outerRadius * outerRadius);
                        int innerRadiusSquared = (int) (innerRadius * innerRadius);
                        float a = xsquared + ysquared + zsquared + outerRadiusSquared - innerRadiusSquared;

                        if ((a * a) - 4 * outerRadiusSquared * (xsquared + ysquared) <= 0) {
                            //TODO
                            //implement fully the torus interior filling
                            boolean bl = true;
                            /*if (innerRadiusXSquared != 0) {
                                float innerXSquared = x * x / innerRadiusXSquared;
                                float innerZSquared = z * z / innerRadiusZSquared;
                                if (innerXSquared + innerZSquared <= 1f) { // pas dans l'ovale intérieur
                                    bl = false;
                                }
                            }*/
                            if (bl) {
                                BlockPos pos = this.getCoordinatesRotation(x, y, z, this.getPos());
                                WorldGenUtil.modifyChunkMap(pos, chunkMap);
                            }
                        }
                    }
                }
            }
        }

    }


    /**
     * generates an empty torus
     * the shape with the torus might be different from the full one if you're using custom torus filling
     */
    public void generateEmptyTore(Map<ChunkPos, Set<BlockPos>> chunkMap) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        List<BlockPos> poslist = new ArrayList<>();

        int maxouterRadius = Math.max(outerRadiusx, outerRadiusz);
        int maxinnerRadius = Math.max(innerRadiusx, innerRadiusz);
        //many if statement to avoid doing multiple if in the loops
        if (this.getXrotation() % 180 == 0 && this.getYrotation() == 0 && this.getSecondXrotation() % 180 == 0) {
            for (int u = 0; u <= this.verticalTorus * 360; u += 40 / maxouterRadius) {
                for (int v = 0; v <= this.horizontalTorus * 360; v += 45 / maxinnerRadius) {
                    Vec3d vec = this.getEllipsoidalToreCoordinates(u, v);
                    BlockPos pos = new BlockPos((int) (getPos().getX() + vec.x), (int) (getPos().getY() + vec.y), (int) (getPos().getZ() + vec.z));
                    WorldGenUtil.modifyChunkMap(pos, chunkMap);
                }
            }
        } else {
            for (int u = 0; u <= 360 * this.verticalTorus; u += 40 / maxouterRadius) {
                for (int v = 0; v <= 360 * this.horizontalTorus; v += 45 / maxinnerRadius) {
                    Vec3d vec = this.getEllipsoidalToreCoordinates(u, v);
                    BlockPos pos = this.getCoordinatesRotation((float) vec.x, (float) vec.y, (float) vec.z, this.getPos());
                    WorldGenUtil.modifyChunkMap(pos, chunkMap);
                }
            }
        }
    }

    private void setTorusFill() {
        if (this.torusType == TorusType.FULL) {
            this.verticalTorus = 1f;
            this.horizontalTorus = 1f;
        } else if (this.torusType == TorusType.HORIZONTAL_HALF) {
            this.horizontalTorus = 0.5f;
            this.verticalTorus = 1f;
        } else if (this.torusType == TorusType.VERTICAL_HALF) {
            this.verticalTorus = 0.5f;
            this.horizontalTorus = 1f;
        } else if (this.torusType == TorusType.HORIZONTAL_CUSTOM) {
            this.verticalTorus = 1f;
        } else if (this.torusType == TorusType.VERTICAL_CUSTOM) {
            this.horizontalTorus = 1f;
        }
    }


    private Vec3d getPreciseToreCoordinates(int u, int v, int innerRadius, int outerRadius) {
        double a = outerRadius + innerRadius * FastMaths.getPreciseCos(v);
        double x = (a * FastMaths.getPreciseCos(u));
        double z = (a * FastMaths.getPreciseSin(u));
        double y = (innerRadius * FastMaths.getPreciseSin(v));
        return new Vec3d(x, y, z);
    }

    public Vec3d getEllipsoidalToreCoordinates(int u, int v) {

        // Interpolating the radii based on the angle
        double R = outerRadiusx + (outerRadiusz - outerRadiusx) * Math.abs(FastMaths.getFastSin(u));
        double r = innerRadiusx + (innerRadiusz - innerRadiusx) * Math.abs(FastMaths.getFastSin(u));

        double a = R + r * FastMaths.getFastCos(v);
        double x = (a * FastMaths.getFastCos(u));
        double z = (a * FastMaths.getFastSin(u));
        double y = (r * FastMaths.getFastSin(v));

        return new Vec3d(x, y, z);
    }

    public double getInnerRadius(int angle) {
        return innerRadiusx + (innerRadiusz - innerRadiusx) * Math.abs(FastMaths.getFastSin(angle));

    }

    public double getOuterRadius(int angle) {
        return outerRadiusx + (outerRadiusz - outerRadiusx) * Math.abs(FastMaths.getFastSin(angle));
    }

    /**
     * change the outside filling of the torus
     * it is different from the Filling Type
     * the torus type changes the exterior filling while the other one change the interior filling type
     */
    public enum TorusType {
        FULL,
        VERTICAL_HALF,
        HORIZONTAL_HALF,
        VERTICAL_CUSTOM,
        HORIZONTAL_CUSTOM,
        CUSTOM
    }
}
