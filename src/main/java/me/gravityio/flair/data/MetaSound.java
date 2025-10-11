package me.gravityio.flair.data;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class MetaSound extends PositionedSoundRecord {
    public final SoundCategory category;
    private final AttenuationType attenuation;

    public MetaSound(String sound, float volume, float pitch, EntityClientPlayerMP thePlayer) {
        super(
                new ResourceLocation(sound), volume, pitch,
                (float) thePlayer.posX, (float) thePlayer.posY, (float) thePlayer.posZ);
        this.attenuation = AttenuationType.NONE;
        this.category = SoundCategory.MASTER;
    }

    @Override
    public AttenuationType getAttenuationType() {
        return this.attenuation;
    }

    public MetaSound(String sound, float volume, float pitch, Vec3 pos, SoundCategory category) {
        super(
                new ResourceLocation(sound), volume, pitch,
                (float) pos.xCoord, (float) pos.yCoord, (float) pos.zCoord);
        this.attenuation = AttenuationType.LINEAR;
        this.category = category;
    }
}
