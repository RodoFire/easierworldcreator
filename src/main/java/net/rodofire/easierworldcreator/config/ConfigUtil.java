package net.rodofire.easierworldcreator.config;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigUtil {
    public static Path getConfigPath(String modId) {
        Path path = FabricLoader.getInstance().getConfigDir();
        path = path.resolve(modId);
        try {
            Files.createDirectories(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }
}
