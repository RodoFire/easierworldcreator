package net.rodofire.easierworldcreator.world.chunk;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.config.ewc.EwcConfig;
import net.rodofire.easierworldcreator.maths.MathUtil;
import net.rodofire.easierworldcreator.util.ChunkUtil;
import net.rodofire.easierworldcreator.util.WorldGenUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class ChunkPosManager {
    private final Set<ChunkPos> unGeneratedChunks = new HashSet<>();
    private final Set<ChunkPos> allowedChunks = new HashSet<>();
    private final Set<ChunkPos> unAllowedChunks = new HashSet<>();
    private BlockPos offset = new BlockPos(0, 0, 0);

    private final @NotNull StructureWorldAccess world;

    public ChunkPosManager(@NotNull StructureWorldAccess world) {
        this.world = world;
    }

    public ChunkPosManager(@NotNull StructureWorldAccess world, Set<ChunkPos> toTest) {
        this.world = world;
        putAll(toTest);
    }

    public ChunkPosManager(@NotNull StructureWorldAccess world, ChunkPos toTest) {
        this.world = world;
        this.put(toTest);
    }

    /**
     * Method to avoid chain call. If a chunk is not generated, it is added to the non-generated chunks. If the chunk is generated and can be placed, it will be added
     *
     * @param chunkPos the pos to put
     * @return true, it can accept world-gen blocks, false if not
     */
    public boolean put(ChunkPos chunkPos) {
        //verifies if the chunk was added previously
        if (unGeneratedChunks.contains(chunkPos)) {
            return true;
        }
        if (allowedChunks.contains(chunkPos)) {
            return true;
        }
        if (unAllowedChunks.contains(chunkPos)) {
            return false;
        }

        //The chunk is then not present. We verify if the chunk was generated
        if (!ChunkUtil.isFeaturesGenerated(world, chunkPos)) {
            unGeneratedChunks.add(chunkPos);
            return true;
        }

        //if the chunk is at a distance minor to the feature distance, it is allowed
        if (canBeAllowed(chunkPos)) {
            allowedChunks.add(chunkPos);
            return true;
        }
        //if not, it is refused
        unAllowedChunks.add(chunkPos);
        return false;
    }

    /**
     * Method to know if a chunk  chain call.
     * If a chunk is not generated, it is added to the non-generated chunks.
     * If the chunk is generated and can be placed, it will be added to the allowed one.
     * If not, nothing is done
     *
     * @param chunkPos the pos to put
     * @return true, it can accept world-gen blocks, false if not
     */
    private boolean putSafe(ChunkPos chunkPos) {
        //The chunk is then not present. We verify if the chunk was generated
        if (!ChunkUtil.isFeaturesGenerated(world, chunkPos)) {
            unGeneratedChunks.add(chunkPos);
            return true;
        }
        return false;
    }

    /**
     * method to know if a chunk can be placed
     *
     * @param chunkPos the pos of the chunk to be tested
     * @return true if the chunk
     */
    private boolean canBeAllowed(ChunkPos chunkPos) {
        int distance = EwcConfig.getFeaturesChunkDistance();
        for (int x = -distance; x <= distance; x++) {
            for (int z = -distance; z <= distance; z++) {
                if (z == 0 && x == 0) continue;

                ChunkPos pos = new ChunkPos(chunkPos.x + x, chunkPos.z + z);
                if (unGeneratedChunks.contains(pos))
                    return true;

                //verifies that the chunk was already tested
                if (unAllowedChunks.contains(pos) || allowedChunks.contains(pos)) continue;

                //if not, we verify it
                if (putSafe(pos))
                    return true;
            }
        }
        return false;
    }

    public void putAll(Set<ChunkPos> chunks) {
        for (ChunkPos chunkPos : chunks) {
            put(chunkPos);
        }
    }

    public boolean containsChunk(ChunkPos chunkPos) {
        return unGeneratedChunks.contains(chunkPos) || unAllowedChunks.contains(chunkPos) || allowedChunks.contains(chunkPos);
    }


    /**
     * method to know if a set of chunk can be placed
     *
     * @param chunks the set to be verified
     * @return true if every chunk can be placed, false if not
     */
    public boolean canGenerate(Set<ChunkPos> chunks, int xOffset, int zOffset) {
        boolean bl = true;
        for (ChunkPos chunkPos : chunks) {
            ChunkPos newChunkPos = WorldGenUtil.addChunkPos(chunkPos, xOffset, zOffset);
            if (!containsChunk(newChunkPos)) {
                put(newChunkPos);
            }
            if (unAllowedChunks.contains(newChunkPos)) {
                bl = false;
            }
        }
        return bl;
    }


    /**
     * method to know if the shape should use multi-chunk feature
     *
     * @param posSetMap the map that will determine if the shape is multi-chunk
     * @return true if it is, false if not
     */
    public boolean isMultiChunk(Set<ChunkPos> posSetMap, BlockPos centerPos) {
        if (posSetMap.size() > Math.pow(2 * EwcConfig.getFeaturesChunkDistance() + 1, 2))
            return true;
        for (ChunkPos set : posSetMap) {
            if (Math.abs(set.x) > EwcConfig.getFeaturesChunkDistance() + Math.abs(new ChunkPos(centerPos).x))
                return true;
            if (Math.abs(set.z) > EwcConfig.getFeaturesChunkDistance() + Math.abs(new ChunkPos(centerPos).z))
                return true;
        }
        return false;
    }

    public boolean canPlaceMultiChunk(Set<ChunkPos> chunkPosSet, int maxDistance) {
        int minOffset = Integer.MAX_VALUE;
        for (int x = -maxDistance; x <= maxDistance; x++) {
            for (int z = -maxDistance; z <= maxDistance; z++) {
                if (canGenerate(chunkPosSet, x, z)) {
                    int offset = MathUtil.absDistance(x, z);
                    if (offset < minOffset) {
                        minOffset = offset;
                        this.offset = new BlockPos(16 * x, 0, 16 * z);
                    }
                }
            }
        }
        return minOffset != Integer.MAX_VALUE;
    }

    public BlockPos getOffset() {
        return offset;
    }

}
