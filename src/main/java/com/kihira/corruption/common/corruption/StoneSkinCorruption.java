package com.kihira.corruption.common.corruption;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class StoneSkinCorruption extends AbstractCorruption {

    public StoneSkinCorruption(EntityPlayer entityPlayer) {
        super(entityPlayer);
    }

    @Override
    public void init() {

    }

    @Override
    public void onUpdate() {
        if (this.thePlayer.worldObj.getTotalWorldTime() % 10 == 0) {
            this.thePlayer.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 20, 4));
            this.thePlayer.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20, 3));
            this.thePlayer.addPotionEffect(new PotionEffect(Potion.resistance.id, 20, 2));
        }
    }

    @Override
    public void finish() {

    }
}
