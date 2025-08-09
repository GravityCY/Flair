package me.gravityio.flair.event;

import cpw.mods.fml.common.eventhandler.Event;

public class SignEvent extends Event {
    public final String[] lines;
    public final char character;
    public final int keyCode;
    public final int line;

    public SignEvent(String[] lines, char c, int keyCode, int line) {
        this.character = c;
        this.keyCode = keyCode;
        this.lines = lines;
        this.line = line;
    }
}
