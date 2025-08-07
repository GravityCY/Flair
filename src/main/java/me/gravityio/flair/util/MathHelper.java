package me.gravityio.flair.util;

public class MathHelper {
    public static float percent(float a, float b, float value) {
        return (value - a) / (b - a);
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static float map(float value, float fromA, float toA, float fromB, float toB) {
        return lerp(fromB, toB, percent(fromA, toA, value));
    }
}
