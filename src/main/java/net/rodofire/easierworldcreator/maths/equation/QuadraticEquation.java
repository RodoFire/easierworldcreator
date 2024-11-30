package net.rodofire.easierworldcreator.maths.equation;

import net.minecraft.util.Pair;
import net.rodofire.easierworldcreator.maths.FastMaths;

public class QuadraticEquation {
    private float a = 0, b = 0, c = 0, precision = 0.2f;

    public QuadraticEquation(float a, float b, float c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public QuadraticEquation(float a, float b, float c, float precision) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Pair<Float, Float> solve() {
        float delta = b * b - 4 * a * c;
        if (delta < 0) {
            return null;
        }
        float r1 = (-b + FastMaths.getFastSqrt(delta, precision)) / (2 * a);
        float r2 = (-b - FastMaths.getFastSqrt(delta, precision)) / (2 * a);
        return new Pair<>(r1, r2);
    }
}
