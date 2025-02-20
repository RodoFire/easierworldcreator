package net.rodofire.easierworldcreator.shape.block.placer;

import net.minecraft.util.Pair;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.Optional;

public class WGShapeData {
    private final String name;

    private final Pair<PlacedFeature, PlacementShift> featureShift;
    private final GenerationStep.Feature step;

    // private constructor to centralize the creation of the object
    WGShapeData(String name, PlacementShift shift, PlacedFeature feature, GenerationStep.Feature step) {
        this.name = name;
        this.featureShift = feature == null ? null : new Pair<>(feature, shift);
        this.step = step;
    }

    public static WGShapeData ofStep(GenerationStep.Feature step, String name) {
        return new WGShapeData(name, null, null, step);
    }

    public static WGShapeData ofShift(PlacementShift shift, PlacedFeature feature, String name) {
        return new WGShapeData(name, shift, feature, null);
    }

    public String getName() {
        return name;
    }

    public Optional<Pair<PlacedFeature, PlacementShift>> getFeatureShift() {
        return Optional.ofNullable(featureShift);
    }

    public Optional<GenerationStep.Feature> getStep() {
        return Optional.ofNullable(step);
    }

    @Override
    public String toString() {
        return "AdvancedWGShapePlacer{name='" + name + ", shift=" + featureShift.getRight() + ", feature=" + featureShift.getLeft() + ", step=" + step + "}";
    }

    public enum PlacementShift {
        BEFORE,
        AFTER
    }
}
