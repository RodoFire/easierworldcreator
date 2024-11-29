package net.rodofire.easierworldcreator.shape.block.instanciator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.DefaultBlockList;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayer;
import net.rodofire.easierworldcreator.util.WorldGenUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unused")
public abstract class AbstractBlockShapeLayer extends AbstractBlockShapePlaceType {
    //enums to define how the structure is defined
    private AbstractBlockShape.LayersType layersType = AbstractBlockShape.LayersType.SURFACE;


    //Only required if layerType == LayerType.ALONG_DIRECTION
    private Vec3d directionalLayerDirection = new Vec3d(0, 1, 0);

    //Center of the structure if PlaceType == Block
    private BlockPos radialCenterPos = this.getPos();

    //Center of the structure if PlaceType == Particle
    private Vec3d radialCenterVec3d;


    /**
     * init the ShapeLayer
     *
     * @param world       the world the spiral will spawn in
     * @param pos         the center of the spiral
     * @param placeMoment define the moment where the shape will be placed
     * @param layerPlace  how the {@code @BlockStates} inside of a {@link BlockLayer} will be placed
     * @param layersType  how the Layers will be placed
     */
    public AbstractBlockShapeLayer(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment, LayerPlace layerPlace, LayersType layersType) {
        super(world, pos, placeMoment, layerPlace);
        this.layersType = layersType;
    }

    /**
     * init the ShapeLayer
     *
     * @param world       the world of the shape
     * @param pos         the pos of the shape (usually the center of the structure)
     * @param placeMoment define the moment where the shape will be placed
     */
    public AbstractBlockShapeLayer(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment) {
        super(world, pos, placeMoment);
    }


    /*---------- Layers Related ----------*/

    /**
     * method to change the direction of the orthogonal vector used when {@code layerType = ALONG_DIRECTION}
     *
     * @param vector the vector that will be set
     */
    public void setLayerDirection(Vec3d vector) {
        this.directionalLayerDirection = vector.normalize();
    }

    /**
     * used to get the layerType initialized
     *
     * @return the layer type of the shape
     */
    public LayersType getLayersType() {
        return layersType;
    }

    /**
     * used to change the layerType
     *
     * @param layersType the layer type that will replace the actual one
     */
    public void setLayersType(LayersType layersType) {
        this.layersType = layersType;
    }

    public Vec3d getRadialCenterVec3d() {
        return radialCenterVec3d;
    }

    public void setRadialCenterVec3d(Vec3d radialCenterVec3d) {
        this.radialCenterVec3d = radialCenterVec3d;
    }

    public BlockPos getRadialCenterPos() {
        return radialCenterPos;
    }

    public void setRadialCenterPos(BlockPos radialCenterPos) {
        this.radialCenterPos = radialCenterPos;
    }

    public Vec3d getDirectionalLayerDirection() {
        return directionalLayerDirection;
    }

    public void setDirectionalLayerDirection(Vec3d directionalLayerDirection) {
        this.directionalLayerDirection = directionalLayerDirection;
    }


    private void addPosToBlockList(BlockPos pos, List<BlockState> states, Set<DefaultBlockList> defaultBlockLists) {
        BlockState state = getBlockToPlace(states, pos);

        for (DefaultBlockList defaultBlockList : defaultBlockLists) {
            if (defaultBlockList.getBlockState().equals(state)) {
                defaultBlockList.addBlockPos(pos);
                return;
            }
        }
        defaultBlockLists.add(new DefaultBlockList(List.of(pos), state));
    }

    private void addVerifiedPosToBlockList(BlockPos pos, List<BlockState> states, Set<Block> blocksToForce, boolean force, Set<DefaultBlockList> defaultBlockLists, Map<BlockPos, BlockState> blockStateMap) {
        if (!verifyBlocks(pos, blocksToForce, force, blockStateMap)) {
            return;
        }
        addPosToBlockList(pos, states, defaultBlockLists);
    }


    /**
     * place the layers of the structure starting from the first layer to the second to the third
     *
     * @param firstPosList list of the BlockPos that compose the structure
     */
    public Set<DefaultBlockList> getLayers(Set<BlockPos> firstPosList) {
        Set<DefaultBlockList> defaultBlockLists = new HashSet<>();
        if (this.getBlockLayer().size() == 1) {
            List<BlockState> states = this.getBlockLayer().get(0).getBlockStates();
            for (BlockPos pos : firstPosList) {
                addPosToBlockList(pos, states, defaultBlockLists);
            }
            return defaultBlockLists;
        }
        switch (layersType) {
            case SURFACE -> defaultBlockLists.addAll(this.getSurfaceBlocks(firstPosList));
            case INNER_RADIAL -> defaultBlockLists.addAll(this.getInnerRadialBlocks(firstPosList));
            case OUTER_RADIAL -> defaultBlockLists.addAll(this.getOuterRadialBlocks(firstPosList));
            case INNER_CYLINDRICAL -> defaultBlockLists.addAll(this.getInnerCylindricalBlocks(firstPosList));
            case OUTER_CYLINDRICAL -> defaultBlockLists.addAll(this.getOuterCylindricalBlocks(firstPosList));
            case ALONG_DIRECTION -> defaultBlockLists.addAll(this.getDirectionalLayers(firstPosList));
            default -> throw new IllegalStateException("Unexpected value: " + layersType);
        }
        return defaultBlockLists;
    }

