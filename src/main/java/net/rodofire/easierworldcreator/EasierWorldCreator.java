package net.rodofire.easierworldcreator;

import net.fabricmc.api.ModInitializer;
import net.rodofire.easierworldcreator.util.FastMaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasierWorldCreator implements ModInitializer {
    public static final String MOD_ID = "easierworldcreator";
    public static final Logger LOGGER = LoggerFactory.getLogger("easierworldcreator");
    private static boolean initialized = false;

    @Override
    public void onInitialize() {
        if (!initialized)
            init();
    }

    public static void init() {
        initialized = true;
        FastMaths.registerMaths();

        LOGGER.info("Starting Easierworldcreator");
    }
}