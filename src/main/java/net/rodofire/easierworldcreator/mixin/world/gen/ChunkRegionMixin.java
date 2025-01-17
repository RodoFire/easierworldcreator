package net.rodofire.easierworldcreator.mixin.world.gen;

import net.minecraft.util.collection.BoundedRegionArray;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.chunk.AbstractChunkHolder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkGenerationStep;
import net.minecraft.world.chunk.ChunkStatus;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.DefaultBlockList;
import net.rodofire.easierworldcreator.blockdata.blocklist.basic.comparator.AbstractBlockListComparator;
import net.rodofire.easierworldcreator.blockdata.blocklist.ordered.comparator.AbstractOrderedBlockListComparator;
import net.rodofire.easierworldcreator.util.ChunkRegionUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;

@Mixin(ChunkRegion.class)
public class ChunkRegionMixin implements ChunkRegionUtil {

    @Final
    @Shadow
    private Chunk centerPos;

    @Final
    @Shadow
    private ChunkGenerationStep generationStep;
    @Final
    @Shadow
    private BoundedRegionArray<AbstractChunkHolder> chunks;


    @Override
    public Chunk getNullableChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        int i = this.centerPos.getPos().getChebyshevDistance(chunkX, chunkZ);
        ChunkStatus chunkStatus = i >= this.generationStep.directDependencies().size() ? null : this.generationStep.directDependencies().get(i);
        AbstractChunkHolder abstractChunkHolder;
        if (chunkStatus != null) {
            abstractChunkHolder = this.chunks.get(chunkX, chunkZ);
            if (leastStatus.isAtMost(chunkStatus)) {
                Chunk chunk = abstractChunkHolder.getUncheckedOrNull(chunkStatus);
                if (chunk != null) {
                    return chunk;
                }
            }
        } else {
            abstractChunkHolder = null;
        }
        if (!create) {
            return null;
        }
        CrashReport crashReport = CrashReport.create(
                new IllegalStateException("Requested chunk unavailable during world generation"), "Exception generating new chunk"
        );
        CrashReportSection crashReportSection = crashReport.addElement("Chunk request details");
        crashReportSection.add("Requested chunk", String.format(Locale.ROOT, "%d, %d", chunkX, chunkZ));
        crashReportSection.add("Generating status", (CrashCallable<String>)(() -> this.generationStep.targetStatus().getId()));
        crashReportSection.add("Requested status", leastStatus::getId);
        crashReportSection.add(
                "Actual status", (CrashCallable<String>)(() -> abstractChunkHolder == null ? "[out of cache bounds]" : abstractChunkHolder.getActualStatus().getId())
        );
        crashReportSection.add("Maximum allowed status", (CrashCallable<String>)(() -> chunkStatus == null ? "null" : chunkStatus.getId()));
        crashReportSection.add("Dependencies", this.generationStep.directDependencies()::toString);
        crashReportSection.add("Requested distance", i);
        crashReportSection.add("Generating chunk", this.centerPos.getPos()::toString);
        throw new CrashException(crashReport);
    }
}
