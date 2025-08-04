package me.gravityio.flair.condition;

public interface ISoundGenerator<T> {
    SoundData getSound(T obj);
}
