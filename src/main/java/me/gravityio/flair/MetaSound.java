package me.gravityio.flair;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.ResourceLocation;

public class MetaSound extends PositionedSoundRecord {
    public MetaSound(String sound, float volume, float pitch, EntityClientPlayerMP thePlayer) {
        super(
                new ResourceLocation(sound), volume, pitch,
                (float) thePlayer.posX, (float) thePlayer.posY, (float) thePlayer.posZ);
    }
}
