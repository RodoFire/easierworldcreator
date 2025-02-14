package net.rodofire.easierworldcreator.shape.block.gen;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayer;
import net.rodofire.easierworldcreator.maths.FastMaths;
import net.rodofire.easierworldcreator.shape.block.instanciator.AbstractBlockShape;
import net.rodofire.easierworldcreator.shape.block.instanciator.AbstractFillableBlockShape;
import net.rodofire.easierworldcreator.shape.block.rotations.Rotator;
import net.rodofire.easierworldcreator.util.LongPosHelper;
import net.rodofire.easierworldcreator.util.WorldGenUtil;
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
 * <p>Since 2.1.0, the shape doesn't return a {@link List<BlockPos>} but it returns instead a {@code List<Set<BlockPos>>}
 * <p>Before 2.1.0, the BlockPos list was a simple list.
 * <p>Starting from 2.1.0, the shapes return a list of {@link ChunkPos} that has a set of {@link BlockPos}
 * <p>The change from {@link List} to {@link Set} was done to avoid duplicates BlockPos which resulted in unnecessary calculations.
 * <p>this allow easy multithreading for the Block assignment done in the {@link AbstractBlockShape} which result in better performance;
 * </p>
 */
@SuppressWarnings("unused")
public class TorusGen extends AbstractFillableBlockShape {
    private int innerRadiusX;
    private int outerRadiusX;
    private int innerRadiusZ;
    private int outerRadiusZ;

    //set the shape of the torus
    private TorusType torusType = TorusType.FULL;
    //float that determines how much of the structure is filled along the y-axis
    private float verticalTorus = 1f;
    //float that determines how much of the structure is filled along the x-axis
    private float horizontalTorus = 1f;

    /**
     * init the Torus Shape
     *
     * @param pos          the center of the spiral
     * @param innerRadiusX the radius of the inner circle on the x-axis
     * @param outerRadiusX the radius of the outer circle on the x-axis
     * @param innerRadiusZ the radius of the inner circle on the z-axis
     * @param outerRadiusZ the radius of the outer circle on the z-axis
     */
    public TorusGen(@NotNull BlockPos pos, Rotator rotator, int innerRadiusX, int outerRadiusX, int innerRadiusZ, int outerRadiusZ) {
        super(pos, rotator);
        this.innerRadiusX = innerRadiusX;
        this.outerRadiusX = outerRadiusX;
        this.innerRadiusZ = innerRadiusZ;
        this.outerRadiusZ = outerRadiusZ;
    }

    /**
     * init the Torus Shape
     *
     * @param pos         the center of the spiral
     * @param innerRadius the radius of the inner circle
     * @param outerRadius the radius of the outer circle
     */
    public TorusGen(@NotNull BlockPos pos, int innerRadius, int outerRadius) {
        super(pos);
        this.innerRadiusX = innerRadius;
        this.outerRadiusX = outerRadius;
        this.innerRadiusZ = innerRadius;
        this.outerRadiusZ = outerRadius;
    }

    /**
     * Gets the inner radius of the torus along the X-axis.
     *
     * @return the inner radius along the X-axis.
     */
    public int getInnerRadiusX() {
        return innerRadiusX;
    }

    /**
     * Sets the inner radius of the torus along the X-axis.
     *
     * @param innerRadiusX the inner radius to set along the X-axis.
     */
    public void setInnerRadiusX(int innerRadiusX) {
        this.innerRadiusX = innerRadiusX;
    }

    /**
     * Gets the outer radius of the torus along the X-axis.
     *
     * @return the outer radius along the X-axis.
     */
    public int getOuterRadiusX() {
        return outerRadiusX;
    }

    /**
     * Sets the outer radius of the torus along the X-axis.
     *
     * @param outerRadiusX the outer radius to set along the X-axis.
     */
    public void setOuterRadiusX(int outerRadiusX) {
        this.outerRadiusX = outerRadiusX;
    }

    /**
     * Gets the inner radius of the torus along the Z-axis.
     *
     * @return the inner radius along the Z-axis.
     */
    public int getInnerRadiusZ() {
        return innerRadiusZ;
    }

    /**
     * Sets the inner radius of the torus along the Z-axis.
     *
     * @param innerRadiusZ the inner radius to set along the Z-axis.
     */
    public void setInnerRadiusZ(int innerRadiusZ) {
        this.innerRadiusZ = innerRadiusZ;
    }

    /**
     * Gets the outer radius of the torus along the Z-axis.
     *
     * @return the outer radius along the Z-axis.
     */
    public int getOuterRadiusZ() {
        return outerRadiusZ;
    }

    /**
     * Sets the outer radius of the torus along the Z-axis.
     *
     * @param outerRadiusZ the outer radius to set along the Z-axis.
     */
    public void setOuterRadiusZ(int outerRadiusZ) {
        this.outerRadiusZ = outerRadiusZ;
    }

