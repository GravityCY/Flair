package me.gravityio.flair.util;

import java.util.function.Function;

public class Utils {
    public static <Y, T> T make(Y y, Function<Y, T> t) {
        return t.apply(y);
    }
}
