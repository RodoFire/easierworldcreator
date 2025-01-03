package net.rodofire.easierworldcreator.config.ewc;

import net.minecraft.util.Identifier;
import net.rodofire.easierworldcreator.EasierWorldCreator;
import net.rodofire.easierworldcreator.config.ConfigCategory;
import net.rodofire.easierworldcreator.config.ModConfig;
import net.rodofire.easierworldcreator.config.client.ConfigScreen;
import net.rodofire.easierworldcreator.config.objects.BooleanConfigObject;

import java.util.Set;

public class EwcConfig {
    public static final ModConfig MOD_CONFIG = new ModConfig(EasierWorldCreator.MOD_ID);
    static final ConfigCategory SERVER_CATEGORY = new ConfigCategory("server");
    static final ConfigCategory FOO_CATEGORY = new ConfigCategory("foo");
    static final ConfigCategory VOO_CATEGORY = new ConfigCategory("voo");
    static final ConfigCategory WOO_CATEGORY = new ConfigCategory("woo");
    static final ConfigCategory XOO_CATEGORY = new ConfigCategory("xoo");
    static final ConfigCategory YOO_CATEGORY = new ConfigCategory("yoo");
    static String SERVER = "server";


    public static void setConfig() {
        BooleanConfigObject bool = new BooleanConfigObject(true, "performance_mode");
        bool.requireRestart = true;
        SERVER_CATEGORY.addBoolean(bool);
        SERVER_CATEGORY.addBoolean(new BooleanConfigObject(true, "multi_chunk_features"));
        SERVER_CATEGORY.addBoolean(new BooleanConfigObject(true, "chat_warns"));
        SERVER_CATEGORY.addBoolean(new BooleanConfigObject(true, "log_warns"));
        SERVER_CATEGORY.addBoolean(new BooleanConfigObject(true, "log_performance_info"));

        MOD_CONFIG.addCategories(SERVER_CATEGORY);

        MOD_CONFIG.init();
    }

    public static boolean getPerformanceConfig() {
        return MOD_CONFIG.getCategory(SERVER).getBools().get("performance_mode").getActualValue();
    }

    public static boolean getMultiChunkFeatures() {
        return MOD_CONFIG.getCategory(SERVER).getBools().get("multi_chunk_features").getActualValue();
    }

    public static boolean getChatWarns() {
        return MOD_CONFIG.getCategory(SERVER).getBools().get("chat_warns").getActualValue();
    }

    public static boolean getLogWarns() {
        return MOD_CONFIG.getCategory(SERVER).getBools().get("log_warns").getActualValue();
    }
    public static boolean getLogPerformanceInfo() {
        return MOD_CONFIG.getCategory(SERVER).getBools().get("log_performance_info").getActualValue();
    }
}
