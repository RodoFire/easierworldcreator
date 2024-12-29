package net.rodofire.easierworldcreator.config.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.rodofire.easierworldcreator.EasierWorldCreator;
import net.rodofire.easierworldcreator.client.hud.widget.ImageButtonWidget;
import net.rodofire.easierworldcreator.client.hud.widget.IntegerEntryWidget;
import net.rodofire.easierworldcreator.client.hud.widget.ScrollBarWidget;
import net.rodofire.easierworldcreator.client.hud.widget.TextButtonWidget;
import net.rodofire.easierworldcreator.config.ConfigCategory;
import net.rodofire.easierworldcreator.config.ModConfig;
import net.rodofire.easierworldcreator.config.objects.AbstractConfigObject;
import net.rodofire.easierworldcreator.config.objects.BooleanConfigObject;
import net.rodofire.easierworldcreator.config.objects.EnumConfigObject;
import net.rodofire.easierworldcreator.config.objects.IntegerConfigObject;

import java.util.Iterator;

@Environment(EnvType.CLIENT)
public class DefaultConfigScreen extends AbstractConfigScreen {
    private final Screen parent;

    private int currentCategoryIndex = 0;
    private final int maxCategoriesVisible = 5;
    private Identifier TEXTURE = new Identifier(EasierWorldCreator.MOD_ID, "textures/gui/config_background.png");

    int backgroundHeight = 1080;
    int backgroundWidth = 1920;
    int backgroundShaderColor = 0xABABABFF;
    int backgroundDarkRectangleShaderColor = 0x00000000;

    private int[] lastDimension = {0, 0};

    private short scrollY = 0;
    private short maxScrollY = 0;

    private boolean cancelScreen = false;
    private boolean restartScreen = false;


    private float backgroundShaderGreen = 0.67f;
    private float backgroundShaderRed = 0.67f;
    private float backgroundShaderBlue = 0.67f;
    private float backgroundShaderAlpha = 0.67f;

    public DefaultConfigScreen(Screen parent, ModConfig config, String modId) {
        super(config, modId);
        this.parent = parent;
        this.categories = config.getCategories();
    }

    public DefaultConfigScreen(Screen parent, ModConfig config, String modId, Identifier background, int backgroundWidth, int backgroundHeight) {
        super(config, modId);
        this.parent = parent;
        this.categories = config.getCategories();
        this.TEXTURE = background;
        this.backgroundWidth = backgroundWidth;
        this.backgroundHeight = backgroundHeight;
    }

    public DefaultConfigScreen(Screen parent, ModConfig config, String modId, Identifier background, int backgroundWidth, int backgroundHeight, int backgroundShaderColor, int backgroundDarkRectangleShaderColor) {
        super(config, modId);
        this.parent = parent;
        this.categories = config.getCategories();
        this.TEXTURE = background;
        this.backgroundWidth = backgroundWidth;
        this.backgroundHeight = backgroundHeight;
        this.backgroundShaderColor = backgroundShaderColor;
        initShaderColors();
    }

    public void initShaderColors() {
        this.backgroundShaderRed = (float) ((this.backgroundShaderColor & 0xFF000000) >> 24) / 0xFF;
        this.backgroundShaderGreen = (float) ((this.backgroundShaderColor & 0x00FF0000) >> 16) / 0xFF;
        this.backgroundShaderBlue = (float) ((this.backgroundShaderColor & 0x0000FF00) >> 8) / 0xFF;
        this.backgroundShaderAlpha = (float) (this.backgroundShaderColor & 0x0000000FF) / 0xFF;

    }


    @Override
    protected void init(ConfigCategory category) {
        if (lastDimension[0] != this.width || lastDimension[1] != this.height) {
            this.scrollY = 0;
            lastDimension[0] = this.width;
            lastDimension[1] = this.height;
        }

        cancelScreen = false;
        int centerX = this.width / 2;
        int startY = 13;
        int buttonWidth = this.width / 8;
        int buttonHeight = 20;


        maxScrollY = (short) calculateContentHeight(category, buttonHeight);


        drawTopCategories(buttonWidth, buttonHeight, startY, centerX);

        addElements(category, buttonWidth, buttonHeight, centerX, startY + 27 - scrollY);

        drawBottomElements();
        this.addDrawableChild(new ScrollBarWidget(this.width - 10, 42, this.height - 35, this.width, this.height - 110, scrollY, maxScrollY, button -> System.out.println("hi"), Text.translatable("config.ewc.scroll_bar")));
    }

