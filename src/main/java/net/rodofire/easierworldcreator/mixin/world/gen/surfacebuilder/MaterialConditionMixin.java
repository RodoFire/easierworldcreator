package net.rodofire.easierworldcreator.mixin.world.gen.surfacebuilder;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import net.rodofire.easierworldcreator.world.gen.surfacebuilder.ExtendedMaterialRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MaterialRules.MaterialCondition.class)
public interface MaterialConditionMixin {
    @Inject(method = "registerAndGetDefault", at = @At("TAIL"))
    private static void addCustomConditions(Registry<MapCodec<? extends MaterialRules.MaterialCondition>> registry, CallbackInfoReturnable<MapCodec<? extends MaterialRules.MaterialCondition>> cir) {
        SurfaceRulesMixin.register(registry, "full_noise_threshold", ExtendedMaterialRules.FullNoiseThresholdMaterialCondition.CODEC);
        SurfaceRulesMixin.register(registry, "random", ExtendedMaterialRules.RandomMaterialCondition.CODEC);
    }
}
