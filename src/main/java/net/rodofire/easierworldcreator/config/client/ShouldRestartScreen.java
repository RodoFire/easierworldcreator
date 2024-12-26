package net.rodofire.easierworldcreator.config.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.rodofire.easierworldcreator.client.hud.widget.TextButtonWidget;

public class ShouldRestartScreen extends Screen {
    protected ShouldRestartScreen() {
        super(Text.translatable("screen.config.restart"));
    }

    @Override
    protected void init() {
        super.init();
        this.addDrawable(new TextButtonWidget(this.width / 2 - this.width / 8, this.height / 2, this.width / 4, 30, Text.translatable("config.ewc.accept_restart"), (button) -> {
            System.out.println("close");
            MinecraftClient.getInstance().stop();
        }, 0xFFFFFF, 0xFF8000));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        MultilineText.create(textRenderer, Text.translatable("config.ewc.restart_message"), this.width / 2).drawCenterWithShadow(context, this.width / 2, this.height / 3, 30, 0xFFFFFF);

    }

    @Override
    public void close() {
        MinecraftClient.getInstance().stop();
    }
}
