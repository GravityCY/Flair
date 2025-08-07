package me.gravityio.flair.condition;

public class NormalSoundGenerator<T> implements ISoundGenerator<T> {
    private final String soundToPlay;
    private final float volume;
    private final float pitch;

    public NormalSoundGenerator(String soundToPlay, float volume, float pitch) {
        this.soundToPlay = soundToPlay;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public SoundData getSound(T unused) {
        return new SoundData(this.soundToPlay, this.volume, this.pitch);
    }
}
