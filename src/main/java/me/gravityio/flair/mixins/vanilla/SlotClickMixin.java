package me.gravityio.flair.mixins.vanilla;

import me.gravityio.flair.Flair;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@SuppressWarnings("UnusedMixin")
@Mixin(Container.class)
public class SlotClickMixin {
    @Shadow List<net.minecraft.inventory.Slot> inventorySlots;

    // MODE 0 == PICKUP, PLACE | MOUSEBUTTON IS REAL
    // MODE 1 == SHIFT CLICK | MOUSEBUTTON IS REAL
    // MODE 2 == NUMBERS | MOUSEBUTTON IS SLOT INDEX
    // MODE 3 == MIDDLE MOUSE | MOUSEBUTTON IS MIDDLE MOUSE
    // MODE 4 == DROP | MOUSEBUTTON: 0 IS DROP 1, 1 IS DROP ALL
    // MODE 5 == SPREAD | MOUSEBUTTON IS PACKED INT: SPREAD STAGE, AND MOUSE BUTTON
    // MODE 6 == PICKUP ALL | MOUSE BUTTON IS REAL BUT ONLY EVER LEFT CLICK

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(

            method = "slotClick",
            at = @At("HEAD")
    )
    private void flair$playHotbarSound(int slotId, int button, int mode, EntityPlayer player, CallbackInfoReturnable<ItemStack> ci) {
        if (!Flair.isClientThread()) return;

        if (slotId < 0 || slotId >= this.inventorySlots.size()) return;
        ItemStack stack = null;
        switch (mode) {
            case 0: {
                stack = this.inventorySlots.get(slotId).getStack();
                if (stack == null) stack = player.inventory.getItemStack();
                break;
            }
            case 1, 3: {
                stack = this.inventorySlots.get(slotId).getStack();
                break;
            }
            case 2: {
                stack = this.inventorySlots.get(slotId).getStack();
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
