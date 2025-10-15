package me.gravityio.flair.mixins.vanilla;

import me.gravityio.flair.event.SwingItemEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class SwingItemMixin {

    @Mixin(Minecraft.class)
    public static class SwingStartMixin {
        @Inject(
                method = "func_147116_af",
                at = @At(
                        value = "INVOKE",
                        target = "Lnet/minecraft/client/entity/EntityClientPlayerMP;swingItem()V"
                )
        )
        private void flair$onSwingStart(CallbackInfo ci) {
            MinecraftForge.EVENT_BUS.post(SwingItemEvent.start(Minecraft.getMinecraft().thePlayer.getHeldItem()));
        }
    }

    @Mixin(EntityLivingBase.class)
    public static class SwingAnimMixin {
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
}
