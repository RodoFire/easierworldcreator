package net.rodofire.easierworldcreator.shape.block.instanciator;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.EasierWorldCreator;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayer;
import net.rodofire.easierworldcreator.particledata.layer.ParticleLayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public abstract class AbstractBlockShapeBase {
    @NotNull
    private final StructureWorldAccess world;
    @NotNull
    private BlockPos pos;


    @NotNull
    private PlaceMoment placeMoment;


    private List<BlockLayer> blockLayers;
    private List<ParticleLayer> particleLayers;

    /**
     * init the ShapeBase
     *
     * @param world       the world of the shape
     * @param pos         the pos of the shape (usually the center of the structure)
     * @param placeMoment define the moment where the shape will be placed
     */
    public AbstractBlockShapeBase(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment) {
        this.world = world;
        this.pos = pos;
        this.placeMoment = placeMoment;
    }


    //boolean used to determine if we have to use the custom chunk building provided by the mod or not
    protected boolean biggerThanChunk = false;

    protected static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();


    /*----------- Pos Related ----------*/
    public @NotNull BlockPos getPos() {
        return pos;
    }

    public void setPos(@NotNull BlockPos pos) {
        this.pos = pos;
    }

    public void addPosOffset(BlockPos pos1) {
        this.pos.add(pos1);
    }

    public @NotNull StructureWorldAccess getWorld() {
        return world;
    }




    /*---------- Layers Related ----------*/

    public void addBlockLayers(List<BlockLayer> blockLayers) {
        this.blockLayers.addAll(blockLayers);
    }

    public void addBlockLayer(BlockLayer blockLayer) {
        this.blockLayers.add(blockLayer);
    }

    public void removeBlockLayer(List<BlockLayer> blockLayers) {
        this.blockLayers.removeAll(blockLayers);
    }

    public void removeBlockLayer(BlockLayer blockLayer) {
        this.blockLayers.remove(blockLayer);
    }

    public void removeBlockLayer(int index) {
        if (index >= this.blockLayers.size()) {
            EasierWorldCreator.LOGGER.error("int index >= blockLayer size");
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.blockLayers.size());
        }
        this.removeBlockLayer(this.blockLayers.get(index));
    }

    public List<BlockLayer> getBlockLayers() {
        return this.blockLayers;
    }

    public void setBlockLayers(List<BlockLayer> blockLayers) {
        this.blockLayers = blockLayers;
    }

    public void setBlockLayers(BlockLayer... layers) {
        this.blockLayers = List.of(layers);
    }

    public BlockLayer getBlockLayer(int index) {
        if (index >= this.blockLayers.size()) {
            EasierWorldCreator.LOGGER.error("int index >= blockLayer size");
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.blockLayers.size());
        }
        return this.blockLayers.get(index);
    }


    public @NotNull PlaceMoment getPlaceMoment() {
        return placeMoment;
    }

    /**
     * allow you to set and change the place moment
     *
     * @param placeMoment the changed parameter
     */
    public void setPlaceMoment(@NotNull PlaceMoment placeMoment) {
        this.placeMoment = placeMoment;
    }


    /**
     * Define the moment the shape will be placed.
     * It is really important that this does match what you want, or you will run into issues or even crash
     **/
    public enum PlaceMoment {
        /**
         * used during the world generation
         */
        WORLD_GEN,
        /**
         * used for any other moment than world gen and with animated blockPos
         */
        ANIMATED_OTHER,
        /**
         * used for any other moment than world gen
         */
        OTHER
    }

}
