package net.rodofire.easierworldcreator.util;

import net.minecraft.util.math.random.Random;

import java.util.List;

public class ListUtil {
    public static <T> T getRandomElement(final List<T> list) {
        return list.get(Random.create().nextBetween(0, list.size() - 1));
    }
}
