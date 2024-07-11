package net.rodofire.easierworldcreator.worldgenutil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Easierworldcreator;
import net.rodofire.easierworldcreator.util.FastMaths;

import java.util.List;

public class GenSpheres {
    public static void generateHalfFullSphere(StructureWorldAccess world, int radius, BlockPos pos, Direction direction, BlockState state) {
        generateHalfFullElipsoid(world, radius, radius, radius, pos, direction, List.of(state));
    }

    public static void generateHalfFullSphere(StructureWorldAccess world, int radius, BlockPos pos, Direction direction, List<BlockState> state) {
        generateHalfFullElipsoid(world, radius, radius, radius, pos, direction, state);
    }


    public static void generateHalfFullElipsoid(StructureWorldAccess world, int largex, int largey, int largez, BlockPos pos, Direction direction, BlockState state) {
        generateHalfFullElipsoid(world, largex, largey, largez, pos, direction, List.of(state));
    }

    public static void generateHalfFullElipsoid(StructureWorldAccess world, int largex, int largey, int largez, BlockPos pos, Direction direction, List<BlockState> state) {
        if (direction == Direction.UP) {
            generateFullEllipsoid(world, largex, largey, largez, pos, false, -largex, largex, 0, largey, -largez, largez, null, state);
            return;
        }
        if (direction == Direction.DOWN) {
            generateFullEllipsoid(world, largex, largey, largez, pos, false, -largex, largex, -largey, 0, -largez, largez, null, state);
            return;
        }
        if (direction == Direction.WEST) {
            generateFullEllipsoid(world, largex, largey, largez, pos, false, 0, largex, -largey, largey, -largez, largez, null, state);
            return;
        }
        if (direction == Direction.EAST) {
            generateFullEllipsoid(world, largex, largey, largez, pos, false, -largex, 0, -largey, largey, -largez, largez, null, state);
            return;
        }
        if (direction == Direction.NORTH) {
            generateFullEllipsoid(world, largex, largey, largez, pos, false, -largex, largex, -largey, largey, -largez, 0, null, state);
            return;
        }
        generateFullEllipsoid(world, largex, largey, largez, pos, false, -largex, largex, -largey, largey, 0, largez, null, state);
    }


