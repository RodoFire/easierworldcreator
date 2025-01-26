package net.rodofire.easierworldcreator.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;

@SuppressWarnings("unused")
public class ExtendedMaterialRules {
    public static MaterialRules.MaterialCondition fullNoiseThresold(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> noise, double minThreshold, double maxThreshold) {
        return new FullNoiseThresholdMaterialCondition(noise, minThreshold, maxThreshold);
    }

    /**
     * method to place a block randomly
     *
     * @param name  the name of the condition
     * @param bound the bound that determines if the block is placed. Must be between {@code 0f - 1f}.
     *              <ul>
     *              <li> 0f means that the value will always be false
     *              <li> 0.5f means that the block will be placed 50% of the time
     *              <li> 1f means that the block will always be placed
     *              </ul>
     * @return the material condition
     */
    public static MaterialRules.MaterialCondition random(Identifier name, float bound) {
        return new RandomMaterialCondition(name, bound);
    }


    public record FullNoiseThresholdMaterialCondition(
            RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> noise, double minThreshold,
            double maxThreshold) implements MaterialRules.MaterialCondition {

        public static final CodecHolder<FullNoiseThresholdMaterialCondition> CODEC = CodecHolder.of(
                RecordCodecBuilder.mapCodec(
                        instance -> instance.group(
                                        RegistryKey.createCodec(RegistryKeys.NOISE_PARAMETERS).fieldOf("noise").forGetter(FullNoiseThresholdMaterialCondition::noise),
                                        Codec.DOUBLE.fieldOf("min_threshold").forGetter(FullNoiseThresholdMaterialCondition::minThreshold),
                                        Codec.DOUBLE.fieldOf("max_threshold").forGetter(FullNoiseThresholdMaterialCondition::maxThreshold)
                                )
                                .apply(instance, FullNoiseThresholdMaterialCondition::new)
                )
        );

        @Override
        public CodecHolder<? extends MaterialRules.MaterialCondition> codec() {
            return CODEC;
        }

        public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
            final DoublePerlinNoiseSampler doublePerlinNoiseSampler = materialRuleContext.noiseConfig.getOrCreateSampler(this.noise);

            class NoiseThresholdPredicate extends MaterialRules.HorizontalLazyAbstractPredicate {
                NoiseThresholdPredicate() {
                    super(materialRuleContext);
                }

                @Override
                protected boolean test() {
                    double d = doublePerlinNoiseSampler.sample(this.context.blockX, this.context.blockY, this.context.blockZ);
                    return d >= FullNoiseThresholdMaterialCondition.this.minThreshold && d <= FullNoiseThresholdMaterialCondition.this.maxThreshold;
                }
            }

            return new NoiseThresholdPredicate();
        }
    }

    public record RandomMaterialCondition(Identifier name, float bound) implements MaterialRules.MaterialCondition {
        public static final CodecHolder<RandomMaterialCondition> CODEC = CodecHolder.of(
                RecordCodecBuilder.mapCodec(
                        instance -> instance.group(
                                        Identifier.CODEC.fieldOf("random_name").forGetter(RandomMaterialCondition::name),
                                        Codec.FLOAT.fieldOf("bound").forGetter(RandomMaterialCondition::bound)
                                )
                                .apply(instance, RandomMaterialCondition::new)
                )
        );

        @Override
        public CodecHolder<? extends MaterialRules.MaterialCondition> codec() {
            return CODEC;
        }

        @Override
        public MaterialRules.BooleanSupplier apply(MaterialRules.MaterialRuleContext materialRuleContext) {
            class RandomPredicate extends MaterialRules.FullLazyAbstractPredicate {
                RandomPredicate() {
                    super(materialRuleContext);
                }

                @Override
                protected boolean test() {
                    final RandomSplitter randomSplitter = materialRuleContext.noiseConfig.getOrCreateRandomDeriver(name);
                    Random random = randomSplitter.split(this.context.blockX, this.context.blockY, this.context.blockZ);
                    return random.nextFloat() < bound;
                }
            }
            return new RandomPredicate();
        }
    }
}
