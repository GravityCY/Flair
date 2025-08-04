package me.gravityio.flair.condition;

import me.gravityio.flair.Flair;
import net.minecraft.util.EnumChatFormatting;

import java.util.function.BiPredicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public enum CompareMethod {
    CONTAINS("contains", (a, b) -> ((String) a).toLowerCase().contains(((String) b).toLowerCase())),
    MATCHES("matches", (a, b) -> {
        try {
            Pattern pattern = Pattern.compile((String) b);
            return pattern.matcher((String) a).matches();
        } catch (PatternSyntaxException e) {
            Flair.sendMessage("Invalid regex '%s'", EnumChatFormatting.RED, b);
            return false;
        }
    }),
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
