package me.gravityio.flair.mixins.vanilla;

import me.gravityio.flair.event.SignEvent;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnusedMixin")
@Mixin(GuiEditSign.class)
public class SignEventMixin {
    @Shadow
    private TileEntitySign tileSign;

    @Shadow
    private int editLine;

    @Inject(
            method = "keyTyped",
            at = @At(
                    "TAIL"
            )
    )
    private void flair$onKeyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new SignEvent(this.tileSign.signText, typedChar, keyCode, this.editLine));
    }
}