    //Using carthesian coordinates beacause it have better performance than using trigonometry
    public static void generateFullEllipsoid(StructureWorldAccess world, int largex, int largey, int largez, BlockPos pos, boolean force, int minx, int maxx, int miny, int maxy, int minz, int maxz, List<Block> blocksToForce, List<BlockState> blocksToPlace) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int length = blocksToPlace.size() - 1;
        int largexsquared = largex * largex;
        int largeysquared = largey * largey;
        int largezsquared = largez * largez;
        if (largex > 32 || largey > 32 || largez > 32) {
            Easierworldcreator.LOGGER.warn("generating huge sphere (diameter > 64)");
        }
        for (float x = minx; x <= maxx; x++) {
            for (float y = miny; y <= maxy; y++) {
                for (float z = minz; z <= maxz; z++) {
                    if ((x * x) / (largexsquared) + (y * y) / (largeysquared) + (z * z) / (largezsquared) <= 1) {
                        mutable.set(pos, (int) x, (int) y, (int) z);
                        BlockState state2 = world.getBlockState(mutable);
                        if (state2.getHardness(world, mutable) < 0) continue;
                        if (!force) {
                            if (!state2.isAir() && blocksToForce.stream().noneMatch(state2.getBlock()::equals))
                                continue;
                        }
                        world.setBlockState(mutable, blocksToPlace.get(Random.create().nextBetween(0, length)), 2);
                    }
                }
            }
        }
    }

    public static void generateSphere(StructureWorldAccess world, int radius, BlockPos pos, BlockState state) {
        generateFullEllipsoid(world, radius, radius, radius, pos, false, -radius, radius, -radius, radius, -radius, radius, null, List.of(state));
    }

    public static void generateSphere(StructureWorldAccess world, int radius, BlockPos pos, BlockState state, boolean force) {
        generateFullEllipsoid(world, radius, radius, radius, pos, force, -radius, radius, -radius, radius, -radius, radius, null, List.of(state));
    }

    public static void generateFullElipsoid(StructureWorldAccess world, int largex, int largey, int largez, BlockPos pos, BlockState state) {
        generateFullEllipsoid(world, largex, largey, largez, pos, false, -largex, largex, -largey, largey, -largez, largez, null, List.of(state));
    }

    public static void generateFullElipsoid(StructureWorldAccess world, int largex, int largey, int largez, BlockPos pos, BlockState state, boolean force) {
        generateFullEllipsoid(world, largex, largey, largez, pos, force, -largex, largex, -largey, largey, -largez, largez, null, List.of(state));
    }


    //better performance when generating an empty sphere
    public static void generateEmptyEllipsoid(StructureWorldAccess world, int largex, int largey, int largez, BlockPos pos, boolean force, int minlarge, int maxlarge, int minheight, int maxheight, List<Block> blocksToForce, List<BlockState> blocksToPlace) {
        int maxlarge1 = Math.max(largez, Math.max(largex, largey));
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int length = blocksToPlace.size() - 1;
        for (double theta = minlarge; theta <= maxlarge; theta += (double) 45 / maxlarge1) {
            for (double phi = minheight; phi <= maxheight; phi += (double) 45 / maxlarge1) {
                int x = (int) (largex * FastMaths.getFastCos(theta) * FastMaths.getFastCos(phi));
                int y = (int) (largey * FastMaths.getFastSin(phi));
                int z = (int) (largex * FastMaths.getFastSin(theta) * FastMaths.getFastCos(phi));
                mutable.set(pos, x, y, z);

                BlockState state2 = world.getBlockState(mutable);
                if (state2.getHardness(world, mutable) < 0) return;
                if (!force) {
                    if (!state2.isAir() && blocksToForce.stream().noneMatch(state2.getBlock()::equals)) continue;
                }
                world.setBlockState(mutable, blocksToPlace.get(Random.create().nextBetween(0, length)), 2);
            }
        }
    }

    public static void generateHalfEmptySphere(StructureWorldAccess world, int radius, BlockPos pos, Direction direction, List<BlockState> state) {
        generateHalfEmptyElipsoid(world, radius, radius, radius, pos, direction, state);
    }

    public static void generateHalfEmptySphere(StructureWorldAccess world, int radius, BlockPos pos, Direction direction, BlockState state) {
        generateHalfEmptyElipsoid(world, radius, radius, radius, pos, direction, List.of(state));
    }

    public static void generateHalfEmptyElipsoid(StructureWorldAccess world, int largex, int largey, int largez, BlockPos pos, Direction direction, BlockState state) {
        generateHalfEmptyElipsoid(world, largex, largey, largez, pos, direction, List.of(state));
    }

    public static void generateHalfEmptyElipsoid(StructureWorldAccess world, int largex, int largey, int largez, BlockPos pos, Direction direction, List<BlockState> state) {
        if (direction == Direction.UP) {
            generateEmptyEllipsoid(world, largex, largey, largez, pos, false, 180, 180, 0, 90, null, state);
            return;
        }
        if (direction == Direction.DOWN) {
            generateEmptyEllipsoid(world, largex, largey, largez, pos, false, 180, 180, -90, 0, null, state);
            return;
        }
        if (direction == Direction.WEST) {
            generateEmptyEllipsoid(world, largex, largey, largez, pos, false, 0, 180, -90, 90, null, state);
            return;
        }
        if (direction == Direction.EAST) {
            generateEmptyEllipsoid(world, largex, largey, largez, pos, false, -180, 0, -90, 90, null, state);
            return;
        }
        if (direction == Direction.NORTH) {
            generateEmptyEllipsoid(world, largex, largey, largez, pos, false, -90, 90, -90, 90, null, state);
            return;
        }
        generateEmptyEllipsoid(world, largex, largey, largez, pos, false, 90, 270, -90, 90, null, state);
    }

    public static void generateEmptySphere(StructureWorldAccess world, int radius, BlockPos pos, BlockState state) {
        generateEmptyEllipsoid(world, radius, radius, radius, pos, false, -180, 180, -90, 90, null, List.of(state));
    }

    public static void generateEmptySphere(StructureWorldAccess world, int radius, BlockPos pos, BlockState state, boolean force) {
        generateEmptyEllipsoid(world, radius, radius, radius, pos, force, -180, 180, -90, 90, null, List.of(state));
    }


}
