package me.gravityio.flair.event.nei;

import codechicken.nei.bookmark.BookmarkItem;
import cpw.mods.fml.common.eventhandler.Event;

import java.util.List;

public class NEIScrollBookmarkEvent extends Event {
    public final BookmarkItem bookmarkItem;
    public final List<BookmarkItem> bookmarkItems;
    public final long shift;

    public NEIScrollBookmarkEvent(BookmarkItem bookmarkItem, List<BookmarkItem> bookmarkItems, long shift) {
        this.bookmarkItem = bookmarkItem;
        this.bookmarkItems = bookmarkItems;
        this.shift = shift;
    }
}
