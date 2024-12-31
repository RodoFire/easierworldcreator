package net.rodofire.easierworldcreator.mixin;

import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.DefaultBlockList;
import net.rodofire.easierworldcreator.fileutil.FileUtil;
import net.rodofire.easierworldcreator.fileutil.LoadChunkShapeInfo;
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
     */
    @Inject(method = "generateFeatures", at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;"))
    private void onGenerateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor, CallbackInfo ci) throws IOException {
        List<Path> pathlist = LoadChunkShapeInfo.verifyFiles(world, chunk);
        if (!pathlist.isEmpty()) {
            for (Path path : pathlist) {
                List<DefaultBlockList> defaultBlockLists = LoadChunkShapeInfo.loadFromJson(world, path);
                LoadChunkShapeInfo.placeStructure(world, defaultBlockLists);
                FileUtil.removeFile(path);
            }
        }
        FileUtil.removeGeneratedChunkDirectory(chunk, world);
    }
}
