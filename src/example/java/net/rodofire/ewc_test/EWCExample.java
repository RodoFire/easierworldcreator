package net.rodofire.ewc_test;

import net.fabricmc.api.ModInitializer;
import net.rodofire.ewc_test.config.ExampleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EWCExample implements ModInitializer {
    public static final String MOD_ID = "ewc-example";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ExampleConfig.registerConfig();
    }
}
