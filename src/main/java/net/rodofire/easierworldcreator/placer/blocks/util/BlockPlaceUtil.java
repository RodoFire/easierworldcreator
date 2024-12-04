package net.rodofire.easierworldcreator.placer.blocks.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.util.FastNoiseLite;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Useful class to verify and place the block in the world.
 */
@SuppressWarnings("unused")
public class BlockPlaceUtil {
    /**
     * method to verify that the block is not an unbreakable block or not and to verify if the block can be put or not.
     *
     * @param world         the world where the {@link Block} will be placed
     * @param pos           the position of the block
     * @return true if it can be posed, false if not
     */
    public static boolean verifyBlock(StructureWorldAccess world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isAir();
    }

    /**
     * method to verify that the block is not an unbreakable block or not and to verify if the block can be put or not.
     *
     * @param world         the world where the {@link Block} will be placed
     * @param force         force the pos
     * @param blocksToForce list of blocks that can still be forced
     * @param pos           the position of the block
     * @return true if it can be posed, false if not
     */
    public static boolean verifyBlock(StructureWorldAccess world, boolean force, Set<Block> blocksToForce, BlockPos pos) {
        BlockState state2 = world.getBlockState(pos);
        return verify(world, force, blocksToForce, pos, state2);
    }

    /**
     * Verifies if a block can be placed at a given position in the world.
     *
     * @param world         The world in which to verify the block.
     * @param force         If the placement should be forced.
     * @param blocksToForce The blocks to force place.
     * @param pos           The position of the block.
     * @param blockStateMap The map of block states.
     * @return true if the block can be placed, false otherwise.
     */
    public static boolean verifyBlock(StructureWorldAccess world, boolean force, Set<Block> blocksToForce, BlockPos pos, Map<BlockPos, BlockState> blockStateMap) {
        BlockState state = blockStateMap.get(pos);
        return verify(world, force, blocksToForce, pos, state);
    }

    /**
     * Verifies if a block can be placed at a given position in the world based on its state.
     *
     * @param world         The world in which to verify the block.
     * @param force         If the placement should be forced.
     * @param blocksToForce The blocks to force place.
     * @param pos           The position of the block.
     * @param state         The state of the block.
     * @return true if the block can be placed, false otherwise.
     */
    private static boolean verify(StructureWorldAccess world, boolean force, Set<Block> blocksToForce, BlockPos pos, BlockState state) {
        if (state.getHardness(world, pos) < 0) return false;
        if (!force) {
            if (blocksToForce == null) blocksToForce = Set.of(Blocks.BEDROCK);
            return state.isAir() || blocksToForce.stream().anyMatch(state.getBlock()::equals);
        }
        return true;
    }

