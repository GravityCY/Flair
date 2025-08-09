package me.gravityio.flair.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.gui.GuiTextField;

public class AnvilTypingEvent extends Event {
    public final GuiTextField inputField;
    public final char typedChar;
    public final int keyCode;

    public AnvilTypingEvent(char typedChar, int keyCode, GuiTextField inputField) {
        this.inputField = inputField;
        this.typedChar = typedChar;
        this.keyCode = keyCode;
    }
}
