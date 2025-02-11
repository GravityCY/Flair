package me.gravityio.orbit;

public class Easings {
    public static double easeInOutCubic(double p) {
        return p < 0.5 ? 4 * p * p * p : 1 - Math.pow(-2 * p + 2, 3) / 2;
    }

    public static double easeOut(double p) {
        return 1 - Math.pow(1 - p, 3);
    }

    public static double easeIn(double p) {
        return p * p * p;
    }
}