    /**
     * Places a block at a given position in the world.
     *
     * @param world The world in which to place the block.
     * @param pos   The position of the block.
     * @param state The state of the block.
     */
    public static void placeBlock(StructureWorldAccess world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state, 3);
    }

    /**
     * Verifies and places a block at a given position in the world if verification passes.
     *
     * @param world         The world in which to place the block.
     * @param force         If the placement should be forced.
     * @param blocksToForce The blocks to force place.
     * @param pos           The position of the block.
     * @param state         The state of the block.
     * @return true if the block was placed, false otherwise.
     */
    public static boolean placeVerifiedBlock(StructureWorldAccess world, boolean force, Set<Block> blocksToForce, BlockPos pos, BlockState state) {
        if (verifyBlock(world, force, blocksToForce, pos)) {
            placeBlock(world, pos, state);
            return true;
        }
        return false;
    }

    /**
     * Places a block at a given position in the world with NBT data.
     *
     * @param world    The world in which to place the block.
     * @param pos      The position of the block.
     * @param state    The state of the block.
     * @param compound The NBT data to apply.
     */
    public static void placeBlockWithNbt(StructureWorldAccess world, BlockPos pos, BlockState state, NbtCompound compound) {
        placeBlock(world, pos, state);
        if (compound != null) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity != null) {
                DynamicRegistryManager registry =  world.getRegistryManager();
                NbtCompound currentNbt = entity.createNbtWithIdentifyingData(registry);
                currentNbt.copyFrom(compound);
                entity.read(currentNbt, registry);
                entity.markDirty();
            }
        }
    }

    /**
     * Verifies and places a block with NBT data at a given position in the world if verification passes.
     *
     * @param world         The world in which to place the block.
     * @param force         If the placement should be forced.
     * @param blocksToForce The blocks to force place.
     * @param pos           The position of the block.
     * @param state         The state of the block.
     * @param compound      The NBT data to apply.
     * @return true if the block was placed, false otherwise.
     */
    public static boolean placeVerifiedBlockWithNbt(StructureWorldAccess world, boolean force, Set<Block> blocksToForce, BlockPos pos, BlockState state, NbtCompound compound) {
        if (verifyBlock(world, force, blocksToForce, pos)) {
            placeBlockWithNbt(world, pos, state, compound);
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
     * @param i             the index of the stage we're at
     * @return boolean (true if the block was placed, else false)
     */
    public static boolean setBlockWithOrderWithVerification(StructureWorldAccess world, boolean force, Set<
            Block> blocksToForce, List<BlockState> blockToPlace, BlockPos pos, int i) {
        if (verifyBlock(world, force, blocksToForce, pos)) {
            placeBlockWithOrder(world, blockToPlace, pos, i);
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
     * @return boolean (true if the block was placed, else false)
     */
    public static boolean setRandomBlockWithVerification(StructureWorldAccess world, boolean force, Set<
            Block> blocksToForce, List<BlockState> blockToPlace, BlockPos pos) {
        if (verifyBlock(world, force, blocksToForce, pos)) {
            placeRandomBlock(world, blockToPlace, pos);
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
    public static boolean set2dNoiseBlockWithVerification(StructureWorldAccess world, boolean force, Set<
            Block> blocksToForce, List<BlockState> blockToPlace, BlockPos pos, FastNoiseLite noise) {
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
    public static boolean set3dNoiseBlockWithVerification(StructureWorldAccess world, boolean force, Set<
            Block> blocksToForce, List<BlockState> blockToPlace, BlockPos pos, FastNoiseLite noise) {
        if (verifyBlock(world, force, blocksToForce, pos)) {
            placeBlockWith3DNoise(world, blockToPlace, pos, noise);
            return true;
        }
        return false;
    }

    /**
     * method to place random blocks based on a given list
     *
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
     * But you can change it to place two same blocks then incrementing
     */
    public static void placeBlockWithOrder(StructureWorldAccess world, List<BlockState> blocksToPlace, BlockPos
            pos, int i) {
        world.setBlockState(pos, blocksToPlace.get(i), 2);
    }

    /**
     * assign the 2d noise value to 'a'
     * then call the method to get the block depending on the noise
     *
     * @param world         the world of the block
     * @param blocksToPlace the blockStates list that would be chosen from
     * @param pos           the pos of the block to test
     * @param noise         the noise
     */
    public static void placeBlockWith2DNoise(StructureWorldAccess
                                                     world, List<BlockState> blocksToPlace, BlockPos pos, FastNoiseLite noise) {
        float a = noise.GetNoise(pos.getX(), pos.getZ());
        placeBlockWithNoise(world, blocksToPlace, pos, a);
    }


    /**
     * assign the 3d noise value to 'a'
     * then call the method to get the block depending on the noise
     **/

    public static void placeBlockWith3DNoise(StructureWorldAccess
                                                     world, List<BlockState> blocksToPlace, BlockPos pos, FastNoiseLite noise) {
        float a = noise.GetNoise(pos);
        placeBlockWithNoise(world, blocksToPlace, pos, a);
    }

    /**
     * Method to place a block depending on the noise and the blocks inside the list.
     * You get the float that corresponds to the value of the noise.
     * <p>the method will compare a with every index of the list and will place the block when e becomes smaller than the index
     * <p>does this: a > i?
     * <p>- true: a > i+1 ? ...
     * <p>- false: place block at the index with the index i-1
     **/
    private static void placeBlockWithNoise(StructureWorldAccess world, List<BlockState> blocksToPlace, BlockPos
            pos, float a) {
        int length = blocksToPlace.size() - 1;
        BlockState state = blocksToPlace.get((int) ((length) * (a / 2 + 0.5)));
        world.setBlockState(pos, state, 3);
    }

    /**
     * return the BlockState wanted based on randomness
     * this method doesn't place the block
     * It is notable used during the shape gen during world gen
     *
     * @param blocksToPlace the block states list that would be chosen from
     * @return the block related to the noise
     */
    public static BlockState getRandomBlock(List<BlockState> blocksToPlace) {
        return blocksToPlace.get(Random.create().nextBetween(0, blocksToPlace.size() - 1));
    }

    /**
     * return the BlockState wanted based on order
     * this method doesn't place the block
     * It is notable used during the shape gen during world gen
     *
     * @param blocksToPlace the blockStates list that would be chosen from
     * @param i             the index to choose from
     * @return the block related to the noise
     */
    public static BlockState getBlockWithOrder(List<BlockState> blocksToPlace, int i) {
        return blocksToPlace.get(i);
    }

    /**
     * return the BlockState wanted based on 2d noise
     * this method doesn't place the block
     * It is notable used during the shape gen during world gen
     *
     * @param blocksToPlace the blockStates list that would be chosen from
     * @param pos           the pos of the block to test
     * @param noise         the noise
     * @return the block related to the noise
     */
    public static BlockState getBlockWith2DNoise(List<BlockState> blocksToPlace, BlockPos pos, FastNoiseLite
            noise) {
        double a = noise.GetNoise(pos.getX(), pos.getZ());
        return getBlockStateWithNoise(blocksToPlace, a);
    }

    /**
     * return the BlockState wanted based on 3d noise
     * this method doesn't place the block
     * It is notable used during the shape gen during world gen
     *
     * @param blocksToPlace the blockStates list that would be chosen from
     * @param pos           the pos of the block to test
     * @param noise         the noise
     * @return the block related to the noise
     */
    public static BlockState getBlockWith3DNoise(List<BlockState> blocksToPlace, BlockPos pos, FastNoiseLite
            noise) {
        double a = noise.GetNoise(pos);
        return getBlockStateWithNoise(blocksToPlace, a);
    }

    /**
     * simplify the choice of block
     *
     * @param blocksToPlace the list of blockStates to place
     * @param a             the value of the noise
     * @return the block related to the noise
     */
    private static BlockState getBlockStateWithNoise(List<BlockState> blocksToPlace, double a) {
        int length = blocksToPlace.size() - 1;
        return blocksToPlace.get((int) ((length) * (a / 2 + 0.5)));
    }
}