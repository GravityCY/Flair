package me.gravityio.flair.condition;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class BlockSoundGenerator implements ISoundGenerator{
    private final BlockSoundType soundToPlay;
    private final float volume;
    private final float pitch;

    public BlockSoundGenerator(BlockSoundType soundToPlay, float volume, float pitch) {
        this.soundToPlay = soundToPlay;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public SoundData getSound(ItemStack stack) {
        ItemBlock itemBlock = (ItemBlock) stack.getItem();
        Block block = itemBlock.field_150939_a;
        return new SoundData(switch (this.soundToPlay) {
            case BREAK -> block.stepSound.getBreakSound();
            case STEP -> block.stepSound.getStepResourcePath();
        }, this.volume, this.pitch);
    }
}
