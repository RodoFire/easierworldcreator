package net.rodofire.ewc_example.config;

import net.rodofire.easierworldcreator.config.ConfigCategory;
import net.rodofire.easierworldcreator.config.ModConfig;
import net.rodofire.easierworldcreator.config.objects.BooleanConfigObject;
import net.rodofire.easierworldcreator.config.objects.EnumConfigObject;
import net.rodofire.easierworldcreator.config.objects.IntegerConfigObject;
import net.rodofire.ewc_test.EWCTest;

import java.util.Set;

public class ExampleConfig {
    public static final ModConfig CONFIG = new ModConfig(EWCTest.MOD_ID);
    public static final ConfigCategory FOO = new ConfigCategory("foo");
    public static final ConfigCategory DOO = new ConfigCategory("doo");

    public static void registerConfig() {
        //booleans
        BooleanConfigObject bool1Test = new BooleanConfigObject(true, "bool1test");
        bool1Test.requireRestart = true;
        FOO.addBoolean(bool1Test);
        FOO.addBoolean(new BooleanConfigObject(false, "here is some description that will be written in the config file", "bool2test"));
        FOO.addBoolean("bool3test", "other way of registering the boolean", true);

        //integers
        IntegerConfigObject int1Test = new IntegerConfigObject(0, "int1test");
        int1Test.requireRestart = true;
        FOO.addInt(int1Test);
        FOO.addInt(new IntegerConfigObject(78, "otherint"));
        FOO.addInt("int2test", 12, "adding min and max values", -9, 15151);

        //enums
        EnumConfigObject enum1Test = new EnumConfigObject("value1", "enum1test", Set.of("value1", "value2", "value3", "value4"));
        enum1Test.requireRestart = true;
        FOO.addEnum(enum1Test);
        FOO.addEnum(new EnumConfigObject("banana", "fruits", "list of some fruits", Set.of("banana", "apple", "orange", "tomato")));
        FOO.addEnum("newenum", "hello", Set.of("hello", "world"));

        CONFIG.addCategory(FOO);
        CONFIG.addCategory(DOO);

        CONFIG.init();
    }

    public static boolean getBool1Test() {
        return CONFIG.getCategory("foo").getBools().get("bool1test").getActualValue();
    }

    public static int getInt1Test() {
        return CONFIG.getCategory("foo").getInts().get("int1test").getActualValue();
    }
}
