package net.rodofire.easierworldcreator.worldgenutil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.rodofire.easierworldcreator.Easierworldcreator;
import net.rodofire.easierworldcreator.shapeutil.BlockLayer;
import net.rodofire.easierworldcreator.util.FastMaths;

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


    //verify if a block is in a list of BlockState
    public static boolean isBlockInBlockStateList(Block block, List<BlockState> state) {
        // Parcourir la liste des BlockState pour vérifier si le bloc correspond
        for (BlockState blockState : state) {
            if (blockState.getBlock().equals(block)) {
                return true;
            }
        }
        return false;
    }

    //Verify if a BlockState is in a list of BlockStates
    public static boolean isBlockStateInBlockStateList(BlockState block, List<BlockState> state) {
        // Parcourir la liste des BlockState pour vérifier si le bloc correspond
        for (BlockState blockState : state) {
            if (blockState == block) {
                return true;
            }
        }
        return false;
    }

    public static float getDistance(BlockPos pos1, BlockPos pos2) {
        return (float) FastMaths.getLength(pos1.getX() - pos2.getX(), pos1.getY() - pos2.getY(), pos1.getZ() - pos2.getZ());
    }

    public static float getDistance(BlockPos pos1, BlockPos pos2, float precision) {
        return (float) FastMaths.getLengthWPrecision(pos1.getX() - pos2.getX(), pos1.getY() - pos2.getY(), pos1.getZ() - pos2.getZ(), precision);
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

    public static float getDistanceFromPointToPlane(Vec3d normal, Vec3d pointOnPlane, Vec3d point) {
        double A = normal.x;
        double B = normal.y;
        double C = normal.z;

        // Find D using a point on the plane
        double D = -(A * pointOnPlane.x + B * pointOnPlane.y + C * pointOnPlane.z);

        // Coordinates of the point
        double x0 = point.x;
        double y0 = point.y;
        double z0 = point.z;

        // Calculate the distance
        double numerator = Math.abs(A * x0 + B * y0 + C * z0 + D);
        double denominator = FastMaths.getFastsqrt((float) (A * A + B * B + C * C), 0.001f);

        return (float) (numerator / denominator);
    }

    public static int getTotalBlockLayerDepth(List<BlockLayer> layers) {
        int i = 0;
        for (BlockLayer blockLayer : layers) {
            i += blockLayer.getDepth();
        }
        return i;
    }

    public static int getBlockLayerDepth(List<BlockLayer> layers, int index) {
        int i = 0;
        if (index >= layers.size()) {
            Easierworldcreator.LOGGER.error("int index >= blocklayer size");
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + layers.size());
        }
        for (int a = 0; a <= index; a++) {
            i += layers.get(a).getDepth();
        }
        return i;
    }

    public static int getBlockLayerDepth(List<BlockLayer> layers, int startindex, int endindex) {
        int i = 0;
        if (startindex >= layers.size()) {
            Easierworldcreator.LOGGER.error("int startindex >= blocklayer size");
            throw new IndexOutOfBoundsException("Index: " + startindex + ", Size: " + layers.size());
        }
        if (endindex >= layers.size()) {
            Easierworldcreator.LOGGER.error("int endindex >= blocklayer size");
            throw new IndexOutOfBoundsException("Index: " + endindex + ", Size: " + layers.size());
        }
        if (endindex < startindex) {
            Easierworldcreator.LOGGER.error("int firstindex > endindex");
            return 0;
        }
        for (int a = startindex; a <= endindex; a++) {
            i += layers.get(a).getDepth();
        }
        return i;
    }

    public static Direction getDirection(BlockPos pos1, BlockPos pos2) {
        if (pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY()) {
            return pos2.getZ() - pos1.getZ() < 0 ? Direction.WEST : Direction.EAST;
        }

        if (pos1.getX() == pos2.getX() && pos1.getZ() == pos2.getZ()) {
            return pos2.getY() - pos1.getY() < 0 ? Direction.DOWN : Direction.UP;
        }

        if (pos1.getY() == pos2.getY() && pos1.getZ() == pos2.getZ()) {
            return pos2.getX() - pos1.getX() < 0 ? Direction.SOUTH : Direction.NORTH;
        }
        return null;
    }


    public static BlockPos getCoordinatesRotation(float x, float y, float z, int rotationx, int rotationy, BlockPos pos) {
        return getCoordinatesRotation(x, y, z, rotationx, rotationy, 0, pos);
    }

    public static BlockPos getCoordinatesRotation(float x, float y, float z, int rotationx, int rotationy, int secondrotationx, BlockPos pos) {
        return getCoordinatesRotation(x, y, z, FastMaths.getFastCos(rotationx), FastMaths.getFastSin(rotationx), FastMaths.getFastCos(rotationy), FastMaths.getFastSin(rotationy), FastMaths.getFastCos(secondrotationx), FastMaths.getFastSin(secondrotationx), pos);
    }

    public static BlockPos getCoordinatesRotation(float x, float y, float z, double cosx, double sinx, double cosy, double siny, double cosx2, double sinx2, BlockPos pos) {
        // first x rotation
        float y_rot1 = (float) (y * cosx - z * sinx);
        float z_rot1 = (float) (y * sinx + z * cosx);

        // y rotation
        float x_rot_z = (float) (x * cosy - y_rot1 * siny);
        float y_rot_z = (float) (x * siny + y_rot1 * cosy);

        // second x rotation
        float y_rot2 = (float) (y_rot_z * cosx2 - z_rot1 * sinx2);
        float z_rot2 = (float) (y_rot_z * sinx2 + z_rot1 * cosx2);

        return new BlockPos(new BlockPos.Mutable().set((Vec3i) pos, (int) x_rot_z, (int) y_rot2, (int) z_rot2));

    }
}
