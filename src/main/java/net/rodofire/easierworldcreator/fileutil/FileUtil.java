package net.rodofire.easierworldcreator.fileutil;

import net.minecraft.util.WorldSavePath;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.rodofire.easierworldcreator.Easierworldcreator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileUtil {
    public static void renameFile(Path oldPath, Path newPath) {
        if (Files.exists(oldPath)) {
            try {
                Path parentDir = newPath.getParent();
                if (parentDir != null && !Files.exists(parentDir)) {
                    Files.createDirectories(parentDir);
                }

                Files.move(oldPath, newPath);

            } catch (IOException e) {
                Easierworldcreator.LOGGER.warn("Failed to rename file. oldPath: " + oldPath.toString() + " newPath: " + newPath.toString(), e);
            } catch (SecurityException e) {
                Easierworldcreator.LOGGER.warn("Insufficient permissions to rename file. oldPath: " + oldPath.toString() + " newPath: " + newPath.toString(), e);
            }
        } else {
            Easierworldcreator.LOGGER.warn("Renaming file failed, file doesn't exist: " + oldPath.toString());
        }
    }

    public static void removeFile(Path path) {
        File file = new File(path.toString());
        if (file.exists()) {
            boolean deleted = file.delete();
        } else {
        }
    }

    public static Path getGeneratedChunkDirectory(Chunk chunk, StructureWorldAccess world) {
        Path generatedPath = Objects.requireNonNull(world.getServer()).getSavePath(WorldSavePath.GENERATED).normalize();
        String chunkDirPrefix = "chunk_" + chunk.getPos().x + "_" + chunk.getPos().z;  // Prefix to match chunk directories
        return generatedPath.resolve(Easierworldcreator.MOD_ID).resolve("structures").resolve(chunkDirPrefix);
    }
    public static void removeGeneratedChunkDirectory(Chunk chunk, StructureWorldAccess world) {
        Path directoryPath = getGeneratedChunkDirectory(chunk, world);
        File file = new File(directoryPath.toString());
        if(file.exists() && file.isDirectory()) {
            boolean deleted = file.delete();
        }
    }
}