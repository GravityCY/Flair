package me.gravityio.flair.event.nei;

import codechicken.nei.bookmark.BookmarkGrid;
import codechicken.nei.bookmark.BookmarkItem;
import cpw.mods.fml.common.eventhandler.Event;

import java.util.List;

public class NEINewBookmarkEvent extends Event {
    public final BookmarkItem bookmarkItem;
    public final List<BookmarkItem> bookmarkItems;
    public final BookmarkGrid bookmarkGrid;

    public NEINewBookmarkEvent(BookmarkItem bookmarkItem, List<BookmarkItem> bookmarkItems, BookmarkGrid grid) {
        this.bookmarkItem = bookmarkItem;
        this.bookmarkItems = bookmarkItems;
        this.bookmarkGrid = grid;
    }
}