    public Set<DefaultBlockList> getLayersWithVerification(Set<BlockPos> firstPosList, Map<BlockPos, BlockState> blockStateMap) {
        Set<DefaultBlockList> defaultBlockLists = new HashSet<>();
        if (this.getBlockLayer().size() == 1) {
            List<BlockState> states = this.getBlockLayer().get(0).getBlockStates();
            for (BlockPos pos : firstPosList) {
                addPosToBlockList(pos, states, defaultBlockLists);
            }
            return defaultBlockLists;
        }
        switch (layersType) {
            case SURFACE -> defaultBlockLists.addAll(this.getVerifiedSurfaceBlocks(firstPosList, blockStateMap));
            case INNER_RADIAL ->
                    defaultBlockLists.addAll(this.getVerifiedInnerRadialBlocks(firstPosList, blockStateMap));
            case OUTER_RADIAL ->
                    defaultBlockLists.addAll(this.getVerifiedOuterRadialBlocks(firstPosList, blockStateMap));
            case INNER_CYLINDRICAL ->
                    defaultBlockLists.addAll(this.getVerifiedInnerCylindricalBlocks(firstPosList, blockStateMap));
            case OUTER_CYLINDRICAL ->
                    defaultBlockLists.addAll(this.getVerifiedOuterCylindricalBlocks(firstPosList, blockStateMap));
            case ALONG_DIRECTION ->
                    defaultBlockLists.addAll(this.getVerifiedDirectionalLayers(firstPosList, blockStateMap));
            default -> throw new IllegalStateException("Unexpected value: " + layersType);
        }
        return defaultBlockLists;
    }

    /**
     * place the layers of the structure depending on the {@link AbstractBlockShape.LayersType}
     *
     * @param firstPosList list of the BlockPos that compose the structure
     */
    public void placeLayers(Set<BlockPos> firstPosList) {
        if (this.getBlockLayer().size() == 1) {
            List<BlockState> states = this.getBlockLayer().get(0).getBlockStates();
            for (BlockPos pos : firstPosList) {
                placeBlocksWithVerification(states, pos);
            }
            return;
        }
        switch (layersType) {
            case SURFACE -> this.placeSurfaceBlocks(firstPosList);
            case INNER_RADIAL -> this.placeInnerRadialBlocks(firstPosList);
            case OUTER_RADIAL -> this.placeOuterRadialBlocks(firstPosList);
            case INNER_CYLINDRICAL -> this.placeInnerCylindricalBlocks(firstPosList);
            case OUTER_CYLINDRICAL -> this.placeOuterCylindricalBlocks(firstPosList);
            case ALONG_DIRECTION -> this.placeDirectionalLayers(firstPosList);
            default -> throw new IllegalStateException("Unexpected value: " + layersType);
        }
    }

    private void placeSurfaceBlocks(Set<BlockPos> firstPosList) {
        Set<BlockPos> posList;
        posList = this.placeFirstSurfaceBlockLayers(firstPosList);

        if (posList == null) return;

        for (int i = 1; i < this.getBlockLayer().size(); ++i) {
            if (posList.isEmpty()) return;
            List<Set<BlockPos>> pos1 = this.placeSurfaceBlockLayer(posList, i);
            posList = pos1.get(1);
            firstPosList = pos1.get(0);

            List<BlockState> states = this.getBlockLayer().get(i - 1).getBlockStates();

            for (BlockPos pos : firstPosList) {
                this.placeBlocks(states, pos);
            }
        }
        List<BlockState> states = this.getBlockLayer().get(this.getBlockLayer().size() - 1).getBlockStates();

        for (BlockPos pos : posList) {
            this.placeBlocks(states, pos);
        }
    }

    /**
     * This method returns a temporary blockPos list of the first layer after verification. The first layer will the be placed in {@link #placeSurfaceBlockLayer(Set, int)}
     *
     * @param firstPosList the list of BlockPos to verify at first
     * @return the set of verified BlockPos
     */
    public Set<BlockPos> placeFirstSurfaceBlockLayers(Set<BlockPos> firstPosList) {
        Set<BlockPos> newPosList = new HashSet<>();
        for (BlockPos pos : firstPosList) {
            if (verifyBlocks(pos)) {
                newPosList.add(pos);
            }
        }
        return newPosList;
    }

