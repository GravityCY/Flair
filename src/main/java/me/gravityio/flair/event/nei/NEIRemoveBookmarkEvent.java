package me.gravityio.flair.event.nei;

import codechicken.nei.bookmark.BookmarkGrid;
import codechicken.nei.bookmark.BookmarkItem;
import cpw.mods.fml.common.eventhandler.Event;

import java.util.List;

public class NEIRemoveBookmarkEvent extends Event {
    public final int groupId;
    public final List<BookmarkItem> bookmarkItems;
    public final BookmarkGrid bookmarkGrid;

    public NEIRemoveBookmarkEvent(List<BookmarkItem> bookmarkItems, int groupId, BookmarkGrid bookmarkGrid) {
        this.bookmarkItems = bookmarkItems;
        this.groupId = groupId;
        this.bookmarkGrid = bookmarkGrid;
    }
}
