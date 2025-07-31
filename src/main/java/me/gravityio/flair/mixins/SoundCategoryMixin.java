package me.gravityio.flair.mixins;

import me.gravityio.flair.Flair;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SoundManager.class)
public class SoundCategoryMixin
{
    @ModifyVariable(
            method = "playSound",
            at = @At(
                    value = "FIELD",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private SoundCategory modCategory(SoundCategory category) {
        if (!Flair.OUR_SOUND) return category;
        Flair.OUR_SOUND = false;
        return SoundCategory.MASTER;
    }
}
