package me.gravityio.flair.condition;

import net.minecraft.item.ItemStack;

public interface VariableType<T> {
    Object getValue(T stack);
    Object convert(String str);
}
