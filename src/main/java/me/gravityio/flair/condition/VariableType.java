package me.gravityio.flair.condition;

public interface VariableType<T> {
    String getSyntaxString();
    Object getValue(T stack);
    Object convert(String str);
    CompareMethod[] getComparators();
    boolean isValidComparison(CompareMethod comparator);
}
