package net.rodofire.easierworldcreator.shapegen;

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
 * <p>Since 2.1.0, the shape doesn't return a {@link List<BlockPos>} but it returns instead a {@code List<Set<BlockPos>>}
 * <p>Before 2.1.0, the BlockPos list was a simple list.
 * <p>Starting from 2.1.0, the shapes return a list of {@link ChunkPos} that has a set of {@link BlockPos}
 * <p>The change from {@link List} to {@link Set} was done to avoid duplicates BlockPos which resulted in unnecessary calculations.
 * <p>this allow easy multithreading for the Block assignment done in the {@link Shape} which result in better performance;
 * </p>
 */
@SuppressWarnings("unused")
public class TorusGen extends FillableShape {
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
     * @param world           the world the spiral will spawn in
     * @param pos             the center of the spiral
     * @param placeMoment     define the moment where the shape will be placed
     * @param layerPlace      how the {@code @BlockStates} inside of a {@link BlockLayer} will be placed
     * @param layersType      how the Layers will be placed
     * @param yRotation       first rotation around the y-axis
     * @param zRotation       second rotation around the z-axis
     * @param secondYRotation last rotation around the y-axis
     * @param featureName     the name of the feature
     * @param innerRadiusX    the radius of the inner circle on the x-axis
     * @param outerRadiusX    the radius of the outer circle on the x-axis
     * @param innerRadiusZ    the radius of the inner circle on the z-axis
     * @param outerRadiusZ    the radius of the outer circle on the z-axis
     */
    public TorusGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment, LayerPlace layerPlace, LayersType layersType, int yRotation, int zRotation, int secondYRotation, String featureName, int innerRadiusX, int outerRadiusX, int innerRadiusZ, int outerRadiusZ) {
        super(world, pos, placeMoment, layerPlace, layersType, yRotation, zRotation, secondYRotation, featureName);
        this.innerRadiusX = innerRadiusX;
        this.outerRadiusX = outerRadiusX;
        this.innerRadiusZ = innerRadiusZ;
        this.outerRadiusZ = outerRadiusZ;
    }

    /**
     * init the Torus Shape
     *
     * @param world       the world the spiral will spawn in
     * @param pos         the center of the spiral
     * @param placeMoment define the moment where the shape will be placed
     * @param innerRadius the radius of the inner circle
     * @param outerRadius the radius of the outer circle
     */
    public TorusGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, PlaceMoment placeMoment, int innerRadius, int outerRadius) {
        super(world, pos, placeMoment);
        this.innerRadiusX = innerRadius;
        this.outerRadiusX = outerRadius;
        this.innerRadiusZ = innerRadius;
        this.outerRadiusZ = outerRadius;
    }

    public int getInnerRadiusX() {
        return innerRadiusX;
    }

    public void setInnerRadiusX(int innerRadiusX) {
        this.innerRadiusX = innerRadiusX;
    }

    public int getOuterRadiusX() {
        return outerRadiusX;
    }

    public void setOuterRadiusX(int outerRadiusX) {
        this.outerRadiusX = outerRadiusX;
    }

    public int getInnerRadiusZ() {
        return innerRadiusZ;
    }

    public void setInnerRadiusZ(int innerRadiusZ) {
        this.innerRadiusZ = innerRadiusZ;
    }

    public int getOuterRadiusZ() {
        return outerRadiusZ;
    }

    public void setOuterRadiusZ(int outerRadiusZ) {
        this.outerRadiusZ = outerRadiusZ;
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
        this.verticalTorus = verticalTorus;
    }

    public float getHorizontalTorus() {
        return horizontalTorus;
    }

    public void setHorizontalTorus(float horizontalTorus) {
        this.horizontalTorus = horizontalTorus;
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
     * generates a full torus / tore with custom filling
     * the shape with the torus might be different from the empty one if you're using custom torus filling
     */
    public void generateFullTore(Map<ChunkPos, Set<BlockPos>> chunkMap) {
        this.setFill();
        List<BlockPos> poslist = new ArrayList<>();

        //TODO fix
        //float innerRadiusXSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusX * radiusX;
        //float innerRadiusZSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusZ * radiusZ;


        int maxInnerRadiusX = Math.max(innerRadiusX, innerRadiusZ);
        int maxOuterRadiusX = Math.max(outerRadiusX, outerRadiusZ);

        int b = maxOuterRadiusX + maxInnerRadiusX;

        if ((verticalTorus == 1f && horizontalTorus == 1f && this.getYRotation() % 180 == 0 && this.getZRotation() % 180 == 0 && this.getSecondYRotation() % 180 == 0)
                || (verticalTorus == 1f && this.getYRotation() % 180 == 0) || (horizontalTorus == 1f && this.getYRotation() % 180 == 0 && this.getSecondYRotation() % 180 == 0)) {

            for (int x = (-b); x <= 2 * b * this.horizontalTorus - b; x++) {
                int xSquared = x * x;
                for (int z = -b; z <= b; z++) {

                    int zSquared = z * z;
                    int angle = (int) Math.toDegrees(Math.atan2(z, x));
                    double outerRadius = getOuterRadius(angle);
                    double innerRadius = getInnerRadius(angle);

                    int outerRadiusSquared = (int) (outerRadius * outerRadius);
                    int innerRadiusSquared = (int) (innerRadius * innerRadius);
                    int a1 = xSquared + zSquared + outerRadiusSquared - innerRadiusSquared;

                    int e = 4 * outerRadiusSquared * (xSquared + zSquared);

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
                                BlockPos pos = new BlockPos(this.getPos().getX() + x, this.getPos().getY() + y, this.getPos().getZ() + z);
                                if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                                    this.biggerThanChunk = true;
                                WorldGenUtil.modifyChunkMap(pos, chunkMap);
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

                    float a1 = xSquared + zSquared + outerRadiusSquared - innerRadiusSquared;

                    float e = 4 * outerRadiusSquared * (xSquared + zSquared);
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


    /**
     * generates an empty torus
     * the shape with the torus might be different from the full one if you're using custom torus filling
     */
    public void generateEmptyTore(Map<ChunkPos, Set<BlockPos>> chunkMap) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        List<BlockPos> poslist = new ArrayList<>();

        int maxOuterRadius = Math.max(outerRadiusX, outerRadiusZ);
        int maxInnerRadius = Math.max(innerRadiusX, innerRadiusZ);
        //many if statement to avoid doing multiple if in the loops
        if (this.getYRotation() % 180 == 0 && this.getZRotation() == 0 && this.getSecondYRotation() % 180 == 0) {
            for (int u = 0; u <= this.verticalTorus * 360; u += 40 / maxOuterRadius) {
                for (int v = 0; v <= this.horizontalTorus * 360; v += 45 / maxInnerRadius) {
                    Vec3d vec = this.getEllipsoidalToreCoordinates(u, v);
                    BlockPos pos = new BlockPos((int) (getPos().getX() + vec.x), (int) (getPos().getY() + vec.y), (int) (getPos().getZ() + vec.z));
                    if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                        this.biggerThanChunk = true;
                    WorldGenUtil.modifyChunkMap(pos, chunkMap);
                }
            }
        } else {
            for (int u = 0; u <= 360 * this.verticalTorus; u += 40 / maxOuterRadius) {
                for (int v = 0; v <= 360 * this.horizontalTorus; v += 45 / maxInnerRadius) {
                    Vec3d vec = this.getEllipsoidalToreCoordinates(u, v);
                    BlockPos pos = this.getCoordinatesRotation((float) vec.x, (float) vec.y, (float) vec.z, this.getPos());
                    if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(pos, this.getPos()))
                        this.biggerThanChunk = true;
                    WorldGenUtil.modifyChunkMap(pos, chunkMap);
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

    public Vec3d getEllipsoidalToreCoordinates(int u, int v) {

        // Interpolating the radii based on the angle
        double R = outerRadiusX + (outerRadiusZ - outerRadiusX) * Math.abs(FastMaths.getFastSin(u));
        double r = innerRadiusX + (innerRadiusZ - innerRadiusX) * Math.abs(FastMaths.getFastSin(u));

        double a = R + r * FastMaths.getFastCos(v);
        double x = (a * FastMaths.getFastCos(u));
        double z = (a * FastMaths.getFastSin(u));
        double y = (r * FastMaths.getFastSin(v));

        return new Vec3d(x, y, z);
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
