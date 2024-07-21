package net.rodofire.easierworldcreator;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import net.rodofire.easierworldcreator.datagen.ModWorldGenerator;
import net.rodofire.easierworldcreator.devtest.FeaturesRelated;

public class EasierworldcreatorDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		/*FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ModWorldGenerator::new);*/
	}

	@Override
	public void buildRegistry(RegistryBuilder registryBuilder) {
		//registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, FeaturesRelated.ModConfiguredFeatures::bootstrap);
	}
}
