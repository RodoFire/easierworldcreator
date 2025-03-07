package net.rodofire.easierworldcreator.util.file;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.rodofire.easierworldcreator.Ewc;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

public class EwcFolderData {

    public static void initFiles() {
        Ewc.LOGGER.info("|\t- Registering Data Folders");
        createDirectories();
    }

    private static void createDirectories() {
        File file = getEwcDataDirectory().toFile();
        if (!file.exists()) {
            file.mkdirs();
        }

        file = getStructuresDirectory().toFile();
        if (!file.exists()) {
            file.mkdirs();
        }

        file = getReferenceDir().toFile();
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    public static Path getEwcDataDirectory() {
        return FabricLoader.getInstance().getGameDir().resolve("ewc_data");
    }

    public static Path getStructuresDirectory() {
        return getEwcDataDirectory().resolve("structures");
    }

    public static Path getStructureDataDir(ChunkPos chunk) {
        return getStructuresDirectory().resolve("chunk_" + chunk.x + "_" + chunk.z);
    }

    public static Path getNVerifyDataDir(ChunkPos chunk) {
        Path path = getStructureDataDir(chunk);
        if (path.toFile().exists()) {
            return path;
        }
        path.toFile().mkdirs();
        return path;
    }

    public static Path getReferenceDir() {
        return getEwcDataDirectory().resolve("structure_references");
    }

    public static Path getStructureReference(ChunkPos chunk) {
        return getReferenceDir().resolve("chunk_" + chunk.x + "_" + chunk.z + ".json");
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
