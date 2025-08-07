package me.gravityio.flair.mixins;

import me.gravityio.flair.MetaSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@SuppressWarnings("UnusedMixin")
@Mixin(SoundManager.class)
public class SoundCategoryMixin
{

    @SuppressWarnings("UnresolvedMixinReference")
    @ModifyVariable(
            method = "playSound",
            at = @At(value = "STORE"),
            ordinal = 0
    )
    private SoundCategory modCategory(SoundCategory category, ISound sound) {
        if (!(sound instanceof MetaSound)) return category;
        return SoundCategory.MASTER;
    }
}
