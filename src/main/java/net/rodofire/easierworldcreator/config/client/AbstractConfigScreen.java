package net.rodofire.easierworldcreator.config.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.rodofire.easierworldcreator.EasierWorldCreator;
import net.rodofire.easierworldcreator.client.hud.widget.AbstractEntryWidget;
import net.rodofire.easierworldcreator.client.hud.widget.ImageButtonWidget;
import net.rodofire.easierworldcreator.client.hud.widget.TextButtonWidget;
import net.rodofire.easierworldcreator.config.ConfigCategory;
import net.rodofire.easierworldcreator.config.ModConfig;
import net.rodofire.easierworldcreator.config.objects.AbstractConfigObject;
import net.rodofire.easierworldcreator.config.objects.BooleanConfigObject;
import net.rodofire.easierworldcreator.config.objects.EnumConfigObject;
import net.rodofire.easierworldcreator.config.objects.IntegerConfigObject;
import org.spongepowered.include.com.google.common.collect.BiMap;
import org.spongepowered.include.com.google.common.collect.HashBiMap;

import java.util.Set;

public abstract class AbstractConfigScreen extends Screen {
    protected int selected = 0;

    protected final String modId;

    private final ModConfig config;
    private final ModConfig copy;

    Set<ConfigCategory> categories;
    protected final BiMap<String, Integer> indexes = HashBiMap.create();

    protected AbstractConfigScreen(ModConfig config, String modId) {
        super(Text.translatable("config.screen." + modId + ".title"));
        this.modId = modId;
        this.config = config;
        this.copy = config.copy();
        this.categories = config.getCategories();
        this.initIndexes();
    }

    @Override
    protected void init() {
        ConfigCategory category = copy.getCategory(indexes.inverse().get(selected));
        this.init(category);
    }

    protected abstract void init(ConfigCategory category);

    public <T extends AbstractConfigObject<U>, U> ImageButtonWidget addResetButton(int startX, int yOffset, T obj) {
        ImageButtonWidget buttonWidget = new ImageButtonWidget(startX, yOffset, 20, 20, Identifier.of(EasierWorldCreator.MOD_ID, "textures/gui/reset_button.png"), press -> reset(obj));
        buttonWidget.setTooltip(Tooltip.of(Text.translatable("config.ewc.reset")));
        return buttonWidget;
    }

    private void initIndexes() {
        int i = 0;
        for (ConfigCategory category : categories) {
            indexes.put(category.getName(), i);
            i++;
        }
    }

    protected void toggleBoolean(BooleanConfigObject configObject, ButtonWidget button) {
        boolean newValue = !configObject.getActualValue();
        configObject.setActualValue(newValue);
        button.setMessage(Text.translatable("config.ewc.boolean." + configObject.getActualValue()));
        if (button instanceof TextButtonWidget textButtonWidget) {
            textButtonWidget.setColor(newValue ? 0x00FF00 : 0xFF0000);
        }
    }

    public void cycleEnum(EnumConfigObject configObject, ButtonWidget button) {
        configObject.setActualValue(configObject.getNext());
        button.setMessage(Text.translatable("config." + modId + "." + configObject.getActualValue()));
    }

    protected void verifyInteger(IntegerConfigObject configObject, AbstractEntryWidget button, String cgr) {
        if (cgr.isEmpty()) return;
        if (cgr.equals("-")) {
            button.setEditableColor(0xFF0000);
            return;
        }
        if (!configObject.isAcceptable(Integer.parseInt(cgr))) {
            button.setEditableColor(0xFF0000);
        } else {
            button.setEditableColor(0xFFFFFF);
            configObject.setActualValue(Integer.parseInt(cgr));
        }
    }

    private <T extends AbstractConfigObject<U>, U> void reset(T configObject) {
        configObject.setActualValue(configObject.getDefaultValue());
        this.clearChildren();
        this.init();
    }

    protected boolean configEquals() {
        return config.equals(copy);
    }

    protected void saveConfig() {
        this.config.apply(copy);
        this.config.save();
    }

    protected boolean shouldRestart() {
        return config.shouldRestart();
    }
}
