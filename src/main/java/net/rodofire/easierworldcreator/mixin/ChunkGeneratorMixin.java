package net.rodofire.easierworldcreator.mixin;

import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.rodofire.easierworldcreator.nbtutil.LoadChunkSapeInfo;
import net.rodofire.easierworldcreator.shapeutil.BlockList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {

    @Inject(method = "generateFeatures", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/ints/IntArraySet;<init>()V"), remap = false)
    private void onGenerateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor, CallbackInfo ci) throws IOException {
        List<Path> pathlist = LoadChunkSapeInfo.verifyFiles(world, chunk);
        for (Path path : pathlist) {
            List<BlockList> blockLists = LoadChunkSapeInfo.loadFromJson(world, path);
            LoadChunkSapeInfo.placeStructure(world, blockLists);
        }
    }
}
