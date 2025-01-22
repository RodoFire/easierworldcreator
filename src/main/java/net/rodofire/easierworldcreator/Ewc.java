package net.rodofire.easierworldcreator;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.rodofire.easierworldcreator.config.ewc.EwcConfig;
import net.rodofire.easierworldcreator.maths.FastMaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class related to the basics of the mod
 */
public class Ewc implements DedicatedServerModInitializer {
    /**
     * Mod id of the mod
     */
    public static final String MOD_ID = "easierworldcreator";
    /**
     * Logger of the mod
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("easierworldcreator");
    private static boolean initialized = false;

    /**
     * Method to init the mod before if needed.
     * If the mod was already initialized, it won't be initialized another time
     */
    public static void init() {
        initialized = true;
        EwcConfig.setConfig();
        FastMaths.registerMaths();

        LOGGER.info("Starting Easierworldcreator");
    }

    @Override
    public void onInitializeServer() {
        if (!initialized)
            init();

    }
}