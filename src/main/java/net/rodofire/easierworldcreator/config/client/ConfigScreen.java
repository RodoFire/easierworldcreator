package net.rodofire.easierworldcreator.config.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.rodofire.easierworldcreator.config.ModClientConfig;
import net.rodofire.easierworldcreator.config.ModConfig;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ConfigScreen {
    private static final Map<String, Screen> screen = new HashMap<>();
    private static Map<String, ModClientConfig> modId = new HashMap<>();
    private static Map<String, Pair<Identifier, Pair<Integer, Integer>>> backgrounds = new HashMap<>();
    private static Map<String, Pair<Integer, Integer>> backgroundsShader = new HashMap<>();

    public static Screen getScreen(Screen parent, String modId) {
        if (screen.containsKey(modId)) {
            return screen.get(modId);
        }
        if (backgrounds.containsKey(modId) && backgroundsShader.containsKey(modId)) {
            return getDefaultScreen(parent, modId, ConfigScreen.modId.get(modId), backgrounds.get(modId).getLeft(), backgrounds.get(modId).getRight().getLeft(), backgrounds.get(modId).getRight().getRight(), backgroundsShader.get(modId).getLeft(), backgroundsShader.get(modId).getRight());
        } else if (backgrounds.containsKey(modId)) {
            return getDefaultScreen(parent, modId, ConfigScreen.modId.get(modId), backgrounds.get(modId).getLeft(), backgrounds.get(modId).getRight().getLeft(), backgrounds.get(modId).getRight().getRight());
        }
        return getDefaultScreen(parent, modId, ConfigScreen.modId.get(modId));
    }

    public static void setScreen(Screen screen, String modId) {
        ConfigScreen.screen.put(modId, screen);
    }


    public static Map<String, ConfigScreenFactory<?>> getScreenMap() {
        Map<String, ConfigScreenFactory<?>> map = new HashMap<>();
        for (Map.Entry<String, ModClientConfig> entry : modId.entrySet()) {
            map.put(entry.getKey(), par -> getScreen(par, entry.getKey()));
        }
        return map;
    }

    public static void putModId(String modId, ModClientConfig config) {
        ConfigScreen.modId.put(modId, config);
    }

    public static void setDefaultScreen(Screen parent, String modId, ModClientConfig modConfig) {
        if (screen.containsKey(modId)) {
            return;
        }

        DefaultConfigScreen screen1 = new DefaultConfigScreen(parent, modConfig, modId);
        screen.put(modId, screen1);
    }

    public static void setBackgroundScreen(String modId, Identifier image, int width, int height) {
        if (backgrounds.containsKey(modId)) {
            return;
        }
        backgrounds.put(modId, new Pair<>(image, new Pair<>(width, height)));
    }

    public static void setBackgroundScreen(String modId, Identifier image, int width, int height, int backgroundShader, int darkRectangleShader) {
        setBackgroundScreen(modId, image, width, height);
        if (backgroundsShader.containsKey(modId)) {
            return;
        }
        ConfigScreen.backgroundsShader.put(modId, new Pair<>(backgroundShader, darkRectangleShader));
    }

    public static Screen getDefaultScreen(Screen parent, String modId, ModClientConfig modConfig) {
        return new DefaultConfigScreen(parent, modConfig, modId);
    }

    public static Screen getDefaultScreen(Screen parent, String modId, ModClientConfig modConfig, Identifier image, int width, int height) {
        return new DefaultConfigScreen(parent, modConfig, modId, image, width, height);
    }

    public static Screen getDefaultScreen(Screen parent, String modId, ModClientConfig modConfig, Identifier image, int width, int height, int backgroundShaderColor, int darkbackgroundShaderColor) {
        return new DefaultConfigScreen(parent, modConfig, modId, image, width, height, backgroundShaderColor, darkbackgroundShaderColor);
    }


}
