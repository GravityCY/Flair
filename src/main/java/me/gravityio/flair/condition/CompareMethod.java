package me.gravityio.flair.condition;

import java.util.function.BiPredicate;

public enum CompareMethod {
    CONTAINS("contains", (a, b) -> ((String) a).contains((String) b)),
    EQUALS("is", Object::equals),
    NEQUALS("isnt", (a, b) -> !a.equals(b));

    private final String str;
    private final BiPredicate<Object, Object> predicate;

    CompareMethod(String str, BiPredicate<Object, Object> predicate) {
        this.str = str;
        this.predicate = predicate;
    }

    public static CompareMethod fromString(String str) {
        for (CompareMethod type : values()) {
            if (type.str.equals(str)) return type;
        }
        return null;
    }

    public boolean compare(Object a, Object b) {
        return predicate.test(a, b);
    }
}
