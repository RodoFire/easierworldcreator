package net.rodofire.easierworldcreator.config.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.rodofire.easierworldcreator.Ewc;
import net.rodofire.easierworldcreator.config.ewc.EwcClientConfig;
import net.rodofire.easierworldcreator.config.ewc.screen.EwcConfigScreen;

import java.util.Map;

public class ModMenuConfigScreen implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return EwcConfigScreen::new;
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return ConfigScreen.getScreenMap();
    }
}
