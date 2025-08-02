package me.gravityio.flair.condition;

import net.minecraft.item.ItemStack;

public interface ISoundGenerator {
    SoundData getSound(ItemStack stack);
}
