package net.rodofire.easierworldcreator.mixin.world.gen;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListHelper;
import net.rodofire.easierworldcreator.blockdata.blocklist.BlockListManager;
import net.rodofire.easierworldcreator.shape.block.placer.WGShapeHandler;
import net.rodofire.easierworldcreator.shape.block.placer.WGShapePlacerManager;
import net.rodofire.easierworldcreator.util.file.EwcFolderData;
import net.rodofire.easierworldcreator.util.file.FileUtil;
import net.rodofire.easierworldcreator.util.file.LoadChunkShapeInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * mixin to change how are the chunk generated to include multi-chunk features
 */
@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {
    @Unique
    WGShapePlacerManager[] placerManagers;

    @Unique
    PlacedFeature old;

    /**
     * We initialize the placer manager that will define how each piece should be placed
     */
    @Inject(method = "generateFeatures", at = @At(value = "HEAD"))
    private void initShapeHandler(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor, CallbackInfo ci) {
        Set<WGShapePlacerManager> shapePlacer = new HashSet<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                ChunkPos pos = new ChunkPos(chunk.getPos().x + i, chunk.getPos().z + j);
                shapePlacer.add(WGShapeHandler.decodeInformation(pos));
            }
        }

        placerManagers = shapePlacer.toArray(new WGShapePlacerManager[0]);
    }


    @Inject(method = "generateFeatures", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/StructureAccessor;shouldGenerateStructures()Z"))
    private void onGenerationStep(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor, CallbackInfo ci, @Local(ordinal = 2) int k) {
        for (WGShapePlacerManager placerManager : placerManagers) {
            if (placerManager == null) return;

            if (k >= GenerationStep.Feature.values().length) {
                return;
            }

            Path[] paths = placerManager.getToPlace(GenerationStep.Feature.values()[k]);
            for (Path path : paths) {
                world.setCurrentlyGeneratingStructureName(() -> "\n\t-ewc multi-chunk feature generating: \n\t\t- " + path.getFileName() + "\n\t\t - step : generation step");
                BlockListManager manager = BlockListHelper.fromJsonPath(world, path);
                if (manager == null) continue;
                manager.placeAllNDelete(world);
                FileUtil.removeFile(path);
            }
        }
    }


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
    private void onFeatureGenerated(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor, CallbackInfo ci, @Local PlacedFeature placedFeature) {
        for (WGShapePlacerManager placerManager : placerManagers) {
            if (placerManager == null) return;
            Path[] paths = placerManager.getToPlace(old, placedFeature);
            for (Path path : paths) {
                world.setCurrentlyGeneratingStructureName(() -> "\n\t-ewc multi-chunk feature generating: \n\t\t- " + path.getFileName() + "\n\t\t - step : feature");
                BlockListManager comparator = LoadChunkShapeInfo.loadFromJson(world, path);
                LoadChunkShapeInfo.placeStructure(world, comparator);
                FileUtil.removeFile(path);
            }
        }

        old = placedFeature;
    }

    /**
     * At then end, with the fact that multi chunk are multithreaded, some files might not have been placed,
     * that's why we add this mixin at the end of the method to make sure that all the files were placed.
     * We also remove all files related to this chunk generation
     *
     * @param world             the world of the chunk
     * @param chunk             the chunk generated
     * @param structureAccessor unused parameters that need to be there in order for the mixin to work
     * @param ci                unused parameters that need to be there in order for the mixin to work
     */
    @Inject(method = "generateFeatures", at = @At(value = "TAIL"))
    private void endGeneration(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor, CallbackInfo ci) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                ChunkPos pos = new ChunkPos(chunk.getPos().x + i, chunk.getPos().z + j);
                Path basePaths = EwcFolderData.getStructureDataDir(pos);
                try (Stream<Path> paths = Files.list(basePaths)) {
                    paths.forEach(path -> {
                        if (path.toString().endsWith(".json")) {
                            BlockListManager manager = BlockListHelper.fromJsonPath(world, path);
                            if (manager == null) return;

                            world.setCurrentlyGeneratingStructureName(() ->
                                    "\n| ewc multi-chunk feature generating: "
                                            + "\n\t- step: end generation"
                                            + "\n\t- center chunkPos: " + chunk.getPos().toString()
                                            + "\n\t- parent directory: " + path.getParent().getFileName().toString()
                                            + "\n\t- generating: " + path.getFileName()
                            );
                            manager.placeAllNDelete(world);
                            FileUtil.removeFile(path);
                        }
                    });
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
                try {
                    Files.delete(basePaths);
                } catch (IOException e) {
                    e.fillInStackTrace();
                }
            }
        }

        Path managerPath = EwcFolderData.getStructureReference(chunk.getPos());
        try {
            Files.delete(managerPath);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }
}
