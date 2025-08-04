package me.gravityio.flair.condition;

public class SoundCondition<T> {
    private final Expression<T> expression;
    private final ISoundGenerator<T> sound;

    public SoundCondition(Expression<T> expression, ISoundGenerator<T> sound) {
        this.expression = expression;
        this.sound = sound;
    }

    public boolean shouldPlay(T obj) {
        return this.expression.check(obj);
    }

    public SoundData getSound(T obj) {
        return this.sound.getSound(obj);
    }
}
