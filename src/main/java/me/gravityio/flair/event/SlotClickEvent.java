package me.gravityio.flair.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.inventory.Container;

public class SlotClickEvent extends Event {
    public final int slot;
    public final int button;
    public final int mode; // Changes meaning very often (Minecraft)
    public final Container container;

    public SlotClickEvent(Container container, int slot, int button, int mode) {
        this.container = container;
        this.slot = slot;
        this.button = button;
        this.mode = mode;
    }
}
