package me.gravityio.flair.mixins.vanilla;

import me.gravityio.flair.event.SwingItemEvent;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnusedMixin")
@Mixin(EntityLivingBase.class)
public class SwingItemMixin {

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
            method = "swingItem",
            at = @At("HEAD")
    )
    private void flair$onSwingImmediate(CallbackInfo ci) {
        @SuppressWarnings("DataFlowIssue") EntityLivingBase self = (EntityLivingBase) (Object) this;
        if (!(self instanceof EntityClientPlayerMP player)) return;
        MinecraftForge.EVENT_BUS.post(SwingItemEvent.immediate(player.getHeldItem()));
    }



    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
            method = "swingItem",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.PUTFIELD,
                    target = "Lnet/minecraft/entity/EntityLivingBase;swingProgressInt:I"
            )
    )
    private void flair$onSwingAnimation(CallbackInfo ci) {
        @SuppressWarnings("DataFlowIssue") EntityLivingBase self = (EntityLivingBase) (Object) this;
        if (!(self instanceof EntityClientPlayerMP player)) return;
        MinecraftForge.EVENT_BUS.post(SwingItemEvent.animation(player.getHeldItem()));
    }

}
