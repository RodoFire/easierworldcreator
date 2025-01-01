package net.rodofire.easierworldcreator.client.hud.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("unused")
public class TextButtonWidget extends ButtonWidget {
    Text text;
    int textColor = 0xFFFFFF;
    int buttonColor = 0xFFFFFF;
    private static final ButtonTextures TEXTURES = new ButtonTextures(
            Identifier.of("widget/button"), Identifier.of("widget/button_disabled"), Identifier.of("widget/button_highlighted")
    );

    public TextButtonWidget(int x, int y, int width, int height, Text text, PressAction onPress) {
        super(x, y, width, height, text, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.text = text;
    }

    public TextButtonWidget(int x, int y, int width, int height, Text text, PressAction onPress, int textColor) {
        super(x, y, width, height, text, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.textColor = textColor;
        this.text = text;
    }

    public TextButtonWidget(int x, int y, int width, int height, Text text, PressAction onPress, int textColor, int buttonColor) {
        super(x, y, width, height, text, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.textColor = textColor;
        this.buttonColor = buttonColor;
        this.text = text;
    }

    public void setColor(int color) {
        this.textColor = color;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.drawGuiTexture(
                RenderLayer::getGuiTextured,
                TEXTURES.get(this.active, this.isSelected()),
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight(),
                ColorHelper.fromFloats(
                        this.alpha,
                        (float) ((buttonColor & 0xFF0000) >> 16) / 256,
                        (float) ((buttonColor & 0xFF00) >> 8) / 256,
                        (float) (buttonColor & 0xFF) / 256
                )
        );
        /*context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.text,
                this.getX() + this.getWidth() / 2,
                this.getY() + this.getHeight() / 2,
                ColorHelper.fromFloats(
                        this.alpha,
                        (float) ((textColor & 0xFF0000) >> 16) / 256,
                        (float) ((textColor & 0xFF00) >> 8) / 256,
                        (float) (textColor & 0xFF) / 256
                )

        );
        int i = this.active ? 16777215 : 10526880;*/
        this.drawMessage(
                context,
                client.textRenderer,
                ColorHelper.fromFloats(
                this.alpha,
                (float) ((textColor & 0xFF0000) >> 16) / 256,
                (float) ((textColor & 0xFF00) >> 8) / 256,
                (float) (textColor & 0xFF) / 256
        ));
    }
}
