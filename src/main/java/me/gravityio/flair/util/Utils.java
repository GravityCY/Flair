package me.gravityio.flair.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Utils {
    public static <Y, T> T make(Y y, Function<Y, T> t) {
        return t.apply(y);
    }

    public static List<String> getClassHierarchy(Class<?> clazz) {
        List<String> hierarchy = new ArrayList<>();
        while (clazz != null) {
            hierarchy.add(clazz.getName());
            clazz = clazz.getSuperclass();
        }
        return hierarchy;
    }
}
