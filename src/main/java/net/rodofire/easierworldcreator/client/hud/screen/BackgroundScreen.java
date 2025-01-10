package net.rodofire.easierworldcreator.client.hud.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public abstract class BackgroundScreen extends Screen {
    protected Identifier TEXTURE = null;
    int backgroundHeight = 32;
    int backgroundWidth = 32;

    int backgroundShaderColor = 0x3F3F3FFF;

    protected BackgroundScreen(Text title) {
        super(title);
    }
    protected BackgroundScreen(Text title,Identifier background, int backgroundWidth, int backgroundHeight) {
        super(title);
        this.TEXTURE = background;
        this.backgroundHeight = backgroundHeight;
        this.backgroundWidth = backgroundWidth;
    }

    protected BackgroundScreen(Text title,Identifier background, int backgroundWidth, int backgroundHeight, int backgroundShaderColor) {
        super(title);
        this.TEXTURE = background;
        this.backgroundHeight = backgroundHeight;
        this.backgroundWidth = backgroundWidth;
        this.backgroundShaderColor = backgroundShaderColor;
    }

    @Override
    public void renderBackgroundTexture(DrawContext context) {
        if (TEXTURE == null) {
            super.renderBackgroundTexture(context);
            return;
        }

        float textureRatio = (float) this.backgroundWidth / this.backgroundHeight;
        float screenRatio = (float) this.width / this.height;

        int renderWidth, renderHeight, offsetX, offsetY;
        context.setShaderColor(
                (float) ((this.backgroundShaderColor & 0xFF000000) >>> 24) / 0xFF,
                (float) ((this.backgroundShaderColor & 0x00FF0000) >> 16) / 0xFF,
                (float) ((this.backgroundShaderColor & 0x0000FF00) >> 8) / 0xFF,
                (float) (this.backgroundShaderColor & 0x0000000FF) / 0xFF
        );
        if (textureRatio == 1) {
            context.drawTexture(
                    TEXTURE, 0, 0,
                    0, 0, this.width, this.height, this.backgroundWidth, this.backgroundHeight
            );
        } else {
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
        }
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }


    public void renderDarkRectangle(DrawContext context, int x, int y, int x2, int y2, int color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        context.setShaderColor(
                (float) ((color & 0x00FF0000) >> 16) / 256,
                (float) ((color & 0x0000FF00) >> 8) / 256,
                (float) (color & 0x000000FF) / 256,
                (float) (color >>> 24) / 256
        );
        context.fill(x, y, x2, y2, color);
    }
}
