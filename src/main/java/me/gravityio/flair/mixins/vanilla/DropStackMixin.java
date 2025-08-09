package me.gravityio.flair.mixins.vanilla;

import me.gravityio.flair.Flair;
import me.gravityio.flair.config.FlairConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnusedMixin")
@Mixin(Minecraft.class)
public class DropStackMixin {

    @Shadow
    private EntityClientPlayerMP thePlayer;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
            method = "runTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/entity/EntityClientPlayerMP;dropOneItem(Z)Lnet/minecraft/entity/item/EntityItem;"
            )
    )
    private void onDropItems(CallbackInfo ci) {
        Flair.INSTANCE.playSound(Flair.DROP_SOUNDS.getSound(this.thePlayer.inventory.getCurrentItem()));
    }
}
