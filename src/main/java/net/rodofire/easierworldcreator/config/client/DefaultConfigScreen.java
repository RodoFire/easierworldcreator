package net.rodofire.easierworldcreator.config.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.rodofire.easierworldcreator.EasierWorldCreator;
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

import java.util.Iterator;
import java.util.Set;

public class DefaultConfigScreen extends Screen {
    private final Screen parent;
    private ModConfig config;
    private final ModConfig copy;

    String modId;

    private int currentCategoryIndex = 0;
    private int selected = 0;
    private final int maxCategoriesVisible = 5;
    private Identifier TEXTURE = Screen.OPTIONS_BACKGROUND_TEXTURE;
    int backgroundHeight = 32;
    int backgroundWidth = 32;
    Set<ConfigCategory> categories;
    protected final BiMap<String, Integer> indexes = HashBiMap.create();

    private int scrollY = 0;
    private int maxScrollY = 0;
    private int scrollBarHeight;
    private boolean isDraggingScrollBar = false;
    private int dragStartY;
    private MinecraftClient mc = MinecraftClient.getInstance();

    public DefaultConfigScreen(Screen parent, ModConfig config, String modId) {
        super(Text.translatable("config.screen." + modId + ".title"));
        this.parent = parent;
        this.modId = modId;
        this.config = config;
        this.copy = config;
        this.categories = config.getCategories();
        initIndexes();
    }

    public DefaultConfigScreen(Screen parent, ModConfig config, String modId, Identifier background) {
        super(Text.translatable("config.screen." + modId + ".title"));
        this.parent = parent;
        this.modId = modId;
        this.config = config;
        this.copy = config;
        this.categories = config.getCategories();
        this.TEXTURE = background;
        this.backgroundHeight = this.height;
        this.backgroundWidth = this.width;
        initIndexes();
    }

    private void initIndexes() {
        int i = 0;
        for (ConfigCategory category : categories) {
            indexes.put(category.getName(), i);
            i++;
        }
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 10;
        int buttonWidth = this.width / 8;
        int buttonHeight = 20;

        super.init();
        ConfigCategory category = copy.getCategory(indexes.inverse().get(selected));

        int contentHeight = calculateContentHeight(category, buttonHeight);
        maxScrollY = Math.max(0, contentHeight - (this.height - 40));

        int visibleAreaHeight = this.height - 40;
        scrollBarHeight = Math.max(20, (visibleAreaHeight * visibleAreaHeight) / contentHeight);

        addTopCategories(buttonWidth, buttonHeight, startY, centerX);

        addElements(category, buttonWidth, buttonHeight, centerX, startY + 45);

        addBottomElements();
    }

    public void addElements(ConfigCategory category, int buttonWidth, int buttonHeight, int startX, int startY) {
        if (!category.getBools().isEmpty()) {
            this.addDrawableChild(new TextWidget(this.width / 12, startY, 2 * this.width / 12, buttonHeight, Text.translatable("config.ewc.boolean_category"), this.textRenderer));
            startY += buttonHeight + 5;
            for (BooleanConfigObject obj : category.getBools().values()) {
                this.addDrawableChild(new TextWidget(3 * this.width / 12, startY, 3 * this.width / 12, buttonHeight, Text.translatable("config." + modId + "." + obj.getKey()), this.textRenderer));
                this.addDrawableChild(new TextButtonWidget(13 * this.width / 24, startY, this.width / 6, buttonHeight, Text.translatable("config.ewc.boolean." + obj.getActualValue()), button -> toggleBoolean(obj, button), obj.getActualValue() ? 0x00FF00 : 0xFF0000));
                addResetButton(9 * this.width / 12, startY, button -> reset(obj));
                startY += buttonHeight + 5;
            }
            startY += 6;
        }
        if (!category.getInts().isEmpty()) {
            this.addDrawableChild(new TextWidget(this.width / 12, startY, 2 * this.width / 12, buttonHeight, Text.translatable("config.ewc.integer_category"), this.textRenderer));
            startY += buttonHeight + 5;
            for (IntegerConfigObject obj : category.getInts().values()) {
                this.addDrawableChild(new TextWidget(3 * this.width / 12, startY, 3 * this.width / 12, buttonHeight, Text.translatable("config." + modId + "." + obj.getKey()), this.textRenderer));
                this.addDrawableChild(new TextFieldWidget(this.textRenderer, 13 * this.width / 24, startY, this.width / 6, buttonHeight, Text.of(String.valueOf(obj.getActualValue()))));
                addResetButton(9 * this.width / 12, startY, button -> reset(obj));
                startY += buttonHeight + 5;
            }
            startY += 6;
        }

        if (!category.getEnums().isEmpty()) {
            this.addDrawableChild(new TextWidget(this.width / 12, startY, 2 * this.width / 12, buttonHeight, Text.translatable("config.ewc.enum_category"), this.textRenderer));
            startY += buttonHeight + 5;
            for (EnumConfigObject obj : category.getEnums().values()) {
                this.addDrawableChild(new TextWidget(3 * this.width / 12, startY, 3 * this.width / 12, buttonHeight, Text.translatable("config." + modId + "." + obj.getKey()), this.textRenderer));
                this.addDrawableChild(new TextButtonWidget(13 * this.width / 24, startY, this.width / 6, buttonHeight, Text.translatable("config." + modId + "." + obj.getActualValue()), button -> cycleEnum(obj, button)));
                addResetButton(9 * this.width / 12, startY, button -> reset(obj));
                startY += buttonHeight + 5;
            }
        }
    }

