package me.gravityio.flair.condition;

public interface Expression<T> {
    boolean check(T stack);
}
