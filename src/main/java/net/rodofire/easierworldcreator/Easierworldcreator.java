package net.rodofire.easierworldcreator;

import net.fabricmc.api.ModInitializer;

import net.rodofire.easierworldcreator.devtest.BlockRelated;
import net.rodofire.easierworldcreator.devtest.FeaturesRelated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Easierworldcreator implements ModInitializer {
	public static final String MOD_ID = "easierworldcreator";
    public static final Logger LOGGER = LoggerFactory.getLogger("easierworldcreator");

	@Override
	public void onInitialize() {
		BlockRelated.ModItems.registerModItems();
		BlockRelated.ModBlocks.registerModBlocks();

		FeaturesRelated.ModFeatures.addFeatures();

		LOGGER.info("Starting Easierworldcreator");
	}
}