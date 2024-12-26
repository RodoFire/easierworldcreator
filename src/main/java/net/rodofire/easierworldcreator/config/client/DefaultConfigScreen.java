package net.rodofire.easierworldcreator.config.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class DefaultConfigScreen extends AbstractConfigScreen {
    private final Screen parent;

    private int currentCategoryIndex = 0;
    private final int maxCategoriesVisible = 5;
    private Identifier TEXTURE = Screen.OPTIONS_BACKGROUND_TEXTURE;
    int backgroundHeight = 32;
    int backgroundWidth = 32;

    /**
     * map used to determine which element should be drawn
     */
    Map<Short, List<Drawable>> drawables = new LinkedHashMap<>();

    private short scrollY = 0;
    private short maxScrollY = 0;
    private int scrollBarHeight;
    private boolean isDraggingScrollBar = false;
    private int dragStartY;

    private boolean cancelScreen = false;
    private boolean restartScreen = false;

    public DefaultConfigScreen(Screen parent, ModConfig config, String modId) {
        super(config, modId);
        this.parent = parent;
        this.categories = config.getCategories();
    }

    public DefaultConfigScreen(Screen parent, ModConfig config, String modId, Identifier background) {
        super(config, modId);
        this.parent = parent;
        this.categories = config.getCategories();
        this.TEXTURE = background;
        this.backgroundHeight = this.height;
        this.backgroundWidth = this.width;
    }

    @Override
    protected void init(ConfigCategory category) {
        cancelScreen = false;
        int centerX = this.width / 2;
        int startY = 10;
        int buttonWidth = this.width / 8;
        int buttonHeight = 20;

        int contentHeight = calculateContentHeight(category, buttonHeight);
        maxScrollY = (short) calculateContentHeight(category, buttonHeight);
        int visibleAreaHeight = this.height - 40;
        scrollBarHeight = Math.max(20, (visibleAreaHeight * visibleAreaHeight) / contentHeight);

        drawTopCategories(buttonWidth, buttonHeight, startY, centerX);

        addElements(category, buttonWidth, buttonHeight, centerX, startY + 45 - scrollY);

        drawBottomElements();
    }

    public void addElements(ConfigCategory category, int buttonWidth, int buttonHeight, int startX, int startY) {
        if (!category.getBools().isEmpty()) {

            this.toDraw(new TextWidget(this.width / 12, startY - scrollY, 2 * this.width / 12, buttonHeight, Text.translatable("config.ewc.boolean_category"), this.textRenderer), startY);
            startY += buttonHeight + 5;
            for (BooleanConfigObject obj : category.getBools().values()) {
                this.toDraw(new TextWidget(3 * this.width / 12, startY - scrollY, 3 * this.width / 12, buttonHeight, Text.translatable("config." + modId + "." + obj.getKey()), this.textRenderer), startY);
                this.toDraw(new TextButtonWidget(13 * this.width / 24, startY - scrollY, this.width / 6, buttonHeight, Text.translatable("config.ewc.boolean." + obj.getActualValue()), button -> toggleBoolean(obj, button), obj.getActualValue() ? 0x00FF00 : 0xFF0000), startY);
                this.toDraw(addResetButton(9 * this.width / 12, startY - scrollY, obj), startY);

                startY += buttonHeight + 5;
            }
            startY += 6;
        }
        if (!category.getInts().isEmpty()) {
            this.toDraw(new TextWidget(this.width / 12, startY - scrollY, 2 * this.width / 12, buttonHeight, Text.translatable("config.ewc.integer_category"), this.textRenderer), startY);
            startY += buttonHeight + 5;
            for (IntegerConfigObject obj : category.getInts().values()) {
                this.toDraw(new TextWidget(3 * this.width / 12, startY - scrollY, 3 * this.width / 12, buttonHeight, Text.translatable("config." + modId + "." + obj.getKey()), this.textRenderer), startY);
                this.toDraw(new TextFieldWidget(this.textRenderer, 13 * this.width / 24, startY - scrollY, this.width / 6, buttonHeight, Text.of(String.valueOf(obj.getActualValue()))), startY);
                this.toDraw(addResetButton(9 * this.width / 12, startY - scrollY, obj), startY);

                startY += buttonHeight + 5;
            }
            startY += 6;
        }

        if (!category.getEnums().isEmpty()) {
            this.toDraw(new TextWidget(this.width / 12, startY - scrollY, 2 * this.width / 12, buttonHeight, Text.translatable("config.ewc.enum_category"), this.textRenderer), startY);
            startY += buttonHeight + 5;
            for (EnumConfigObject obj : category.getEnums().values()) {
                this.toDraw(new TextWidget(3 * this.width / 12, startY - scrollY, 3 * this.width / 12, buttonHeight, Text.translatable("config." + modId + "." + obj.getKey()), this.textRenderer), startY);
                this.toDraw(new TextButtonWidget(13 * this.width / 24, startY - scrollY, this.width / 6, buttonHeight, Text.translatable("config." + modId + "." + obj.getActualValue()), button -> cycleEnum(obj, button)), startY);
                this.toDraw(addResetButton(9 * this.width / 12, startY - scrollY, obj), startY);

                startY += buttonHeight + 5;
            }
        }
    }

    protected <T extends Element & Drawable & Selectable> void toDraw(T drawableElement, int startY) {
        if (startY - this.scrollY > 50 && startY - this.scrollY < this.height - 55)
            this.addDrawableChild(drawableElement);
    }

    private int calculateContentHeight(ConfigCategory category, int buttonHeight) {
        int height = -this.height + 110;
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

    private void drawTopCategories(int buttonWidth, int buttonHeight, int startY, int centerX) {
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

    public void drawBottomElements() {
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
        if (!shouldRestart())
            this.restartScreen = true;
        close();
    }

    private void cancel() {
        //if(config != copy)
        this.cancelScreen = true;
        close();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (maxScrollY > 0) {
            scrollY = (short) Math.max(0, Math.min(maxScrollY, scrollY - (int) (amount * 10)));
            this.clearChildren();
            this.init();
            return true;
        }
        super.mouseScrolled(mouseX, mouseY, amount);
        return true;
    }

    @Override
    public void close() {
        super.close();
        if (this.restartScreen) {
            System.out.println("restart");
            MinecraftClient.getInstance().setScreen(new ShouldRestartScreen());
        } else if (this.cancelScreen) {
            System.out.println("cancel");
            MinecraftClient.getInstance().setScreen(new CancelScreen(parent, this));
        } else {
            MinecraftClient.getInstance().setScreen(this.parent);
        }
    }
}
