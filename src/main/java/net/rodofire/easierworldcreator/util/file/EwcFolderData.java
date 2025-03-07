package net.rodofire.easierworldcreator.util.file;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.rodofire.easierworldcreator.Ewc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class EwcFolderData {
    private static final Map<RegistryKey<World>, Path> dimensionPath = new HashMap<>();

    public static void initFiles() {
        Ewc.LOGGER.info("|\t- Registering Data Folders");
        ServerWorldEvents.LOAD.register((minecraftServer, serverWorld) -> {
            dimensionPath.put(serverWorld.getRegistryKey(), DimensionType.getSaveDirectory(serverWorld.getRegistryKey(), minecraftServer.getSavePath(WorldSavePath.ROOT)));
            createDirectories(serverWorld);
        });
    }

    private static void createDirectories(ServerWorld world) {
        File file = getEwcDataDirectory(world).toFile();
        if (!file.exists()) {
            file.mkdirs();
        }

        file = getStructuresDirectory(world).toFile();
        if (!file.exists()) {
            file.mkdirs();
        }

        file = getReferenceDir(world).toFile();
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    public static Path getEwcDataDirectory(StructureWorldAccess world) {
        ServerWorld serverWorld = world.toServerWorld();
        return dimensionPath.get(serverWorld.getRegistryKey()).resolve("ewc_data");
    }

    public static Path getStructuresDirectory(StructureWorldAccess world) {
        Path path = getEwcDataDirectory(world);
        return path.resolve("structures");
    }

    public static Path getStructureDataDir(StructureWorldAccess world, ChunkPos chunk) {
        Path path = getStructuresDirectory(world);
        return path.resolve("chunk_" + chunk.x + "_" + chunk.z);
    }

    public static Path getNVerifyDataDir(StructureWorldAccess world, ChunkPos chunk) {
        Path path = getStructureDataDir(world, chunk);
        if (path.toFile().exists()) {
            return path;
        }
        path.toFile().mkdirs();
        return path;
    }

    public static Path getReferenceDir(StructureWorldAccess world) {
        Path path = getEwcDataDirectory(world);
        return path.resolve("structure_references");
    }

    public static Path getStructureReference(StructureWorldAccess world, ChunkPos chunk) {
        Path path = getReferenceDir(world);
        return path.resolve("chunk_" + chunk.x + "_" + chunk.z + ".json");
    }

    public static Path getEwcDataDirectory(ServerWorld world) {
        return dimensionPath.get(world.getRegistryKey()).resolve("ewc_data");

    }

    public static Path getStructuresDirectory(ServerWorld world) {
        Path path = getEwcDataDirectory(world);
        return path.resolve("structures");
    }

    public static Path getStructureDataDir(ServerWorld world, ChunkPos chunk) {
        Path path = getStructuresDirectory(world);
        return path.resolve("chunk_" + chunk.x + "_" + chunk.z);
    }

    public static Path getNVerifyDataDir(ServerWorld world, ChunkPos chunk) {
        Path path = getStructureDataDir(world, chunk);
        if (path.toFile().exists()) {
            return path;
        }
        path.toFile().mkdirs();
        return path;
    }

    public static Path getReferenceDir(ServerWorld world) {
        Path path = getEwcDataDirectory(world);
        return path.resolve("structure_references");
    }

    public static Path getStructureReference(ServerWorld world, ChunkPos chunk) {
        Path path = getReferenceDir(world);
        return path.resolve("chunk_" + chunk.x + "_" + chunk.z + ".json");
    }

    public static class Legacy {
        /**
         * method to get the path of the chunk under the generated folder
         *
         * @param chunk the chunk of the folder
         * @param world the world used to get the generated folder
         * @return the path
         */
        public static Path getLegacyGeneratedChunkDir(Chunk chunk, StructureWorldAccess world) {
            Path generatedPath = Objects.requireNonNull(world.getServer()).getSavePath(WorldSavePath.GENERATED).normalize();
            String chunkDirPrefix = "chunk_" + chunk.getPos().x + "_" + chunk.getPos().z;
            return generatedPath.resolve(Ewc.MOD_ID).resolve("structures").resolve(chunkDirPrefix);
        }

        public static Path getLegacyStructureDir(StructureWorldAccess world) {
            Path generatedPath = Objects.requireNonNull(world.getServer()).getSavePath(WorldSavePath.GENERATED).normalize();
            return generatedPath.resolve(Ewc.MOD_ID).resolve("structures");
        }
    }

}