    private int calculateContentHeight(ConfigCategory category, int buttonHeight) {
        int height = 55;
        if (!category.getBools().isEmpty()) {
            height += buttonHeight + 11;
            height += category.getBools().size() * (buttonHeight + 5);
        }
        if (!category.getInts().isEmpty()) {
            height += buttonHeight + 11;
            height += category.getInts().size() * (buttonHeight + 5);
        }
        if (!category.getEnums().isEmpty()) {
            height += buttonHeight + 5;
            height += category.getEnums().size() * (buttonHeight + 5);
        }
        return height;
    }

    private void addTopCategories(int buttonWidth, int buttonHeight, int startY, int centerX) {
        int categoriesNumber = Math.min(this.categories.size(), maxCategoriesVisible);
        int startX;
        startX = centerX - (buttonWidth + 2) * categoriesNumber / 2 + 1;

        Iterator<ConfigCategory> iterator = categories.iterator();
        for (int i = 0; i < currentCategoryIndex; i++) {
            iterator.next();
        }
        for (int i = 0; i < Math.min(this.categories.size(), maxCategoriesVisible); i++) {
            ConfigCategory category = iterator.next();
            if (currentCategoryIndex + i == this.selected) {
                this.addDrawableChild(new TextButtonWidget(startX, startY, buttonWidth, buttonHeight, Text.translatable(category.getName()), action -> openCategory(action.getMessage().getString()), 0xFFFFFF, 0x00FF00));
            } else {
                this.addDrawableChild(new TextButtonWidget(startX, startY, buttonWidth, buttonHeight, Text.translatable(category.getName()), action -> openCategory(action.getMessage().getString())));
            }
            startX += buttonWidth + 2;
        }
        if (categories.size() > maxCategoriesVisible) {
            if (currentCategoryIndex > 0) {
                this.addDrawableChild(new ImageButtonWidget(centerX - 10 - (maxCategoriesVisible * buttonWidth / 2 + 15), startY, 20, buttonHeight, new Identifier(EasierWorldCreator.MOD_ID, "textures/gui/before_button.png"), button -> scrollCategories(-1)));
            }
            if (this.categories.size() - currentCategoryIndex > 5) {
                this.addDrawableChild(new ImageButtonWidget(centerX - 10 + (maxCategoriesVisible * buttonWidth / 2 + 15), startY, 20, buttonHeight, new Identifier(EasierWorldCreator.MOD_ID, "textures/gui/after_button.png"), button -> scrollCategories(1)));
            }
        }
    }

    public void addBottomElements() {
        int buttonWidth = this.width / 5;
        int buttonHeight = 20;
        int startX = this.width / 2 - buttonWidth - 10;
        int startY = this.height - 40;

        this.addDrawableChild(new TextButtonWidget(startX, startY, buttonWidth, buttonHeight, Text.translatable("config.ewc.cancel"), button -> this.cancel(), 0xFFFFFF, 0xFF0000));
        this.addDrawableChild(new TextButtonWidget(startX + buttonWidth + 20, startY, buttonWidth, buttonHeight, Text.translatable("config.ewc.save_exit"), button -> this.saveExit(), 0xFFFFFF, 0x00FF00));
    }

    public void openCategory(String name) {
        this.selected = this.indexes.get(name);
        this.clearChildren();
        this.init();
    }

    private void scrollCategories(int direction) {
        int newIndex = currentCategoryIndex + direction;
        if (newIndex >= 0 && newIndex <= categories.size() - maxCategoriesVisible) {
            currentCategoryIndex = newIndex;
            this.clearChildren();
            this.init();
        }
    }

    public void addResetButton(int startX, int yOffset, ButtonWidget.PressAction action) {
        this.addDrawableChild(new ImageButtonWidget(startX, yOffset, 20, 20, new Identifier(EasierWorldCreator.MOD_ID, "textures/gui/reset_button.png"), action));
    }

    private void toggleBoolean(BooleanConfigObject configObject, ButtonWidget button) {
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

    private <T extends AbstractConfigObject<U>, U> void reset(T configObject) {
        configObject.setActualValue(configObject.getDefaultValue());
        this.clearChildren();
        this.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackgroundTexture(DrawContext context) {
        context.setShaderColor(0.25F, 0.25F, 0.25F, 1.0F);
        int i = 32;

        context.drawTexture(TEXTURE, 0, 0, 0, 0.0F, 0.0F, this.width, this.height, this.backgroundWidth, this.backgroundHeight);
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void saveExit() {
        this.config = copy;
        this.config.save();
        System.out.println("before close");
        this.clearChildren();
        System.out.println("after close");
        if (!config.shouldRestart()) {
            MinecraftClient.getInstance().setScreen(new ShouldRestartScreen());
        } else {
            MinecraftClient.getInstance().setScreen(parent);
        }
    }

    private void cancel() {
        this.clearChildren();
        if (client != null) {
            client.setScreen(new CancelScreen(parent, this));
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (maxScrollY > 0) {
            scrollY = Math.max(0, Math.min(maxScrollY, scrollY - (int) (amount * 10)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void close() {
        super.close();
        MinecraftClient.getInstance().setScreen(parent);
    }
}
