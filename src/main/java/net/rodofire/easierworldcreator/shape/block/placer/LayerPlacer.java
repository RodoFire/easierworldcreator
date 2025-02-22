package net.rodofire.easierworldcreator.shape.block.placer;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.StructurePlacementRuleManager;
import net.rodofire.easierworldcreator.util.BlockPlaceUtil;
import net.rodofire.easierworldcreator.util.FastNoiseLite;

import java.util.List;

public class LayerPlacer {
    private LayerPlace type = LayerPlace.RANDOM;
    FastNoiseLite noise;
    int placedBlocks = 0;

    public LayerPlacer(int placedBlocks, LayerPlace type) {
        this.placedBlocks = placedBlocks;
        this.type = type;
    }

    public LayerPlacer(LayerPlace type, FastNoiseLite noise) {
        this.type = type;
        this.noise = noise;
    }

    public LayerPlacer(LayerPlace type) {
        this.type = type;
    }

    public boolean place(StructureWorldAccess worldAccess, List<BlockState> states, BlockPos pos) {
        return place(worldAccess, states, pos, null);
    }

    public boolean place(StructureWorldAccess worldAccess, BlockState[] states, BlockPos pos) {
        return place(worldAccess, states, pos, null);
    }

    /**
     * Used to get the blocksState notably used during this.getWorld() gen, this doesn't place anything.
     * Used for a precomputed BlockState list
     *
     * @param states the states that will be chosen
     * @param pos    the pos of the block
     * @return the BlockState related to the pos
     **/

    public boolean place(StructureWorldAccess worldAccess, List<BlockState> states, BlockPos pos, StructurePlacementRuleManager ruler) {
        BlockState state;
        switch (this.type) {
            case NOISE2D -> state = BlockPlaceUtil.getBlockWith2DNoise(states, pos, this.noise);
            case NOISE3D -> state = BlockPlaceUtil.getBlockWith3DNoise(states, pos, this.noise);
            case ORDER -> {
                state = BlockPlaceUtil.getBlockWithOrder(states, this.placedBlocks);
                this.placedBlocks = (this.placedBlocks + 1) % (states.size() - 1);
            }
            default -> state = BlockPlaceUtil.getRandomBlock(states);
        }
        if (ruler == null) {
            return worldAccess.getBlockState(pos).isAir() && worldAccess.setBlockState(pos, state, 3);
        }
        return ruler.canPlace(state) && worldAccess.setBlockState(pos, state, 3);
    }

    public boolean place(StructureWorldAccess worldAccess, BlockState[] states, BlockPos pos, StructurePlacementRuleManager ruler) {
        BlockState state;
        switch (this.type) {
            case NOISE2D -> state = BlockPlaceUtil.getBlockWith2DNoise(states, pos, this.noise);
            case NOISE3D -> state = BlockPlaceUtil.getBlockWith3DNoise(states, pos, this.noise);
            case ORDER -> {
                state = BlockPlaceUtil.getBlockWithOrder(states, this.placedBlocks);
                this.placedBlocks = (this.placedBlocks + 1) % (states.length - 1);
            }
            default -> state = BlockPlaceUtil.getRandomBlock(states);
        }
        if (ruler == null) {
            return worldAccess.getBlockState(pos).isAir() && worldAccess.setBlockState(pos, state, 3);
        }
        return ruler.canPlace(state) && worldAccess.setBlockState(pos, state, 3);
    }

    /**
     * Used to get the blocksState notably used during this.getWorld() gen, this doesn't place anything.
     * Used for a precomputed BlockState list
     *
     * @param states the states that will be chosen
     * @param pos    the pos of the block
     * @return the BlockState related to the pos
     **/
    public BlockState get(List<BlockState> states, BlockPos pos) {
        return switch (this.type) {
            case RANDOM -> BlockPlaceUtil.getRandomBlock(states);
            case NOISE2D -> BlockPlaceUtil.getBlockWith2DNoise(states, pos, this.noise);
            case NOISE3D -> BlockPlaceUtil.getBlockWith3DNoise(states, pos, this.noise);
            case ORDER -> {
                BlockState blockState = BlockPlaceUtil.getBlockWithOrder(states, this.placedBlocks);
                this.placedBlocks = (this.placedBlocks + 1) % (states.size() - 1);
                yield blockState;
            }
        };
    }

    public BlockState get(BlockState[] states, BlockPos pos) {
        return switch (this.type) {
            case RANDOM -> BlockPlaceUtil.getRandomBlock(states);
            case NOISE2D -> BlockPlaceUtil.getBlockWith2DNoise(states, pos, this.noise);
            case NOISE3D -> BlockPlaceUtil.getBlockWith3DNoise(states, pos, this.noise);
            case ORDER -> {
                BlockState blockState = BlockPlaceUtil.getBlockWithOrder(states, this.placedBlocks);
                this.placedBlocks = (this.placedBlocks + 1) % (states.length - 1);
                yield blockState;
            }
        };
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
