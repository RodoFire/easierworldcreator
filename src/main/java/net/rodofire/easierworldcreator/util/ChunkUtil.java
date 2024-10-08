package net.rodofire.easierworldcreator.util;

import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WrapperProtoChunk;
import net.rodofire.easierworldcreator.Easierworldcreator;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.*;

public class ChunkUtil {
    public static boolean isChunkGenerated(StructureWorldAccess world, ChunkPos chunkPos ) {
        Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FEATURES, false);
        return chunk!= null;
    }
}
