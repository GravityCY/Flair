package me.gravityio.flair;

import cpw.mods.fml.common.registry.GameData;
import me.gravityio.flair.condition.sound.ISoundGenerator;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemSoundRegistry extends SoundRegistry<ItemStack> {

    public ItemSoundRegistry(int expectedConditions, int expectedMappings) {
        super(expectedConditions, expectedMappings);
    }

    public ItemSoundRegistry(int expectedConditions, int expectedMappings, ISoundGenerator<ItemStack> defaultSound) {
        super(expectedConditions, expectedMappings, defaultSound);
    }

    @Override
    public String getName(ItemStack obj) {
        return GameData.getItemRegistry().getNameForObject(obj.getItem());
    }

    @Nullable
    @Override
    public String getMetaname(ItemStack obj) {
        if (obj.getHasSubtypes()) {
            return this.getName(obj) + "@" + obj.getItemDamage();
        }
        return null;
    }
}
