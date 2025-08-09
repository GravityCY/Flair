package me.gravityio.flair.condition.sound;

import me.gravityio.flair.data.SoundData;

public interface ISoundGenerator<T> {
    SoundData getSound(T obj);
}
