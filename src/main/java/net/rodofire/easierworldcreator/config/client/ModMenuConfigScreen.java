package net.rodofire.easierworldcreator.config.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.rodofire.easierworldcreator.EasierWorldCreator;

import java.util.Map;

public class ModMenuConfigScreen implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ConfigScreen.getScreen(parent, EasierWorldCreator.MOD_ID);
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return ConfigScreen.getScreenMap();
    }
}
