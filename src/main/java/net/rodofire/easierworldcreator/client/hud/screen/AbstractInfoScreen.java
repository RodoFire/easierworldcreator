package net.rodofire.easierworldcreator.client.hud.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public abstract class AbstractInfoScreen extends Screen {
    Screen parent;
    protected AbstractInfoScreen(Text title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }
}