    public void addElements(ConfigCategory category, int buttonWidth, int buttonHeight, int startX, int startY) {
        boolean bl = false;
        boolean write;
        if (!category.getBools().isEmpty()) {

            if (this.toDraw(new TextWidget(this.width / 12, startY - scrollY, 2 * this.width / 12, buttonHeight, Text.translatable("config.ewc.boolean_category"), this.textRenderer), startY, buttonHeight)) {
                bl = true;
            }
            startY += buttonHeight + 3;
            for (BooleanConfigObject obj : category.getBools().values()) {
                TextWidget textWidget = new TextWidget(5 * this.width / 24, startY - scrollY, 7 * this.width / 24, buttonHeight, Text.translatable("config." + modId + "." + obj.getKey()), this.textRenderer);
                textWidget.setTooltip(Tooltip.of(Text.translatable(obj.getDescriptionKey(modId))));

                write = this.toDraw(textWidget, startY, buttonHeight);

                if (!write && bl)
                    return;
                else if (write && !bl) {
                    bl = true;
                }

                this.toDraw(
                        new TextButtonWidget(
                                14 * this.width / 24, startY - scrollY,
                                3 * this.width / 12, buttonHeight,
                                Text.translatable("config.ewc.boolean." + obj.getActualValue()),
                                button -> toggleBoolean(obj, button),
                                obj.getActualValue() ? 0x00FF00 : 0xFF0000), startY, buttonHeight
                );

                this.toDraw(addResetButton(27 * this.width / 32, startY - scrollY, obj), startY, buttonHeight);

                startY += buttonHeight + 3;
            }
            startY += 4;
        }
        if (!category.getInts().isEmpty()) {
            write = this.toDraw(new TextWidget(this.width / 12, startY - scrollY, 2 * this.width / 12, buttonHeight, Text.translatable("config.ewc.integer_category"), this.textRenderer), startY, buttonHeight);
            if (!write && bl)
                return;
            else if (write && !bl) {
                bl = true;
            }
            startY += buttonHeight + 3;

            for (IntegerConfigObject obj : category.getInts().values()) {
                TextWidget textWidget = new TextWidget(5 * this.width / 24, startY - scrollY, 7 * this.width / 24, buttonHeight, Text.translatable("config." + modId + "." + obj.getKey()), this.textRenderer);
                textWidget.setTooltip(Tooltip.of(Text.translatable(obj.getDescriptionKey(modId))));
                write = this.toDraw(textWidget, startY, buttonHeight);
                if (!write && bl)
                    return;
                else if (write && !bl) {
                    bl = true;
                }

                IntegerEntryWidget integerEntryWidget = new IntegerEntryWidget(this.textRenderer,
                        14 * this.width / 24, startY - scrollY,
                        3 * this.width / 12, buttonHeight,
                        null, Text.literal("config_entry"), String.valueOf(obj.getActualValue()), null, (button, chr) -> this.verifyInteger(obj, button, chr));

                integerEntryWidget.setTooltip(Tooltip.of(Text.of(
                        Text.translatable("config.ewc.min_value").getString() + ": " + obj.getMinValue() + ", " + Text.translatable("config.ewc.max_value").getString() + ": " + obj.getMaxValue()
                )));

                this.toDraw(integerEntryWidget,
                        startY, buttonHeight
                );

                this.toDraw(addResetButton(27 * this.width / 32, startY - scrollY, obj), startY, buttonHeight);

                startY += buttonHeight + 3;
            }
            startY += 4;
        }

        if (!category.getEnums().isEmpty()) {
            write = this.toDraw(new TextWidget(this.width / 12, startY - scrollY, 2 * this.width / 12, buttonHeight, Text.translatable("config.ewc.enum_category"), this.textRenderer), startY, buttonHeight);
            if (!write && bl)
                return;
            else if (write && !bl) {
                bl = true;
            }
            startY += buttonHeight + 3;
            for (EnumConfigObject obj : category.getEnums().values()) {
                TextWidget textWidget = new TextWidget(5 * this.width / 24, startY - scrollY, 7 * this.width / 24, buttonHeight, Text.translatable("config." + modId + "." + obj.getKey()), this.textRenderer);
                textWidget.setTooltip(Tooltip.of(Text.translatable(obj.getDescriptionKey(modId))));
                write = this.toDraw(textWidget, startY, buttonHeight);
                if (!write && bl)
                    return;
                else if (write && !bl) {
                    bl = true;
                }

                this.toDraw(new TextButtonWidget(14 * this.width / 24, startY - scrollY, 3 * this.width / 12, buttonHeight, Text.translatable("config." + modId + "." + obj.getActualValue()), button -> cycleEnum(obj, button)), startY, buttonHeight);
                this.toDraw(addResetButton(27 * this.width / 32, startY - scrollY, obj), startY, buttonHeight);

                startY += buttonHeight + 5;
            }
        }
    }

