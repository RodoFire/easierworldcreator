package net.rodofire.easierworldcreator.util;

import net.rodofire.easierworldcreator.Easierworldcreator;

/**
 * Own implementation of maths focused on better performance since that precision is not needed that much
 */
public class FastMaths {
    //fast but unprecise, it is useful to use this when using high radius values

    private static final int FAST_TRIGO_TABLE_SIZE = 360;
    private static final int FAST_TABLE_SIZE = 1000;
    private static final double[] cosfastTable = new double[FAST_TRIGO_TABLE_SIZE];
    private static final double[] sinfastTable = new double[FAST_TRIGO_TABLE_SIZE];
    private static final double[] expfastTable = new double[FAST_TABLE_SIZE];

    //took more time but more precise, you should use this when you need to generate big things
    private static final int PRECISE_TRIGO_TABLE_SIZE = 3600;
    private static final int PRECISE_TABLE_SIZE = 10000;
    private static final double[] cospreciseTable = new double[PRECISE_TRIGO_TABLE_SIZE];
    private static final double[] sinpreciseTable = new double[PRECISE_TRIGO_TABLE_SIZE];
    private static final double[] exppreciseTable = new double[PRECISE_TABLE_SIZE];

    /**
     * precompute the cosinus and the sinus tables on launch
     */
    static {
        for (int i = 0; i < FAST_TRIGO_TABLE_SIZE; i++) {
            cosfastTable[i] = Math.cos(Math.toRadians(i));
            sinfastTable[i] = Math.sin(Math.toRadians(i));
        }
        for (int i = 0; i < PRECISE_TRIGO_TABLE_SIZE; i++) {
            double a = (double) i / 10;
            cospreciseTable[i] = Math.cos(Math.toRadians(a));
            sinpreciseTable[i] = Math.sin(Math.toRadians(a));
        }
        for (int i = 0; i < FAST_TABLE_SIZE; i++) {
            expfastTable[i] = Math.exp((double) i / 100);
        }
        for (int i = 0; i < PRECISE_TABLE_SIZE; i++) {
            exppreciseTable[i] = Math.exp(-Math.exp((double) i / 1000));
        }

    }

    /**
     * method to initialize class when launching the mod
     */
    public static void registerMaths() {
        Easierworldcreator.LOGGER.info("registering Maths");
    }

    /**
     * method to get a fast sinus: less precise but faster (0.5° precision)
     *
     * @param x the angle in degrees
     * @return the sinus of the angle
     */
    public static double getFastSin(double x) {
        x = ((x % 360) + 360) % 360;
        return sinfastTable[((int) x + 360) % FAST_TRIGO_TABLE_SIZE];
    }

    /**
     * method to get a fast cosinus: less precise but faster (0.5° precision)
     *
     * @param x the angle in degrees
     * @return the cosinus of the angle
     */
    public static double getFastCos(double x) {
        x = x % 360;
        return cosfastTable[((int) x + 360) % FAST_TRIGO_TABLE_SIZE];
    }

    /**
     * method to get a fast tan: less precise but faster (0.5° precision), based of fast cosinus and sinus
     *
     * @param x the angle in degrees
     * @return the tan of the angle
     */
    public static double getFastTan(double x) {
        if ((int) x % 180 == 90) {
            Easierworldcreator.LOGGER.error("thrown error, tan can't accept values equal to 90° +- 180° (div by 0 error)");
            return 0;
        }
        return sinfastTable[((int) x) % 360] / cosfastTable[((int) x) % 360];
    }

    /**
     * method to get a fast exponential: less precise but faster (0.05 of precision)
     *
     * @param x the value
     * @return the exponential of the angle
     */
    public static double getFastExp(double x) {
        if (x > 10.05) {
            Easierworldcreator.LOGGER.warn("getFastexp() : too big exponential, returning real exp value");
            return Math.exp(x);
        }
        return expfastTable[((int) (x * 100))];
    }

    /**
     * method to get a precise sinus: more precise but slower (0.05° of precision)
     *
     * @param x the angle in degrees
     * @return the sinus of the angle
     */
    public static double getPreciseSin(double x) {
        return sinpreciseTable[((int) x * 10) % PRECISE_TRIGO_TABLE_SIZE];
    }

    /**
     * method to get a precise sinus: more precise but slower (0.05° of precision)
     *
     * @param x the angle in degrees
     * @return the cosinus of the angle
     */
    public static double getPreciseCos(double x) {
        return cospreciseTable[((int) x * 10) % PRECISE_TRIGO_TABLE_SIZE];
    }

    /**
     * method to get a precise tan: more precise but slower (0.05° of precision), based of fast cosinus and sinus
     *
     * @param x the angle in degrees
     * @return the tan of the angle
     */
    public static double getPreciseTan(double x) {
        if ((int) x % 180 == 90) {
            Easierworldcreator.LOGGER.error("getPreciseexp() :thrown error, tan can't accept values equal to 90° +- 180° (div by 0 error)");
            return 0;
        }
        return sinpreciseTable[((int) x * 10) % PRECISE_TRIGO_TABLE_SIZE] / cospreciseTable[((int) x * 10) % PRECISE_TRIGO_TABLE_SIZE];


    }

