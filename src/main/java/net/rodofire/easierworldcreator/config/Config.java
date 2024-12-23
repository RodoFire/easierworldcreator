package net.rodofire.easierworldcreator.config;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Config {
    boolean protectedConfig = false;
    private final String MOD_ID;
    Set<ConfigCaterory> categories = new HashSet<>();

    public Config(String modID) {
        this.MOD_ID = modID;
    }

    public void addCategory(ConfigCaterory category) {
        categories.add(category);
    }

    public void addCategory(Set<ConfigCaterory> categories) {
        this.categories.addAll(categories);
    }

    public void addCategories(ConfigCaterory... categories) {
        this.categories.addAll(Arrays.stream(categories).collect(Collectors.toSet()));
    }

    public ConfigCaterory getCategory(String name) {
        ConfigCaterory cat = categories.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
        if (cat == null) {
            return null;
        }
        ReadableConfig reader = new ReadableConfig(MOD_ID);
        reader.refresh(cat);
        return cat;
    }

    public void refreshValues() {
        ReadableConfig reader = new ReadableConfig(MOD_ID);
        for (ConfigCaterory cat : categories) {
            reader.refresh(cat);
        }
    }

    public void init() {
        List<ConfigCaterory> caterories = shouldWrite();
        if (!caterories.isEmpty()) {
            writeConfigs(caterories);
        }
        repair();
        refreshValues();
    }

    private void repair() {
        Path path = ConfigUtil.getConfigPath(MOD_ID);
        for (ConfigCaterory category : categories) {
            WritableConfig writableConfig = new WritableConfig(MOD_ID, category);
            writableConfig.repairConfig(path.resolve(category.getName() + ".toml"));
        }
    }

    private List<ConfigCaterory> shouldWrite() {
        List<ConfigCaterory> configCaterories = new ArrayList<>();
        Path path = ConfigUtil.getConfigPath(MOD_ID);
        for (ConfigCaterory cat : categories) {
            path = path.resolve(cat.getName() + ".toml");
            if (!path.toFile().exists()) {
                configCaterories.add(cat);
            }
        }
        return configCaterories;
    }

    private void writeConfigs(List<ConfigCaterory> categories) {
        Path path = ConfigUtil.getConfigPath(MOD_ID);
        ExecutorService service = Executors.newFixedThreadPool(Math.min(categories.size(), Runtime.getRuntime().availableProcessors()));
        for (ConfigCaterory category : categories) {
            //service.submit(() -> {
            WritableConfig wc = new WritableConfig(this.MOD_ID, category);
            wc.write(path.resolve(category.getName() + ".toml"));
            //});
        }
        service.shutdown();
        try {
            if (!service.awaitTermination(1, TimeUnit.MINUTES)) {
                service.shutdownNow(); // Forcer l'arrêt si nécessaire
            }
        } catch (InterruptedException e) {
            service.shutdownNow();
            Thread.currentThread().interrupt(); // Restaurer le statut d'interruption
        }
    }
}
