package net.rodofire.easierworldcreator.mixin.world.gen;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.rodofire.easierworldcreator.config.ewc.EwcConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Mixin to change the radius of chunks at which features can have access.
 * Default is 3x3 but can be increased. The mod config limits it to 9x9, which should be enough anyway.
 * Note that the higher the value, the higher the ram usage.
 */
@Mixin(ChunkStatus.class)
public class ChunkStatusMixin {
    /**
     * the mixin targeting FEATURES status, we change the value depending on the config
     *
     * @param targetStatus the sat
     * @param world        the world used by ChunkRegion
     * @param generator    fields used by ChunkRegion
     * @param chunks       fields used by ChunkRegion
     * @param chunk        fields used by ChunkRegion
     * @param ci           used to cancel the vanilla registration
     */
    @Inject(
            method = "method_51375",
            at = @At(value = "INVOKE", target = "Ljava/util/EnumSet;of(Ljava/lang/Enum;Ljava/lang/Enum;Ljava/lang/Enum;Ljava/lang/Enum;)Ljava/util/EnumSet;", ordinal = 0),
            cancellable = true
    )
    private static void modifyPlacementRadius(
            ChunkStatus targetStatus, ServerWorld world, ChunkGenerator generator, List<Chunk> chunks, Chunk chunk, CallbackInfo ci
    ) {
        int placementRadius = EwcConfig.getFeaturesChunkDistance();
        ChunkRegion chunkRegion = new ChunkRegion(world, chunks, targetStatus, placementRadius);
        generator.generateFeatures(chunkRegion, chunk, world.getStructureAccessor().forRegion(chunkRegion));
        Blender.tickLeavesAndFluids(chunkRegion, chunk);

        ci.cancel();
    }
}
