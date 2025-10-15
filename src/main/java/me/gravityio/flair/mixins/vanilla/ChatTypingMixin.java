package me.gravityio.flair.mixins.vanilla;

import me.gravityio.flair.event.ChatTypingEvent;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnusedMixin")
@Mixin(GuiChat.class)
public class ChatTypingMixin {

    @Shadow
    protected GuiTextField inputField;

    @Inject(
            method = "keyTyped",
            at = @At(
                    "HEAD"
            )
    )
    private void flair$onKeyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new ChatTypingEvent(this.inputField, typedChar, keyCode));
    }
}
