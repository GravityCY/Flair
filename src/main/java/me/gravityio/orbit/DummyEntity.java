package me.gravityio.orbit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.Session;
import net.minecraft.world.World;

public class DummyEntity extends EntityClientPlayerMP implements OrbitEntity{
    public DummyEntity(Minecraft mc, World world, Session session, NetHandlerPlayClient net, StatFileWriter stats) {
        super(mc, world, session, net, stats);
    }

    @Override
    public ItemStack getHeldItem() {
        return null;
    }

    @Override
    public ItemStack getEquipmentInSlot(int p_71124_1_) {
        return null;
    }

    @Override
    public void setCurrentItemOrArmor(int slotIn, ItemStack itemStackIn) {

    }

    @Override
    public ItemStack[] getLastActiveItems() {
        return new ItemStack[0];
    }

    @Override
    public void setPos(float x, float y, float z) {
        this.lastTickPosX = this.prevPosX = this.posX = x;
        this.lastTickPosY = this.prevPosY = this.posY = y;
        this.lastTickPosZ = this.prevPosZ = this.posZ = z;
    }

    @Override
    public void setRot(float pitch, float yaw) {
        this.prevRotationPitch = this.rotationPitch = pitch;
        this.prevRotationYaw = this.rotationYaw = yaw;
    }

    @Override
    public float getX() {
        return (float) this.posX;
    }

    @Override
    public float getY() {
        return (float) this.posY;
    }

    @Override
    public float getZ() {
        return (float) this.posZ;
    }
}
