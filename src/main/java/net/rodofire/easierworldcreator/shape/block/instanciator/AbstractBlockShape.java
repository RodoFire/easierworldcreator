package net.rodofire.easierworldcreator.shape.block.instanciator;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.rodofire.easierworldcreator.shape.block.rotations.Rotator;
import net.rodofire.easierworldcreator.util.LongPosHelper;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to create custom shapes
 * <p> - Since 2.1.0, the shape doesn't return a {@link List<BlockPos>} but it returns instead a {@code List<Set<BlockPos>>}
 * <p> - Before 2.1.0, the BlockPos list was a simple list.
 * <p> - Starting from 2.1.0, the shapes return a list of {@link ChunkPos} that has a set of {@link BlockPos}
 * <p>The change from {@link List} to {@link Set} was done to avoid duplicates BlockPos, which resulted in unnecessary calculations.
 * <p>this allows easy multithreading for the Block assignment done in the {@link AbstractBlockShape} which result in better performance;
 * </p>
 */
@SuppressWarnings("unused")
public abstract class AbstractBlockShape {
    protected long centerPos;

    protected int centerX;
    protected int centerY;
    protected int centerZ;

    protected Rotator rotator;

    protected Map<ChunkPos, LongOpenHashSet> chunkMap = new HashMap<>();

    MutableObject<ChunkPos> lastChunkPos = new MutableObject<>(null);
    MutableObject<LongOpenHashSet> lastSet = new MutableObject<>(null);

    /**
     * init the Shape
     *
     * @param centerPos the center of the spiral
     */
    public AbstractBlockShape(@NotNull BlockPos centerPos) {
        this.centerPos = LongPosHelper.encodeBlockPos(centerPos);
        setCenterPos();
    }

    /**
     * init the shape
     *
     * @param centerPos the center BlockPos
     * @param rotator   the rotator uses to rotate the shape
     */
    public AbstractBlockShape(BlockPos centerPos, Rotator rotator) {
        this.centerPos = LongPosHelper.encodeBlockPos(centerPos);
        this.rotator = rotator;
        setCenterPos();
    }

    public void setRotator(Rotator rotator) {
        this.rotator = rotator;
    }

    /**
     * method to get the coordinates that will be placed later
     *
     * @return a map of ChunkPos of blockPos for every shape
     */
    public abstract Map<ChunkPos, LongOpenHashSet> getShapeCoordinates();

    private void setCenterPos() {
        centerX = LongPosHelper.decodeX(centerPos);
        centerY = LongPosHelper.decodeY(centerPos);
        centerZ = LongPosHelper.decodeZ(centerPos);
    }

    protected void modifyChunkMap(long pos) {
        ChunkPos chunkPos = LongPosHelper.getChunkPos(pos);

        if (!chunkPos.equals(lastChunkPos.getValue())) {
            lastChunkPos.setValue(chunkPos);
            lastSet.setValue(chunkMap.computeIfAbsent(chunkPos, k -> new LongOpenHashSet()));
        }

        lastSet.getValue().add(pos);
    }
}