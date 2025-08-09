package me.gravityio.flair.condition;

import me.gravityio.flair.condition.variable.VariableType;

public class IfExpression<T> implements Expression<T> {
    private final VariableType<T> variable;
    private final CompareMethod compareMethod;
    private final Object argument;

    public IfExpression(VariableType<T> variable, CompareMethod compareMethod, Object argument) {
        this.variable = variable;
        this.compareMethod = compareMethod;
        this.argument = argument;
    }

    public boolean check(T obj) {
        return this.compareMethod.compare(this.variable.getValue(obj), this.argument);
    }
}