    /**
     * method to get a fast exponential: less precise but faster (0.005 of precision)
     *
     * @param x the value
     * @return the exp of the angle
     */
    public static double getPreciseExp(double x) {
        if (x > 10.05) {
            Easierworldcreator.LOGGER.warn("too big exponential, returning real exp value");
            return Math.exp(x);
        }
        return exppreciseTable[((int) (x * 1000))];

    }


    /**
     * method to get a length of a 3d vector (x,y,z) with the center point being (0,0,0) with a precision of 0.2f
     *
     * @param x the x coordinate of the vector
     * @param y the y coordinate of the vector
     * @param z the z coordinate of the vector
     * @return the length of the vector
     */
    public static double getLength(int x, int y, int z) {
        return getFastsqrt(x * x + y * y + z * z);
    }

    /**
     * method to get a length of a 3d vector (x,y,z) with the center point being (0,0,0) with the precision wanted
     *
     * @param x         the x coordinate of the vector
     * @param y         the y coordinate of the vector
     * @param z         the z coordinate of the vector
     * @param precision the wanted precision of the result
     * @return the length of the vector
     */
    public static double getLengthWPrecision(int x, int y, int z, float precision) {
        return getFastsqrt(x * x + y * y + z * z, precision);
    }

    /**
     * method to get a length in the case of a 3d vector with floats coordinates:
     * (x,y,z) with the center point being (0,0,0) with a precision of 0.2f
     *
     * @param x the x coordinate of the vector
     * @param y the y coordinate of the vector
     * @param z the z coordinate of the vector
     * @return the length of the vector
     */
    public static double getLength(float x, float y, float z) {
        return getFastsqrt(x * x + y * y + z * z);
    }

    /**
     * method to get a length in the case of a 3d vector with floats coordinates:
     * (x,y,z) with the center point being (0,0,0) with the wanted precision
     *
     * @param x         the x coordinate of the vector
     * @param y         the y coordinate of the vector
     * @param z         the z coordinate of the vector
     * @param precision the wanted precision of the result
     * @return the length of the vector
     */
    public static double getLengthWPrecision(float x, float y, float z, float precision) {
        return getFastsqrt(x * x + y * y + z * z, precision);
    }

    /**
     * method to get a length in the case of a 2d vector:
     * (x,y) with the center point being (0,0) with a precision of 0.2f
     *
     * @param x the x coordinate of the vector
     * @param z the z coordinate of the vector
     * @return the length of the vector
     */
    public static double getLength(int x, int z) {
        return getFastsqrt(x * x + z * z);
    }

    /**
     * method to get a length in the case of a 2d vector:
     * (x,y) with the center point being (0,0) with the wanted precision
     *
     * @param x         the x coordinate of the vector
     * @param z         the z coordinate of the vector
     * @param precision the wanted precision of the result
     * @return the length of the vector
     */
    public static double getLengthWPrecision(int x, int z, float precision) {
        return getFastsqrt(x * x + z * z, precision);
    }

    /**
     * method to get a length in the case of a 2d vector with floats coordinates:
     * (x,y) with the center point being (0,0) with a precision of 0.2f
     *
     * @param x the x coordinate of the vector
     * @param z the z coordinate of the vector
     * @return the length of the vector
     */
    public static double getLength(float x, float z) {
        return getFastsqrt(x * x + z * z);
    }

    /**
     * method to get a length in the case of a 2d vector with floats coordinates:
     * (x,y) with the center point being (0,0) with the wanted precision
     *
     * @param x         the x coordinate of the vector
     * @param z         the z coordinate of the vector
     * @param precision the wanted precision of the result
     * @return the length of the vector
     */
    public static double getLengthWPrecision(float x, float z, float precision) {
        return getFastsqrt(x * x + z * z, precision);
    }

    /**
     * method to get a precise sqrt (0.2 of precision)
     *
     * @param number the angle in degrees
     * @return the sqrt of the number
     */
    public static double getFastsqrt(float number) {
        return getFastsqrt(number, 0.2f);
    }

    /**
     * method to get a precise sqrt with the wanted precision
     *
     * @param number    the angle in degrees
     * @param precision the wanted precision of the result
     * @return the sqrt of the number
     */
    public static double getFastsqrt(float number, float precision) {

        if (number < 0) {
            throw new IllegalArgumentException("Bound must be positive inside of sqrt");
        }

        if (number == 0 || number == 1) {
            return number;
        }

        float x = number;
        float prev;

        do {
            prev = x;
            x = (x + number / x) / 2;
        } while (Math.abs(x - prev) > precision);
        return x;
    }
}
