package me.gravityio.flair.condition;

import me.gravityio.flair.Flair;
import net.minecraft.util.EnumChatFormatting;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public enum CompareMethod {
    CONTAINS("contains", (obj, stringPattern) -> {
        if (obj instanceof String) {
            return ((String) obj).toLowerCase().contains(
                    ((String) stringPattern).toLowerCase());
        } else if (obj instanceof String[]) {
            for (String s : (String[]) obj) {
                if (s.toLowerCase().contains(
                        ((String) stringPattern).toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }),

    MATCHES("matches", (obj, stringPattern) -> {
        if (obj instanceof String) {
            try {
                Pattern pattern = Pattern.compile((String) stringPattern);
                return pattern.matcher((String) obj).matches();
            } catch (PatternSyntaxException e) {
                Flair.sendMessage("Invalid regex '%s'", EnumChatFormatting.RED, stringPattern);
                return false;
            }
        } else if (obj instanceof String[]) {
            for (String s : (String[]) obj) {
                try {
                    Pattern pattern = Pattern.compile((String) stringPattern);
                    if (pattern.matcher(s).matches()) {
                        return true;
                    }
                } catch (PatternSyntaxException e) {
                    Flair.sendMessage("Invalid regex '%s'", EnumChatFormatting.RED, stringPattern);
                    return false;
                }
            }
        }
        return false;
    }),

    EQUALS("is", (obj, test) -> {
        if (obj instanceof Object[]) {
            for (Object o : (Object[]) obj) {
                if (o.equals(test)) {
                    return true;
                }
            }
        } else {
            return Objects.equals(obj, test);
        }

        return false;
    }),

    NEQUALS("isnt", (obj, test) -> {
        if (obj instanceof Object[]) {
            for (Object o : (Object[]) obj) {
                if (Objects.equals(o, test)) {
                    return true;
                }
            }
        } else {
            return Objects.equals(obj, test);
        }

        return false;
    });

    public final String str;
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
