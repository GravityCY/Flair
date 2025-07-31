package me.gravityio.flair;

import java.util.function.Function;

public class Util {
    public static <Y, T> T make(Y y, Function<Y, T> t) {
        return t.apply(y);
    }
}
