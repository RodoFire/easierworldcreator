package net.rodofire.easierworldcreator.shape.block.gen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.DefaultBlockList;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayer;
import net.rodofire.easierworldcreator.shape.block.instanciator.AbstractBlockShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;


/**
 * In the case you have a custom {@link BlockPos} list, or a custom {@link DefaultBlockList}, you can initialize the shape with this class. However, don't use the {@code place()} method, use the {@code place(List<Set<BlockPos>>} or use {@code placeWBlockList(List<Set<BlockList>>)}to avoid potential problems.
 */
@SuppressWarnings("unused")
public class DefaultBlockShapeGen extends AbstractBlockShape {

    /**
     * init the Default Shape
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
     */
    public DefaultBlockShapeGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment, LayerPlace layerPlace, LayersType layersType, int yRotation, int zRotation, int secondYRotation, String featureName) {
        super(world, pos, placeMoment, layerPlace, layersType, yRotation, zRotation, secondYRotation, featureName);
    }

    /**
     * init the Default Shape
     *
     * @param world       the world the spiral will spawn in
     * @param pos         the center of the spiral
     * @param placeMoment define the moment where the shape will be placed
     */
    public DefaultBlockShapeGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment) {
        super(world, pos, placeMoment);
    }

    @Override
    public List<Set<BlockPos>> getBlockPos() {
        return List.of();
    }
}
