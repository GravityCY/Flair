package me.gravityio.flair.mixins;

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

@Mixin(Container.class)
public class SlotMixin {
    @Shadow List<net.minecraft.inventory.Slot> inventorySlots;

    @Inject(
        method = "slotClick",
        at = @At("RETURN")
    )
    private void test(int slotId, int clickedButton, int mode, EntityPlayer player, CallbackInfoReturnable<ItemStack> ci) {
        ItemStack cursorStack = ci.getReturnValue();
        if (slotId < 0 || slotId >= this.inventorySlots.size()) {
            return;
        }

        if (cursorStack == null) {
            cursorStack = this.inventorySlots.get(slotId).getStack();
        }

        Flair.INSTANCE.playSound(cursorStack);
    }
}
