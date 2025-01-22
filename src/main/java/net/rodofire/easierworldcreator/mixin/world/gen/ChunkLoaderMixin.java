package net.rodofire.easierworldcreator.mixin.world.gen;

import net.minecraft.util.collection.BoundedRegionArray;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkLoadingManager;
import net.minecraft.world.chunk.AbstractChunkHolder;
import net.minecraft.world.chunk.ChunkGenerationSteps;
import net.minecraft.world.chunk.ChunkLoader;
import net.minecraft.world.chunk.ChunkStatus;
import net.rodofire.easierworldcreator.Ewc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkLoader.class)
public class ChunkLoaderMixin {
    @Inject(method = "create", at = @At(value = "HEAD"))
    private static void print(ChunkLoadingManager chunkLoadingManager, ChunkStatus targetStatus, ChunkPos pos, CallbackInfoReturnable<ChunkLoader> cir) {
        /*if (!targetStatus.isAtMost(ChunkStatus.FEATURES))
            return;
        System.out.println(targetStatus + " :  " + ChunkGenerationSteps.GENERATION.get(targetStatus).getAdditionalLevel(ChunkStatus.EMPTY));*/
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(ChunkLoadingManager chunkLoadingManager, ChunkStatus targetStatus, ChunkPos pos, BoundedRegionArray<AbstractChunkHolder> chunks, CallbackInfo ci) {
        /*if (!targetStatus.isAtMost(ChunkStatus.FEATURES))
            return;
        var a = Thread.currentThread().getStackTrace();
        for (var b : a) {
            Ewc.LOGGER.info(b.toString());
        }*/
    }
}
