package me.gravityio.flair.event;

import cpw.mods.fml.common.eventhandler.Event;

public class NEIRecipeTypingEvent extends Event {
    public final String oldString;
    public final String newString;

    public NEIRecipeTypingEvent(String oldString, String newString) {
        this.oldString = oldString;
        this.newString = newString;
    }
}
