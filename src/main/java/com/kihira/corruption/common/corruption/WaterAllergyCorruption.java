package com.kihira.corruption.common.corruption;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;

public class WaterAllergyCorruption extends AbstractCorruption {

    public WaterAllergyCorruption() {
        super("waterAllergy");
    }

    @Override
    public void init(String player, Side side) {
        if (side == Side.SERVER) MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(player).addChatComponentMessage(new ChatComponentText("You feel your skin begin to burn slightly, perhaps from the water in the air?"));
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
    public void finish(String player, Side side) {

    }

    @Override
    public boolean shouldContinue(EntityPlayer player, Side side) {
        return true;
    }
}
