package me.gravityio.flair.data;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class MetaSound extends PositionedSoundRecord {
    public final SoundCategory category;

    public MetaSound(String sound, float volume, float pitch, EntityClientPlayerMP thePlayer) {
        super(
                new ResourceLocation(sound), volume, pitch,
                (float) thePlayer.posX, (float) thePlayer.posY, (float) thePlayer.posZ);
        this.category = SoundCategory.MASTER;
    }

    public MetaSound(String sound, float volume, float pitch, Vec3 pos, SoundCategory category) {
        super(
                new ResourceLocation(sound), volume, pitch,
                (float) pos.xCoord, (float) pos.yCoord, (float) pos.zCoord);
        this.category = category;
    }
}
