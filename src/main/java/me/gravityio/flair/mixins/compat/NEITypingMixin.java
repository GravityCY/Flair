package me.gravityio.flair.mixins.compat;

import codechicken.nei.SearchField;
import codechicken.nei.TextField;
import me.gravityio.flair.event.nei.NEISearchTypingEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnusedMixin")
@Mixin(value = SearchField.class, remap = false)
public abstract class NEITypingMixin extends TextField {
    public NEITypingMixin(String ident) {
        super(ident);
    }

    @Inject(
            method = "onTextChange",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;length()I"
            )
    )
    private void flair$onTypingEvent(String oldText, CallbackInfo ci) {
        String newText = super.text();
        MinecraftForge.EVENT_BUS.post(new NEISearchTypingEvent(oldText, newText));
    }
}
