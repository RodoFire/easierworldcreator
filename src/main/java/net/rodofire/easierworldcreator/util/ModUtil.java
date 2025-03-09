package net.rodofire.easierworldcreator.util;

import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ModUtil {
    public static @NotNull Set<String> getModsList() {
        return FabricLoader.getInstance().getAllMods()
                .stream()
                .map(mod -> mod.getMetadata().getId())
                .collect(Collectors.toSet());
    }
}
