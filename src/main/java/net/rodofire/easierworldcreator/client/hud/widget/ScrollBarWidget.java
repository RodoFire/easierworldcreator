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
@Environment(EnvType.CLIENT)
public class ScrollBarWidget extends PressableWidget {
    short currentScroll = 0;
    short maxScroll = 0;
    short scrollHeight;
    int startY;
    int endY;
    int buttonColor = 0xFFFFFF;

    boolean bl = false;

    ScrollBarWidget.PressAction pressAction;

    public ScrollBarWidget(int x, int startY, int endY, short maxScroll, ScrollBarWidget.PressAction action, Text message) {
        super(x - 2, startY, 14, endY - startY, message);
        this.startY = startY;
        this.endY = endY;
        this.maxScroll = maxScroll;
        this.pressAction = action;
    }

    public ScrollBarWidget(int x, int startY, int endY, short maxScroll, ScrollBarWidget.PressAction action, Text message, int buttonColor) {
        super(x, startY, 10, endY - startY, message);
        this.startY = startY;
        this.endY = endY;
        this.maxScroll = maxScroll;
        this.buttonColor = buttonColor;
        this.pressAction = action;
    }

    @Override
    public void onPress() {
        this.pressAction.onPress(this);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, int height) {
        int xBound = this.getX() + this.width;
        int yBound = this.getY() + this.height;
        if (isMouseOver(mouseX, mouseY) || bl) {
            bl = true;
            if (deltaY != 0) {
                currentScroll = (short) Math.max(0, Math.min(currentScroll + (int) (deltaY * height / (startY + endY - scrollHeight)), maxScroll));
            }
            return true;
        }
        if (mouseX >= this.getX() && mouseX <= this.getX() + width) {
            return false;
        }
        if (deltaY != 0) {
            currentScroll = (short) Math.max(0, Math.min(currentScroll - (int) (deltaY), maxScroll));
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        bl = false;
        return true;
    }

    public int getScroll() {
        return currentScroll;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (amount != 0) {
            currentScroll = (short) Math.max(0, Math.min(currentScroll - (int) (amount * 10), maxScroll));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public void refresh(int x, int startY, int endY, int maxScroll) {
        this.maxScroll = (short) maxScroll;
        this.setX(x);
        this.startY = startY;
        this.setY(startY);
        this.endY = endY;
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        int adjustedHeight = this.endY - this.startY;
        int adjustedMaxScroll = adjustedHeight;
        int adjustedCurrentScroll = (short) (((float) currentScroll / maxScroll) * adjustedHeight);
        if (maxScroll <= 0) {
            return;
        }
        this.visible = true;

        MinecraftClient minecraftClient = MinecraftClient.getInstance();

        scrollHeight = (short) (((float) (adjustedHeight) / (maxScroll + adjustedHeight)) * (adjustedHeight));


        context.setShaderColor(
                (float) ((buttonColor & 0xFF0000) >> 16) / 256,
                (float) ((buttonColor & 0xFF00) >> 8) / 256,
                (float) (buttonColor & 0xFF) / 256,
                this.alpha);

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        int currentPos = (int) (this.startY + (float) adjustedCurrentScroll / adjustedMaxScroll * (adjustedHeight - scrollHeight));

        this.setY(currentPos);
        this.height = this.scrollHeight;
        context.drawNineSlicedTexture(WIDGETS_TEXTURE, this.getX(), this.getY(), 10, this.height, 20, 4, 200, 20, 0, this.getTextureY());


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
