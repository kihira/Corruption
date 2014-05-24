package com.kihira.corruption.common.corruption;

import com.kihira.corruption.Corruption;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.List;

public class BloodLossCorruption implements ICorruptionEffect {

    @Override
    public void init(String player, Side side) {
    }

    @Override
    public void onUpdate(EntityPlayer player, Side side) {
        if (side.isClient() && player.worldObj.getTotalWorldTime() % 2 == 0 && (player.posX != player.prevPosX || player.posY != player.prevPosY || player.posZ != player.prevPosZ)) {
            Corruption.proxy.spawnBloodParticle(player);
        }
        else if (side.isServer() && player.worldObj.getTotalWorldTime() % 10 == 0) {
            player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20, (int) (3 - (player.getHealth()/ 2))));
            World world = player.worldObj;
            List list = world.getEntitiesWithinAABB(EntityZombie.class, player.boundingBox.expand((7 - player.getHealth()) * 5, 5, (7 - player.getHealth()) * 5));
            if (list != null && !list.isEmpty()) {
                for (Object obj : list) {
                    EntityZombie entityZombie = (EntityZombie) obj;
                    if (!entityZombie.hasPath()) {
                        entityZombie.setAttackTarget(player);
                    }
                }
            }
        }
    }

    @Override
    public void finish(String player, Side side) {
    }

    @Override
    public boolean shouldContinue(EntityPlayer player, Side side) {
        return player.getHealth() <= 6;
    }

    @Override
    public String getPageDataName() {
        return "bloodLoss";
    }

    @Override
    public boolean canApply(EntityPlayer player) {
        return player.getHealth() <= 6;
    }
}
