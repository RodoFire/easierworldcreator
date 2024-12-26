package net.rodofire.easierworldcreator.config;

import com.moandjiezana.toml.Toml;
import net.rodofire.easierworldcreator.config.objects.BooleanConfigObject;
import net.rodofire.easierworldcreator.config.objects.EnumConfigObject;
import net.rodofire.easierworldcreator.config.objects.IntegerConfigObject;

import java.util.Map;

public class ReadableConfig {
    private final String MOD_ID;

    public ReadableConfig(String modID) {
        this.MOD_ID = modID;
    }

    public void refresh(ConfigCategory caterory) {
        Toml toml = new Toml();
        toml.read(ConfigUtil.getConfigPath(MOD_ID).resolve(caterory.getName() + ".toml").toFile());
        for (Map.Entry<String, BooleanConfigObject> obj : caterory.bools.entrySet()) {
            obj.getValue().setActualValue(toml.getBoolean(obj.getKey()));
        }

        for (Map.Entry<String, IntegerConfigObject> obj : caterory.ints.entrySet()) {
            obj.getValue().setActualValue(toml.getLong(obj.getKey()).intValue());
        }

        for (Map.Entry<String, EnumConfigObject> obj : caterory.enums.entrySet()) {
            obj.getValue().setActualValue(toml.getString(obj.getKey()));
        }
    }
}
