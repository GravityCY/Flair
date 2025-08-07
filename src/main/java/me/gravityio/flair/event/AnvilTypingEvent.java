package me.gravityio.flair.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.gui.GuiTextField;

public class AnvilTypingEvent extends Event {
    public final char typedChar;
    public final int keyCode;
    public final GuiTextField textField;

    public AnvilTypingEvent(char typedChar, int keyCode, GuiTextField textField) {
        this.typedChar = typedChar;
        this.keyCode = keyCode;
        this.textField = textField;
    }
}
