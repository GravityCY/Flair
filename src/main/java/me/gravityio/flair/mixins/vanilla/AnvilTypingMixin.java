package me.gravityio.flair.mixins.vanilla;


import me.gravityio.flair.event.AnvilTypingEvent;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnusedMixin")
@Mixin(GuiRepair.class)
public class AnvilTypingMixin
{
    @Shadow
    private GuiTextField field_147091_w;

    @Inject(
            method = "keyTyped",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiTextField;textboxKeyTyped(CI)Z"
            )
    )
    private void flair$onKeyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new AnvilTypingEvent(typedChar, keyCode, this.field_147091_w));
    }

}
