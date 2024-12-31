package net.rodofire.easierworldcreator.client.hud.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("unused")
public class TextButtonWidget extends ButtonWidget {
    int textColor = 0xFFFFFF;
    int buttonColor = 0xFFFFFF;

    public TextButtonWidget(int x, int y, int width, int height, Text text, ButtonWidget.PressAction onPress) {
        super(x, y, width, height, text, onPress, DEFAULT_NARRATION_SUPPLIER);
    }

    public TextButtonWidget(int x, int y, int width, int height, Text text, ButtonWidget.PressAction onPress, int textColor) {
        super(x, y, width, height, text, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.textColor = textColor;
    }

    public TextButtonWidget(int x, int y, int width, int height, Text text, ButtonWidget.PressAction onPress, int textColor, int buttonColor) {
        super(x, y, width, height, text, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.textColor = textColor;
        this.buttonColor = buttonColor;
    }

    public void setColor(int color) {
        this.textColor = color;
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        context.setShaderColor(
                (float) ((buttonColor & 0xFF0000) >> 16) / 256,
                (float) ((buttonColor & 0xFF00) >> 8) / 256,
                (float) (buttonColor & 0xFF) / 256,
                this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.drawNineSlicedTexture(WIDGETS_TEXTURE, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
        context.setShaderColor(
                (float) ((textColor & 0xFF0000) >> 16) / 256,
                (float) ((textColor & 0xFF00) >> 8) / 256,
                (float) (textColor & 0xFF) / 256,
                1.0F
        );

        int i = this.active ? 16777215 : 10526880;
        this.drawMessage(context, client.textRenderer, i | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    private int getTextureY() {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (this.isSelected()) {
            i = 2;
        }

        return 46 + i * 20;
    }
}
