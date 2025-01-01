package net.rodofire.easierworldcreator.util;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

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
    public static boolean isChunkGenerated(StructureWorldAccess world, ChunkPos chunkPos ) {
        Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FEATURES, false);
        return chunk!= null;
    }
}
