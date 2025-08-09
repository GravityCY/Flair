package me.gravityio.flair.condition.sound;

import me.gravityio.flair.condition.BlockSoundType;
import me.gravityio.flair.data.SoundData;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemSoundGenerator implements ISoundGenerator<ItemStack> {
    private final BlockSoundType soundToPlay;
    private final float volume;
    private final float pitch;

    public ItemSoundGenerator(BlockSoundType soundToPlay, float volume, float pitch) {
        this.soundToPlay = soundToPlay;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public SoundData getSound(ItemStack stack) {
        if (stack == null) return null;
        if (!(stack.getItem() instanceof ItemBlock itemBlock)) return null;

        Block block = itemBlock.field_150939_a;
        return new SoundData(switch (this.soundToPlay) {
            case BREAK -> block.stepSound.getBreakSound();
            case STEP -> block.stepSound.getStepResourcePath();
        }, this.volume, this.pitch);
    }
}
