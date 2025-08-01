package me.gravityio.flair.condition;

import net.minecraft.item.ItemStack;

public class BinaryExpression implements Expression {
    private final Expression left;
    private final Expression right;
    private final BinaryOperator operator;

    public BinaryExpression(Expression left, Expression right, BinaryOperator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public boolean check(ItemStack stack) {
        return switch (this.operator) {
            case AND -> this.left.check(stack) && this.right.check(stack);
            case OR -> this.left.check(stack) || this.right.check(stack);
        };
    }
}
