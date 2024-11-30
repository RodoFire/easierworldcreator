package net.rodofire.easierworldcreator;

import net.fabricmc.api.ModInitializer;
import net.rodofire.easierworldcreator.maths.FastMaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class related to the basics of the mod
 */
public class EasierWorldCreator implements ModInitializer {
    /**
     * Mod id of the mod
     */
    public static final String MOD_ID = "easierworldcreator";
    /**
     * Logger of the mod
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("easierworldcreator");
    private static boolean initialized = false;

    @Override
    public void onInitialize() {
        if (!initialized)
            init();
    }

    /**
     * Method to init the mod before if needed.
     * If the mod was already initialized, it won't be initialized another time
     */
    public static void init() {
        initialized = true;
        FastMaths.registerMaths();

        LOGGER.info("Starting Easierworldcreator");
    }
}