    /**
     * Gets the type of the torus.
     *
     * <p>The torus type determines the configuration of the torus, such as whether it is hollow, solid, or has other specific properties.</p>
     *
     * @return the {@link TorusType} of the torus.
     */
    public TorusType getTorusType() {
        return torusType;
    }

    /**
     * Sets the type of the torus.
     *
     * <p>The torus type determines the configuration of the torus, such as whether it is hollow, solid, or has other specific properties.</p>
     *
     * @param torusType the {@link TorusType} to set.
     */
    public void setTorusType(TorusType torusType) {
        this.torusType = torusType;
    }

    /**
     * Gets the vertical torus ratio.
     *
     * <p>The vertical torus determines the percentage of the torus's height relative to its overall dimensions.</p>
     *
     * @return the vertical torus ratio as a float.
     */
    public float getVerticalTorus() {
        return verticalTorus;
    }

    /**
     * Sets the vertical torus ratio.
     *
     * <p>The vertical torus determines the percentage of the torus's height relative to its overall dimensions.</p>
     *
     * @param verticalTorus the vertical torus ratio to set, as a float.
     */
    public void setVerticalTorus(float verticalTorus) {
        this.verticalTorus = verticalTorus;
    }

    /**
     * Gets the horizontal torus ratio.
     *
     * <p>The horizontal torus determines the percentage of the torus's width relative to its overall dimensions.</p>
     *
     * @return the horizontal torus ratio as a float.
     */
    public float getHorizontalTorus() {
        return horizontalTorus;
    }

    /**
     * Sets the horizontal torus ratio.
     *
     * <p>The horizontal torus determines the percentage of the torus's width relative to its overall dimensions.</p>
     *
     * @param horizontalTorus the horizontal torus ratio to set, as a float.
     */
    public void setHorizontalTorus(float horizontalTorus) {
        this.horizontalTorus = horizontalTorus;
    }

    /**
     * Method to get all the BlockPos needed to place the shape
     *
     * @return a list that contain divided BlockPos depending in the chunkPos
     */
    @Override
    public Map<ChunkPos, LongOpenHashSet> getShapeCoordinates() {
        setTorusFill();
        Map<ChunkPos, LongOpenHashSet> chunkMap = new HashMap<>();
        if (this.getFillingType() == Type.EMPTY) {
            this.generateEmptyTore(chunkMap);
        } else {
            this.generateFullTore(chunkMap);
        }
        return chunkMap;
    }


