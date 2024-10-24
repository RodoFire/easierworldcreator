package net.rodofire.easierworldcreator.util;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

public class ChunkUtil {
    public static boolean isChunkGenerated(StructureWorldAccess world, ChunkPos chunkPos ) {
        Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FEATURES, false);
        return chunk!= null;
    }
}
