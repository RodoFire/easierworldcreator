package net.rodofire.easierworldcreator.fileutil;

import net.rodofire.easierworldcreator.Easierworldcreator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
}
