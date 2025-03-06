package net.rodofire.easierworldcreator.world.chunk;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

public interface ChunkRegionUtil {
    /**
     * Method to get a chunk. If the chunk doesn't exists yet, it will return null instead of crashing the game
     * @param chunkX the x coordinate of the chunk
     * @param chunkZ the z coordinate of the chunk
     * @param leastStatus the status wanted
     */
    Chunk ewc_main$getNullableChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create);
}
