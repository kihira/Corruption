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

    //TODO switch to MultiSet String
    private final Multiset<String> playerCount = ConcurrentHashMultiset.create();

    public StoneSkinCorruption() {
        super("stoneSkin");
    }

    @Override
    public void init(EntityPlayer player, Side side) {
        if (side == Side.CLIENT) {
            this.playerCount.add(player.getCommandSenderName());
        }
    }

    @Override
    public void onUpdate(EntityPlayer player, Side side) {
        if (side == Side.SERVER) {
/*            if (this.activeCount > 200) {
                Corruption.logger.info("Removing " + this.getClass());
                CorruptionRegistry.currentCorruption.remove(this.thePlayer, this);
            }*/
            if (player.worldObj.getTotalWorldTime() % 10 == 0) {
                player.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 20, 4));
                player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20, 3));
                player.addPotionEffect(new PotionEffect(Potion.resistance.id, 20, 2));
            }
        }
        else if (side == Side.CLIENT) {
            Corruption.proxy.stonifyPlayerSkin((AbstractClientPlayer) player, this.playerCount.count(player.getCommandSenderName()));
        }
    }

    @Override
    public void finish(EntityPlayer player, Side side) {
        if (side == Side.CLIENT) {
            this.playerCount.setCount(player.getCommandSenderName(), 0);
        }
    }
}
