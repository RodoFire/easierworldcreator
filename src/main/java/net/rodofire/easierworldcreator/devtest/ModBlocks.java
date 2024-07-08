package net.rodofire.easierworldcreator.devtest;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.rodofire.easierworldcreator.Easierworldcreator;

import java.util.Optional;

public class ModBlocks {
    public static final Block FEATURETESTER = Registry.register(Registries.BLOCK, new Identifier(Easierworldcreator.MOD_ID, "featuretester"), new FeatureBlock(FabricBlockSettings.copyOf(Blocks.OAK_SAPLING), ModConfiguredFeatures.FEATURE_TESTER_KEY));
}

class ModItems {
    public static final Item FEATURETESTER = Registry.register(Registries.ITEM, new Identifier(Easierworldcreator.MOD_ID, "feature_tester"), new AliasedBlockItem(ModBlocks.FEATURETESTER, new Item.Settings()));
}

class FeatureBlock extends Block implements Fertilizable {
    private final RegistryKey<ConfiguredFeature<?, ?>> featureKey;

    public FeatureBlock(Settings settings, RegistryKey<ConfiguredFeature<?, ?>> featureKey) {
        super(settings);
        this.featureKey = featureKey;
    }


    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        Optional<RegistryEntry.Reference<ConfiguredFeature<?, ?>>> optional = world.getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE).getEntry(this.featureKey);
        if (optional.isEmpty()) {
            return;
        }
        world.removeBlock(pos, false);
        if (((ConfiguredFeature) ((RegistryEntry) optional.get()).value()).generate(world, world.getChunkManager().getChunkGenerator(), random, pos)) {
            return;
        }
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
    }
}
