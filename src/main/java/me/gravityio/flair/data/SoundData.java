package me.gravityio.flair.data;

import javax.annotation.Nullable;

public class SoundData {
    public String sound;
    public float volume;
    public float pitch;

    public SoundData(String sound) {
        this.sound = sound;
        this.volume = 1.0f;
        this.pitch = 1.0f;
    }

    public SoundData(String sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public SoundData copy(@Nullable String newSound, @Nullable Float newVolume, @Nullable Float newPitch) {
        return new SoundData(
                newSound == null ? this.sound : newSound,
                newVolume == null ? this.volume : newVolume,
                newPitch == null ? this.pitch : newPitch
        );
    }
}
