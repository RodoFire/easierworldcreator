package net.rodofire.easierworldcreator.config.objects;

public class IntegerConfigObject extends AbstractConfigObject<Integer> {
    private int minValue = Integer.MIN_VALUE;
    private int maxValue = Integer.MAX_VALUE;

    public IntegerConfigObject(final int defaultValue) {
        super(defaultValue);
    }

    public IntegerConfigObject(final int defaultValue, String description) {
        super(description, defaultValue);
    }

    public IntegerConfigObject(final int defaultValue, final int minValue, final int maxValue) {
        super(defaultValue);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public IntegerConfigObject(final int defaultValue, final int minValue, final int maxValue, String description) {
        super(description, defaultValue);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public void setActualValue(Integer actualValue) {
        if (actualValue > maxValue) {
            super.setActualValue(maxValue);
        } else if (actualValue < minValue) {
            super.setActualValue(minValue);
        } else {
            super.setActualValue(actualValue);
        }
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    @Override
    public String getObjectCategory() {
        return "Integers";
    }

    @Override
    public String getDefaultDescription(String modId) {
        return super.getDefaultDescription(modId) + ("#\tMin value: " + minValue + "\n#\tMax value: " + maxValue);
    }
}
