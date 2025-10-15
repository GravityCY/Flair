package me.gravityio.flair.mixins.vanilla;

import me.gravityio.flair.Flair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("UnusedMixin")
@Debug(export = true)
@Mixin(PlayerControllerMP.class)
public class SlotClickBackupMixin {

    // MODE 0 == PICKUP, PLACE | MOUSEBUTTON IS REAL
    // MODE 1 == SHIFT CLICK | MOUSEBUTTON IS REAL
    // MODE 2 == NUMBERS | MOUSEBUTTON IS SLOT INDEX
    // MODE 3 == MIDDLE MOUSE | MOUSEBUTTON IS MIDDLE MOUSE
    // MODE 4 == DROP | MOUSEBUTTON: 0 IS DROP 1, 1 IS DROP ALL
    // MODE 5 == SPREAD | MOUSEBUTTON IS PACKED INT: SPREAD STAGE, AND MOUSE BUTTON
    // MODE 6 == PICKUP ALL | MOUSE BUTTON IS REAL BUT ONLY EVER LEFT CLICK

    @Shadow
    @Final
    private Minecraft mc;

    @Inject(

            method = "windowClick",
            at = @At("HEAD")
    )
    private void flair$playHotbarSound(int windowId, int slotId, int button, int mode, EntityPlayer player, CallbackInfoReturnable<ItemStack> cir) {
        if (!Flair.isClientThread()) return;
        var inventorySlots = player.openContainer.inventorySlots;

        if (slotId < 0 || slotId >= inventorySlots.size()) return;
        ItemStack stack = null;
        switch (mode) {
            case 0: {
                stack = inventorySlots.get(slotId).getStack();
                if (stack == null) stack = player.inventory.getItemStack();
                break;
            }
            case 1, 3: {
                stack = inventorySlots.get(slotId).getStack();
                break;
            }
            case 2: {
                stack = inventorySlots.get(slotId).getStack();
                if (stack == null) stack = player.inventory.getStackInSlot(button);
                break;
            }
            case 5, 6: {
                stack = player.inventory.getItemStack();
                break;
            }
        }

        if (stack == null) return;
        Flair.INSTANCE.playSound(Flair.ITEM_SOUNDS.getSound(stack));
    }
}
