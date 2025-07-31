package me.gravityio.flair.mixins;

import codechicken.nei.recipe.GuiUsageRecipe;
import me.gravityio.flair.Flair;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GuiUsageRecipe.class)
public class NEIUsageClickMixin {

    @Inject(
        method = "openRecipeGui",
        at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"
        )
    )
    private static void onOpenItem(String inputId, Object[] ingredients, CallbackInfoReturnable<Boolean> cir) {
        if (!inputId.equals("item")) return;
        Flair.INSTANCE.playSound((ItemStack) ingredients[0]);
    }
}
