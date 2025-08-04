package me.gravityio.flair.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.entity.AbstractClientPlayer;

public class HotbarChangedEvent extends Event {
    public final int oldSlot;
    public final int newSlot;
    public final AbstractClientPlayer player;

    public HotbarChangedEvent(AbstractClientPlayer player, int oldSlot, int newSlot) {
        this.player = player;
        this.oldSlot = oldSlot;
        this.newSlot = newSlot;
    }
}
