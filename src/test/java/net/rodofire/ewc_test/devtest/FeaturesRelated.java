package net.rodofire.ewc_test.devtest;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerManager;
import net.rodofire.easierworldcreator.shape.block.LayerPlacer;
import net.rodofire.easierworldcreator.shape.block.ShapePlacer;
import net.rodofire.easierworldcreator.shape.block.gen.SphereGen;
import net.rodofire.easierworldcreator.shape.block.layer.LayerManager;
import net.rodofire.ewc_test.EWCTest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FeaturesRelated {
    public static class FeatureTester extends Feature<DefaultFeatureConfig> {

        public FeatureTester(Codec<DefaultFeatureConfig> configCodec) {
            super(configCodec);
        }

        @Override
        public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
            StructureWorldAccess world = context.getWorld();
            BlockPos pos = context.getOrigin();

            long startTimeCartesian = System.nanoTime();
            //NbtPlacer placer = new NbtPlacer(world, Identifier.of("village/plains/houses/plains_accessory_1"));
            /*SphereGen sphereGen = new SphereGen(world, pos, AbstractBlockShapeBase.PlaceMoment.ANIMATED_OTHER, 32);
            TorusGen torusGen = new TorusGen(world, pos, AbstractBlockShapeBase.PlaceMoment.ANIMATED_OTHER, 20, 50);
            BlockSorter sorter = new BlockSorter(BlockSorter.BlockSorterType.ALONG_AXIS);
            sorter.setCenterPoint(pos);
            StructurePlaceAnimator animator = new StructurePlaceAnimator(world, sorter, StructurePlaceAnimator.AnimatorTime.QUADRATIC_TICKS);
            animator.setBounds(new Pair<>(1,3000));
            animator.setBlocksPerTick(10);
            animator.setTicks(100);
            torusGen.setAnimator(animator);
            torusGen.setBlockLayer(new BlockLayerComparator(List.of(new BlockLayer(List.of(Blocks.BAMBOO_BLOCK.getDefaultState(), Blocks.BAMBOO_MOSAIC.getDefaultState(), Blocks.BAMBOO_PLANKS.getDefaultState()), 1), new BlockLayer(List.of(Blocks.OAK_WOOD.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), Blocks.STRIPPED_OAK_WOOD.getDefaultState(), Blocks.OAK_WOOD.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState()), 1))));
            torusGen.setLayersType(AbstractBlockShapeLayer.LayersType.SURFACE);
            torusGen.setZRotation(45);
            torusGen.setSecondYRotation(30);
            torusGen.place();*/


            SphereGen sphereGen = new SphereGen(pos, 48);
            Map<ChunkPos, LongOpenHashSet> posSet = sphereGen.getShapeCoordinates();
            ShapePlacer placer = new ShapePlacer(world, ShapePlacer.PlaceMoment.WORLD_GEN, pos);
            placer.place(posSet, new LayerManager(
                    LayerManager.Type.SURFACE,
                    new BlockLayerManager(
                            new LayerPlacer(world, LayerPlacer.LayerPlace.RANDOM),
                            Blocks.REDSTONE_BLOCK.getDefaultState(),
                            (short) 1)
            ));


            long endTimeCartesian = (System.nanoTime());
            long durationCartesian = (endTimeCartesian - startTimeCartesian) / 1000000;
            System.out.println("duration : " + durationCartesian + " ms");
            return true;
        }
    }

    public static class ModConfiguredFeatures<FC extends FeatureConfig> {
        public static final RegistryKey<ConfiguredFeature<?, ?>> FEATURE_TESTER_KEY = registerKey("feature_teste_key");

        public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
            register(context, FEATURE_TESTER_KEY, ModFeatures.FEATURE_TESTER, new DefaultFeatureConfig());
        }


        public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
            return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(EWCTest.MOD_ID, name));
        }

        private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                       RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
            context.register(key, new ConfiguredFeature<>(feature, configuration));
        }
    }

    public static class ModFeatures {
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
            return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(EWCTest.MOD_ID, name));
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
    }

    public static class ModBlocks {
        public static final Block FEATURETESTER = Registry.register(Registries.BLOCK, Identifier.of(EWCTest.MOD_ID, "featuretester"), new FeatureBlock(FabricBlockSettings.copyOf(Blocks.OAK_SAPLING), FeaturesRelated.ModConfiguredFeatures.FEATURE_TESTER_KEY));

        public static void registerModBlocks() {
            EWCTest.LOGGER.info("Registering ModBlocks");
        }
    }

    public static class ModItems {
        public static final Item FEATURETESTER = Registry.register(Registries.ITEM, Identifier.of(EWCTest.MOD_ID, "feature_tester"), new AliasedBlockItem(ModBlocks.FEATURETESTER, new Item.Settings()));

        public static void registerModItems() {
            EWCTest.LOGGER.info("Registering ModItems");
        }
    }

    public static class FeatureBlock extends Block implements Fertilizable {
        private final RegistryKey<ConfiguredFeature<?, ?>> featureKey;

        public FeatureBlock(Settings settings, RegistryKey<ConfiguredFeature<?, ?>> featureKey) {
            super(settings);
            this.featureKey = featureKey;
        }

        @Override
        public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
            return true;
        }

        @Override
        public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
            return true;
        }

        @Override
        public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
            System.out.println("rr");
            Optional<RegistryEntry.Reference<ConfiguredFeature<?, ?>>> optional = world.getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE).getEntry(this.featureKey);
            if (optional.isEmpty()) {
                return;
            }
            System.out.println("rrr");
            world.removeBlock(pos, false);
            if (((ConfiguredFeature) ((RegistryEntry) optional.get()).value()).generate(world, world.getChunkManager().getChunkGenerator(), random, pos)) {
                return;
            }
            System.out.println("rrrr");
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
        }
    }
}
