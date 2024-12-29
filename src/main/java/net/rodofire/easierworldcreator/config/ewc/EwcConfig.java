package net.rodofire.easierworldcreator.config.ewc;

import net.rodofire.easierworldcreator.EasierWorldCreator;
import net.rodofire.easierworldcreator.config.ConfigCategory;
import net.rodofire.easierworldcreator.config.ModConfig;
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
        SERVER_CATEGORY.addInt("testint", 2, -1000, 150 );
        SERVER_CATEGORY.addEnum("testenum", "bonjour", Set.of("bonjour", "banane", "hi"));

        FOO_CATEGORY.addBoolean("voo","salut tout le monde. j'espere que vous allez bien", false);
        FOO_CATEGORY.addBoolean("woo","some desc", false);
        FOO_CATEGORY.addBoolean("xoo","some desc", true);
        FOO_CATEGORY.addBoolean("zoo","bonjour diane", true);

        MOD_CONFIG.addCategories(SERVER_CATEGORY);
        MOD_CONFIG.addCategories(FOO_CATEGORY);
        MOD_CONFIG.addCategories(VOO_CATEGORY);
        MOD_CONFIG.addCategories(WOO_CATEGORY);
        MOD_CONFIG.addCategories(XOO_CATEGORY);
        MOD_CONFIG.addCategories(YOO_CATEGORY);

        MOD_CONFIG.init();
    }

    public static boolean getPerformanceConfig() {
        return MOD_CONFIG.getCategory(SERVER).getBools().get("performance_mode").getActualValue();
    }
}
