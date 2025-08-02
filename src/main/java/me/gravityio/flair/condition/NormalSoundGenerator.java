package me.gravityio.flair.condition;

import net.minecraft.item.ItemStack;

public class NormalSoundGenerator implements ISoundGenerator {
    private final String soundToPlay;
    private final float volume;
    private final float pitch;

    public NormalSoundGenerator(String soundToPlay, float volume, float pitch) {
        this.soundToPlay = soundToPlay;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public SoundData getSound(ItemStack stack) {
        return new SoundData(this.soundToPlay, this.volume, this.pitch);
    }
}
