package net.rodofire.easierworldcreator.shape.block.simplegen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.util.BlockPlaceUtil;
import net.rodofire.easierworldcreator.shape.block.gen.LineGen;

import java.util.List;
import java.util.Set;

/**
 * switch to new generation :
 * @see LineGen
 * this class will not be updated anymore and won't receive any support
 */
@Deprecated
@SuppressWarnings("unused")
public class GenLines {
    public static void generateAxisLine(StructureWorldAccess world, BlockPos pos, int length,Direction dir,BlockState state){
        generateAxisLine(world,pos,length,dir,false,null, List.of(state));
    }

    public static void generateAxisLine(StructureWorldAccess world, BlockPos pos, int length, Direction dir, boolean force, Set<Block> blocksToForce, List<BlockState> blocksToPlace) {
        for (int i = 0; i < length; i++){
            BlockPlaceUtil.setRandomBlockWithVerification(world,force,blocksToForce,blocksToPlace,pos.offset(dir,i));
        }
    }


    public static void drawLine(StructureWorldAccess world, BlockPos start, BlockPos end, BlockState state) {
        drawLine(world, start, end, state, false);
    }

    public static void drawLine(StructureWorldAccess world, BlockPos start, BlockPos end, BlockState state, boolean force) {
        if(!force){
            if(world.getBlockState(start).isAir()){
                world.setBlockState(start, state, 2);
            }
        }else {
            world.setBlockState(start, state, 2);
        }
        int x1 = start.getX();
        int y1 = start.getY();
        int z1 = start.getZ();
        int x2 = end.getX();
        int y2 = end.getY();
        int z2 = end.getZ();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int dz = Math.abs(z2 - z1);

        int xs = x1 < x2 ? 1 : -1;
        int ys = y1 < y2 ? 1 : -1;
        int zs = z1 < z2 ? 1 : -1;

        if (dx >= dy && dx >= dz) {
            int p1 = 2 * dy - dx;
            int p2 = 2 * dz - dx;
            while (x1 != x2) {
                x1 += xs;
                if (p1 >= 0) {
                    y1 += ys;
                    p1 -= 2 * dx;
                }
                if (p2 >= 0) {
                    z1 += zs;
                    p2 -= 2 * dx;
                }
                p1 += 2 * dy;
                p2 += 2 * dz;
                BlockPos currentPos = new BlockPos(x1, y1, z1);
                BlockState currentState = world.getBlockState(currentPos);
                if (!force) {
                    if (currentState.isAir() || currentState.isIn(BlockTags.FLOWERS)) {
                        world.setBlockState(currentPos, state, 2);
                    }
                    continue;
                }
                world.setBlockState(currentPos, state, 2);
            }
        } else if (dy >= dx && dy >= dz) {
            int p1 = 2 * dx - dy;
            int p2 = 2 * dz - dy;
            while (y1 != y2) {
                y1 += ys;
                if (p1 >= 0) {
                    x1 += xs;
                    p1 -= 2 * dy;
                }
                if (p2 >= 0) {
                    z1 += zs;
                    p2 -= 2 * dy;
                }
                p1 += 2 * dx;
                p2 += 2 * dz;
                BlockPos currentPos = new BlockPos(x1, y1, z1);
                BlockState currentState = world.getBlockState(currentPos);
                if (!force) {
                    if (currentState.isAir() || currentState.isIn(BlockTags.FLOWERS)) {
                        world.setBlockState(currentPos, state, 2);
                    }
                    continue;
                }
                world.setBlockState(currentPos, state, 2);
            }
        } else {
            int p1 = 2 * dy - dz;
            int p2 = 2 * dx - dz;
            while (z1 != z2) {
                z1 += zs;
                if (p1 >= 0) {
                    y1 += ys;
                    p1 -= 2 * dz;
                }
                if (p2 >= 0) {
                    x1 += xs;
                    p2 -= 2 * dz;
                }
                p1 += 2 * dy;
                p2 += 2 * dx;
                BlockPos currentPos = new BlockPos(x1, y1, z1);
                BlockState currentState = world.getBlockState(currentPos);
                if (!force) {
                    if (currentState.isAir() || currentState.isIn(BlockTags.FLOWERS)) {
                        world.setBlockState(currentPos, state, 2);
                    }
                    continue;
                }
                world.setBlockState(currentPos, state, 2);
            }
        }
    }
}


