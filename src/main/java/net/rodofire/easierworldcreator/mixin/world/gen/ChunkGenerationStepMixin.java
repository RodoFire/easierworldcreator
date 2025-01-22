package net.rodofire.easierworldcreator.mixin.world.gen;

import net.minecraft.world.chunk.ChunkGenerating;
import net.minecraft.world.chunk.ChunkGenerationStep;
import net.minecraft.world.chunk.ChunkGenerationSteps;
import net.minecraft.world.chunk.ChunkStatus;
import net.rodofire.easierworldcreator.config.ewc.EwcConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to change the radius of chunks at which features can have access.
 * Default is 3x3 but can be increased. The mod config limits it to 9x9, which should be enough anyway.
 * Note that the higher the value, the higher the ram usage.
 */
@Mixin(ChunkGenerationSteps.class)
public class ChunkGenerationStepMixin {
    /**
     * Modify the block placement radius in chunk generation features based on the config
     *
     * @return the radius at which the blockStates have access
     */
   /* @ModifyArg(
            method = "method_60535",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/ChunkGenerationStep$Builder;blockStateWriteRadius(I)Lnet/minecraft/world/chunk/ChunkGenerationStep$Builder;"),
            index = 0

    )
    private static int modifyPlacementRadius(int blockStateWriteRadius) {
        return 10/*EwcConfig.getFeaturesChunkDistance();
    }

    @ModifyArg(
            method = "method_60535",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/ChunkGenerationStep$Builder;dependsOn(Lnet/minecraft/world/chunk/ChunkStatus;I)Lnet/minecraft/world/chunk/ChunkGenerationStep$Builder;", ordinal = 0),
            index = 1

    )
    private static int modifyStructureAccessRadius(int level) {
        return /*(int) (Math.pow(2 * EwcConfig.getFeaturesChunkDistance(), 2) - 1)8;
    }

    @ModifyArg(
            method = "method_60535",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/ChunkGenerationStep$Builder;dependsOn(Lnet/minecraft/world/chunk/ChunkStatus;I)Lnet/minecraft/world/chunk/ChunkGenerationStep$Builder;", ordinal = 1),
            index = 1

    )
    private static int modifyCarversAccessRadius(int level) {
        System.out.println("Modifying carvers access radius: Current value -> " + level);
        return 1;
    }

    @ModifyArg(method = "method_60521", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/ChunkGenerationStep$Builder;dependsOn(Lnet/minecraft/world/chunk/ChunkStatus;I)Lnet/minecraft/world/chunk/ChunkGenerationStep$Builder;"), index = 1)
    private static int modifyCarversCarversAccessRadius(int level) {
        return 3;
    }

    @Inject(method = "method_60522", at = @At("HEAD"), cancellable = true)
    private static void modifyCavesAccessRadius(ChunkGenerationStep.Builder builder, CallbackInfoReturnable<ChunkGenerationStep.Builder> cir) {
        cir.setReturnValue(builder.dependsOn(ChunkStatus.FEATURES, 8).task(ChunkGeneratingMixin::invokeInitializeLight));
    }*/
}
