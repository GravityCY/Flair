package me.gravityio.flair.condition;

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
}
