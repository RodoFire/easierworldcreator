package net.rodofire.easierworldcreator.shapegen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.shapeutil.BlockLayer;
import net.rodofire.easierworldcreator.shapeutil.Shape;
import net.rodofire.easierworldcreator.worldgenutil.WorldGenUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LineGen extends Shape {
    private BlockPos secondPos;

    public LineGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, List<BlockLayer> layers, boolean force, List<Block> blocksToForce, int xrotation, int yrotation, int secondxrotation, BlockPos secondPos) {
        super(world, pos, layers, force, blocksToForce, xrotation, yrotation, secondxrotation);
        this.secondPos = secondPos;
    }

    public LineGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, BlockPos secondPos) {
        super(world, pos);
        this.secondPos = secondPos;
    }

    @Override
    public List<BlockPos> getBlockPos() {
        Direction direction;
        //faster coordinates generation
        if ((direction = WorldGenUtil.getDirection(this.getPos(),secondPos)) != null){
            return this.generateAxisLine(direction);
        }
        return this.drawLine();
    }

    @Override
    public List<Vec3d> getVec3d() {
        return List.of();
    }


    public List<BlockPos> generateAxisLine(Direction dir) {
        int length = (int) WorldGenUtil.getDistance(this.getPos(), secondPos);
        List<BlockPos> poslist = new ArrayList<BlockPos>();
        for (int i = 0; i < length; i++) {
            poslist.add(this.getPos().offset(dir, i));
        }
        return poslist;
    }


    public List<BlockPos> drawLine() {
        List<BlockPos> poslist = new ArrayList<>();
        poslist.add(this.getPos());

        int x1 = this.getPos().getX();
        int y1 = this.getPos().getY();
        int z1 = this.getPos().getZ();
        int x2 = secondPos.getX();
        int y2 = secondPos.getY();
        int z2 = secondPos.getZ();

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
                poslist.add(currentPos);
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
                poslist.add(currentPos);
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
                poslist.add(currentPos);
            }
        }
        return poslist;
    }
}