    /**
     * generates a full torus / tore with custom filling
     * the shape with the torus might be different from the empty one if you're using custom torus filling
     */
    public void generateFullTore(Map<ChunkPos, LongOpenHashSet> chunkMap) {
        this.setFill();

        //TODO fix
        //float innerRadiusXSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusX * radiusX;
        //float innerRadiusZSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusZ * radiusZ;


        int maxInnerRadiusX = Math.max(innerRadiusX, innerRadiusZ);
        int maxOuterRadiusX = Math.max(outerRadiusX, outerRadiusZ);

        int b = maxOuterRadiusX + maxInnerRadiusX;

        if (rotator == null) {
            for (int x = (-b); x <= 2 * b * this.horizontalTorus - b; x++) {
                int xSquared = x * x;
                for (int z = -b; z <= b; z++) {

                    int zSquared = z * z;
                    int angle = (int) Math.toDegrees(Math.atan2(z, x));
                    double outerRadius = getOuterRadius(angle);
                    double innerRadius = getInnerRadius(angle);
                    int squaredSum = xSquared + zSquared;
                    int outerRadiusSquared = (int) (outerRadius * outerRadius);
                    int innerRadiusSquared = (int) (innerRadius * innerRadius);
                    int a1 = squaredSum + outerRadiusSquared - innerRadiusSquared;

                    int e = 4 * outerRadiusSquared * (squaredSum);

                    for (int y = -b; y <= 2 * b * this.verticalTorus - b; y++) {
                        int ySquared = y * y;


                        int a = a1 + ySquared;


                        if ((a * a) - e <= 0) {
                            boolean bl = true;
                            /*if (innerRadiusXSquared != 0) {
                                float innerXSquared = x * x / innerRadiusXSquared;
                                float innerZSquared = z * z / innerRadiusZSquared;
                                if (innerXSquared + innerZSquared <= 1f) {
                                    bl = false;
                                }
                            }*/
                            if (bl) {
                                WorldGenUtil.modifyChunkMap(LongPosHelper.encodeBlockPos(x + centerX, y + centerY, z + centerZ), chunkMap);
                            }
                        }

                    }
                }
            }
        } else {
            for (float x = -b; x <= 2 * b * this.horizontalTorus - b; x += 0.5f) {
                float xSquared = x * x;
                for (float z = -b; z <= b; z += 0.5f) {
                    float zSquared = z * z;

                    int angle = (int) Math.toDegrees(Math.atan2(z, x));
                    double outerRadius = getOuterRadius(angle);
                    double innerRadius = getInnerRadius(angle);

                    int outerRadiusSquared = (int) (outerRadius * outerRadius);
                    int innerRadiusSquared = (int) (innerRadius * innerRadius);
                    float squaredSum = xSquared + zSquared;

                    float a1 = squaredSum + outerRadiusSquared - innerRadiusSquared;

                    float e = 4 * outerRadiusSquared * (squaredSum);
                    for (float y = -b; y <= 2 * b * this.verticalTorus - b; y += 0.5f) {
                        float ySquared = y * y;

                        float a = a1 + ySquared;

                        if ((a * a) - e <= 0) {
                            //TODO
                            //implement fully the torus interior filling
                            boolean bl = true;
                            /*if (innerRadiusXSquared != 0) {
                                float innerXSquared = x * x / innerRadiusXSquared;
                                float innerZSquared = z * z / innerRadiusZSquared;
                                if (innerXSquared + innerZSquared <= 1f) {
                                    bl = false;
                                }
                            }*/
                            if (bl) {
                                WorldGenUtil.modifyChunkMap(rotator.get(x, y, z), chunkMap);
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
    public void generateEmptyTore(Map<ChunkPos, LongOpenHashSet> chunkMap) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        List<BlockPos> poslist = new ArrayList<>();

        int maxOuterRadius = Math.max(outerRadiusX, outerRadiusZ);
        int maxInnerRadius = Math.max(innerRadiusX, innerRadiusZ);
        //many if statement to avoid doing multiple if in the loops
        if (rotator == null) {
            for (int u = 0; u <= this.verticalTorus * 360; u += 40 / maxOuterRadius) {
                for (int v = 0; v <= this.horizontalTorus * 360; v += 45 / maxInnerRadius) {
                    float[] vec = this.getEllipsoidalToreCoordinates(u, v);
                    WorldGenUtil.modifyChunkMap(LongPosHelper.encodeBlockPos((int) (centerX + vec[0]), (int) (centerY + vec[1]), (int) (centerZ + vec[3])), chunkMap);
                }
            }
        } else {
            for (int u = 0; u <= 360 * this.verticalTorus; u += 40 / maxOuterRadius) {
                for (int v = 0; v <= 360 * this.horizontalTorus; v += 45 / maxInnerRadius) {
                    float[] vec = this.getEllipsoidalToreCoordinates(u, v);
                    WorldGenUtil.modifyChunkMap(rotator.get(vec[0], vec[1], vec[2]), chunkMap);
                }
            }
        }
    }

    private void setTorusFill() {
        switch (this.torusType) {
            case FULL:
                this.verticalTorus = 1f;
                this.horizontalTorus = 1f;
                break;
            case HORIZONTAL_HALF:
                this.horizontalTorus = 0.5f;
                this.verticalTorus = 1f;
                break;
            case VERTICAL_HALF:
                this.verticalTorus = 0.5f;
                this.horizontalTorus = 1f;
                break;
            case HORIZONTAL_CUSTOM:
                this.verticalTorus = 1f;
                break;
            case VERTICAL_CUSTOM:
                this.horizontalTorus = 1f;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this.torusType);
        }
    }


    private Vec3d getPreciseToreCoordinates(int u, int v, int innerRadius, int outerRadius) {
        double a = outerRadius + innerRadius * FastMaths.getPreciseCos(v);
        double x = (a * FastMaths.getPreciseCos(u));
        double z = (a * FastMaths.getPreciseSin(u));
        double y = (innerRadius * FastMaths.getPreciseSin(v));
        return new Vec3d(x, y, z);
    }

    public float[] getEllipsoidalToreCoordinates(int u, int v) {

        // Interpolating the radii based on the angle
        double R = outerRadiusX + (outerRadiusZ - outerRadiusX) * Math.abs(FastMaths.getFastSin(u));
        double r = innerRadiusX + (innerRadiusZ - innerRadiusX) * Math.abs(FastMaths.getFastSin(u));

        double a = R + r * FastMaths.getFastCos(v);
        float x = (float) (a * FastMaths.getFastCos(u));
        float z = (float) (a * FastMaths.getFastSin(u));
        float y = (float) (r * FastMaths.getFastSin(v));

        return new float[]{x, y, z};
    }

    public double getInnerRadius(int angle) {
        return innerRadiusX + (innerRadiusZ - innerRadiusX) * Math.abs(FastMaths.getFastSin(angle));

    }

    public double getOuterRadius(int angle) {
        return outerRadiusX + (outerRadiusZ - outerRadiusX) * Math.abs(FastMaths.getFastSin(angle));
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
