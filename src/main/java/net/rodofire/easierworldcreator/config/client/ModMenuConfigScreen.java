package net.rodofire.easierworldcreator.config.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.rodofire.easierworldcreator.EasierWorldCreator;
import net.rodofire.easierworldcreator.config.ewc.EwcConfig;
import net.rodofire.easierworldcreator.config.ewc.EwcConfigScreen;

import java.util.Map;

public class ModMenuConfigScreen implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new EwcConfigScreen(parent, EwcConfig.MOD_CONFIG, EasierWorldCreator.MOD_ID);
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return ConfigScreen.getScreenMap();
    }
}