    /**
     * <p>This method is used to determine and place the layers of the structure
     * <p>It determines for every {@link BlockPos} provided if the pos can be replaced by the new Layer.
     * <p>To determine it, it gets the depth of the previous layer.
     * It verifies if there is in the {@link List} the actual {@link  BlockPos} with an offset of the depth
     * <p>  -if there is,
     * it adds the {@link BlockPos} in a new {@link List}
     * and remove the {@link BlockPos} from the existing list.
     * <p>  -If not, it does nothing.
     *
     * @param posList    the list of {@link BlockPos} of the precedent Layer
     * @param layerIndex the index to get the depth
     * @return two {@link List}.
     * One corresponding to the final {@code List<BlockPos>} of the previous layer.
     * The other one, the rest of the {@code List<BlockPos>} of the structure that will be used by the next iteration.
     */
    public List<Set<BlockPos>> placeSurfaceBlockLayer(Set<BlockPos> posList, int layerIndex) {
        Set<BlockPos> newPosList = new HashSet<>();

        // Precompute the depth of the previous layer if it exists
        int previousLayerDepth = this.getBlockLayer().get(layerIndex - 1).getDepth();

        // Convert posList to a set for faster lookups
        Set<BlockPos> posSet = new HashSet<>(posList);
        Iterator<BlockPos> iterator = posList.iterator();

        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            BlockPos posUp = pos.up(previousLayerDepth);
            if (!posSet.contains(posUp)) {
                continue;
            }
            iterator.remove();
            //placeBlocks(layerIndex, pos);
            newPosList.add(pos);
        }
        return List.of(posList, newPosList);
    }

    public void placeInnerCylindricalBlocks(Set<BlockPos> posList) {
        List<Integer> layerDistance = new ArrayList<>();
        layerDistance.add(this.getBlockLayer().get(0).getDepth());

        for (int i = 1; i < this.getBlockLayer().size(); i++) {
            layerDistance.add(this.getBlockLayer().get(i).getDepth() + layerDistance.get(i - 1));
        }

        int layerDistanceSize = layerDistance.size();
        for (BlockPos pos : posList) {
            boolean bl = false;
            float distance = WorldGenUtil.getDistance(new BlockPos(this.radialCenterPos.getX(), pos.getY(), this.radialCenterPos.getZ()), pos);
            for (int i = 0; i < layerDistanceSize - 1; i++) {
                if (distance < layerDistance.get(i)) {
                    placeBlocksWithVerification(i, pos);
                    bl = true;
                }
            }
            if (!bl) {
                placeBlocksWithVerification(layerDistanceSize - 1, pos);
            }
        }
    }

    public void placeOuterCylindricalBlocks(Set<BlockPos> posList) {
        List<Integer> layerDistance = new ArrayList<>();
        layerDistance.add(this.getBlockLayer().get(0).getDepth());

        float maxDistance = 0;
        for (BlockPos pos : posList) {
            float distance = WorldGenUtil.getDistance(new BlockPos(this.radialCenterPos.getX(), pos.getY(), this.radialCenterPos.getZ()), pos);
            maxDistance = Math.max(distance, maxDistance);
        }
        for (int i = 1; i < this.getBlockLayer().size(); i++) {
            layerDistance.add(this.getBlockLayer().get(i).getDepth() + layerDistance.get(i - 1));
        }

        int layerDistanceSize = layerDistance.size();
        for (BlockPos pos : posList) {
            boolean bl = false;
            float distance = WorldGenUtil.getDistance(new BlockPos(this.radialCenterPos.getX(), pos.getY(), this.radialCenterPos.getZ()), pos);
            for (int i = 0; i < layerDistanceSize - 1; i++) {
                if (distance > maxDistance - layerDistance.get(i)) {
                    placeBlocksWithVerification(i, pos);
                    bl = true;
                }
            }
            if (!bl) {
                placeBlocksWithVerification(layerDistanceSize - 1, pos);
            }
        }
    }

    //be careful when using layers with 1 block depth, that might do some weird things
    public void placeInnerRadialBlocks(Set<BlockPos> posList) {
        Set<DefaultBlockList> defaultBlockLists = new HashSet<>();
        List<Integer> layerDistance = new ArrayList<>();
        layerDistance.add(this.getBlockLayer().get(0).getDepth());

        for (int i = 1; i < this.getBlockLayer().size(); i++) {
            layerDistance.add(this.getBlockLayer().get(i).getDepth() + layerDistance.get(i - 1));
        }

        int layerDistanceSize = layerDistance.size();
        for (BlockPos pos : posList) {
            boolean bl = false;
            float distance = WorldGenUtil.getDistance(new BlockPos(this.radialCenterPos.getX(), pos.getY(), this.radialCenterPos.getZ()), pos);
            for (int i = 0; i < layerDistanceSize - 1; i++) {
                if (distance < layerDistance.get(i)) {
                    placeBlocksWithVerification(this.getBlockLayer().get(i).getBlockStates(), pos);
                    bl = true;
                }
            }
            if (!bl) {
                placeBlocksWithVerification(this.getBlockLayer().get(layerDistanceSize - 1).getBlockStates(), pos);
            }
        }
    }

    public void placeOuterRadialBlocks(Set<BlockPos> posList) {
        List<Integer> layerDistance = new ArrayList<>();
        layerDistance.add(this.getBlockLayer().get(0).getDepth());

        float maxDistance = 0;
        for (BlockPos pos : posList) {
            float distance = WorldGenUtil.getDistance(this.radialCenterPos, pos);
            maxDistance = Math.max(distance, maxDistance);
        }
        for (int i = 1; i < this.getBlockLayer().size(); i++) {
            layerDistance.add(this.getBlockLayer().get(i).getDepth() + layerDistance.get(i - 1));
        }

        int layerDistanceSize = layerDistance.size();
        for (BlockPos pos : posList) {
            boolean bl = false;
            float distance = WorldGenUtil.getDistance(radialCenterPos, pos);
            for (int i = 0; i < layerDistanceSize - 1; i++) {
                if (distance > maxDistance - layerDistance.get(i)) {
                    placeBlocksWithVerification(i, pos);
                    bl = true;
                }
            }
            if (!bl) {
                placeBlocksWithVerification(layerDistanceSize - 1, pos);
            }
        }
    }


    //TODO
    //known bug where the first layers is a little wider than what it is supposed to be

    /**
     * This method place the {@link Block} in a normal plan of the directional {@link Vec3d}
     * <p>This method assign for every {@link BlockPos} in the list a {@link Block} depending on the {@link LayerPlace} provided
     * and then place it.
     * <p>- To determine the {@link BlockLayer} to place,
     * <p> First, it sorts the list depending on the {@link Vec3d}.
     * <p> Then for every {@link Block}, it calculates the distance between the actual {@link Block} and the first one of the sorted list.
     * It will then place the Block corresponding to the actual depth, determined distance / first distance
     *
     * @param firstPosList the list of the BlockPos
     */
    private void placeDirectionalLayers(Set<BlockPos> firstPosList) {
        Vec3d direction = this.directionalLayerDirection.normalize();
        List<BlockPos> poslist = new ArrayList<>(firstPosList);

        // Sort positions according to the directional vector
        poslist.sort(Comparator.comparingDouble(pos -> -pos.getX() * direction.x - pos.getY() * direction.y - pos.getZ() * direction.z));

        BlockPos firstPoint = poslist.get(0);

        int u = 0;
        int a = this.getBlockLayer().get(u).getDepth();
        float b;
        float g = 0;
        float h = 0;
        List<BlockState> states = getBlockLayer().get(u).getBlockStates();
        int size = this.getBlockLayer().size() - 1;
        for (BlockPos pos : poslist) {

            if (u != size) {
                b = WorldGenUtil.getDistanceFromPointToPlane(direction, firstPoint.toCenterPos(), pos.toCenterPos());
                if (g == 0 && b > 2.0E-4) {
                    g = b;
                    h = (float) ((a) * g + 0.00002);
                }

                if (b <= h) {
                    placeBlocksWithVerification(states, pos);
                } else {
                    u++;
                    a += this.getBlockLayer().get(u).getDepth();
                    h = (float) (a * g + 0.00002);
                    states = getBlockLayer().get(u).getBlockStates();
                    placeBlocksWithVerification(states, pos);

                }
            }
            //place the last layer on all the structure everything was placed
            else {
                placeBlocksWithVerification(u, pos);
            }
        }
    }


    /*----------------------------------------------------------------------------------------------------------------*/
    private Set<DefaultBlockList> getVerifiedSurfaceBlocks(Set<BlockPos> firstPosList, Map<BlockPos, BlockState> blockStateMap) {
        Set<BlockPos> posList = firstPosList;
        Set<DefaultBlockList> defaultBlockLists = new HashSet<>();

        if (posList == null) {
            return defaultBlockLists;
        }

        for (int i = 1; i < this.getBlockLayer().size(); ++i) {
            if (posList.isEmpty()) return defaultBlockLists;
            BlockLayer layer = this.getBlockLayer().get(i);
            List<Set<BlockPos>> pos1 = this.placeSurfaceBlockLayer(new HashSet<>(posList), i);
            posList = pos1.get(1);
            firstPosList = pos1.get(0);
            List<BlockState> states = this.getBlockLayer().get(i - 1).getBlockStates();

            Set<BlockPos> firstPosListCopy = new HashSet<>(firstPosList);
            for (BlockPos pos : firstPosListCopy) {
                addVerifiedPosToBlockList(pos, states, layer.getBlocksToForce(), layer.isForce(), defaultBlockLists, blockStateMap);
            }
        }
        BlockLayer layer = this.getBlockLayer().get(this.getBlockLayer().size() - 1);
        List<BlockState> states = layer.getBlockStates();

        // Create a copy for safe iteration
        Set<BlockPos> posListCopy = new HashSet<>(posList);
        for (BlockPos pos : posListCopy) {
            addVerifiedPosToBlockList(pos, states, layer.getBlocksToForce(), layer.isForce(), defaultBlockLists, blockStateMap);
        }
        return defaultBlockLists;
    }

    public Set<DefaultBlockList> getVerifiedInnerCylindricalBlocks(Set<BlockPos> posList, Map<BlockPos, BlockState> blockStateMap) {
        Set<DefaultBlockList> defaultBlockLists = new HashSet<>();
        List<Integer> layerDistance = new ArrayList<>();
        layerDistance.add(this.getBlockLayer().get(0).getDepth());

        for (int i = 1; i < this.getBlockLayer().size(); i++) {
            layerDistance.add(this.getBlockLayer().get(i).getDepth() + layerDistance.get(i - 1));
        }

        int layerDistanceSize = layerDistance.size() - 1;
        for (BlockPos pos : posList) {
            boolean bl = false;
            float distance = WorldGenUtil.getDistance(new BlockPos(this.radialCenterPos.getX(), pos.getY(), this.radialCenterPos.getZ()), pos);
            for (int i = 0; i < layerDistanceSize; i++) {
                if (distance < layerDistance.get(i)) {
                    BlockLayer layer = this.getBlockLayer().get(i);
                    addVerifiedPosToBlockList(pos, layer.getBlockStates(), layer.getBlocksToForce(), layer.isForce(), defaultBlockLists, blockStateMap);
                    bl = true;
                }
            }
            if (!bl) {
                BlockLayer layer = this.getBlockLayer().get(layerDistanceSize);
                addVerifiedPosToBlockList(pos, layer.getBlockStates(), layer.getBlocksToForce(), layer.isForce(), defaultBlockLists, blockStateMap);
            }
        }
        return defaultBlockLists;
    }

    public Set<DefaultBlockList> getVerifiedOuterCylindricalBlocks(Set<BlockPos> posList, Map<BlockPos, BlockState> blockStateMap) {
        Set<DefaultBlockList> defaultBlockLists = new HashSet<>();
        List<Integer> layerDistance = new ArrayList<>();

        //get the max distance of the list
        float maxDistance = 0;
        for (BlockPos pos : posList) {
            float distance = WorldGenUtil.getDistance(new BlockPos(this.radialCenterPos.getX(), pos.getY(), this.radialCenterPos.getZ()), pos);
            maxDistance = Math.max(distance, maxDistance);
        }

        layerDistance.add(this.getBlockLayer().get(0).getDepth());

        for (int i = 1; i < this.getBlockLayer().size(); i++) {
            layerDistance.add(this.getBlockLayer().get(i).getDepth() + layerDistance.get(i - 1));
        }

        int layerDistanceSize = layerDistance.size();
        for (BlockPos pos : posList) {
            boolean bl = false;
            float distance = WorldGenUtil.getDistance(new BlockPos(this.radialCenterPos.getX(), pos.getY(), this.radialCenterPos.getZ()), pos);
            for (int i = 0; i < layerDistanceSize - 1; i++) {
                if (distance > maxDistance - layerDistance.get(i)) {
                    BlockLayer layer = this.getBlockLayer().get(i);
                    addVerifiedPosToBlockList(pos, layer.getBlockStates(), layer.getBlocksToForce(), layer.isForce(), defaultBlockLists, blockStateMap);
                    bl = true;
                }
            }
            if (!bl) {
                BlockLayer layer = this.getBlockLayer().get(layerDistanceSize);
                addVerifiedPosToBlockList(pos, layer.getBlockStates(), layer.getBlocksToForce(), layer.isForce(), defaultBlockLists, blockStateMap);
            }
        }
        return defaultBlockLists;
    }

    //be careful when using layers with 1 block depth, that might do some weird things
    public Set<DefaultBlockList> getVerifiedInnerRadialBlocks(Set<BlockPos> posList, Map<BlockPos, BlockState> blockStateMap) {
        Set<DefaultBlockList> defaultBlockLists = new HashSet<>();
        List<Integer> layerDistance = new ArrayList<>();
        layerDistance.add(this.getBlockLayer().get(0).getDepth());

        for (int i = 1; i < this.getBlockLayer().size(); i++) {
            layerDistance.add(this.getBlockLayer().get(i).getDepth() + layerDistance.get(i - 1));
        }

        int layerDistanceSize = layerDistance.size();
        for (BlockPos pos : posList) {
            boolean bl = false;
            float distance = WorldGenUtil.getDistance(radialCenterPos, pos);
            for (int i = 0; i < layerDistanceSize - 1; i++) {
                if (distance < layerDistance.get(i)) {
                    BlockLayer layer = this.getBlockLayer().get(layerDistanceSize);
                    addVerifiedPosToBlockList(pos, layer.getBlockStates(), layer.getBlocksToForce(), layer.isForce(), defaultBlockLists, blockStateMap);
                    bl = true;
                }
            }
            if (!bl) {
                BlockLayer layer = this.getBlockLayer().get(layerDistanceSize);
                addVerifiedPosToBlockList(pos, layer.getBlockStates(), layer.getBlocksToForce(), layer.isForce(), defaultBlockLists, blockStateMap);
            }
        }
        return defaultBlockLists;
    }

    public Set<DefaultBlockList> getVerifiedOuterRadialBlocks(Set<BlockPos> posList, Map<BlockPos, BlockState> blockStateMap) {
        Set<DefaultBlockList> defaultBlockLists = new HashSet<>();
        List<Integer> layerDistance = new ArrayList<>();
        layerDistance.add(this.getBlockLayer().get(0).getDepth());

        float maxDistance = 0;
        for (BlockPos pos : posList) {
            float distance = WorldGenUtil.getDistance(this.radialCenterPos, pos);
            maxDistance = Math.max(distance, maxDistance);
        }
        for (int i = 1; i < this.getBlockLayer().size(); i++) {
            layerDistance.add(this.getBlockLayer().get(i).getDepth() + layerDistance.get(i - 1));
        }

        int layerDistanceSize = layerDistance.size();
        for (BlockPos pos : posList) {
            boolean bl = false;
            float distance = WorldGenUtil.getDistance(radialCenterPos, pos);
            for (int i = 0; i < layerDistanceSize - 1; i++) {
                if (distance > maxDistance - layerDistance.get(i)) {
                    BlockLayer layer = this.getBlockLayer().get(layerDistanceSize);
                    addVerifiedPosToBlockList(pos, layer.getBlockStates(), layer.getBlocksToForce(), layer.isForce(), defaultBlockLists, blockStateMap);
                    bl = true;
                }
            }
            if (!bl) {
                BlockLayer layer = this.getBlockLayer().get(layerDistanceSize);
                addVerifiedPosToBlockList(pos, layer.getBlockStates(), layer.getBlocksToForce(), layer.isForce(), defaultBlockLists, blockStateMap);
            }
        }
        return defaultBlockLists;
    }

    //TODO
    //known bug where the first layers is a little wider than what it is supposed to be
    private Set<DefaultBlockList> getVerifiedDirectionalLayers(Set<BlockPos> firstPosList, Map<BlockPos, BlockState> blockStateMap) {
        Vec3d direction = this.directionalLayerDirection.normalize();
        List<BlockPos> poslist = new ArrayList<>(firstPosList);
        Set<DefaultBlockList> defaultBlockLists = new HashSet<>();

        // Sort positions according to the directional vector
        poslist.sort(Comparator.comparingDouble(pos -> -pos.getX() * direction.x - pos.getY() * direction.y - pos.getZ() * direction.z));

        BlockPos firstPoint = poslist.get(0);

        int layerIndex = 0;
        int cumulativeDepth = this.getBlockLayer().get(layerIndex).getDepth();
        float initialDistance = 0;
        float maxDistance = 0;

        List<BlockState> currentStates = this.getBlockLayer().get(layerIndex).getBlockStates();
        int totalLayers = this.getBlockLayer().size() - 1;

        for (BlockPos pos : poslist) {
            float currentDistance = WorldGenUtil.getDistanceFromPointToPlane(direction, firstPoint.toCenterPos(), pos.toCenterPos());

            if (initialDistance == 0 && currentDistance > 2.0E-4) {
                initialDistance = currentDistance;
                maxDistance = cumulativeDepth * initialDistance + 0.00002f;
            }
            BlockLayer layer = this.getBlockLayer().get(layerIndex);
            // Move to the next layer if needed
            while (layerIndex < totalLayers && currentDistance > maxDistance) {
                layerIndex++;
                layer = this.getBlockLayer().get(layerIndex);
                cumulativeDepth += layer.getDepth();
                maxDistance = cumulativeDepth * initialDistance + 0.00002f;
                currentStates = layer.getBlockStates();
            }

            // Add the block position to the appropriate layer

            addVerifiedPosToBlockList(pos, currentStates, layer.getBlocksToForce(), layer.isForce(), defaultBlockLists, blockStateMap);
        }

        return defaultBlockLists;
    }


    /*-----------------------------------------------------------------------------------------------------------------------------*/
    private Set<DefaultBlockList> getSurfaceBlocks(Set<BlockPos> firstPosList) {
        Set<BlockPos> posList = firstPosList;
        Set<DefaultBlockList> defaultBlockLists = new HashSet<>();

        if (posList == null) {
            return defaultBlockLists;
        }

        for (int i = 1; i < this.getBlockLayer().size(); ++i) {
            if (posList.isEmpty()) return defaultBlockLists;
            List<Set<BlockPos>> pos1 = this.placeSurfaceBlockLayer(new HashSet<>(posList), i);
            posList = pos1.get(1);
            firstPosList = pos1.get(0);
            List<BlockState> states = this.getBlockLayer().get(i - 1).getBlockStates();

            Set<BlockPos> firstPosListCopy = new HashSet<>(firstPosList);
            for (BlockPos pos : firstPosListCopy) {
                addPosToBlockList(pos, states, defaultBlockLists);
            }
        }
        List<BlockState> states = this.getBlockLayer().get(this.getBlockLayer().size() - 1).getBlockStates();

        // Create a copy for safe iteration
        Set<BlockPos> posListCopy = new HashSet<>(posList);
        for (BlockPos pos : posListCopy) {
            addPosToBlockList(pos, states, defaultBlockLists);
        }
        return defaultBlockLists;
    }

    public Set<DefaultBlockList> getInnerCylindricalBlocks(Set<BlockPos> posList) {
        Set<DefaultBlockList> defaultBlockLists = new HashSet<>();
        List<Integer> layerDistance = new ArrayList<>();
        layerDistance.add(this.getBlockLayer().get(0).getDepth());

        for (int i = 1; i < this.getBlockLayer().size(); i++) {
            layerDistance.add(this.getBlockLayer().get(i).getDepth() + layerDistance.get(i - 1));
        }

        int layerDistanceSize = layerDistance.size();
        for (BlockPos pos : posList) {
            boolean bl = false;
            float distance = WorldGenUtil.getDistance(new BlockPos(this.radialCenterPos.getX(), pos.getY(), this.radialCenterPos.getZ()), pos);
            for (int i = 0; i < layerDistanceSize - 1; i++) {
                if (distance < layerDistance.get(i)) {
                    addPosToBlockList(pos, this.getBlockLayer().get(i).getBlockStates(), defaultBlockLists);
                    bl = true;
                }
            }
            if (!bl) {
                addPosToBlockList(pos, this.getBlockLayer().get(layerDistanceSize - 1).getBlockStates(), defaultBlockLists);
            }
        }
        return defaultBlockLists;
    }

    public Set<DefaultBlockList> getOuterCylindricalBlocks(Set<BlockPos> posList) {
        Set<DefaultBlockList> defaultBlockLists = new HashSet<>();
        List<Integer> layerDistance = new ArrayList<>();

        //get the max distance of the list
        float maxDistance = 0;
        for (BlockPos pos : posList) {
            float distance = WorldGenUtil.getDistance(new BlockPos(this.radialCenterPos.getX(), pos.getY(), this.radialCenterPos.getZ()), pos);
            maxDistance = Math.max(distance, maxDistance);
        }

        layerDistance.add(this.getBlockLayer().get(0).getDepth());

        for (int i = 1; i < this.getBlockLayer().size(); i++) {
            layerDistance.add(this.getBlockLayer().get(i).getDepth() + layerDistance.get(i - 1));
        }

        int layerDistanceSize = layerDistance.size();
        for (BlockPos pos : posList) {
            boolean bl = false;
            float distance = WorldGenUtil.getDistance(new BlockPos(this.radialCenterPos.getX(), pos.getY(), this.radialCenterPos.getZ()), pos);
            for (int i = 0; i < layerDistanceSize - 1; i++) {
                if (distance > maxDistance - layerDistance.get(i)) {
                    addPosToBlockList(pos, this.getBlockLayer().get(i).getBlockStates(), defaultBlockLists);
                    bl = true;
                }
            }
            if (!bl) {
                addPosToBlockList(pos, this.getBlockLayer().get(layerDistanceSize - 1).getBlockStates(), defaultBlockLists);
            }
        }
        return defaultBlockLists;
    }

    //be careful when using layers with 1 block depth, that might do some weird things
    public Set<DefaultBlockList> getInnerRadialBlocks(Set<BlockPos> posList) {
        Set<DefaultBlockList> defaultBlockLists = new HashSet<>();
        List<Integer> layerDistance = new ArrayList<>();
        layerDistance.add(this.getBlockLayer().get(0).getDepth());

        for (int i = 1; i < this.getBlockLayer().size(); i++) {
            layerDistance.add(this.getBlockLayer().get(i).getDepth() + layerDistance.get(i - 1));
        }

        int layerDistanceSize = layerDistance.size();
        for (BlockPos pos : posList) {
            boolean bl = false;
            float distance = WorldGenUtil.getDistance(radialCenterPos, pos);
            for (int i = 0; i < layerDistanceSize - 1; i++) {
                if (distance < layerDistance.get(i)) {
                    addPosToBlockList(pos, this.getBlockLayer().get(i).getBlockStates(), defaultBlockLists);
                    bl = true;
                }
            }
            if (!bl) {
                addPosToBlockList(pos, this.getBlockLayer().get(layerDistanceSize - 1).getBlockStates(), defaultBlockLists);
            }
        }
        return defaultBlockLists;
    }

    public Set<DefaultBlockList> getOuterRadialBlocks(Set<BlockPos> posList) {
        Set<DefaultBlockList> defaultBlockLists = new HashSet<>();
        List<Integer> layerDistance = new ArrayList<>();
        layerDistance.add(this.getBlockLayer().get(0).getDepth());

        float maxDistance = 0;
        for (BlockPos pos : posList) {
            float distance = WorldGenUtil.getDistance(this.radialCenterPos, pos);
            maxDistance = Math.max(distance, maxDistance);
        }
        for (int i = 1; i < this.getBlockLayer().size(); i++) {
            layerDistance.add(this.getBlockLayer().get(i).getDepth() + layerDistance.get(i - 1));
        }

        int layerDistanceSize = layerDistance.size();
        for (BlockPos pos : posList) {
            boolean bl = false;
            float distance = WorldGenUtil.getDistance(radialCenterPos, pos);
            for (int i = 0; i < layerDistanceSize - 1; i++) {
                if (distance > maxDistance - layerDistance.get(i)) {
                    addPosToBlockList(pos, this.getBlockLayer().get(i).getBlockStates(), defaultBlockLists);
                    bl = true;
                }
            }
            if (!bl) {
                addPosToBlockList(pos, this.getBlockLayer().get(layerDistanceSize - 1).getBlockStates(), defaultBlockLists);
            }
        }
        return defaultBlockLists;
    }


    //TODO
    //known bug where the first layers is a little wider than what it is supposed to be
    private Set<DefaultBlockList> getDirectionalLayers(Set<BlockPos> firstPosList) {
        Vec3d direction = this.directionalLayerDirection.normalize();
        List<BlockPos> poslist = new ArrayList<>(firstPosList);
        Set<DefaultBlockList> defaultBlockLists = new HashSet<>();

        // Sort positions according to the directional vector
        poslist.sort(Comparator.comparingDouble(pos -> -pos.getX() * direction.x - pos.getY() * direction.y - pos.getZ() * direction.z));

        BlockPos firstPoint = poslist.get(0);

        int layerIndex = 0;
        int cumulativeDepth = this.getBlockLayer().get(layerIndex).getDepth();
        float initialDistance = 0;
        float maxDistance = 0;

        List<BlockState> currentStates = this.getBlockLayer().get(layerIndex).getBlockStates();
        int totalLayers = this.getBlockLayer().size() - 1;

        for (BlockPos pos : poslist) {
            float currentDistance = WorldGenUtil.getDistanceFromPointToPlane(direction, firstPoint.toCenterPos(), pos.toCenterPos());

            if (initialDistance == 0 && currentDistance > 2.0E-4) {
                initialDistance = currentDistance;
                maxDistance = cumulativeDepth * initialDistance + 0.00002f;
            }

            // Move to the next layer if needed
            while (layerIndex < totalLayers && currentDistance > maxDistance) {
                layerIndex++;
                cumulativeDepth += this.getBlockLayer().get(layerIndex).getDepth();
                maxDistance = cumulativeDepth * initialDistance + 0.00002f;
                currentStates = this.getBlockLayer().get(layerIndex).getBlockStates();
            }

            // Add the block position to the appropriate layer
            addPosToBlockList(pos, currentStates, defaultBlockLists);
        }

        return defaultBlockLists;
    }


    /**
     * change how the blocks are put
     */
    public enum LayersType {
        /**
         * for a natural aspect,
         * Put the first BlockStates on top of the structure for a coordinate x and z,
         * and until it reaches the depth of the layer
         */
        SURFACE,
        /**
         * place the blocks in a sphere shape, first layer being placed at the center
         */
        INNER_RADIAL,
        /**
         * place the blocks in a sphere shape, the last layer being placed at the center
         */
        OUTER_RADIAL,
        /**
         * place the blocks in a cylindrical shape, the first layer being placed at the center
         */
        INNER_CYLINDRICAL,
        /**
         * place the blocks in a cylindrical shape, the last layer being placed near the center
         */
        OUTER_CYLINDRICAL,
        /**
         * place the blocks on a plan
         * the plan is defined by the vector "directionalLayerDirection"
         */
        ALONG_DIRECTION
    }
}
