package net.rodofire.easierworldcreator.config;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.rodofire.easierworldcreator.config.client.ConfigScreen;
import net.rodofire.easierworldcreator.config.objects.AbstractConfigObject;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ModConfig {
    boolean protectedConfig = false;
    private final String MOD_ID;
    Set<ConfigCategory> categories = new LinkedHashSet<>();

    public ModConfig(String modID) {
        this.MOD_ID = modID;
    }

    public void addCategory(ConfigCategory category) {
        categories.add(category);
    }

    public void addCategory(Set<ConfigCategory> categories) {
        this.categories.addAll(categories);
    }

    public void addCategories(ConfigCategory... categories) {
        this.categories.addAll(Arrays.stream(categories).collect(Collectors.toSet()));
    }

    public ConfigCategory getCategory(String name) {
        ConfigCategory cat = categories.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
        if (cat == null) {
            return null;
        }
        if (!protectedConfig) {
            ReadableConfig reader = new ReadableConfig(MOD_ID);
            reader.refresh(cat);
        }
        return cat;
    }

    public Set<ConfigCategory> getCategories() {
        return categories;
    }

    public void refreshValues() {
        ReadableConfig reader = new ReadableConfig(MOD_ID);
        for (ConfigCategory cat : categories) {
            reader.refresh(cat);
        }
    }

    public void save() {
        Path path = ConfigUtil.getConfigPath(MOD_ID);
        for (ConfigCategory cat : categories) {
            WritableConfig writer = new WritableConfig(MOD_ID, cat);
            writer.write(path.resolve(cat.getName() + ".toml"));
        }
    }

    public <T extends AbstractConfigObject<U>, U> void updateValues(String name) {
        WritableConfig writer = new WritableConfig(MOD_ID, getCategory(name));
        writer.repairConfig(getCategoryPath(name));
    }

    public boolean shouldRestart() {
        return this.categories.stream().anyMatch(
                configCategory -> configCategory.getBools().values().stream().anyMatch(AbstractConfigObject::shouldRestart)
        )
                || this.categories.stream().anyMatch(
                configCategory -> configCategory.getInts().values().stream().anyMatch(AbstractConfigObject::shouldRestart)
        )
                || this.categories.stream().anyMatch(
                configCategory -> configCategory.getEnums().values().stream().anyMatch(AbstractConfigObject::shouldRestart)
        );
    }

    public void init() {
        List<ConfigCategory> caterories = shouldWrite();
        if (!caterories.isEmpty()) {
            writeConfigs(caterories);
        }
        repair();
        refreshValues();
        ConfigScreen.putModId(MOD_ID, this);
        ServerWorldEvents.LOAD.register((minecraftServer, world) -> this.protectedConfig = true);
        ServerWorldEvents.UNLOAD.register((minecraftServer, world) -> this.protectedConfig = false);
    }

    public Path getCategoryPath(String name) {
        return ConfigUtil.getConfigPath(MOD_ID).resolve(getCategory(name).getName() + ".toml");
    }

    private void repair() {
        Path path = ConfigUtil.getConfigPath(MOD_ID);
        for (ConfigCategory category : categories) {
            WritableConfig writableConfig = new WritableConfig(MOD_ID, category);
            writableConfig.repairConfig(path.resolve(category.getName() + ".toml"));
        }
    }

    private List<ConfigCategory> shouldWrite() {
        List<ConfigCategory> configCaterories = new ArrayList<>();
        Path path = ConfigUtil.getConfigPath(MOD_ID);
        for (ConfigCategory cat : categories) {
            path = path.resolve(cat.getName() + ".toml");
            if (!path.toFile().exists()) {
                configCaterories.add(cat);
            }
        }
        return configCaterories;
    }

    private void writeConfigs(List<ConfigCategory> categories) {
        Path path = ConfigUtil.getConfigPath(MOD_ID);
        for (ConfigCategory category : categories) {
            WritableConfig wc = new WritableConfig(this.MOD_ID, category);
            wc.write(path.resolve(category.getName() + ".toml"));
        }
    }

    public ModConfig copy() {
        ModConfig config = new ModConfig(MOD_ID);
        config.categories = new LinkedHashSet<>(categories);
        config.protectedConfig = this.protectedConfig;
        return config;
    }

    public void apply(ModConfig config) {
        this.protectedConfig = config.protectedConfig;
        this.categories = config.categories;
    }

    public boolean equals(ModConfig obj) {
        if (categories.size() != obj.categories.size()) {
            return false;
        }
        Iterator<ConfigCategory> catIt = categories.iterator();
        Iterator<ConfigCategory> catIt2 = obj.categories.iterator();
        for (int i = 0; i < categories.size(); i++) {
            ConfigCategory cat = catIt.next();
            ConfigCategory cat2 = catIt2.next();
            if (!cat.equals(cat2)) return false;
        }
        boolean bl = true;
        return Objects.equals(obj.MOD_ID, this.MOD_ID);
    }
}
