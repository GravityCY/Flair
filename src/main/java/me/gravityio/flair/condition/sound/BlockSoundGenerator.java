package me.gravityio.flair.condition.sound;

import me.gravityio.flair.condition.BlockSoundType;
import me.gravityio.flair.data.SoundData;
import me.gravityio.flair.data.BlockInstance;

public class BlockSoundGenerator implements ISoundGenerator<BlockInstance> {
    private final BlockSoundType soundToPlay;
    private final float volume;
    private final float pitch;

    public BlockSoundGenerator(BlockSoundType soundToPlay, float volume, float pitch) {
        this.soundToPlay = soundToPlay;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public SoundData getSound(BlockInstance block) {
        return new SoundData(switch (this.soundToPlay) {
            case BREAK -> block.block.stepSound.getBreakSound();
            case STEP -> block.block.stepSound.getStepResourcePath();
        }, this.volume, this.pitch);
    }
}
