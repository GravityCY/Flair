package me.gravityio.flair.condition;

import net.minecraft.item.ItemStack;

public interface Expression {
    boolean check(ItemStack stack);
}
