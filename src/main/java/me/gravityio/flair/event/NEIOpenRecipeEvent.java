package me.gravityio.flair.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.item.ItemStack;

public class NEIOpenRecipeEvent extends Event {
    public ItemStack stack;

    public NEIOpenRecipeEvent(ItemStack stack) {
        this.stack = stack;
    }
}
