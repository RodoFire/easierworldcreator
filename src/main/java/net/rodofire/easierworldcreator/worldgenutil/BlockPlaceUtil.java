package net.rodofire.easierworldcreator.worldgenutil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;

import java.util.List;

/**
 * Useful class to verify and place the block in the world.
 */
public class BlockPlaceUtil {
    /**
     * method to verify that the block is not an unbreakable block or not and to verify if the block can be put or not.
     *
     * @param world         the world where the {@link Block} will be placed
     * @param force         force the pos
     * @param blocksToForce list of blocks that can still be forced
     * @param pos           the position of the block
     * @return
     */
    public static boolean verifyBlock(StructureWorldAccess world, boolean force, List<Block> blocksToForce, BlockPos pos) {
        BlockState state2 = world.getBlockState(pos);

        if (state2.getHardness(world, pos) < 0) return false;
        if (!force) {
            if (blocksToForce == null) blocksToForce = List.of(Blocks.BEDROCK);
            return state2.isAir() || blocksToForce.stream().anyMatch(state2.getBlock()::equals);
        }
        return true;
    }

    /**
     * verify if it can put the block before placing it
     *
     * @param world         the world where the {@link Block} will be placed
     * @param force         force the pos
     * @param blocksToForce list of blocks that can still be forced
     * @param blocktoplace  list of blocks to place
     * @param pos           the position of the block
     * @param i             the index of the stage we're at
     * @return boolean (true if the block was placed, else false)
     */
    public static boolean setBlockWithOrderWithVerification(StructureWorldAccess world, boolean force, List<Block> blocksToForce, List<BlockState> blocktoplace, BlockPos pos, int i) {
        if (verifyBlock(world, force, blocksToForce, pos)) {
            placeBlockWithOrder(world, blocktoplace, pos, i);
            return true;
        }
        return false;
    }

    /**
     * verify if it can put the block before placing it
     *
     * @param world         the world where the {@link Block} will be placed
     * @param force         force the pos
     * @param blocksToForce list of blocks that can still be forced
     * @param blocktoplace  list of blocks to place
     * @param pos           the position of the block
     * @return boolean (true if the block was placed, else false)
     */
    public static boolean setRandomBlockWithVerification(StructureWorldAccess world, boolean force, List<Block> blocksToForce, List<BlockState> blocktoplace, BlockPos pos) {
        if (verifyBlock(world, force, blocksToForce, pos)) {
            placeRandomBlock(world, blocktoplace, pos);
            return true;
        }
        return false;
    }

    /**
     * verify if it can put the block before placing it
     *
     * @param world         the world where the {@link Block} will be placed
     * @param force         force the pos
     * @param blocksToForce list of blocks that can still be forced
     * @param blockToPlace  list of blocks to place
     * @param pos           the position of the block
     * @param noise         the 2d noise that will determine if the block can be placed
     * @return boolean (true if the block was placed, else false)
     */
    public static boolean set2dNoiseBlockWithVerification(StructureWorldAccess world, boolean force, List<Block> blocksToForce, List<BlockState> blockToPlace, BlockPos pos, FastNoiseLite noise) {
        if (verifyBlock(world, force, blocksToForce, pos)) {
            placeBlockWith2DNoise(world, blockToPlace, pos, noise);
            return true;
        }
        return false;
    }

    /**
     * verify if it can put the block before placing it
     *
     * @param world         the world where the {@link Block} will be placed
     * @param force         force the pos
     * @param blocksToForce list of blocks that can still be forced
     * @param blockToPlace  list of blocks to place
     * @param pos           the position of the block
     * @param noise         the 2d noise that will determine if the block can be placed
     * @return boolean (true if the block was placed, else false)
     */
    public static boolean set3dNoiseBlockWithVerification(StructureWorldAccess world, boolean force, List<Block> blocksToForce, List<BlockState> blockToPlace, BlockPos pos, FastNoiseLite noise) {
        if (verifyBlock(world, force, blocksToForce, pos)) {
            placeBlockWith3DNoise(world, blockToPlace, pos, noise);
            return true;
        }
        return false;
    }

    /**
     * @param world        the world where the {@link Block} will be placed
     * @param blockToPlace list of blocks to place
     * @param pos          the position of the block
     */
    public static void placeRandomBlock(StructureWorldAccess world, List<BlockState> blockToPlace, BlockPos pos) {
        int length = blockToPlace.size() - 1;
        world.setBlockState(pos, blockToPlace.get(Random.create().nextBetween(0, length)), 2);
    }

