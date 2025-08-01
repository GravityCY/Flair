package me.gravityio.flair.condition;

import net.minecraft.item.ItemStack;

public class ItemCondition {
    private final Expression expression;
    private final String soundToPlay;
    private final float volume;
    private final float pitch;

    public ItemCondition(Expression expression, String soundToPlay) {
        this.expression = expression;
        this.soundToPlay = soundToPlay;
        this.volume = 1.0F;
        this.pitch = 1.0F;
    }

    public ItemCondition(Expression expression, String soundToPlay, float volume, float pitch) {
        this.expression = expression;
        this.soundToPlay = soundToPlay;
        this.volume = volume;
        this.pitch = pitch;
    }

    public boolean shouldPlay(ItemStack stack) {
        return this.expression.check(stack);
    }

    public String getSound() {
        return this.soundToPlay;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }
}
