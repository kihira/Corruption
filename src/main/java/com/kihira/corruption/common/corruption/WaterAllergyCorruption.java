package com.kihira.corruption.common.corruption;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;

public class WaterAllergyCorruption extends AbstractCorruption {

    public WaterAllergyCorruption(EntityPlayer entityPlayer) {
        super(entityPlayer);

        entityPlayer.addChatComponentMessage(new ChatComponentText("You feel your skin begin to burn slightly, perhaps from the water in the air?"));
    }

    @Override
    public void init() {

    }

    @Override
    public void onUpdate() {
        if (this.thePlayer.isInWater() && this.thePlayer.worldObj.getTotalWorldTime() % 10 == 0) {
            this.thePlayer.attackEntityFrom(DamageSource.drown, 1);
        }
    }

    @Override
    public void finish() {

    }
}
