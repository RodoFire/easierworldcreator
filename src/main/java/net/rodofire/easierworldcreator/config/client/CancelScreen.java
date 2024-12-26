package net.rodofire.easierworldcreator.config.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.rodofire.easierworldcreator.client.hud.widget.TextButtonWidget;

public class CancelScreen extends Screen {
    private Screen parent;
    private Screen base;

    protected CancelScreen(Screen parent, Screen base) {
        super(Text.translatable("config.ewc.cancel"));
        System.out.println(parent);
        this.parent = parent;
        this.base = base;
    }

    @Override
    protected void init() {
        this.addDrawable(new TextWidget(0, this.height / 3, this.width, 30, Text.translatable("config.ewc.wanna_quit"), this.textRenderer));
        this.addDrawable(new TextButtonWidget(this.width / 2 - this.width / 3, this.height / 2, this.width / 3 - 10, 25, Text.translatable("config.ewc.cancel"), button -> {
            this.clearChildren();
            this.close();
            MinecraftClient.getInstance().setScreen(base);
        }, 0xFFFFFF, 0x00FF00));
        this.addDrawable(new TextButtonWidget(this.width / 2 + 10, this.height / 2, this.width / 3 - 10, 25, Text.translatable("config.ewc.confirm"), button -> {
            this.clearChildren();
            this.close();
            MinecraftClient.getInstance().setScreen(null);
        }, 0xFFFFFF, 0xFF0000));
    }
}
