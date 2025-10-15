package me.gravityio.flair.mixins.vanilla;

import me.gravityio.flair.Flair;
import me.gravityio.flair.event.DropStackEvent;
import me.gravityio.flair.event.SlotClickEvent;
import me.gravityio.flair.FlairMixinData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("UnusedMixin")
@Mixin(PlayerControllerMP.class)
public class SlotClickBackupMixin {

    @Shadow
    @Final
    private Minecraft mc;

    @Inject(

            method = "windowClick",
            at = @At("HEAD")
    )
    private void flair$playHotbarSound(int windowId, int slotId, int button, int mode, EntityPlayer player, CallbackInfoReturnable<ItemStack> cir) {
        if (!Flair.isClientThread()) return;
        FlairMixinData.FROM_WINDOW_CLICK = true;

        var inventorySlots = player.openContainer.inventorySlots;
        MinecraftForge.EVENT_BUS.post(new SlotClickEvent(player.openContainer, slotId, button, mode));

        ItemStack cursorStack = player.inventory.getItemStack();
        ItemStack eventStack = null;
        if ((mode == 0 || mode == 1) && slotId == -999 && cursorStack != null) {
            eventStack = cursorStack;
        }

        if (mode == 4 && slotId >= 0 && slotId < inventorySlots.size()) {
            cursorStack = inventorySlots.get(slotId).getStack();
            if (cursorStack != null) {
                eventStack = cursorStack;
            }
        }

        if (eventStack != null) {
            MinecraftForge.EVENT_BUS.post(new DropStackEvent(eventStack, player));
        }
    }
}
