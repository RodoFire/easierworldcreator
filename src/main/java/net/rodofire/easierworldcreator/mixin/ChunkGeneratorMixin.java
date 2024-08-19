package net.rodofire.easierworldcreator.mixin;

import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.rodofire.easierworldcreator.nbtutil.LoadChunkShapeInfo;
import net.rodofire.easierworldcreator.shapeutil.BlockList;
import org.apache.http.annotation.Experimental;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {
    @Experimental
    @Inject(method = "generateFeatures", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/ints/IntSet;size()I"), remap = false)
    private void onGenerateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor, CallbackInfo ci) throws IOException {
        List<Path> pathlist = LoadChunkShapeInfo.verifyFiles(world, chunk);
        for (Path path : pathlist) {
            List<BlockList> blockLists = LoadChunkShapeInfo.loadFromJson(world, path);
            LoadChunkShapeInfo.placeStructure(world, blockLists);
            LoadChunkShapeInfo.removeFile(path);
        }
    }
}
