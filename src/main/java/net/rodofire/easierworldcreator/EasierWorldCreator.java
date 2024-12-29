package net.rodofire.easierworldcreator;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.resource.language.I18n;
import net.rodofire.easierworldcreator.config.ewc.EwcConfig;
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
        EwcConfig.setConfig();
        FastMaths.registerMaths();
        System.out.println(I18n.translate("config.easierworldcreator.performance_mode"));

        LOGGER.info("Starting Easierworldcreator");
    }
}