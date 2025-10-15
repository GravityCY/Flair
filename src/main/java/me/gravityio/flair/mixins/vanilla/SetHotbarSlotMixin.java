package me.gravityio.flair.mixins.vanilla;

import me.gravityio.flair.event.HotbarChangedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class SetHotbarSlotMixin {
    @Shadow
    public EntityClientPlayerMP thePlayer;
    @Unique
    private int flair$prevSlot;

    @Inject(
            method = "runTick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/player/InventoryPlayer;currentItem:I"
            )
    )
    private void flair$onSetHotbarSlotBefore(CallbackInfo ci) {
        this.flair$prevSlot = this.thePlayer.inventory.currentItem;
    }

    @Inject(
            method = "runTick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/player/InventoryPlayer;currentItem:I",
                    shift = At.Shift.AFTER
            )
    )
    private void flair$onSetHotbarSlotAfter(CallbackInfo ci) {
        if (this.flair$prevSlot == -1) return;
        MinecraftForge.EVENT_BUS.post(new HotbarChangedEvent(this.thePlayer, this.flair$prevSlot, this.thePlayer.inventory.currentItem));
        this.flair$prevSlot = -1;
    }
}
