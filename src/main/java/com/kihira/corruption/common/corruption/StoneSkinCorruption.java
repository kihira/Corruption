package com.kihira.corruption.common.corruption;

import com.kihira.corruption.Corruption;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class StoneSkinCorruption extends AbstractCorruption {

    private short activeCount = 0;

    public StoneSkinCorruption(EntityPlayer entityPlayer) {
        super(entityPlayer);
    }

    @Override
    public void onUpdate(Side side) {
        if (side == Side.SERVER) {
            if (this.activeCount > 200) {
                Corruption.logger.info("Removing " + this.getClass());
                CorruptionRegistry.currentCorruption.remove(this.thePlayer, this);
            }
            if (this.thePlayer.worldObj.getTotalWorldTime() % 10 == 0) {
                this.thePlayer.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 20, 4));
                this.thePlayer.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20, 3));
                this.thePlayer.addPotionEffect(new PotionEffect(Potion.resistance.id, 20, 2));
            }
        }
        this.activeCount++;
    }

    @Override
    public void finish() {}
}
