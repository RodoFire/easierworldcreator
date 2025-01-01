package net.rodofire.easierworldcreator.shape.block.instanciator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayer;
import net.rodofire.easierworldcreator.placer.blocks.util.BlockPlaceUtil;
import net.rodofire.easierworldcreator.util.FastNoiseLite;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public abstract class AbstractBlockShapePlaceType extends AbstractBlockShapeBase {
    private LayerPlace layerPlace = LayerPlace.RANDOM;
    private FastNoiseLite noise = new FastNoiseLite();

    //int for the number of placed Blocks, used if layerPlace == LayerPlace.ORDER
    private int placedBlocks = 0;

    /**
     * init the ShapePlaceType
     *
     * @param world       the world the spiral will spawn in
     * @param pos         the center of the spiral
     * @param placeMoment define the moment where the shape will be placed
     * @param layerPlace  how the {@code @BlockStates} inside of a {@link BlockLayer} will be placed
     */
    public AbstractBlockShapePlaceType(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment, LayerPlace layerPlace) {
        super(world, pos, placeMoment);
        this.layerPlace = layerPlace;
    }

    /**
     * init the ShapePlaceType
     *
     * @param world       the world of the shape
     * @param pos         the pos of the shape (usually the center of the structure)
     * @param placeMoment define the moment where the shape will be placed
     */
    public AbstractBlockShapePlaceType(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment) {
        super(world, pos, placeMoment);
    }


    /*---------- LayerPlace Related ----------*/
    public void setLayerPlace(LayerPlace layerPlace) {
        this.layerPlace = layerPlace;
    }

    public LayerPlace getLayerPlace() {
        return layerPlace;
    }

    /*---------- Noise Related ----------*/
    public void setNoise(FastNoiseLite noise) {
        this.noise = noise;
    }

    public FastNoiseLite getNoise() {
        return this.noise;
    }


    /**
     * place blocks without verification
     *
     * @param index the index of the the {@link  BlockLayer}
     * @param pos   the pos of the block
     */
    public void placeBlocks(int index, BlockPos pos) {
        switch (this.layerPlace) {
            case RANDOM:
                BlockPlaceUtil.placeRandomBlock(this.getWorld(), this.getBlockLayer().get(index).getBlockStates(), pos);
                break;
            case NOISE2D:
                BlockPlaceUtil.placeBlockWith2DNoise(this.getWorld(), this.getBlockLayer().get(index).getBlockStates(), pos, this.noise);
                break;
            case NOISE3D:
                BlockPlaceUtil.placeBlockWith3DNoise(this.getWorld(), this.getBlockLayer().get(index).getBlockStates(), pos, this.noise);
                break;
            default:
                BlockPlaceUtil.placeBlockWithOrder(this.getWorld(), this.getBlockLayer().get(index).getBlockStates(), pos, this.placedBlocks);
                this.placedBlocks = this.placedBlocks % (this.getBlockLayer().size() - 1);
                break;
        }
    }

    /**
     * Place blocks without verification. Used for precomputed {@code List<BlockStates>} instead of searching it on the BlockLayer
     *
     * @param states states the states that will be chosen
     * @param pos    the pos of the block
     */
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
                this.placedBlocks = this.placedBlocks % (this.getBlockLayer().size() - 1);
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
        BlockLayer blockLayer = this.getBlockLayer().get(index);
        switch (this.layerPlace) {
            case RANDOM:
                return BlockPlaceUtil.setRandomBlockWithVerification(this.getWorld(), blockLayer.isForce(), blockLayer.getBlocksToForce(), blockLayer.getBlockStates(), pos);
            case NOISE2D:
                return BlockPlaceUtil.set2dNoiseBlockWithVerification(this.getWorld(), blockLayer.isForce(), blockLayer.getBlocksToForce(), this.getBlockLayer().get(index).getBlockStates(), pos, this.noise);
            case NOISE3D:
                return BlockPlaceUtil.set3dNoiseBlockWithVerification(this.getWorld(), blockLayer.isForce(), blockLayer.getBlocksToForce(), this.getBlockLayer().get(index).getBlockStates(), pos, this.noise);
            default:
                boolean bl = BlockPlaceUtil.setBlockWithOrderWithVerification(this.getWorld(), blockLayer.isForce(), blockLayer.getBlocksToForce(), this.getBlockLayer().get(index).getBlockStates(), pos, this.placedBlocks);
                this.placedBlocks = (this.placedBlocks + 1) % (this.getBlockLayer().size() - 1);
                return bl;
        }
    }

    /**
     * place a block in the this.getWorld() at the pos if it is able to.
     * precomputed list for little performance improvement
     *
     * @param states        the states that will be chosen
     * @param pos           the pos of the block
     * @param force         determine if the block can be posed on top of any block
     * @param blocksToForce set of blocks that determine the blocks that can be still forced
     * @return boolean if the block was placed
     */
    public boolean placeBlocksWithVerification(List<BlockState> states, BlockPos pos, boolean force, Set<Block> blocksToForce) {
        switch (this.layerPlace) {
            case RANDOM:
                return BlockPlaceUtil.setRandomBlockWithVerification(this.getWorld(), force, blocksToForce, states, pos);
            case NOISE2D:
                return BlockPlaceUtil.set2dNoiseBlockWithVerification(this.getWorld(), force, blocksToForce, states, pos, this.noise);
            case NOISE3D:
                return BlockPlaceUtil.set3dNoiseBlockWithVerification(this.getWorld(), force, blocksToForce, states, pos, this.noise);
            default:
                boolean bl = BlockPlaceUtil.setBlockWithOrderWithVerification(this.getWorld(), force, blocksToForce, states, pos, this.placedBlocks);
                this.placedBlocks = (this.placedBlocks + 1) % (this.getBlockLayer().size() - 1);
                return bl;
        }

    }

    /**
     * place a block in the this.getWorld() at the pos if it is able to.
     * precomputed list for little performance improvement
     *
     * @param states the states that will be chosen
     * @param pos    the pos of the block
     * @return boolean if the block was placed
     */
    public boolean placeBlocksWithVerification(List<BlockState> states, BlockPos pos) {
        return this.placeBlocksWithVerification(states, pos, false, Set.of());
    }

    /**
     * Used to get the blocksState notably used during this.getWorld() gen, this doesn't place anything
     *
     * @param index the index of the the {@link  BlockLayer}
     * @param pos   the pos of the block
     * @return the BlockState related to the pos
     */
    public BlockState getBlockToPlace(int index, BlockPos pos) {
        switch (this.layerPlace) {
            case RANDOM:
                return BlockPlaceUtil.getRandomBlock(this.getBlockLayer().get(index).getBlockStates());
            case NOISE2D:
                return BlockPlaceUtil.getBlockWith2DNoise(this.getBlockLayer().get(index).getBlockStates(), pos, this.noise);
            case NOISE3D:
                return BlockPlaceUtil.getBlockWith3DNoise(this.getBlockLayer().get(index).getBlockStates(), pos, this.noise);
            default:
                BlockState blockState = BlockPlaceUtil.getBlockWithOrder(this.getBlockLayer().get(index).getBlockStates(), this.placedBlocks);
                this.placedBlocks = (this.placedBlocks + 1) % (this.getBlockLayer().size() - 1);
                return blockState;
        }
    }

    /**
     * Used to get the blocksState notably used during this.getWorld() gen, this doesn't place anything.
     * Used for a precomputed BlockState list
     *
     * @param states the states that will be chosen
     * @param pos    the pos of the block
     * @return the BlockState related to the pos
     **/
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
                this.placedBlocks = (this.placedBlocks + 1) % (this.getBlockLayer().size() - 1);
                return blockState;
        }
    }

    /**
     * method to know if the block can be placed
     *
     * @param pos the blockPos that will be tested
     * @return true if it can be placed, false if not
     */
    public boolean verifyBlocks(BlockPos pos) {
        return BlockPlaceUtil.verifyBlock(this.getWorld(), false, Set.of(), pos);
    }

    /**
     * method to know if the block can be placed
     *
     * @param pos           the blockPos that will be tested
     * @param blocksToForce the blocks that can be forced
     * @param force         if true, all blocks will be forced, except blocks like bedrock or end portals
     * @return true if it can be placed, false if not
     */
    public boolean verifyBlocks(BlockPos pos, Set<Block> blocksToForce, boolean force) {
        return BlockPlaceUtil.verifyBlock(this.getWorld(), force, blocksToForce, pos);
    }

    public boolean verifyBlocks(BlockPos pos, Set<Block> blocksToForce, boolean force, Map<BlockPos, BlockState> blockStateMap) {
        return BlockPlaceUtil.verifyBlock(this.getWorld(), force, blocksToForce, pos, blockStateMap);
    }

    /**
     * set how the blocks/particles will be chosen inside a layer
     */
    public enum LayerPlace {
        /**
         * will choose random Block/Particle in the layer
         */
        RANDOM,
        /**
         * will place the first Block/particle in the layer, then the second, then the third, in the order
         */
        ORDER,
        /**
         * will place the Block/Particle according to a 2d noise
         */
        NOISE2D,
        /**
         * will place the Block/Particle according to a 3d noise
         */
        NOISE3D
    }
}
