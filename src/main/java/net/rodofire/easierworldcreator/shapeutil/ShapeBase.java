package net.rodofire.easierworldcreator.shapeutil;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Easierworldcreator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class ShapeBase {
    @NotNull
    private final StructureWorldAccess world;
    @NotNull
    private BlockPos pos;

    private boolean force = false;
    private List<Block> blocksToForce;

    @NotNull
    private PlaceMoment placeMoment;


    private List<BlockLayer> blockLayers;
    private List<ParticleLayer> particleLayers;

    /**
     * init the BaseShape
     * @param world           the world the spiral will spawn in
     * @param pos             the center of the spiral
     * @param placeMoment     define the moment where the shape will be placed
     * @param force           boolean to force the pos of the blocks
     * @param blocksToForce   a list of blocks that the blocks of the spiral can still force if force = false
     */
    public ShapeBase(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment, boolean force, List<Block> blocksToForce) {
        this.world = world;
        this.pos = pos;
        this.force = force;
        this.blocksToForce = new ArrayList<>(blocksToForce);
        this.placeMoment = placeMoment;
    }

    public ShapeBase(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment) {
        this.world = world;
        this.pos = pos;
        this.force = force;
    }


    //boolean used to determine if we have to use the custom chunk building provided by the mod or not
    protected boolean biggerThanChunk = false;

    protected static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();


    /*---------- Force Related ----------*/

    /**
     * used to get the boolean force, to know if it is possible to force the pos of a block
     *
     * @return the boolean force
     */
    public boolean getForce() {
        return force;
    }

    /**
     * used to set the boolean force used when placing block
     *
     * @param force the boolean that will be set
     */
    public void setForce(boolean force) {
        this.force = force;
    }

    public List<Block> getBlocksToForce() {
        return blocksToForce;
    }

    public void setBlocksToForce(List<Block> blocksToForce) {
        this.blocksToForce = new ArrayList<>();
        this.blocksToForce.addAll(blocksToForce);
    }

    public void addBlocksToForce(Block block) {
        this.blocksToForce.add(block);
    }

    public void addBlocksToForce(List<Block> blocks) {
        this.blocksToForce.addAll(blocks);
    }

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
            Easierworldcreator.LOGGER.error("int index >= blocklayer size");
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.blockLayers.size());
        }
        this.removeBlockLayer(index);
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
            Easierworldcreator.LOGGER.error("int index >= blocklayer size");
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.blockLayers.size());
        }
        return this.blockLayers.get(index);
    }


    public PlaceMoment getPlaceMoment() {
        return placeMoment;
    }

    /**
     * allow you to set and change the place moment
     *
     * @param placeMoment the changed parameter
     */
    public void setPlaceMoment(PlaceMoment placeMoment) {
        this.placeMoment = placeMoment;
    }


    /**
     * Define the moment the shape will be placed.
     * It is really important that this does match what you want or you will run into issues or even crash
     **/
    public enum PlaceMoment {
        /**
         * used during the world generation
         */
        WORLD_GEN,
        /**
         * used for any other moment than world gen
         */
        OTHER
    }

}
