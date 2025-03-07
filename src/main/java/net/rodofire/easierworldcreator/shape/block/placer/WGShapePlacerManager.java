package net.rodofire.easierworldcreator.shape.block.placer;

import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.util.Pair;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.rodofire.easierworldcreator.mixin.world.gen.ChunkGeneratorMixin;
import net.rodofire.easierworldcreator.util.file.EwcFolderData;

import java.nio.file.Path;
import java.util.*;

/**
 * Class to manager all the multi-chunk features of a chunk.
 * This an object of this class is created for each chunk in {@link ChunkGeneratorMixin} when the chunk get generated.
 * The class gives the JSON files path of the moment being generated.
 */
public class WGShapePlacerManager {

    private final Map<PlacedFeature, ShortSet> beforeFeatures = new HashMap<>();
    private final Map<PlacedFeature, ShortSet> afterFeatures = new HashMap<>();

    private final Map<GenerationStep.Feature, ShortSet> steps = new HashMap<>();

    ChunkPos pos;

    /**
     * represents the name of the features.
     */
    private final String[] references;
    private final Set<String> placed = new HashSet<>();

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

    public Path[] getToPlace(StructureWorldAccess worldAccess, PlacedFeature beforeFeature, PlacedFeature featureAfter) {
        ShortSet set = new ShortOpenHashSet();
        if (beforeFeature != null) {
            ShortSet result = beforeFeatures.get(beforeFeature);
            if (result != null) {
                set.addAll(beforeFeatures.get(beforeFeature));
            }
        }

        ShortSet result = afterFeatures.get(featureAfter);
        if (result != null) {
            set.addAll(afterFeatures.get(featureAfter));
        }

        if (set.isEmpty()) {
            return new Path[0];
        }

        return getPath(worldAccess, set);
    }

    public Path[] getToPlace(StructureWorldAccess worldAccess, GenerationStep.Feature feature) {
        ShortSet set = steps.get(feature);
        if (set == null) {
            return new Path[0];
        }
        return getPath(worldAccess, set);
    }

    private Path[] getPath(StructureWorldAccess worldAccess, ShortSet set) {
        Path basePath = EwcFolderData.getStructureDataDir(worldAccess, pos);
        Path[] paths = new Path[set.size()];

        int i = 0;
        for (short index : set) {
            paths[i] = basePath.resolve(references[index] + ".json");
            System.out.println(paths[i]);
            placed.add(references[index]);
            i++;
        }
        return paths;
    }

    public Path[] getLeft(StructureWorldAccess worldAccess) {
        Path basePath = EwcFolderData.getStructureDataDir(worldAccess, pos);
        if(basePath == null) return new Path[0];
        Set<Path> paths = new HashSet<>();
        for (String string : references) {
            if (!placed.contains(string)) {
                paths.add(basePath.resolve(string + ".json"));
            }
        }
        return paths.toArray(new Path[0]);
    }
}
