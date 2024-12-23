package net.rodofire.easierworldcreator.config.objects;

public abstract class AbstractConfigObject<T> {
    String description;
    T defaultValue;
    T actualValue;

    public AbstractConfigObject(String description, T defaultValue) {
        this.description = description;
        this.defaultValue = defaultValue;
        this.actualValue = defaultValue;
    }

    public AbstractConfigObject(T defaultValue) {
        this.defaultValue = defaultValue;
        this.actualValue = defaultValue;
    }

    public abstract String getObjectCategory();

    public void resetToDefaultValue() {
        actualValue = defaultValue;
    }

    public T getActualValue() {
        return actualValue;
    }

    public void setActualValue(T actualValue) {
        this.actualValue = actualValue;
    }

    public String getDescriptionKey(String modId) {
        return "# " + /*Text.of("config." + modId + "." +*/ description/*)*/;
    }

    public String getDefaultDescription(String modId) {
        return description == null ? ("# Possible Values:\n") : getDescriptionKey(modId) + ("\n# Possible Values:\n");
    }

    public T getDefaultValue() {
        return defaultValue;
    }
}
