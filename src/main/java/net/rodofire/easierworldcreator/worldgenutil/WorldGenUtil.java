package net.rodofire.easierworldcreator.worldgenutil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;

import java.util.ArrayList;
import java.util.List;

public class WorldGenUtil {

    public static Direction getRandomDirection() {
        return switch (Random.create().nextBetween(0, 5)) {
            case 0 -> Direction.WEST;
            case 1 -> Direction.EAST;
            case 2 -> Direction.NORTH;
            case 3 -> Direction.SOUTH;
            case 4 -> Direction.UP;
            default -> Direction.DOWN;
        };
    }

    public static Direction getRandomVerticalDirection() {
        return switch (Random.create().nextBetween(0, 1)) {
            case 0 -> Direction.UP;
            default -> Direction.DOWN;
        };
    }

    public static Direction getRandomHorizontalDirection() {
        return switch (Random.create().nextBetween(0, 3)) {
            case 0 -> Direction.WEST;
            case 1 -> Direction.EAST;
            case 2 -> Direction.NORTH;
            default -> Direction.SOUTH;
        };
    }

    public static int getRandomOpposite() {
        return (Random.create().nextBetween(0, 1) == 0) ? 1 : -1;
    }

    public static boolean getRandomBoolean(float chance) {
        return Random.create().nextFloat() < chance;
    }


    public static int getSign(int a) {
        return (a < 0) ? -1 : 1;
    }

    public static int getSign(double a) {
        return (a < 0) ? -1 : 1;
    }

    public static int getSign(float a) {
        return (a < 0) ? -1 : 1;
    }


    //return a random int between minheight and maxheight if the chance
    public static int getSecondHeight(float chance, int maxheight) {
        return getSecondHeight(chance, 0, maxheight);
    }

    public static int getSecondHeight(float chance, int minheight, int maxheight) {
        if (Random.create().nextFloat() < chance) {
            return Random.create().nextBetween(minheight, maxheight);
        }
        return 0;
    }

    //method to verify that the block is not an unbreakable block or not and to verify if the block can be put or not. If the block was placed, it returns true
    public static boolean verifyBlock(StructureWorldAccess world, boolean force, List<Block> blocksToForce, List<BlockState> blocksToPlace, BlockPos pos) {
        BlockState state2 = world.getBlockState(pos);
        int length = blocksToPlace.size() - 1;
        if (state2.getHardness(world, pos) < 0) return false;
        if (!force) {
            if (blocksToForce == null) blocksToForce = List.of(Blocks.BEDROCK);
            if (!state2.isAir() && blocksToForce.stream().noneMatch(state2.getBlock()::equals)) return false;
        }
        world.setBlockState(pos, blocksToPlace.get(Random.create().nextBetween(0, length)), 2);
        return true;
    }

    public static boolean isBlockInBlockStateList(Block block, List<BlockState> state) {
        // Parcourir la liste des BlockState pour vérifier si le bloc correspond
        for (BlockState blockState : state) {
            if (blockState.getBlock().equals(block)) {
                return true;
            }
        }
        return false;
    }

    public static List<Block> addBlockStateListtoBlockList(List<Block> block, List<BlockState> state) {
        List<Block> secondlist = new ArrayList<>();
        if (block == null) block = new ArrayList<>();
        for (BlockState blockState : state) {
            secondlist.add(blockState.getBlock());
        }
        block.addAll(secondlist);
        return block;
    }


}
