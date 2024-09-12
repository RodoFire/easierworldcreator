package net.rodofire.easierworldcreator.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.WanderingTraderManager;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.CatSpawner;
import net.minecraft.world.spawner.PatrolSpawner;
import net.minecraft.world.spawner.PhantomSpawner;
import net.minecraft.world.spawner.Spawner;
import net.rodofire.easierworldcreator.fileutil.FileUtil;
import net.rodofire.easierworldcreator.fileutil.MCAUtil;
import net.rodofire.easierworldcreator.util.ChunkUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * mixin to assure compatibility for already generated worlds.
 */
@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Final
    @Shadow
    protected SaveProperties saveProperties;

    @Final
    @Shadow
    private Executor workerExecutor;

    @Final
    @Shadow
    protected LevelStorage.Session session;

    @Final
    @Shadow
    private CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries;

    @Final
    @Shadow
    private WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory;

    /**
     * This method is injected at the beginning of {@code loadWorld()} and verify if the list of chunks generated under {@code [save name]/chunkList}.
     * If no file are presents and that some region files exist, it gets the chunks in the region files and adds it to the {@code chunkList} folder
     * @param ci unused parameter. It is only there to ensure that the mixin is working.
     */
    @Inject(method = "loadWorld", at = @At("HEAD"))
    @SuppressWarnings("UnreachableCode")
    private void loadWorld(CallbackInfo ci) throws IOException {
        ServerWorldProperties serverWorldProperties = this.saveProperties.getMainWorldProperties();
        if (serverWorldProperties.isInitialized()) {
            boolean bl = this.saveProperties.isDebugWorld();
            WorldGenerationProgressListener worldGenerationProgressListener = this.worldGenerationProgressListenerFactory.create(11);
            GeneratorOptions generatorOptions = this.saveProperties.getGeneratorOptions();
            long l = generatorOptions.getSeed();
            long m = BiomeAccess.hashSeed(l);
            List<Spawner> list = ImmutableList.of(
                    new PhantomSpawner(), new PatrolSpawner(), new CatSpawner(), new ZombieSiegeManager(), new WanderingTraderManager(serverWorldProperties)
            );
            Registry<DimensionOptions> registry = this.combinedDynamicRegistries.getCombinedRegistryManager().get(RegistryKeys.DIMENSION);
            DimensionOptions dimensionOptions = registry.get(DimensionOptions.OVERWORLD);
            ServerWorld serverWorld = new ServerWorld(
                    (MinecraftServer) (Object) this, this.workerExecutor, this.session, serverWorldProperties, World.OVERWORLD, dimensionOptions, worldGenerationProgressListener, bl, m, list, true, null
            );
            AtomicBoolean bl2 = new AtomicBoolean(true);
            AtomicBoolean bl2b = new AtomicBoolean(true);

            Path path = FileUtil.getWorldSavePathDirectory(serverWorld, ChunkUtil.DIRECTORY);
            File file = new File(path.toString());

            if (!file.exists()) {
                Files.createDirectories(path);
                bl2b.set(false);
            }

            if (bl2b.get()) {
                try (Stream<Path> stream = Files.list(path)) {
                    stream.forEach(filePath -> {
                        if (filePath.toString().endsWith(".bin") && filePath.getFileName().toString().startsWith("region")) {
                            bl2.set(false);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (bl2.get() && !MCAUtil.getFiles(serverWorld).isEmpty()) {
                List<Path> pathList = MCAUtil.getFiles(serverWorld);
                for (Path path1 : pathList) {
                    List<ChunkPos> posList = MCAUtil.getChunks(path1);
                    ChunkUtil.writeChunks(posList, serverWorld);
                }

            }
        }
    }
}
