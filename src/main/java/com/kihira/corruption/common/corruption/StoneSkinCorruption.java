package com.kihira.corruption.common.corruption;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;
import com.kihira.corruption.Corruption;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class StoneSkinCorruption extends AbstractCorruption {

    //TODO Check performance hit
    private final Multiset<String> playerCount = ConcurrentHashMultiset.create();

    public StoneSkinCorruption() {
        super("stoneSkin");
    }

    @Override
    public void init(String player, Side side) {
        this.playerCount.add(player);
    }

    @Override
    public void onUpdate(EntityPlayer player, Side side) {
        if (side == Side.SERVER) {
            if (player.worldObj.getTotalWorldTime() % 10 == 0) {
                player.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 20, 4));
                player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20, 2));
                player.addPotionEffect(new PotionEffect(Potion.resistance.id, 20, 3));
            }
        }
        else if (side == Side.CLIENT) {
            if (this.playerCount.count(player.getCommandSenderName()) < 350) Corruption.proxy.stonifyPlayerSkin((AbstractClientPlayer) player, this.playerCount.count(player.getCommandSenderName())); //TODO possible performance issue on slower machines
        }

        this.playerCount.add(player.getCommandSenderName());
    }

    @Override
    public void finish(String player, Side side) {
        if (side == Side.CLIENT) {
            this.playerCount.setCount(player, 0);
            Corruption.proxy.uncorruptPlayerSkin(player);
        }
    }

    @Override
    public boolean shouldContinue(EntityPlayer player, Side side) {
        return this.playerCount.count(player.getCommandSenderName()) <= 200;
    }
}
