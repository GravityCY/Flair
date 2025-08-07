package me.gravityio.flair.mixins.vanilla;

import me.gravityio.flair.Flair;
import me.gravityio.flair.FlairConfig;
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
        if (FlairConfig.INSTANCE.DEFAULT_DROP_SOUND == null) return;
        Flair.INSTANCE.playSound(FlairConfig.INSTANCE.DEFAULT_DROP_SOUND.getSound(this.thePlayer.inventory.getCurrentItem()));
    }
}
