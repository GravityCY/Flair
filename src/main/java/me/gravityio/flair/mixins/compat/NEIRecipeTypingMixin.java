package me.gravityio.flair.mixins.compat;

import codechicken.nei.TextField;
import me.gravityio.flair.event.nei.NEIRecipeTypingEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@SuppressWarnings("UnusedMixin")
@Mixin(targets = "codechicken.nei.recipe.GuiRecipe$2", remap = false)
public abstract class NEIRecipeTypingMixin extends TextField {
    public NEIRecipeTypingMixin(String ident) {
        super(ident);
    }

    @Inject(
            method = "onTextChange",
            at = @At("HEAD")
    )
    private void flair$onTyping(String oldText, CallbackInfo ci) {
        if (oldText.equals(super.text())) return;
        MinecraftForge.EVENT_BUS.post(new NEIRecipeTypingEvent(oldText, super.text()));
    }
}
