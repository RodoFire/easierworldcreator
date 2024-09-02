package net.rodofire.easierworldcreator.devtest;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import net.rodofire.easierworldcreator.Easierworldcreator;
import net.rodofire.easierworldcreator.shapegen.TorusGen;
import net.rodofire.easierworldcreator.shapeutil.BlockLayer;
import net.rodofire.easierworldcreator.shapeutil.Shape;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class FeaturesRelated {
    /*public static class FeatureTester extends Feature<DefaultFeatureConfig> {

        public FeatureTester(Codec<DefaultFeatureConfig> configCodec) {
            super(configCodec);
        }

        @Override
        public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
            StructureWorldAccess world = context.getWorld();
            Random random = context.getRandom();
            BlockPos pos = context.getOrigin();
            BlockState state = Blocks.REDSTONE_BLOCK.getDefaultState();
            long startTimeCartesian = System.nanoTime();

            TorusGen torus = new TorusGen(world, pos, Shape.PlaceMoment.WORLD_GEN, 8, 35);

            torus.setYrotation(55);
            torus.setSecondxrotation(24);

            torus.setLayersType(Shape.LayersType.SURFACE);

            BlockLayer layer1 = new BlockLayer(Blocks.GRASS_BLOCK.getDefaultState(), 1);
            BlockLayer layer2 = new BlockLayer(Blocks.STONE.getDefaultState(), 5);
            BlockLayer layer3 = new BlockLayer(Blocks.DEEPSLATE.getDefaultState(), 2);
            //torus.setBlockLayers(List.of(layer1, layer2, layer1, layer2, layer1, layer2, layer1, layer2, layer1, layer2, layer1, layer2, layer1, layer2, layer1, layer2, layer1, layer2));
            torus.setBlockLayers(List.of(layer1, layer2, layer3));
            List<Path> pathlist = null;

            try {
                torus.place();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            long endTimeCartesian = (System.nanoTime());
            long durationCartesian = (endTimeCartesian - startTimeCartesian) / 1000000;
            System.out.println("duration : " + durationCartesian + " ms");
            return true;
        }
    }

    public class ModConfiguredFeatures<FC extends FeatureConfig> {
        public static final RegistryKey<ConfiguredFeature<?, ?>> FEATURE_TESTER_KEY = registerKey("feature_teste_key");

        public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
            register(context, FEATURE_TESTER_KEY, ModFeatures.FEATURE_TESTER, new DefaultFeatureConfig());
        }


        public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
            return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new Identifier(Easierworldcreator.MOD_ID, name));
        }

        private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                       RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
            context.register(key, new ConfiguredFeature<>(feature, configuration));
        }
    }

    public class ModFeatures {
        public static Feature<DefaultFeatureConfig> FEATURE_TESTER;

        public static void addFeatures() {
            FEATURE_TESTER = registercustomfeature("feature_tester", new FeatureTester(DefaultFeatureConfig.CODEC));
        }

        private static <C extends FeatureConfig, F extends Feature<C>> F registercustomfeature(String name, F feature) {
            return Registry.register(Registries.FEATURE, name, feature);
        }


    }

    public class ModPLacedFeatures {

        public static final RegistryKey<PlacedFeature> FEATURE_TESTER = registerKey("feature_tester");

        public static void bootstrap(Registerable<PlacedFeature> context) {
            var configuredFeatureRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

            register(context, FEATURE_TESTER, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.FEATURE_TESTER_KEY), RarityFilterPlacementModifier.of(100), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, BiomePlacementModifier.of());
        }

        public static RegistryKey<PlacedFeature> registerKey(String name) {
            return RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(Easierworldcreator.MOD_ID, name));
        }

        private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                     List<PlacementModifier> modifiers) {
            context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
        }

        private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
                                                                                       RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                                                                       PlacementModifier... modifiers) {
            register(context, key, configuration, List.of(modifiers));
        }
    }*/
}
