package me.gravityio.flair.mixins;

import me.gravityio.flair.event.HotbarChangedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryPlayer.class)
public class ChangeSlotMixin {
    @Unique
    int flair$prevSlot;

    @Shadow
    public int currentItem;

    @Inject(
            method = "changeCurrentItem",
            at = @At(
                    "HEAD"
            )
    )
    private void onScrollSlotStart(int delta, CallbackInfo ci) {
        this.flair$prevSlot = this.currentItem;
    }

    @Inject(
            method = "changeCurrentItem",
            at = @At(
                    "TAIL"
            )
    )
    private void onScrollSlotEnd(int delta, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new HotbarChangedEvent(Minecraft.getMinecraft().thePlayer, this.flair$prevSlot, this.currentItem));
    }
}
