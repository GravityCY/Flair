package me.gravityio.flair.event;

import cpw.mods.fml.common.eventhandler.Event;

public class SignEvent extends Event {
    public final String[] lines;
    public final char character;
    public final int line;

    public SignEvent(String[] lines, char c, int line) {
        this.character = c;
        this.lines = lines;
        this.line = line;
    }
}
