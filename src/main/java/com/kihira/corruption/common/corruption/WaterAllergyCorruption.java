package com.kihira.corruption.common.corruption;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPotion;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;

public class WaterAllergyCorruption implements ICorruptionEffect {

    public final Multiset<String> playerCount = HashMultiset.create();

    @Override
    public void init(String player, Side side) {
        if (side == Side.SERVER) {
            this.playerCount.add(player);
        }
    }

    @Override
    public void onUpdate(EntityPlayer player, Side side) {
        if (side == Side.SERVER) {
            if (player.worldObj.getTotalWorldTime() % 10 == 0) {
                if (player.isInWater() || (player.worldObj.isRaining() && player.worldObj.canBlockSeeTheSky((int) player.posX, (int) player.posY, (int) player.posZ))) {
                    player.attackEntityFrom(DamageSource.drown, 1);
                    this.playerCount.add(player.getCommandSenderName(), 10);
                }
                if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemPotion){
                    player.dropOneItem(true);
                    player.attackEntityFrom(DamageSource.drown, 1);
                    this.playerCount.add(player.getCommandSenderName(), 10);
                    player.addChatComponentMessage(new ChatComponentText("The condensation from the bottle burns your hands, causing you to drop it.").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.AQUA)));
                }
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
