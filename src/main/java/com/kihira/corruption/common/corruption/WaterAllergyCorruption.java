package com.kihira.corruption.common.corruption;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;

public class WaterAllergyCorruption extends AbstractCorruption {

    private short activeCount = 0;

    public WaterAllergyCorruption(EntityPlayer entityPlayer) {
        super(entityPlayer);

        entityPlayer.addChatComponentMessage(new ChatComponentText("You feel your skin begin to burn slightly, perhaps from the water in the air?"));
    }

    @Override
    public void onUpdate(Side side) {
        if (side == Side.SERVER) {
            if (this.activeCount > 200) {
                CorruptionRegistry.currentCorruption.remove(this.thePlayer, this);
            }
            if (this.thePlayer.isInWater() && this.thePlayer.worldObj.getTotalWorldTime() % 10 == 0) {
                this.thePlayer.attackEntityFrom(DamageSource.drown, 1);
            }
        }
        this.activeCount++;
    }

    @Override
    public void finish() {}
}
