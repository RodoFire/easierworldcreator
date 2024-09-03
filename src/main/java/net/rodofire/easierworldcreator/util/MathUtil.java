package net.rodofire.easierworldcreator.util;

import net.minecraft.util.math.random.Random;

/**
 * util math class
 */
public class MathUtil {
    /**
     * method to get a random opposite with a 50% chance
     * @return a random opposite
     */
    public static int getRandomOpposite() {
        return (Random.create().nextBetween(0, 1) == 0) ? 1 : -1;
    }

    /**
     * method to get a random opposite with the wanted chance
     * @param chance the chance of -1 being selected
     * @return a random opposite
     */
    public static int getRandomOpposite(float chance) {
        return (Random.create().nextFloat() <= chance) ? 1 : -1;
    }

    /**
     * method to get a random boolean
     * @param chance the chance of true being selected
     * @return the random boolean
     */
    public static boolean getRandomBoolean(float chance) {
        return Random.create().nextFloat() < chance;
    }

    /**
     * method to get the sign of a number
     * @param a the int to compare
     * @return the sign of the int
     */
    public static int getSign(int a) {
        return (a < 0) ? -1 : 1;
    }

    /**
     * method to get the sign of a number
     * @param a the double to compare
     * @return the sign of the double
     */
    public static int getSign(double a) {
        return (a < 0) ? -1 : 1;
    }

    /**
     * method to get the sign of a number
     * @param a the float to compare
     * @return the sign of the float
     */
    public static int getSign(float a) {
        return (a < 0) ? -1 : 1;
    }
}
