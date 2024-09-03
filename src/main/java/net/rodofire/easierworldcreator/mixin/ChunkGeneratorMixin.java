package net.rodofire.easierworldcreator.mixin;

import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.rodofire.easierworldcreator.fileutil.FileUtil;
import net.rodofire.easierworldcreator.fileutil.LoadChunkShapeInfo;
import net.rodofire.easierworldcreator.shapeutil.BlockList;
import net.rodofire.easierworldcreator.util.ChunkUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * mixin to change how are the chunk generated to include multi-chunk features
 */
@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {
    /**
     * <p>The method verifies
     * if there are some files under {@code [save name]/generated/easierworldcreator/structures/chunk_[chunk.x]_[chunk.z].}
     * <p>If yes, for every file, it will get every BlockList of the JSON file.
     * Then it will place every block of the BlockList and will then remove the file.
     * When everything is done, it will remove the chunk folder
     * <p>Else, it will continue the normal generation.
     *
     * @param world             the world of the chunk
     * @param chunk             the chunk generated
     * @param structureAccessor unused parameters that need to be there in order for the mixin to work
     * @param ci                unused parameters that need to be there in order for the mixin to work
     * @throws IOException
     */
    @Inject(method = "generateFeatures", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/ints/IntSet;size()I"), remap = false)
    private void onGenerateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor, CallbackInfo ci) throws IOException {
        List<Path> pathlist = LoadChunkShapeInfo.verifyFiles(world, chunk);
        if (!pathlist.isEmpty()) {
            for (Path path : pathlist) {
                List<BlockList> blockLists = LoadChunkShapeInfo.loadFromJson(world, path);
                LoadChunkShapeInfo.placeStructure(world, blockLists);
                FileUtil.removeFile(path);
            }
        }
        FileUtil.removeGeneratedChunkDirectory(chunk, world);
    }

    /**
     * when the features are finished to be generated, the chunkPos will be added to the list of generated chunk under {@code [save name]/chunkList}
     *
     * @param world             the world of the chunk
     * @param chunk             the chunk generated
     * @param structureAccessor unused parameters that need to be there in order for the mixin to work
     * @param ci                unused parameters that need to be there in order for the mixin to work
     */
    @Inject(method = "generateFeatures", at = @At("TAIL"), remap = false)
    private void addGeneratedChunk(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor, CallbackInfo ci) {
        ChunkUtil.addChunk(chunk.getPos(), world);
    }
}
