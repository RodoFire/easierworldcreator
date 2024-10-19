package net.rodofire.easierworldcreator.worldgenutil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.rodofire.easierworldcreator.Easierworldcreator;
import net.rodofire.easierworldcreator.shapeutil.BlockLayer;
import net.rodofire.easierworldcreator.util.FastMaths;

import java.util.*;

@SuppressWarnings("unused")
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
        return Random.create().nextBetween(0,1) == 1 ? Direction.UP : Direction.DOWN;
    }

    public static Direction getRandomHorizontalDirection() {
        return switch (Random.create().nextBetween(0, 3)) {
            case 0 -> Direction.WEST;
            case 1 -> Direction.EAST;
            case 2 -> Direction.NORTH;
            default -> Direction.SOUTH;
        };
    }

    //return a random int between min height and max height if the chance
    public static int getSecondHeight(float chance, int maxHeight) {
        return getSecondHeight(chance, 0, maxHeight);
    }

    public static int getSecondHeight(float chance, int minHeight, int maxHeight) {
        if (Random.create().nextFloat() < chance) {
            return Random.create().nextBetween(minHeight, maxHeight);
        }
        return 0;
    }


    //verify if a block is in a list of BlockState
    public static boolean isBlockInBlockStateList(Block block, List<BlockState> state) {
        for (BlockState blockState : state) {
            if (blockState.getBlock().equals(block)) {
                return true;
            }
        }
        return false;
    }

    //Verify if a BlockState is in a list of BlockStates
    public static boolean isBlockStateInBlockStateList(BlockState block, List<BlockState> state) {
        for (BlockState blockState : state) {
            if (blockState == block) {
                return true;
            }
        }
        return false;
    }

    public static float getDistance(BlockPos pos1, BlockPos pos2) {
        return FastMaths.getLength(pos1.getX() - pos2.getX(), pos1.getY() - pos2.getY(), pos1.getZ() - pos2.getZ());
    }

    public static float getDistance(BlockPos pos1, BlockPos pos2, float precision) {
        return FastMaths.getLengthWPrecision(pos1.getX() - pos2.getX(), pos1.getY() - pos2.getY(), pos1.getZ() - pos2.getZ(), precision);
    }

    public static boolean isPosAChunkFar(BlockPos pos1, BlockPos pos2) {
        if(Math.abs(pos1.getX() - pos2.getX()) > 16) return true;
        return Math.abs(pos1.getZ() - pos2.getZ()) > 16;
    }


    public static Set<Block> addBlockStateListToBlockList(Set<Block> block, List<BlockState> state) {
        List<Block> secondlist = new ArrayList<>();
        if (block == null) block = new HashSet<>();
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
        double denominator = FastMaths.getFastSqrt((float) (A * A + B * B + C * C), 0.001f);

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
            Easierworldcreator.LOGGER.error("int index >= blockLayer size");
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + layers.size());
        }
        for (int a = 0; a <= index; a++) {
            i += layers.get(a).getDepth();
        }
        return i;
    }

    public static int getBlockLayerDepth(List<BlockLayer> layers, int startIndex, int endIndex) {
        int i = 0;
        if (startIndex >= layers.size()) {
            Easierworldcreator.LOGGER.error("int startIndex >= blockLayer size");
            throw new IndexOutOfBoundsException("Index: " + startIndex + ", Size: " + layers.size());
        }
        if (endIndex >= layers.size()) {
            Easierworldcreator.LOGGER.error("int endIndex >= blockLayer size");
            throw new IndexOutOfBoundsException("Index: " + endIndex + ", Size: " + layers.size());
        }
        if (endIndex < startIndex) {
            Easierworldcreator.LOGGER.error("int firstIndex > endIndex");
            return 0;
        }
        for (int a = startIndex; a <= endIndex; a++) {
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


    public static BlockPos getCoordinatesRotation(float x, float y, float z, int rotationX, int rotationY, BlockPos pos) {
        return getCoordinatesRotation(x, y, z, rotationX, rotationY, 0, pos);
    }

    public static BlockPos getCoordinatesRotation(float x, float y, float z, int rotationX, int rotationY, int secondRotationX, BlockPos pos) {
        return getCoordinatesRotation(x, y, z, FastMaths.getFastCos(rotationX), FastMaths.getFastSin(rotationX), FastMaths.getFastCos(rotationY), FastMaths.getFastSin(rotationY), FastMaths.getFastCos(secondRotationX), FastMaths.getFastSin(secondRotationX), pos);
    }

    public static BlockPos getCoordinatesRotation(float x, float y, float z, double cosX, double sinX, double cosy, double sinY, double cosX2, double sinX2, BlockPos pos) {
        // first x rotation
        float y_rot1 = (float) (y * cosX - z * sinX);
        float z_rot1 = (float) (y * sinX + z * cosX);

        // y rotation
        float x_rot_z = (float) (x * cosy - y_rot1 * sinY);
        float y_rot_z = (float) (x * sinY + y_rot1 * cosy);

        // second x rotation
        float y_rot2 = (float) (y_rot_z * cosX2 - z_rot1 * sinX2);
        float z_rot2 = (float) (y_rot_z * sinX2 + z_rot1 * cosX2);

        return new BlockPos(new BlockPos.Mutable().set(pos, (int) x_rot_z, (int) y_rot2, (int) z_rot2));

    }

    /**
     * This method allows you to divide a list of blockPos into chunks.
     * It is used later to put the blocks
     * @param posList the list of BlockPos that will be divided
     * @return a list of set of BlockPos that represents a list of chunks
     */
    public static List<Set<BlockPos>> divideBlockPosIntoChunk(List<BlockPos> posList){
        Map<ChunkPos, Set<BlockPos>> chunkMap = new HashMap<>();
        for (BlockPos pos : posList){
            modifyChunkMap(pos, chunkMap);
        }
        return new ArrayList<>(chunkMap.values());
    }

    public static void modifyChunkMap(BlockPos pos, Map<ChunkPos, Set<BlockPos>> chunkMap) {
        ChunkPos chunkPos = new ChunkPos(pos);
        Set<BlockPos> blockPosInChunk = chunkMap.computeIfAbsent(chunkPos, k -> new HashSet<>());
        blockPosInChunk.add(pos);
    }
}
