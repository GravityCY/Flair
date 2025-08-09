package me.gravityio.flair;

import cpw.mods.fml.common.registry.GameData;
import me.gravityio.flair.condition.sound.ISoundGenerator;
import me.gravityio.flair.data.BlockInstance;

import javax.annotation.Nullable;

public class BlockSoundRegistry extends SoundRegistry<BlockInstance> {

    public BlockSoundRegistry(int expectedConditions, int expectedMappings) {
        super(expectedConditions, expectedMappings);
    }

    public BlockSoundRegistry(int expectedConditions, int expectedMappings, ISoundGenerator<BlockInstance> defaultSound) {
        super(expectedConditions, expectedMappings, defaultSound);
    }

    @Override
    public String getName(BlockInstance obj) {
        return GameData.getBlockRegistry().getNameForObject(obj.block);
    }

    @Nullable
    @Override
    public String getMetaname(BlockInstance obj) {
        return this.getName(obj)  + "@" + obj.meta;
    }
}
