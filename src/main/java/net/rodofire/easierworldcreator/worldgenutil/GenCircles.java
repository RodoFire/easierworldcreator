package net.rodofire.easierworldcreator.worldgenutil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import java.util.List;

public class GenCircles {
    //Validate
    public static void generateCircle(StructureWorldAccess world, int radius, BlockState state, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radius, radius, x, z, y, false, false, null, List.of(state));
    }

    public static void generateFullCircle(StructureWorldAccess world, int radius, BlockState state, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radius, radius, x, z, y, false, true, null, List.of(state));
    }

    public static void generateFullCircle(StructureWorldAccess world, int radius, BlockState state, BlockPos pos, boolean force) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radius, radius, x, z, y, force, true, null, List.of(state));
    }

    public static void generateFullCircle(StructureWorldAccess world, int radius, BlockPos pos, boolean force, List<Block> stateToForce, List<BlockState> stateToPlace) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radius, radius, x, z, y, force, true, stateToForce, stateToPlace);
    }

    public static void generateCircle(StructureWorldAccess world, int radius, List<BlockState> state, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radius, radius, x, z, y, false, false, null, state);
    }

    public static void generateFullCircle(StructureWorldAccess world, int radius, List<BlockState> state, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radius, radius, x, z, y, false, true, null, state);
    }

    public static void generateFullCircle(StructureWorldAccess world, int radius, List<BlockState> state, BlockPos pos, boolean force) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radius, radius, x, z, y, force, true, null, state);
    }


    public static void generateElipsoid(StructureWorldAccess world, int radiusx, int radiusz, BlockState state, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radiusx, radiusz, x, z, y, false, false, null, List.of(state));
    }

    public static void generateFullElipsoid(StructureWorldAccess world, int radiusx, int radiusz, BlockState state, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radiusx, radiusz, x, z, y, false, true, null, List.of(state));
    }

    public static void generateFullElipsoid(StructureWorldAccess world, int radiusx, int radiusz, BlockState state, BlockPos pos, boolean force) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radiusx, radiusz, x, z, y, force, true, null, List.of(state));
    }

    public static void generateElipsoid(StructureWorldAccess world, int radiusx, int radiusz, List<BlockState> state, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radiusx, radiusz, x, z, y, false, false, null, state);
    }

    public static void generateFullElipsoid(StructureWorldAccess world, int radiusx, int radiusz, List<BlockState> state, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radiusx, radiusz, x, z, y, false, true, null, state);
    }

    public static void generateFullElipsoid(StructureWorldAccess world, int radiusx, int radiusz, List<BlockState> state, BlockPos pos, boolean force) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radiusx, radiusz, x, z, y, force, true, null, state);
    }


    /*---------- Algorithm based on Bressen Algorithms for circle ----------*/
    public static void generateOval(StructureWorldAccess world, int radiusX, int radiusZ, int centerX, int centerZ, int y, boolean force, boolean full, List<Block> blockToForce, List<BlockState> blockToPlace) {
        int x = 0;
        int z = radiusZ;
        int twoASquare = 2 * radiusX * radiusX;
        int twoBSquare = 2 * radiusZ * radiusZ;
        int decision1 = (int) (radiusZ * radiusZ - radiusX * radiusX * radiusZ + 0.25 * radiusX * radiusX);
        int dx = twoBSquare * x;
        int dz = twoASquare * z;

        // Region 1
        while (dx < dz) {
            if (!full) {
                placeOvalBlocks(world, centerX, centerZ, x, y, z, force, blockToForce, blockToPlace);
            } else {
                placeFullOval(world, centerX, centerZ, x, y, z, force, blockToForce, blockToPlace);
            }
            if (decision1 < 0) {
                x++;
                dx = dx + twoBSquare;
                decision1 = decision1 + dx + radiusZ * radiusZ;
            } else {
                x++;
                z--;
                dx = dx + twoBSquare;
                dz = dz - twoASquare;
                decision1 = decision1 + dx - dz + radiusZ * radiusZ;
            }
        }

        // Region 2
        int decision2 = (int) (radiusZ * radiusZ * (x + 0.5) * (x + 0.5) + radiusX * radiusX * (z - 1) * (z - 1) - radiusX * radiusX * radiusZ * radiusZ);
        while (z >= 0) {
            if (!full) {
                placeOvalBlocks(world, centerX, centerZ, x, y, z, force, blockToForce, blockToPlace);
            } else {
                placeFullOval(world, centerX, centerZ, x, y, z, force, blockToForce, blockToPlace);
            }
            if (decision2 > 0) {
                z--;
                dz = dz - twoASquare;
                decision2 = decision2 + radiusX * radiusX - dz;
            } else {
                z--;
                x++;
                dx = dx + twoBSquare;
                dz = dz - twoASquare;
                decision2 = decision2 + dx - dz + radiusX * radiusX;
            }
        }
    }

    public static void placeOvalBlocks(StructureWorldAccess world, int centerX, int centerZ, int x, int y, int z, boolean force, List<Block> blocksToForce, List<BlockState> blockToPlace) {
        BlockPos.Mutable pos = new BlockPos.Mutable();
        int length = blockToPlace.size() - 1;
        WorldGenUtil.verifyBlock(world, force, blocksToForce, blockToPlace, pos.set(centerX + x, 0, centerZ + z), length);
        WorldGenUtil.verifyBlock(world, force, blocksToForce, blockToPlace, pos.set(centerX + x, 0, centerZ - z), length);
        WorldGenUtil.verifyBlock(world, force, blocksToForce, blockToPlace, pos.set(centerX - x, 0, centerZ + z), length);
        WorldGenUtil.verifyBlock(world, force, blocksToForce, blockToPlace, pos.set(centerX - x, 0, centerZ - z), length);

    }

    //place lines between the blocks
    public static void placeFullOval(StructureWorldAccess world, int centerX, int centerZ, int x, int y, int z, boolean force, List<Block> blocksToForce, List<BlockState> blocksToPlace) {
        BlockPos start1 = new BlockPos(centerX + x, y, centerZ + z);
        BlockPos start2 = new BlockPos(centerX - x, y, centerZ + z);

        BlockPos end1 = new BlockPos(centerX + x, y, centerZ - z);
        BlockPos end2 = new BlockPos(centerX - x, y, centerZ - z);

        int length = blocksToPlace.size() - 1;

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        System.out.println(end2 + "  " + end1);
        for (int i = 0; i <= 2 * z; i++) {
            mutable.set(start1, 0, 0, -i);
            WorldGenUtil.verifyBlock(world, force, blocksToForce, blocksToPlace, mutable, length);
            mutable.set(start2, 0, 0, -i);
            WorldGenUtil.verifyBlock(world, force, blocksToForce, blocksToPlace, mutable, length);

        }
        //GenLines.drawLine(world, start1, end1, blockstate.get(Random.create().nextBetween(0,length)), force);
        //GenLines.drawLine(world, start2, end2, blockstate.get(Random.create().nextBetween(0,length)), force);
    }
}
