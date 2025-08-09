package me.gravityio.flair.condition.variable;

import me.gravityio.flair.condition.CompareMethod;

public interface VariableType<T> {
    String getSyntaxString();
    Object getValue(T stack);
    Object convert(String str);
    CompareMethod[] getComparators();
    boolean isValidComparison(CompareMethod comparator);
}
