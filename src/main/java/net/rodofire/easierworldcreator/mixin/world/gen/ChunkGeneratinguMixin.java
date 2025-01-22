package net.rodofire.easierworldcreator.mixin.world.gen;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.BoundedRegionArray;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.chunk.*;
import net.rodofire.easierworldcreator.Ewc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(ChunkGenerating.class)
public class ChunkGeneratinguMixin {


    @Inject(method = "generateFeatures", at = @At("HEAD"))
    private static void banana(ChunkGenerationContext context, ChunkGenerationStep step, BoundedRegionArray<AbstractChunkHolder> chunks, Chunk chunk, CallbackInfoReturnable<CompletableFuture<Chunk>> cir) {
        /*ServerWorld serverWorld = context.world();
        var a = Thread.currentThread().getStackTrace();
        for (var b : a){
            Ewc.LOGGER.info(b.toString());
        }
        System.out.println(new ChunkRegion(serverWorld, chunks, step, chunk));*/
    }
}