    /**
     * Place the block corresponding to the index 'i'.
     * Generally, after that, the index 'i' will be incremented by one every time this method is called.
     * But you can change it to place 2 same blocks then incrementing
     */
    public static void placeBlockWithOrder(StructureWorldAccess world, List<BlockState> blocksToPlace, BlockPos pos, int i) {
        world.setBlockState(pos, blocksToPlace.get(i), 2);
    }

    /**
     * assign the 2d noise value to 'a'
     * then call the method to get the block depending on the noise
     *
     * @param world         the world of the block
     * @param blocksToPlace the blockstates list that would be chosen from
     * @param pos           the pos of the block to test
     * @param noise         the noise
     */
    public static void placeBlockWith2DNoise(StructureWorldAccess world, List<BlockState> blocksToPlace, BlockPos pos, FastNoiseLite noise) {
        float a = noise.GetNoise(pos.getX(), pos.getZ());
        placeBlockWithNoise(world, blocksToPlace, pos, a);
    }


    /**
     * assign the 3d noise value to 'a'
     * then call the method to get the block depending on the noise
     **/

    public static void placeBlockWith3DNoise(StructureWorldAccess world, List<BlockState> blocksToPlace, BlockPos pos, FastNoiseLite noise) {
        float a = noise.GetNoise(pos);
        placeBlockWithNoise(world, blocksToPlace, pos, a);
    }

    /**
     * method to place a block depending on the noise and the blocks inside the list
     * you get the float a that correspond to the value of the noise.
     * <p>the method will compare a with every index of the list and will place the block when e becomes smaller than the index
     * <p>does this: a > i ?
     * <p>- true: a > i+1 ? ...
     * <p>- false: place block at the index with the index i-1
     **/
    private static void placeBlockWithNoise(StructureWorldAccess world, List<BlockState> blocksToPlace, BlockPos pos, float a) {
        int length = blocksToPlace.size() - 1;
        BlockState state = blocksToPlace.get((int) ((length - 1) * (a / 2 + 0.5)));
        world.setBlockState(pos, state, 3);
    }

    /**
     * return the BlockState wanted based on randomness
     * this method doesn't place the block
     * It is notabely used during the shape gen during world gen
     *
     * @param blocksToPlace the blockstates list that would be chosen from
     * @return the block related to the noise
     */
    public static BlockState getRandomBlock(List<BlockState> blocksToPlace) {
        return blocksToPlace.get(Random.create().nextBetween(0, blocksToPlace.size() - 1));
    }

    /**
     * return the BlockState wanted based on order
     * this method doesn't place the block
     * It is notabely used during the shape gen during world gen
     *
     * @param blocksToPlace the blockstates list that would be chosen from
     * @param i the index to choose from
     * @return the block related to the noise
     */
    public static BlockState getBlockWithOrder(List<BlockState> blocksToPlace, int i) {
        return blocksToPlace.get(i);
    }

    /**
     *
     * return the BlockState wanted based on 2d noise
     * this method doesn't place the block
     * It is notabely used during the shape gen during world gen
     * @param blocksToPlace the blockstates list that would be chosen from
     * @param pos           the pos of the block to test
     * @param noise         the noise
     * @return the block related to the noise
     */
    public static BlockState getBlockWith2DNoise(List<BlockState> blocksToPlace, BlockPos pos, FastNoiseLite noise) {
        double a = noise.GetNoise(pos.getX(), pos.getZ());
        return getBlockStateWithNoise(blocksToPlace, a);
    }

    /**
     * return the BlockState wanted based on 3d noise
     * this method doesn't place the block
     * It is notabely used during the shape gen during world gen
     * @param blocksToPlace the blockstates list that would be chosen from
     * @param pos           the pos of the block to test
     * @param noise         the noise
     * @return the block related to the noise
     */
    public static BlockState getBlockWith3DNoise(List<BlockState> blocksToPlace, BlockPos pos, FastNoiseLite noise) {
        double a = noise.GetNoise(pos);
        return getBlockStateWithNoise(blocksToPlace, a);
    }

    /**
     * simplify the choose of block
     *
     * @param blocksToPlace the list of blockStates to place
     * @param a             the value of the noise
     * @return the block related to the noise
     */
    private static BlockState getBlockStateWithNoise(List<BlockState> blocksToPlace, double a) {
        int length = blocksToPlace.size() - 1;
        return blocksToPlace.get((int) ((length - 1) * (a / 2 + 0.5)));
    }
}