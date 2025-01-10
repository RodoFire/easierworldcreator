package net.rodofire.easierworldcreator.mixin;

import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.comparator.DefaultBlockListComparator;
import net.rodofire.easierworldcreator.fileutil.FileUtil;
import net.rodofire.easierworldcreator.fileutil.LoadChunkShapeInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.List;

/**
 * mixin to change how are the chunk generated to include multi-chunk features
 */
@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {
    @Unique
    boolean bl = true;

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
    @Inject(method = "generateFeatures", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/StructureWorldAccess;setCurrentlyGeneratingStructureName(Ljava/util/function/Supplier;)V", ordinal = 1))
    private void onGenerateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor, CallbackInfo ci) {
        if (bl) {
            bl = false;
            List<Path> pathlist = LoadChunkShapeInfo.getWorldGenFiles(world, chunk);

            if (!pathlist.isEmpty()) {
                for (Path path : pathlist) {
                    world.setCurrentlyGeneratingStructureName(() -> "ewc multi-chunk feature generating: " + path.getFileName());
                    DefaultBlockListComparator comparator = LoadChunkShapeInfo.loadFromJson(world, path);
                    LoadChunkShapeInfo.placeStructure(world, comparator);
                    FileUtil.removeFile(path);
                }
            }
            FileUtil.removeGeneratedChunkDirectory(chunk, world);
        }
    }

    /**
     * At then end, with the fact that multi chunk are multithreaded, some files might not have been placed,
     * that's why we add this mixin at the end of the method to make sure that all the files were placed
     *
     * @param world             the world of the chunk
     * @param chunk             the chunk generated
     * @param structureAccessor unused parameters that need to be there in order for the mixin to work
     * @param ci                unused parameters that need to be there in order for the mixin to work
     */
    @Inject(method = "generateFeatures", at = @At(value = "TAIL"))
    private void endGeneration(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor, CallbackInfo ci) {

        List<Path> pathlist = LoadChunkShapeInfo.getWorldGenFiles(world, chunk);

        if (!pathlist.isEmpty()) {
            for (Path path : pathlist) {
                DefaultBlockListComparator comparator = LoadChunkShapeInfo.loadFromJson(world, path);
                LoadChunkShapeInfo.placeStructure(world, comparator);
                FileUtil.removeFile(path);
            }
        }
        FileUtil.removeGeneratedChunkDirectory(chunk, world);

    }
}
