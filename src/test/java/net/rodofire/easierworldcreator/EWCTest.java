package net.rodofire.easierworldcreator;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.rodofire.easierworldcreator.devtest.FeaturesRelated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EWCTest implements ModInitializer {
    public static final String MOD_ID = "ewc-test";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        FeaturesRelated.ModItems.registerModItems();
        FeaturesRelated.ModBlocks.registerModBlocks();
        FeaturesRelated.ModFeatures.addFeatures();
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.PLAINS), GenerationStep.Feature.TOP_LAYER_MODIFICATION, FeaturesRelated.ModPLacedFeatures.FEATURE_TESTER);

        EasierWorldCreator.init();
        LOGGER.info("starting ewc test");
    }
}
