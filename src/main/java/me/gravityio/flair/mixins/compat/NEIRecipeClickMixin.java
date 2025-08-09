package me.gravityio.flair.mixins.compat;

import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiRecipe;
import me.gravityio.flair.Flair;
import me.gravityio.flair.event.NEIOpenRecipeEvent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("UnusedMixin")
@Mixin(value = GuiCraftingRecipe.class)
public class NEIRecipeClickMixin {

    @Inject(
        method = "createRecipeGui",
        at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"
        )
    )
    private static void onOpenItem(String outputId, boolean _open, Object[] results, CallbackInfoReturnable<GuiRecipe<?>> cir) {
        if (!outputId.equals("item")) return;
        MinecraftForge.EVENT_BUS.post(new NEIOpenRecipeEvent((ItemStack) results[0]));
    }
}
