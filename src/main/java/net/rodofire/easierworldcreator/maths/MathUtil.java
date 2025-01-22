package net.rodofire.easierworldcreator.maths;

import net.minecraft.util.math.random.Random;

/**
 * util math class
 */
@SuppressWarnings("unused")
public class MathUtil {
    /**
     * method to get a random opposite with a 50% chance
     *
     * @return a random opposite
     */
    public static int getRandomOpposite() {
        return (Random.create().nextBetween(0, 1) == 0) ? 1 : -1;
    }

    /**
     * method to get a random opposite with the wanted chance
     *
     * @param chance the chance of -1 being selected
     * @return a random opposite
     */
    public static int getRandomOpposite(float chance) {
        return (Random.create().nextFloat() <= chance) ? 1 : -1;
    }

    /**
     * method to get a random boolean
     *
     * @param chance the chance of true being selected
     * @return the random boolean
     */
    public static boolean getRandomBoolean(float chance) {
        return Random.create().nextFloat() < chance;
    }

    /**
     * method to get the sign of a number
     *
     * @param a the int to compare
     * @return the sign of the int
     */
    public static int getSign(int a) {
        return (a < 0) ? -1 : 1;
    }

    /**
     * method to get the sign of a number
     *
     * @param a the double to compare
     * @return the sign of the double
     */
    public static int getSign(double a) {
        return (a < 0) ? -1 : 1;
    }

    /**
     * method to get the sign of a number
     *
     * @param a the float to compare
     * @return the sign of the float
     */
    public static int getSign(float a) {
        return (a < 0) ? -1 : 1;
    }

    /**
     * Calculates the absolute distance between two float values.
     *
     * @param a the first float value
     * @param b the second float value
     * @return the sum of the absolute values of {@code a} and {@code b}
     */
    public static float absDistance(float a, float b) {
        return Math.abs(a) + Math.abs(b);
    }

    /**
     * Calculates the absolute distance between two integer values.
     *
     * @param a the first integer value
     * @param b the second integer value
     * @return the sum of the absolute values of {@code a} and {@code b}
     */
    public static int absDistance(int a, int b) {
        return Math.abs(a) + Math.abs(b);
    }

    /**
     * Calculates the total absolute distance for an array of float values.
     *
     * @param floats an array of float values
     * @return the sum of the absolute values of the elements in {@code floats}
     */
    public static float absDistance(float... floats) {
        float distance = 0;
        for (float aFloat : floats) {
            distance += Math.abs(aFloat);
        }
        return distance;
    }

    /**
     * Calculates the total absolute distance for an array of integer values.
     *
     * @param ints an array of integer values
     * @return the sum of the absolute values of the elements in {@code ints}
     */
    public static int absDistance(int... ints) {
        int distance = 0;
        for (int anInt : ints) {
            distance += Math.abs(anInt);
        }
        return distance;
    }
}
