package net.rodofire.easierworldcreator.shapegen;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.shapeutil.BlockLayer;
import net.rodofire.easierworldcreator.shapeutil.Shape;
import net.rodofire.easierworldcreator.shapeutil.BlockList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;


/**
 * In the case you have a custom {@link BlockPos} list, or a custom {@link BlockList}, you can init the shape with this class. However don't use the {@code place()} method, use the {@code place(List<Set<BlockPos>>} or use {@code placeWBlockList(List<Set<BlockList>>)}to avoid potential problems.
 */
public class DefaultShapeGen extends Shape {

    /**
     * init the Default Shape
     *
     * @param world           the world the spiral will spawn in
     * @param pos             the center of the spiral
     * @param placeMoment     define the moment where the shape will be placed
     * @param force           boolean to force the pos of the blocks
     * @param blocksToForce   a list of blocks that the blocks of the spiral can still force if force = false
     * @param layerPlace      how the {@code @BlockStates} inside of a {@link BlockLayer} will be placed
     * @param layersType      how the Layers will be placed
     * @param xrotation       first rotation around the x-axis
     * @param yrotation       second rotation around the y-axis
     * @param secondxrotation last rotation around the x-axis
     * @param featureName     the name of the feature
     */
    public DefaultShapeGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment, boolean force, List<Block> blocksToForce, LayerPlace layerPlace, LayersType layersType, int xrotation, int yrotation, int secondxrotation, String featureName) {
        super(world, pos, placeMoment, force, blocksToForce, layerPlace, layersType, xrotation, yrotation, secondxrotation, featureName);
    }

    /**
     * init the Default Shape
     *
     * @param world       the world the spiral will spawn in
     * @param pos         the center of the spiral
     * @param placeMoment define the moment where the shape will be placed
     */
    public DefaultShapeGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment) {
        super(world, pos, placeMoment);
    }

    @Override
    public List<Set<BlockPos>> getBlockPos() {
        return List.of();
    }

    @Override
    public List<Vec3d> getVec3d() {
        return List.of();
    }
}
