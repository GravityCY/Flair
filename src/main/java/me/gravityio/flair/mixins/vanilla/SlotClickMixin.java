package me.gravityio.flair.mixins.vanilla;

import me.gravityio.flair.Flair;
import me.gravityio.flair.event.DropStackEvent;
import me.gravityio.flair.event.SlotClickEvent;
import me.gravityio.flair.FlairMixinData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@SuppressWarnings("UnusedMixin")
@Mixin(Container.class)
public class SlotClickMixin {
    @Shadow
    public List<net.minecraft.inventory.Slot> inventorySlots;

    @Inject(

            method = "slotClick",
            at = @At("HEAD")
    )
    private void flair$playHotbarSound(int slotId, int button, int mode, EntityPlayer player, CallbackInfoReturnable<ItemStack> ci) {
        if (!Flair.isClientThread()) return;
        if (FlairMixinData.FROM_WINDOW_CLICK) {
            FlairMixinData.FROM_WINDOW_CLICK = false;
            return;
        }

        MinecraftForge.EVENT_BUS.post(new SlotClickEvent((Container) (Object) this, slotId, button, mode));

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
