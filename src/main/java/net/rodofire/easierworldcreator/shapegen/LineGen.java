package net.rodofire.easierworldcreator.shapegen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.shapeutil.BlockLayer;
import net.rodofire.easierworldcreator.shapeutil.Shape;
import net.rodofire.easierworldcreator.worldgenutil.WorldGenUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Class to generate Line related shapes
 * <p>
 * Since 2.1.0, the shape doesn't return a {@link List<BlockPos>} but it returns instead a {@code List<Set<BlockPos>>}
 * Before 2.1.0, the BlockPos list was a simple list.
 * Starting from 2.1.0, the shapes returns a list of {@link ChunkPos} that has a set of {@link BlockPos}
 * The change from {@link List} to {@link Set} was done to avoid duplicates BlockPos which resulted in unnecessary calculations.
 * this allow easy multithreading for the Block assignment done in the {@link Shape} which result in better performance;
 * </p>
 */
@SuppressWarnings("unused")
public class LineGen extends Shape {
    private BlockPos secondPos;

    /**
     * init the Line Shape
     *
     * @param world           the world the spiral will spawn in
     * @param pos             the center of the spiral
     * @param placeMoment     define the moment where the shape will be placed
     * @param layerPlace      how the {@code @BlockStates} inside of a {@link BlockLayer} will be placed
     * @param layersType      how the Layers will be placed
     * @param xRotation       first rotation around the x-axis
     * @param yRotation       second rotation around the y-axis
     * @param secondXRotation last rotation around the x-axis
     * @param featureName     the name of the feature
     * @param secondPos       the second pos on which the line has to go
     */
    public LineGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment, LayerPlace layerPlace, LayersType layersType, int xRotation, int yRotation, int secondXRotation, String featureName, BlockPos secondPos) {
        super(world, pos, placeMoment, layerPlace, layersType, xRotation, yRotation, secondXRotation, featureName);
        this.secondPos = secondPos;
    }

    /**
     * init the Line Shape
     *
     * @param world       the world the spiral will spawn in
     * @param pos         the center of the spiral
     * @param placeMoment define the moment where the shape will be placed
     * @param secondPos   the second pos on which the line has to go
     */
    public LineGen(@NotNull StructureWorldAccess world, @NotNull BlockPos pos, @NotNull PlaceMoment placeMoment, BlockPos secondPos) {
        super(world, pos, placeMoment);
        this.secondPos = secondPos;
    }

    @Override
    public List<Set<BlockPos>> getBlockPos() {
        Direction direction;
        Map<ChunkPos, Set<BlockPos>> chunkMap = new HashMap<>();
        //faster coordinates generation
        if (!this.biggerThanChunk && WorldGenUtil.isPosAChunkFar(this.secondPos, this.getPos()))
            this.biggerThanChunk = true;
        if ((direction = WorldGenUtil.getDirection(this.getPos(), secondPos)) != null) {
            this.generateAxisLine(direction, chunkMap);
        } else {
            this.drawLine(chunkMap);
        }
        return new ArrayList<>(chunkMap.values());
    }

    @Override
    public List<Vec3d> getVec3d() {
        return List.of();
    }

    /**
     * this method generates the coordinates
     *
     * @param dir      the direction of the line
     * @param chunkMap the map used to get the coordinates
     */
    public void generateAxisLine(Direction dir, Map<ChunkPos, Set<BlockPos>> chunkMap) {
        int length = (int) WorldGenUtil.getDistance(this.getPos(), secondPos);
        for (int i = 0; i < length; i++) {
            BlockPos pos = this.getPos().offset(dir, i);
            WorldGenUtil.modifyChunkMap(pos, chunkMap);
        }
    }


    public void drawLine(Map<ChunkPos, Set<BlockPos>> chunkMap) {
        WorldGenUtil.modifyChunkMap(this.getPos(), chunkMap);

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
                WorldGenUtil.modifyChunkMap(currentPos, chunkMap);
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
                WorldGenUtil.modifyChunkMap(currentPos, chunkMap);
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
                WorldGenUtil.modifyChunkMap(currentPos, chunkMap);
            }
        }
    }
}
