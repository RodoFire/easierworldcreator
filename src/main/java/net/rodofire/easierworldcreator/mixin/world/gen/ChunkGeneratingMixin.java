package net.rodofire.easierworldcreator.mixin.world.gen;

import net.minecraft.util.collection.BoundedRegionArray;
import net.minecraft.world.chunk.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.concurrent.CompletableFuture;

@Mixin(ChunkGenerating.class)
public interface ChunkGeneratingMixin {
    @Invoker("initializeLight")
    static CompletableFuture<Chunk> invokeInitializeLight(
            ChunkGenerationContext context,
            ChunkGenerationStep step,
            BoundedRegionArray<AbstractChunkHolder> chunks,
            Chunk chunk
    ) {
        throw new AssertionError();
    }
}
