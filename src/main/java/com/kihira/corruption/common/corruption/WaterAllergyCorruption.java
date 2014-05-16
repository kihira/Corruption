package com.kihira.corruption.common.corruption;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;

public class WaterAllergyCorruption extends AbstractCorruption {

    public WaterAllergyCorruption() {
        super("waterAllergy");
    }

    @Override
    public void init(EntityPlayer player, Side side) {
        player.addChatComponentMessage(new ChatComponentText("You feel your skin begin to burn slightly, perhaps from the water in the air?"));
    }

    @Override
    public void onUpdate(EntityPlayer player, Side side) {
        if (side == Side.SERVER) {
/*            if (this.activeCount > 200) {
                Corruption.logger.info("Removing " + this.getClass());
                CorruptionRegistry.currentCorruption.remove(this.thePlayer, this);
            }*/
            if (player.isInWater() && player.worldObj.getTotalWorldTime() % 10 == 0) {
                player.attackEntityFrom(DamageSource.drown, 1);
            }
        }
    }

    @Override
    public void finish(EntityPlayer player, Side side) {

    }
}
