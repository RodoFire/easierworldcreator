package net.rodofire.easierworldcreator.config.objects;


public class BooleanConfigObject extends AbstractConfigObject<Boolean> {

    public BooleanConfigObject(final boolean defaultValue) {
        super(defaultValue);
    }

    public BooleanConfigObject(final boolean defaultValue, String description) {
        super(description, defaultValue);
        this.actualValue = defaultValue;
    }

    @Override
    public String getObjectCategory() {
        return "Boolean";
    }

    @Override
    public String getDefaultDescription(String modId) {
        return super.getDefaultDescription(modId) + ("#\ttrue\n#\tfalse");
    }
}
