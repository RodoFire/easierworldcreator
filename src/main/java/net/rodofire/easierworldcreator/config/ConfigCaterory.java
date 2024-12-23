package net.rodofire.easierworldcreator.config;

import net.rodofire.easierworldcreator.config.objects.BooleanConfigObject;
import net.rodofire.easierworldcreator.config.objects.EnumConfigObject;
import net.rodofire.easierworldcreator.config.objects.IntegerConfigObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigCaterory {
    final String name;
    Map<String, BooleanConfigObject> bools = new HashMap<>();
    Map<String, IntegerConfigObject> ints = new HashMap<>();
    Map<String, EnumConfigObject> enums = new HashMap<>();

    public ConfigCaterory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addBoolean(String name, boolean defaultValue) {
        bools.put(name, new BooleanConfigObject(defaultValue));
    }

    public void addInt(String name, int defaultValue) {
        ints.put(name, new IntegerConfigObject(defaultValue));
    }

    public void addInt(String name, int defaultValue, String description, int minValue, int maxValue) {
        ints.put(name, new IntegerConfigObject(defaultValue, minValue, maxValue, description));
    }

    public void addInt(String name, int defaultValue, int minValue, int maxValue) {
        ints.put(name, new IntegerConfigObject(defaultValue, minValue, maxValue));
    }

    public void addBoolean(String name, String description, boolean defaultValue) {
        bools.put(name, new BooleanConfigObject(defaultValue, description));
    }

    public void addInt(String name, String description, int defaultValue) {
        ints.put(name, new IntegerConfigObject(defaultValue, description));
    }

    public void addEnum(String name, String defaultValue, Set<String> strings) {
        if (!strings.contains(defaultValue)) {
            throw new IllegalStateException("Default enum value not present in possible values");
        }
        enums.put(name, new EnumConfigObject(defaultValue, strings));
    }

    public void addEnum(String name, String description, String defaultValue, Set<String> strings) {
        if (!strings.contains(name)) {
            throw new IllegalStateException("Default enum value not present in possible values");
        }
        enums.put(name, new EnumConfigObject(defaultValue, strings, description));
    }

    public Map<String, BooleanConfigObject> getBools() {
        return bools;
    }

    public Map<String, IntegerConfigObject> getInts() {
        return ints;
    }

    public Map<String, EnumConfigObject> getEnums() {
        return enums;
    }

    public void init() {

    }

}
