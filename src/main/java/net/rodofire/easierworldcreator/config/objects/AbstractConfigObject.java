package net.rodofire.easierworldcreator.config.objects;

public abstract class AbstractConfigObject<T> {
    /**
     * the key of the name used in the screen.
     * <p> letters should be undercase
     * <p>in your translation file, you should translate "config.[mod_id].[key]"
     * if not set, the [key] value will be set to [name]
     */
    String key;
    /**
     * the name used in the toml file
     */
    String name;
    /**
     * the description used in the toml file
     */
    String description;
    /**
     * the key of the description used in the screen.
     * <p> letters should be undercase
     * <p>in your translation file, you should translate "config.[mod_id].[key].descriptionKey"
     */
    String descriptionKey;
    T defaultValue;
    T actualValue;
    public boolean requireRestart = false;
    boolean restart = false;

    public AbstractConfigObject(String description, String name, T defaultValue) {
        this.description = description;
        this.defaultValue = defaultValue;
        this.actualValue = defaultValue;
        this.key = name;
        this.name = name;
    }

    public AbstractConfigObject(String name, T defaultValue) {
        this.defaultValue = defaultValue;
        this.actualValue = defaultValue;
        this.key = name;
        this.name = name;
    }

    public abstract String getObjectCategory();

    public void resetToDefaultValue() {
        actualValue = defaultValue;
    }

    public T getActualValue() {
        return actualValue;
    }

    public void setActualValue(T actualValue) {
        if (requireRestart && actualValue != this.actualValue)
            restart = true;
        this.actualValue = actualValue;
    }


    public boolean shouldRestart() {
        return restart;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getDescriptionKey(String modId) {
        return "# " + /*Text.of("config." + modId + "." +*/ description/*)*/;
    }

    public String getDefaultDescription(String modId) {
        return description == null ? ("# Possible Values:\n") : getDescriptionKey(modId) + ("\n# Possible Values:\n");
    }


    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public void setDescriptionKey(String descriptionKey) {
        this.descriptionKey = descriptionKey;
    }

    public T getDefaultValue() {
        return defaultValue;
    }
}
