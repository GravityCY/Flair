package me.gravityio.flair.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.gui.GuiTextField;

public class ChatTypingEvent extends Event {
    public final char typedChar;
    public final int keyCode;
    public final GuiTextField inputField;  // Add this field

    public ChatTypingEvent(GuiTextField inputField, char typedChar, int keyCode) {
        this.inputField = inputField;
        this.typedChar = typedChar;
        this.keyCode = keyCode;
    }
}
