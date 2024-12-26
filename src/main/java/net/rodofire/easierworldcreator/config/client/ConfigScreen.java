package net.rodofire.easierworldcreator.config.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import net.rodofire.easierworldcreator.EasierWorldCreator;
import net.rodofire.easierworldcreator.config.ModConfig;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ConfigScreen {
    private static final Map<String, Screen> screen = new HashMap<>();
    private static Map<String, ModConfig> modId = new HashMap<>();

    public static Screen getScreen(Screen parent, String modId) {
        if (screen.containsKey(modId)) {
            return screen.get(modId);
        }
        return getDefaultScreen(parent, modId, ConfigScreen.modId.get(modId));
    }

    public static void setScreen(Screen screen, String modId) {
        ConfigScreen.screen.put(modId, screen);
    }

    public static Map<String, ConfigScreenFactory<?>> getScreenMap() {
        Map<String, ConfigScreenFactory<?>> map = new HashMap<>();
        for (Map.Entry<String, ModConfig> entry : modId.entrySet()) {
            map.put(entry.getKey(), par -> getDefaultScreen(par, entry.getKey(), entry.getValue()));
        }
        return map;
    }

    public static void putModId(String modId, ModConfig config) {
        ConfigScreen.modId.put(modId, config);
    }

    public static void setDefaultScreen(Screen parent, String modId, ModConfig modConfig) {
        if (screen.containsKey(modId)) {
            return;
        }

        DefaultConfigScreen screen1 = new DefaultConfigScreen(parent, modConfig, modId, new Identifier(EasierWorldCreator.MOD_ID, "textures/gui/config_background.png"));
        screen.put(modId, screen1);
    }

    public static Screen getDefaultScreen(Screen parent, String modId, ModConfig modConfig) {
        return new DefaultConfigScreen(parent, modConfig, modId);
    }


}
