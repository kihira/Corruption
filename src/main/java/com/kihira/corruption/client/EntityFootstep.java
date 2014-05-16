package com.kihira.corruption.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

@SideOnly(Side.CLIENT)
public class EntityFootstep extends Entity {

    public final EntityPlayer thePlayer;
    public int fadeOutTimer = 40;
    public float scale = 1F;

    public EntityFootstep(EntityPlayer entityPlayer) {
        super(entityPlayer.getEntityWorld());
        this.thePlayer = entityPlayer;
        this.lastTickPosX = this.prevPosX = this.posX = entityPlayer.posX;
        this.lastTickPosY = this.prevPosY = this.posY = entityPlayer.posY;
        this.lastTickPosZ = this.prevPosZ = this.posZ = entityPlayer.posZ;
        this.ignoreFrustumCheck = true;
    }

    @Override
    public void onUpdate() {
        //If player moves away, being fading out
        if (this.getDistanceToEntity(this.thePlayer) > 1.4 || this.fadeOutTimer < 40) {
            this.fadeOutTimer--;
        }
        else if (this.scale < 1.5F) {
            this.scale += 0.001F;
        }

        if (this.fadeOutTimer < 0) {
            this.setDead();
        }
    }

    @Override
    public void setDead() {
        super.setDead();
    }

    @Override
    protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound var1) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1) {}
}
