package net.rodofire.easierworldcreator.config.ewc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.rodofire.easierworldcreator.config.ModClientConfig;
import net.rodofire.easierworldcreator.config.ewc.screen.MultiChunkInfoScreen;

@Environment(EnvType.CLIENT)
public class EwcClientConfig {
    public static final ModClientConfig CLIENT_CONFIG = new ModClientConfig(EwcConfig.MOD_CONFIG);

    public static void init() {
        CLIENT_CONFIG.put("server", CLIENT_CONFIG.getConfig().getCategory("server").getBools().get("multi_chunk_features"), new MultiChunkInfoScreen());
    }
}
