package me.gravityio.flair.condition;

import net.minecraft.item.ItemStack;

public class ItemCondition {
    private final Expression expression;
    private final ISoundGenerator sound;

    public ItemCondition(Expression expression, ISoundGenerator sound) {
        this.expression = expression;
        this.sound = sound;
    }

    public boolean shouldPlay(ItemStack stack) {
        return this.expression.check(stack);
    }

    public SoundData getSound(ItemStack stack) {
        return this.sound.getSound(stack);
    }
}
