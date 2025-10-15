package me.gravityio.flair.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class DropStackEvent extends Event {
    public final ItemStack item;
    public final EntityPlayer player;

    public DropStackEvent(ItemStack item, EntityPlayer player) {
        this.item = item;
        this.player = player;
    }
}
