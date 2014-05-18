package com.kihira.corruption.common.corruption;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

public class WaterAllergyCorruption extends AbstractCorruption {

    private final Multiset<String> playerCount = HashMultiset.create();

    public WaterAllergyCorruption() {
        super("waterAllergy");
    }

    @Override
    public void init(String player, Side side) {
        if (side == Side.SERVER) {
            this.playerCount.add(player);
        }
    }

    @Override
    public void onUpdate(EntityPlayer player, Side side) {
        if (side == Side.SERVER) {
            if (player.isInWater() || (player.worldObj.isRaining() && player.worldObj.canBlockSeeTheSky((int) player.posX, (int) player.posY, (int) player.posZ)) && player.worldObj.getTotalWorldTime() % 10 == 0) {
                player.attackEntityFrom(DamageSource.drown, 1);
            }
            this.playerCount.add(player.getCommandSenderName());
        }
    }

    @Override
    public void finish(String player, Side side) {
        this.playerCount.setCount(player, 0);
    }

    @Override
    public boolean shouldContinue(EntityPlayer player, Side side) {
        return this.playerCount.count(player.getCommandSenderName()) < 1000;
    }

    @Override
    public String getPageDataName() {
        return "waterAllergy";
    }

    @Override
    public boolean canApply(EntityPlayer player) {
        return true;
    }
}
