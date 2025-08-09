package me.gravityio.flair.event;


import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.item.ItemStack;

public class SwingItemEvent extends Event {
    public ItemStack stack;
    public SwingType type;

    public SwingItemEvent(ItemStack stack, SwingType type) {
        this.stack = stack;
        this.type = type;
    }

    public static SwingItemEvent immediate(ItemStack stack) {
        return new SwingItemEvent(stack, SwingType.IMMEDIATE);
    }

    public static SwingItemEvent animation(ItemStack stack) {
        return new SwingItemEvent(stack, SwingType.ANIMATION_START);
    }

    public enum SwingType {
        IMMEDIATE, ANIMATION_START
    }
}
