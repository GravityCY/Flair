package me.gravityio.flair.mixins.vanilla;

import me.gravityio.flair.Flair;
import me.gravityio.flair.event.DropStackEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnusedMixin")
@Mixin(Minecraft.class)
public class DropStackMixin {

    @Shadow
    public EntityClientPlayerMP thePlayer;

    @Inject(
            method = "runTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/entity/EntityClientPlayerMP;dropOneItem(Z)Lnet/minecraft/entity/item/EntityItem;"
            )
    )
    private void onDropItems(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new DropStackEvent(this.thePlayer.inventory.getCurrentItem(), this.thePlayer));

    }
}
