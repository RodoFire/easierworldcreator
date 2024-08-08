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
    //method to verify that the block is not an unbreakable block or not and to verify if the block can be put or not.
    public static boolean verifyBlock(StructureWorldAccess world, boolean force, List<Block> blocksToForce, BlockPos pos) {
        BlockState state2 = world.getBlockState(pos);

        if (state2.getHardness(world, pos) < 0) return false;
        if (!force) {
            if (blocksToForce == null) blocksToForce = List.of(Blocks.BEDROCK);
            if (!state2.isAir() && blocksToForce.stream().noneMatch(state2.getBlock()::equals)) return false;
        }
        return true;
    }

    //verify if it can put the block before placing it
    public static boolean setBlockWithOrderWithVerification(StructureWorldAccess world, boolean force, List<Block> blocksToForce, List<BlockState> blocktoplace, BlockPos pos, int i) {
        if (verifyBlock(world, force, blocksToForce, pos)) {
            placeBlockWithOrder(world, blocktoplace, pos, i);
            return true;
        }
        return false;
    }

    //verify if it can put the block before placing it
    public static boolean setRandomBlockWithVerification(StructureWorldAccess world, boolean force, List<Block> blocksToForce, List<BlockState> blocktoplace, BlockPos pos) {
        if (verifyBlock(world, force, blocksToForce, pos)) {
            placeRandomBlock(world, blocktoplace, pos);
            return true;
        }
        return false;
    }

    //verify if it can put the block before placing it
    public static boolean set2dNoiseBlockWithVerification(StructureWorldAccess world, boolean force, List<Block> blocksToForce, List<BlockState> blocktoplace, BlockPos pos, FastNoiseLite noise) {
        if (verifyBlock(world, force, blocksToForce, pos)) {
            placeBlockWith2DNoise(world, blocktoplace, pos, noise);
            return true;
        }
        return false;
    }

    //verify if it can put the block before placing it
    public static boolean set3dNoiseBlockWithVerification(StructureWorldAccess world, boolean force, List<Block> blocksToForce, List<BlockState> blocktoplace, BlockPos pos, FastNoiseLite noise) {
        if (verifyBlock(world, force, blocksToForce, pos)) {
            placeBlockWith3DNoise(world, blocktoplace, pos, noise);
            return true;
        }
        return false;
    }

    public static void placeRandomBlock(StructureWorldAccess world, List<BlockState> blocksToPlace, BlockPos pos) {
        int length = blocksToPlace.size() - 1;
        world.setBlockState(pos, blocksToPlace.get(Random.create().nextBetween(0, length)), 2);
    }

    //Place the block corresponding to the index 'i'.
    //Generally, after that, the index 'i' will be incremented by one every time this method is called.
    //But you can change it to place 2 same blocks then incrementing
    public static void placeBlockWithOrder(StructureWorldAccess world, List<BlockState> blocksToPlace, BlockPos pos, int i) {
        world.setBlockState(pos, blocksToPlace.get(i), 2);
    }

    //assign the 2d noise value to 'a'
    //then call the method to get the block depending on the noise
    public static void placeBlockWith2DNoise(StructureWorldAccess world, List<BlockState> blocksToPlace, BlockPos pos, FastNoiseLite noise) {
        float a = noise.GetNoise(pos.getX(), pos.getZ());
        placeBlockWithNoise(world, blocksToPlace, pos, a);
    }


    //assign the 3d noise value to 'a'
    //then call the method to get the block depending on the noise
    public static void placeBlockWith3DNoise(StructureWorldAccess world, List<BlockState> blocksToPlace, BlockPos pos, FastNoiseLite noise) {
        float a = noise.GetNoise(pos.getX(), pos.getY(), pos.getZ());
        placeBlockWithNoise(world, blocksToPlace, pos, a);
    }

    //method to place a block depending on the noise and the blocks inside the list
    //you get the float a that correspond to the value of the noise.
    //the method will compare a with every index of the list and will place the block when e become smaller than the index
    //does this:
    //a > i ?
    //  - true: a > i+1 ? ...
    //  - false: place block at the index with the index i-1
    private static void placeBlockWithNoise(StructureWorldAccess world, List<BlockState> blocksToPlace, BlockPos pos, float a) {
        int length = blocksToPlace.size() - 1;
        for (float i = 1; i <= length; i += 1) {
            if (a <= i * 2 / length - length) {
                world.setBlockState(pos, blocksToPlace.get((int) i - 1), 2);
            }
        }
    }
}