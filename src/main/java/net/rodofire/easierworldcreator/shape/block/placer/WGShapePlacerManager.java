package net.rodofire.easierworldcreator.shape.block.placer;

import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.util.Pair;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.rodofire.easierworldcreator.util.file.FileUtil;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WGShapePlacerManager {

    private final Map<PlacedFeature, ShortSet> beforeFeatures = new HashMap<>();
    private final Map<PlacedFeature, ShortSet> afterFeatures = new HashMap<>();

    private final Map<GenerationStep.Feature, ShortSet> steps = new HashMap<>();

    ChunkPos pos;

    /**
     * represents the name of the features.
     */
    private String[] references;
    short putReferences = 0;

    public WGShapePlacerManager(ChunkPos pos, int referenceSize) {
        references = new String[referenceSize];
        this.pos = pos;
    }

    public void put(WGShapeData placer) {
        String name = placer.getName();
        references[putReferences] = name;

        if (placer.getFeatureShift().isPresent()) {
            Pair<PlacedFeature, WGShapeData.PlacementShift> feature = placer.getFeatureShift().get();

            if (feature.getRight() == WGShapeData.PlacementShift.AFTER) {
                afterFeatures.computeIfAbsent(feature.getLeft(), (k) -> new ShortOpenHashSet()).add(putReferences);
            } else {
                beforeFeatures.computeIfAbsent(feature.getLeft(), (k) -> new ShortOpenHashSet()).add(putReferences);
            }


        } else if (placer.getStep().isPresent()) {
            steps.computeIfAbsent(placer.getStep().get(), (k) -> new ShortOpenHashSet()).add(putReferences);
        }
        putReferences++;
    }

    public void putAll(List<WGShapeData> placers) {
        for (WGShapeData placer : placers) {
            put(placer);
        }
    }

    public Path[] getToPlace(PlacedFeature beforeFeature, PlacedFeature featureAfter) {
        ShortSet set = new ShortOpenHashSet();
        if (beforeFeature != null)
            set.addAll(beforeFeatures.get(beforeFeature));
        set.addAll(afterFeatures.get(featureAfter));

        return getPath(set);
    }

    public Path[] getToPlace(GenerationStep.Feature feature) {
        return getPath(steps.get(feature));
    }

    private Path[] getPath(ShortSet set) {
        Path basePath = FileUtil.getStructureChunkDirectory(pos);
        Path[] paths = new Path[set.size()];

        int i = 0;
        for (short index : set) {
            paths[i++] = basePath.resolve(references[index] + ".json");
        }
        return paths;
    }
}
