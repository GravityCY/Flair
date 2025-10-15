package me.gravityio.flair.mixins.compat;

import codechicken.nei.bookmark.BookmarkGrid;
import codechicken.nei.bookmark.BookmarkItem;
import codechicken.nei.recipe.Recipe;
import com.llamalad7.mixinextras.sugar.Local;
import me.gravityio.flair.event.nei.NEINewBookmarkEvent;
import me.gravityio.flair.event.nei.NEIRemoveBookmarkEvent;
import me.gravityio.flair.event.nei.NEIScrollBookmarkEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = BookmarkGrid.class, remap = false)
public class NEIBookmarkMixin {
    @Shadow
    protected List<BookmarkItem> bookmarkItems;

    @Inject(
            method = "addItem",
            at = @At("HEAD")
    )
    private void flair$onAddBookmarkItem(BookmarkItem item, boolean animate, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new NEINewBookmarkEvent(item, this.bookmarkItems, (BookmarkGrid) (Object) this));
    }

    @Inject(
            method = "removeGroup",
            at = @At("HEAD")
    )
    private void flair$onRemoveGroup(int groupId, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new NEIRemoveBookmarkEvent(this.bookmarkItems, groupId, (BookmarkGrid) (Object) this));
    }

    @Inject(
            method = "removeRecipe(Lcodechicken/nei/recipe/Recipe$RecipeId;I)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lcodechicken/nei/bookmark/BookmarkGrid;onItemsChanged()V"
            )
    )
    private void flair$onRemoveRecipe1(Recipe.RecipeId recipeId, int groupId, CallbackInfoReturnable<Boolean> cir) {
        MinecraftForge.EVENT_BUS.post(new NEIRemoveBookmarkEvent(this.bookmarkItems, groupId, (BookmarkGrid) (Object) this));

    }

    @Inject(
            method = "removeRecipe(IZ)V",
            at = @At(
                    value = "FIELD",
                    target = "Lcodechicken/nei/bookmark/BookmarkItem;recipeId:Lcodechicken/nei/recipe/Recipe$RecipeId;",
                    ordinal = 0
            )
    )
    private void flair$onRemoveRecipe2(int itemIndex, boolean removeFullRecipe, CallbackInfo ci, @Local BookmarkItem item) {
        MinecraftForge.EVENT_BUS.post(new NEIRemoveBookmarkEvent(this.bookmarkItems, item.groupId, (BookmarkGrid) (Object) this));
    }

    @Inject(
            method = "shiftItemAmount",
            at = @At(
                    value = "FIELD",
                    target = "Lcodechicken/nei/bookmark/BookmarkItem;recipeId:Lcodechicken/nei/recipe/Recipe$RecipeId;",
                    ordinal = 0
            )
    )
    private void flair$onScrollBookmark(int targetItemIndex, long shift, CallbackInfo ci, @Local BookmarkItem bookmark) {
        MinecraftForge.EVENT_BUS.post(new NEIScrollBookmarkEvent(bookmark, this.bookmarkItems, shift));
    }
}
