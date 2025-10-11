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

    public static SwingItemEvent start(ItemStack stack) {
        return new SwingItemEvent(stack, SwingType.START);
    }

    public static SwingItemEvent animation(ItemStack stack) {
        return new SwingItemEvent(stack, SwingType.ANIMATION);
    }

    public enum SwingType {
        START, ANIMATION
    }
}
