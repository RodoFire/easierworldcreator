package net.rodofire.easierworldcreator.shapeutil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.worldgenutil.BlockPlaceUtil;
import net.rodofire.easierworldcreator.worldgenutil.FastNoiseLite;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class ShapePlaceType extends ShapeBase {
    private PlaceType placeType = PlaceType.BLOCKS;
    private LayerPlace layerPlace = LayerPlace.RANDOM;
    private FastNoiseLite noise = new FastNoiseLite();

    //int for the number of placed Blocks, used if layerPlace == LayerPlace.ORDER
    private int placedBlocks = 0;

    /**
     * init the ShapePlaceType
     * @param world           the world the spiral will spawn in
     * @param pos             the center of the spiral
     * @param placeMoment     define the moment where the shape will be placed
     * @param force           boolean to force the pos of the blocks
     * @param blocksToForce   a list of blocks that the blocks of the spiral can still force if force = false
     * @param layerPlace      how the {@code @BlockStates} inside of a {@link BlockLayer} will be placed
     */
    public ShapePlaceType(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment, boolean force, List<Block> blocksToForce, LayerPlace layerPlace) {
        super(world, pos, placeMoment, force, blocksToForce);
        this.layerPlace = layerPlace;
    }

    public ShapePlaceType(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment) {
        super(world, pos, placeMoment);
    }


    /*---------- LayerPlace Related ----------*/
    public void setLayerPlace(LayerPlace layerPlace) {
        this.layerPlace = layerPlace;
    }

    public LayerPlace getLayerPlace() {
        return layerPlace;
    }

    /*---------- Place Related ----------*/
    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
    }

    public PlaceType getPlaceType() {
        return placeType;
    }

    /*---------- Noise Related ----------*/
    public void setNoise(FastNoiseLite noise) {
        this.noise = noise;
    }

    public FastNoiseLite getNoise() {
        return this.noise;
    }



    //place blocks without verification
    public void placeBlocks(int index, BlockPos pos) {
        switch (this.layerPlace) {
            case RANDOM:
                BlockPlaceUtil.placeRandomBlock(this.getWorld(), this.getBlockLayers().get(index).getBlockStates(), pos);
                break;
            case NOISE2D:
                BlockPlaceUtil.placeBlockWith2DNoise(this.getWorld(), this.getBlockLayers().get(index).getBlockStates(), pos, this.noise);
                break;
            case NOISE3D:
                BlockPlaceUtil.placeBlockWith3DNoise(this.getWorld(), this.getBlockLayers().get(index).getBlockStates(), pos, this.noise);
                break;
            default:
                BlockPlaceUtil.placeBlockWithOrder(this.getWorld(), this.getBlockLayers().get(index).getBlockStates(), pos, this.placedBlocks);
                this.placedBlocks = this.placedBlocks % (this.getBlockLayers().size() - 1);
                break;
        }
    }

    //Place blocks without verification. Used for precomputed List<BlockStates> instead of searching it on the BlockLayer
    public void placeBlocks(List<BlockState> states, BlockPos pos) {
        switch (this.layerPlace) {
            case RANDOM:
                BlockPlaceUtil.placeRandomBlock(this.getWorld(), states, pos);
                break;
            case NOISE2D:
                BlockPlaceUtil.placeBlockWith2DNoise(this.getWorld(), states, pos, this.noise);
                break;
            case NOISE3D:
                BlockPlaceUtil.placeBlockWith3DNoise(this.getWorld(), states, pos, this.noise);
                break;
            default:
                BlockPlaceUtil.placeBlockWithOrder(this.getWorld(), states, pos, this.placedBlocks);
                this.placedBlocks = this.placedBlocks % (this.getBlockLayers().size() - 1);
                break;
        }
    }

    /**
     * place a block in the this.getWorld() at the pos if it is able to
     *
     * @param index the index of the the {@link  BlockLayer}
     * @param pos   the pos of the block
     * @return boolean if the block was placed
     */
    public boolean placeBlocksWithVerification(int index, BlockPos pos) {
        switch (this.layerPlace) {
            case RANDOM:
                return BlockPlaceUtil.setRandomBlockWithVerification(this.getWorld(), this.getForce(), this.getBlocksToForce(), this.getBlockLayers().get(index).getBlockStates(), pos);
            case NOISE2D:
                return BlockPlaceUtil.set2dNoiseBlockWithVerification(this.getWorld(), this.getForce(), this.getBlocksToForce(), this.getBlockLayers().get(index).getBlockStates(), pos, this.noise);
            case NOISE3D:
                return BlockPlaceUtil.set3dNoiseBlockWithVerification(this.getWorld(), this.getForce(), this.getBlocksToForce(), this.getBlockLayers().get(index).getBlockStates(), pos, this.noise);
            default:
                boolean bl = BlockPlaceUtil.setBlockWithOrderWithVerification(this.getWorld(), this.getForce(), this.getBlocksToForce(), this.getBlockLayers().get(index).getBlockStates(), pos, this.placedBlocks);
                this.placedBlocks = (this.placedBlocks + 1) % (this.getBlockLayers().size() - 1);
                return bl;
        }
    }

    /**
     * place a block in the this.getWorld() at the pos if it is able to
     * precomputed list for little performance improvement
     *
     * @param states the states that will be choosed
     * @param pos    the pos of the block
     * @return boolean if the block was placed
     */
    public boolean placeBlocksWithVerification(List<BlockState> states, BlockPos pos) {
        switch (this.layerPlace) {
            case RANDOM:
                return BlockPlaceUtil.setRandomBlockWithVerification(this.getWorld(), this.getForce(), this.getBlocksToForce(), states, pos);
            case NOISE2D:
                return BlockPlaceUtil.set2dNoiseBlockWithVerification(this.getWorld(), this.getForce(), this.getBlocksToForce(), states, pos, this.noise);
            case NOISE3D:
                return BlockPlaceUtil.set3dNoiseBlockWithVerification(this.getWorld(), this.getForce(), this.getBlocksToForce(), states, pos, this.noise);
            default:
                boolean bl = BlockPlaceUtil.setBlockWithOrderWithVerification(this.getWorld(), this.getForce(), this.getBlocksToForce(), states, pos, this.placedBlocks);
                this.placedBlocks = (this.placedBlocks + 1) % (this.getBlockLayers().size() - 1);
                return bl;
        }

    }

    //Used to get the blocksState notably used during this.getWorld() gen, this doesn't place anything
    public BlockState getBlockToPlace(int index, BlockPos pos) {
        switch (this.layerPlace) {
            case RANDOM:
                return BlockPlaceUtil.getRandomBlock(this.getBlockLayers().get(index).getBlockStates());
            case NOISE2D:
                return BlockPlaceUtil.getBlockWith2DNoise(this.getBlockLayers().get(index).getBlockStates(), pos, this.noise);
            case NOISE3D:
                return BlockPlaceUtil.getBlockWith3DNoise(this.getBlockLayers().get(index).getBlockStates(), pos, this.noise);
            default:
                BlockState blockState = BlockPlaceUtil.getBlockWithOrder(this.getBlockLayers().get(index).getBlockStates(), this.placedBlocks);
                this.placedBlocks = (this.placedBlocks + 1) % (this.getBlockLayers().size() - 1);
                return blockState;
        }
    }

    //Used to get the blocksState notably used during this.getWorld() gen, this doesn't place anything
    //Used for precomputed BlockState list
    public BlockState getBlockToPlace(List<BlockState> states, BlockPos pos) {
        switch (this.layerPlace) {
            case RANDOM:
                return BlockPlaceUtil.getRandomBlock(states);
            case NOISE2D:
                return BlockPlaceUtil.getBlockWith2DNoise(states, pos, this.noise);
            case NOISE3D:
                return BlockPlaceUtil.getBlockWith3DNoise(states, pos, this.noise);
            default:
                BlockState blockState = BlockPlaceUtil.getBlockWithOrder(states, this.placedBlocks);
                this.placedBlocks = (this.placedBlocks + 1) % (this.getBlockLayers().size() - 1);
                return blockState;
        }
    }

    public boolean verifyBlocks(BlockPos pos) {
        return BlockPlaceUtil.verifyBlock(this.getWorld(), this.getForce(), getBlocksToForce(), pos);
    }

    /**
     * set the type of objects that will be placed
     */
    public enum PlaceType {
        //place blocks
        BLOCKS,
        //place particles
        //particles are not implemented for the moment
        PARTICLE
    }

    /**
     * set how the blocks/particles will be chosen inside a layer
     */
    public enum LayerPlace {
        //will choose random Block/Particle in the layer
        RANDOM,
        //will place the first Block/particle in the layer, then the second, then the third, in the order
        ORDER,
        //will place the Block/Particle according to a 2d noise
        NOISE2D,
        //will place the Block/Particle according to a 3d noise
        NOISE3D
    }
}