    protected <T extends Element & Drawable & Selectable> boolean toDraw(T drawableElement, int startY, int height) {
        if (startY - this.scrollY >= 40 && startY + height - this.scrollY < this.height - 35) {
            this.addDrawableChild(drawableElement);
            return true;
        }
        return false;
    }

    private int calculateContentHeight(ConfigCategory category, int buttonHeight) {
        int height = -this.height + 75;
        if (!category.getBools().isEmpty()) {
            height += buttonHeight + 4;
            height += category.getBools().size() * (buttonHeight + 3);
        }
        if (!category.getInts().isEmpty()) {
            height += buttonHeight + 4;
            height += category.getInts().size() * (buttonHeight + 3);
        }
        if (!category.getEnums().isEmpty()) {
            height += buttonHeight + 4;
            height += category.getEnums().size() * (buttonHeight + 3);
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
        int buttonWidth = 3 * this.width / 10;
        int buttonHeight = 20;
        int startX = this.width / 2 - buttonWidth - 10;
        int startY = this.height - 35;

        this.addDrawableChild(new TextButtonWidget(startX, startY, buttonWidth, buttonHeight, Text.translatable("config.ewc.cancel"), button -> this.cancel(), 0xFFFFFF, 0xFF0000));
        this.addDrawableChild(new TextButtonWidget(startX + buttonWidth + 20, startY, buttonWidth, buttonHeight, Text.translatable("config.ewc.save_exit"), button -> this.saveExit(), 0xFFFFFF, 0x00FF00));
    }

    public void openCategory(String name) {
        this.selected = this.indexes.get(name);
        this.scrollY = 0;
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


    public void renderBackground(DrawContext context) {
        assert this.client != null;
        if (this.client.world != null) {
            context.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            this.renderBackgroundTexture(context);
        }
    }

    @Override
    public void renderBackgroundTexture(DrawContext context) {
        float textureRatio = (float) this.backgroundWidth / this.backgroundHeight;
        float screenRatio = (float) this.width / this.height;

        int renderWidth, renderHeight, offsetX, offsetY;

        if (screenRatio > textureRatio) {
            renderWidth = this.width;
            renderHeight = (int) (this.width / textureRatio);
            offsetX = 0;
            offsetY = (renderHeight - this.height) / 2;
        } else {
            renderWidth = (int) (this.height * textureRatio);
            renderHeight = this.height;
            offsetX = (renderWidth - this.width) / 2;
            offsetY = 0;
        }

        context.setShaderColor(backgroundShaderRed, backgroundShaderGreen, backgroundShaderBlue, backgroundShaderAlpha);
        context.drawTexture(
                TEXTURE,
                -offsetX,
                -offsetY,
                0, 0,
                renderWidth,
                renderHeight,
                renderWidth,
                renderHeight
        );

        int darkRectX = 0;
        int darkRectY = 40;
        int darkRectWidth = this.width;
        int darkRectHeight = this.height - 80;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        context.setShaderColor(0.0F, 0.0F, 0.0F, 0.85F);
        context.fill(
                darkRectX,
                darkRectY,
                darkRectX + darkRectWidth,
                darkRectY + darkRectHeight,
                0xD8000000
        );

        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void saveExit() {
        this.saveConfig();
        if (shouldRestart())
            this.restartScreen = true;
        close();
    }

    private void cancel() {
        if (!this.configEquals())
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
        if (scrollY > 0) {
            scrollY = 0;
        }
        super.mouseScrolled(mouseX, mouseY, amount);
        return true;
    }

    @Override
    public void close() {
        super.close();
        if (this.restartScreen) {
            MinecraftClient.getInstance().setScreen(new ShouldRestartScreen());
        } else if (this.cancelScreen) {
            MinecraftClient.getInstance().setScreen(new CancelScreen(parent, this));
        } else {
            MinecraftClient.getInstance().setScreen(this.parent);
        }
    }
}
