package me.gravityio.flair.condition;

public enum BinaryOperator {
    AND, OR;

    public static BinaryOperator fromString(String str) {
        return str.equals("and") ? AND : OR;
    }
}
