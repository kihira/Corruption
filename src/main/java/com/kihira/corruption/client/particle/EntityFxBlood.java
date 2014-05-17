package com.kihira.corruption.client.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;

public class EntityFxBlood extends EntityFX {

    private final EntityPlayer entityPlayer;
    private boolean shouldAlwaysFall;

    public EntityFxBlood(EntityPlayer player) {
        super(player.worldObj, player.posX + 0.1F + (player.getRNG().nextFloat() / 10F), player.boundingBox.minY + 0.5F + player.getRNG().nextFloat(), player.posZ + 0.1F + (player.getRNG().nextFloat() / 10F));
        this.particleRed = 1F;
        this.particleBlue = this.particleGreen = 0.1F;
        this.setSize(0.01F, 0.01F);
        this.particleGravity = 0.06F;
        this.particleMaxAge = (int)(32.0D / (Math.random() * 0.8D + 0.2D));
        this.motionX = this.motionY = this.motionZ = 0.0D;
        this.entityPlayer = player;
        this.shouldAlwaysFall = false;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.motionY -= (double) this.particleGravity;

        if (!this.shouldAlwaysFall) {
            if (this.entityPlayer.posX != this.entityPlayer.prevPosX || this.entityPlayer.posY != this.entityPlayer.prevPosY || this.entityPlayer.posZ != this.entityPlayer.prevPosZ
                    || this.entityPlayer.rotationPitch != this.entityPlayer.prevRotationPitch || this.entityPlayer.rotationYaw != this.entityPlayer.prevRotationYaw) this.shouldAlwaysFall = true;
            this.motionX *= 0.02D;
            this.motionY *= 0.02D;
            this.motionZ *= 0.02D;
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.6800000190734863D;
        this.motionY *= 0.6800000190734863D;
        this.motionZ *= 0.6800000190734863D;

        if (this.particleMaxAge-- <= 0) {
            this.setDead();
        }

        if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }
}
