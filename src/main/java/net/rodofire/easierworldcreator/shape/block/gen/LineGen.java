package net.rodofire.easierworldcreator.shape.block.gen;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.rodofire.easierworldcreator.shape.block.instanciator.AbstractBlockShape;
import net.rodofire.easierworldcreator.shape.block.rotations.Rotator;
import net.rodofire.easierworldcreator.util.LongPosHelper;
import net.rodofire.easierworldcreator.util.WorldGenUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to generate Line related shapes
 * <p>
 * Since 2.1.0, the shape doesn't return a {@link List<BlockPos>} but it returns instead a {@code List<Set<BlockPos>>}
 * Before 2.1.0, the BlockPos list was a simple list.
 * Starting from 2.1.0, the shapes returns a list of {@link ChunkPos} that has a set of {@link BlockPos}
 * The change from {@link List} to {@link Set} was done to avoid duplicates BlockPos which resulted in unnecessary calculations.
 * This allow easy multithreading for the Block assignment done in the {@link AbstractBlockShape} which result in better performance;
 * </p>
 */
@SuppressWarnings("unused")
public class LineGen extends AbstractBlockShape {
    private BlockPos secondPos;

    /**
     * init the Line Shape
     *
     * @param pos       the position of the start of the line
     * @param secondPos the second pos on which the line has to go
     */
    public LineGen(@NotNull BlockPos pos, Rotator rotator, BlockPos secondPos) {
        super(pos, rotator);
        this.secondPos = secondPos;
    }

    /**
     * init the Line Shape
     *
     * @param pos       the position of the start of the line
     * @param secondPos the second pos on which the line has to go
     */
    public LineGen(@NotNull BlockPos pos, BlockPos secondPos) {
        super(pos);
        this.secondPos = secondPos;
    }

    public BlockPos getSecondPos() {
        return secondPos;
    }

    public void setSecondPos(BlockPos secondPos) {
        this.secondPos = secondPos;
    }

    @Override
    public Map<ChunkPos, LongOpenHashSet> getShapeCoordinates() {
        Direction direction;
        if ((direction = WorldGenUtil.getDirection(LongPosHelper.decodeBlockPos(this.centerPos), secondPos)) != null) {
            this.generateAxisLine(direction);
        } else {
            this.drawLine();
        }
        return chunkMap;
    }

    /**
     * this method generates the coordinates
     *
     * @param dir      the direction of the line
     * @param chunkMap the map used to get the coordinates
     */
    public void generateAxisLine(Direction dir) {
        int length = (int) WorldGenUtil.getDistance(LongPosHelper.decodeBlockPos(centerPos), secondPos);
        for (int i = 0; i < length; i++) {
            modifyChunkMap(LongPosHelper.offset(dir, centerPos, i));
        }
    }


    public void drawLine() {
        modifyChunkMap(this.centerPos);

        int x1 = centerX;
        int y1 = centerY;
        int z1 = centerZ;
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
                WorldGenUtil.modifyChunkMap(LongPosHelper.encodeBlockPos(x1, y1, z1), chunkMap);
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
                WorldGenUtil.modifyChunkMap(LongPosHelper.encodeBlockPos(x1, y1, z1), chunkMap);
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
                WorldGenUtil.modifyChunkMap(LongPosHelper.encodeBlockPos(x1, y1, z1), chunkMap);
            }
        }
    }
}
