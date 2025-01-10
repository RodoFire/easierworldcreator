package net.rodofire.easierworldcreator.client.hud.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.rodofire.easierworldcreator.EasierWorldCreator;
import net.rodofire.easierworldcreator.client.hud.screen.AbstractInfoScreen;

/**
 * class that allows to open an info screen
 */
public class InfoButtonWidget extends ImageButtonWidget {
    public <T extends AbstractInfoScreen> InfoButtonWidget(int x, int y, int width, int height, T screen) {
        super(x, y, width, height, new Identifier(EasierWorldCreator.MOD_ID, "textures/gui/info_widget.png"), button -> MinecraftClient.getInstance().setScreen(screen));
    }
}
