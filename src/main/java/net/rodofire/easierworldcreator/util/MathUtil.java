package net.rodofire.easierworldcreator.util;

import net.minecraft.util.math.random.Random;

public class MathUtil {
    public static int getRandomOpposite() {
        return (Random.create().nextBetween(0, 1) == 0) ? 1 : -1;
    }

    public static boolean getRandomBoolean(float chance) {
        return Random.create().nextFloat() < chance;
    }


    public static int getSign(int a) {
        return (a < 0) ? -1 : 1;
    }

    public static int getSign(double a) {
        return (a < 0) ? -1 : 1;
    }

    public static int getSign(float a) {
        return (a < 0) ? -1 : 1;
    }
}
