package net.rodofire.easierworldcreator.client.hud.widget;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ImageButtonWidget extends ButtonWidget {
    private final Identifier image;

    public ImageButtonWidget(int x, int y, int width, int height, Identifier image, PressAction onPress) {
        super(x, y, width, height, Text.literal(""), onPress, DEFAULT_NARRATION_SUPPLIER);
        this.image = image;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        context.drawTexture(image, this.getX() + 3, this.getY() + 3, 0, 0, 0, this.width - 6, this.height - 6, this.width - 6, this.height - 6);
    }
}

