package net.rodofire.easierworldcreator.util;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WrapperProtoChunk;
import net.rodofire.easierworldcreator.config.ewc.EwcConfig;
import net.rodofire.easierworldcreator.mixin.world.gen.ChunkRegionMixin;
import net.rodofire.easierworldcreator.world.chunk.ChunkRegionUtil;

/**
 * util class related to chunks
 */
public class ChunkUtil {

    /**
     * method to know if the features of a chunk were generated
     *
     * @param world    the world where the
     * @param chunkPos the chunk that will be tested
     * @return <p>- true if the features of the chunk were generated. <p>- false if not
     */
    public static boolean isFeaturesGenerated(WorldView world, ChunkPos chunkPos) {
        Chunk chunk = ((ChunkRegionUtil) world).getNullableChunk(chunkPos.x, chunkPos.z, ChunkStatus.EMPTY, false);
        return !(chunk instanceof ProtoChunk && chunk.getStatus().isAtMost(ChunkStatus.FEATURES));
    }

    public static boolean areNearbyFeaturesUnGenerated(WorldView world, ChunkPos chunkPos) {
        if (!isFeaturesGenerated(world, chunkPos)) {
            return true;
        }
        int distance = EwcConfig.getFeaturesChunkDistance();
        for (int i = -distance; i <= distance; i++) {
            for (int j = -distance; j <= distance; j++) {
                ChunkPos newChunkPos = new ChunkPos(chunkPos.x + i, chunkPos.z + j);
                if (!isFeaturesGenerated(world, newChunkPos)) {
                    return true;
                }
            }
        }
        return false;
    }
}
