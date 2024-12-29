package net.rodofire.easierworldcreator.client.hud.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("unused")
public class ScrollBarWidget extends PressableWidget {
    short currentScroll;
    short maxScroll;
    short height;
    int endY;
    int buttonColor = 0xFFFFFF;

    ScrollBarWidget.PressAction pressAction;

    public ScrollBarWidget(int x, int startY, int endY, int width, int height, short currentScroll, short maxScroll, ScrollBarWidget.PressAction action, Text message) {
        super(x, startY, width, height, message);

        this.endY = endY;
        this.currentScroll = currentScroll;
        this.maxScroll = maxScroll;
        this.pressAction = action;
    }

    public ScrollBarWidget(int x, int startY, int endY, int width, int height, short currentScroll, short maxScroll, ScrollBarWidget.PressAction action, Text message, int buttonColor) {
        super(x, startY, width, height, message);

        this.endY = endY;
        this.currentScroll = currentScroll;
        this.maxScroll = maxScroll;
        this.buttonColor = buttonColor;
        this.pressAction = action;
    }

    @Override
    public void onPress() {
        this.pressAction.onPress(this);
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        int adjustedHeight = this.endY - this.getY();
        int adjustedMaxScroll = maxScroll + adjustedHeight;
        if (adjustedMaxScroll <= adjustedHeight) {
            return;
        }
        this.visible = true;

        MinecraftClient minecraftClient = MinecraftClient.getInstance();

        height = (short) (((float) (adjustedHeight) / adjustedMaxScroll) * (adjustedHeight));

        int adjustedCurrentScroll = (short) (((float) currentScroll / maxScroll) * adjustedHeight);

        context.setShaderColor(
                (float) ((buttonColor & 0xFF0000) >> 16) / 256,
                (float) ((buttonColor & 0xFF00) >> 8) / 256,
                (float) (buttonColor & 0xFF) / 256,
                this.alpha);

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        int currentPos = (int) (this.getY() + (float) adjustedCurrentScroll / adjustedMaxScroll * (adjustedHeight - height));

        context.drawNineSlicedTexture(WIDGETS_TEXTURE, this.getX(), currentPos, 10, this.height, 20, 4, 200, 20, 0, this.getTextureY());


        int i = this.active ? 16777215 : 10526880;
        this.drawMessage(context, minecraftClient.textRenderer, i | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
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


    @Environment(EnvType.CLIENT)
    public interface PressAction {
        void onPress(ScrollBarWidget button);
    }
}
