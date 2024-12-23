package net.rodofire.easierworldcreator.config.objects;


import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class EnumConfigObject extends AbstractConfigObject<String> {
    public LinkedHashSet<String> getEnums() {
        return enums;
    }

    private LinkedHashSet<String> enums = new LinkedHashSet<>();

    public EnumConfigObject(String defaultValue, Set<String> enums) {
        super(defaultValue);
        this.enums.addAll(enums);
    }

    public EnumConfigObject(String defaultValue, Set<String> enums, String description) {
        super(description, defaultValue);
        this.enums.addAll(enums);
    }

    public String getNext() {
        Iterator<String> iter = enums.iterator();
        String actual = iter.next();
        while (!actual.equals(actualValue)) {
            actual = iter.next();
        }
        return iter.next();
    }

    @Override
    public String getObjectCategory() {
        return "Enums";
    }

    @Override
    public String getDefaultDescription(String modId) {
        return super.getDefaultDescription(modId) + ("#\t" + enums.toString());
    }

    public LinkedHashSet<String> getPossibleValues() {
        return enums;
    }
}
