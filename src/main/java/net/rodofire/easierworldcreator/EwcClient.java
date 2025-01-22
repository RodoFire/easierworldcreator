package net.rodofire.easierworldcreator;

import net.fabricmc.api.ClientModInitializer;
import net.rodofire.easierworldcreator.config.ewc.EwcClientConfig;

public class EwcClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Ewc.init();
        EwcClientConfig.init();
    }
}
