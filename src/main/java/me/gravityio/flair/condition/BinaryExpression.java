package me.gravityio.flair.condition;

public class BinaryExpression<T> implements Expression<T> {
    private final Expression<T> left;
    private final Expression<T> right;
    private final BinaryOperator operator;

    public BinaryExpression(Expression<T> left, Expression<T> right, BinaryOperator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public boolean check(T obj) {
        return switch (this.operator) {
            case AND -> this.left.check(obj) && this.right.check(obj);
            case OR -> this.left.check(obj) || this.right.check(obj);
        };
    }
}
