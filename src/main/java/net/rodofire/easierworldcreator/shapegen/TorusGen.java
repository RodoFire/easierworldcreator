package net.rodofire.easierworldcreator.shapegen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.shapeutil.BlockLayer;
import net.rodofire.easierworldcreator.shapeutil.FillableShape;
import net.rodofire.easierworldcreator.util.FastMaths;
import net.rodofire.easierworldcreator.worldgenutil.BlockPlaceUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

/**
 * allow you to generate torus shape
 */
public class TorusGen extends FillableShape {
    private int innerRadiusx;
    private int outerRadiusx;
    private int innerRadiusz;
    private int outerRadiusz;
    private int radiusx;
    private int radiusz;


    public TorusGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, int innerRadius, int outerRadius, int radius) {
        super(world, pos);
        this.innerRadiusx = innerRadius;
        this.outerRadiusx = outerRadius;
        this.innerRadiusz = innerRadius;
        this.outerRadiusz = outerRadius;
        this.radiusx = radius;
        this.radiusz = radius;
    }

    public TorusGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, List<BlockLayer> layers, boolean force, List<Block> blocksToForce, int xrotation, int yrotation, int secondxrotation, int innerRadius, int outerRadius, int radiusx, int radiusz) {
        super(world, pos, layers, force, blocksToForce, xrotation, yrotation, secondxrotation);
        this.innerRadiusx = innerRadius;
        this.outerRadiusx = outerRadius;
        this.innerRadiusz = innerRadius;
        this.outerRadiusz = outerRadius;
        this.radiusx = radiusx;
        this.radiusz = radiusz;
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

    public int getRadiusx() {
        return radiusx;
    }

    public void setRadiusx(int radiusx) {
        this.radiusx = radiusx;
    }

    public int getRadiusz() {
        return radiusz;
    }

    public void setRadiusz(int radiusz) {
        this.radiusz = radiusz;
    }

    @Override
    public List<BlockPos> getBlockPos() {
        if(this.getFillingType() == Type.EMPTY){
            return this.generateEmptyTore();
        }
        return this.generateFullTore();
    }

    @Override
    public List<Vec3d> getVec3d() {
        return List.of();
    }


    public List<BlockPos> generateFullTore() {
        List<BlockPos> poslist = new ArrayList<>();

        float innerRadiusXSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusx * radiusx;
        float innerRadiusZSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusz * radiusz;
        int outerRadiusSquaredx = outerRadiusx * outerRadiusx;
        int innerRadiusSquaredx = innerRadiusx * innerRadiusx;
        int innerRadiusSquaredz = innerRadiusz * innerRadiusz;
        int outerRadiusSquaredz = outerRadiusz * outerRadiusz;


        int mainnerRadiusx = Math.max(innerRadiusx, innerRadiusz);
        int maouterRadiusx = Math.max(outerRadiusx, outerRadiusz);


        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() % 180 == 0) {
            for (int x = -maouterRadiusx - mainnerRadiusx; x <= maouterRadiusx + mainnerRadiusx; x++) {
                int xsquared = x * x;
                for (int z = -maouterRadiusx - mainnerRadiusx; z <= maouterRadiusx + mainnerRadiusx; z++) {
                    int zsquared = z * z;
                    for (int y = -maouterRadiusx - mainnerRadiusx; y <= maouterRadiusx + mainnerRadiusx; y++) {
                        int ysquared = y * y;

                        int angle = (int) Math.toDegrees(Math.atan2(y, x));
                        double outerRadius = getOuterRadius(angle);
                        double innerRadius = getInnerRadius(angle);

                        int outerRadiusSquared = (int) (outerRadius * outerRadius);
                        int innerRadiusSquared = (int) (innerRadius * innerRadius);
                        int a = xsquared + ysquared + zsquared + outerRadiusSquared - innerRadiusSquared;


                        if ((a * a) - 4 * outerRadiusSquared * (xsquared + ysquared) <= 0) {
                            /*boolean bl = true;
                            if (innerRadiusXSquared != 0) {
                                float innerXSquared = x * x / innerRadiusXSquared;
                                float innerZSquared = z * z / innerRadiusZSquared;
                                if (innerXSquared + innerZSquared <= 1f) { // pas dans l'ovale intérieur
                                    bl = false;
                                }
                            }
                            if (bl) {*/
                                poslist.add(new BlockPos((int) (this.getPos().getX() + x), (int) (this.getPos().getY() + y), (int) (this.getPos().getZ() + z)));
                            //}
                        }

                    }
                }
            }
        } else {
            for (float x = -maouterRadiusx - mainnerRadiusx; x <= maouterRadiusx + mainnerRadiusx; x += 0.5f) {
                for (float z = -maouterRadiusx - mainnerRadiusx; z <= maouterRadiusx + mainnerRadiusx; z += 0.5f) {
                    for (float y = -maouterRadiusx - mainnerRadiusx; y <= maouterRadiusx + mainnerRadiusx; y += 0.5f) {
                        float xsquared = x * x;
                        float ysquared = y * y;
                        float zsquared = z * z;

                        int angle = (int) Math.toDegrees(Math.atan2(y, x));
                        double outerRadius = getOuterRadius(angle);
                        double innerRadius = getInnerRadius(angle);

                        int outerRadiusSquared = (int) (outerRadius * outerRadius);
                        int innerRadiusSquared = (int) (innerRadius * innerRadius);
                        float a = xsquared + ysquared + zsquared + outerRadiusSquared - innerRadiusSquared;

                        if ((a * a) - 4 * outerRadiusSquared * (xsquared + ysquared) <= 0) {
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

        return poslist;
    }


    public List<BlockPos> generateEmptyTore() {

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        List<BlockPos> poslist = new ArrayList<>();

        int maxouterRadius = Math.max(outerRadiusx, outerRadiusz);
        int maxinnerRadius = Math.max(innerRadiusx, innerRadiusz);
        //many if statement to avoid doing multiple if in the loops
        if (this.getXrotation() % 180 == 0 && this.getYrotation() % 180 == 0 && this.getSecondXrotation() % 180 == 0) {
            for (int u = 0; u <= 360; u += 40 / maxouterRadius) {
                for (int v = 0; v <= 360; v += 45 / maxinnerRadius) {
                    Vec3d vec = this.getEllipsoidalToreCoordinates(u, v);
                    poslist.add(new BlockPos((int) (this.getPos().getX()+vec.x), (int) (this.getPos().getY()+ vec.y), (int) (this.getPos().getZ() + vec.z)));
                }
            }
        } else {
            for (int u = 0; u <= 360; u += 40 / maxouterRadius) {
                for (int v = 0; v <= 360; v += 45 / maxinnerRadius) {
                    Vec3d vec = this.getEllipsoidalToreCoordinates(u, v);
                    poslist.add(this.getCoordinatesRotation((float) vec.x, (float) vec.y, (float) vec.z, this.getPos()));
                }
            }
        }
        return poslist;
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
}
