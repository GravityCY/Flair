package me.gravityio.flair.event.nei;

import cpw.mods.fml.common.eventhandler.Event;

public class NEISearchTypingEvent extends Event {
    public final String oldString;
    public final String newString;

    public NEISearchTypingEvent(String oldString, String newString) {
        this.oldString = oldString;
        this.newString = newString;
    }
}
