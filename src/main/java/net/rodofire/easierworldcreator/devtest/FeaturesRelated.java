package net.rodofire.easierworldcreator.devtest;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.rodofire.easierworldcreator.Easierworldcreator;
import net.rodofire.easierworldcreator.worldgenutil.GenSpiral;

import java.util.List;

public class FeaturesRelated {
    public static class FeatureTester extends Feature<DefaultFeatureConfig> {

        public FeatureTester(Codec<DefaultFeatureConfig> configCodec) {
            super(configCodec);
        }

        @Override
        public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
            System.out.println("ok");
            StructureWorldAccess world = context.getWorld();
            Random random = context.getRandom();
            BlockPos pos = context.getOrigin();
            BlockState state = Blocks.REDSTONE_BLOCK.getDefaultState();
            long startTimeCartesian = System.nanoTime();
            GenSpiral.ElipsoidSpiral.generateElipsoidFullSpiral(10,5,65,2,0,10,world,pos,true,List.of(Blocks.REDSTONE_BLOCK.getDefaultState()));
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
}
