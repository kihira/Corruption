package com.kihira.corruption.common;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.client.EntityFootstep;
import com.kihira.corruption.common.corruption.AbstractCorruption;
import com.kihira.corruption.common.corruption.CorruptionRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.resources.I18n;

import java.util.List;
import java.util.Set;

public class TickHandler {

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.END) {
            //Main corruption tick
            //Server
            if (e.side == Side.SERVER) {
                if (Corruption.isCorruptionActiveGlobal && CorruptionDataHelper.canBeCorrupted(e.player)) {
                    if (e.player.worldObj.getTotalWorldTime() % 200 == 0) {
                        CorruptionDataHelper.increaseCorruptionForPlayer(e.player, 1);

                        //12 hours
                        if (e.player.worldObj.rand.nextInt(4320) < CorruptionDataHelper.getCorruptionForPlayer(e.player)) {
                            AbstractCorruption corruption = CorruptionRegistry.getRandomCorruptionEffect(e.player);
                            Corruption.logger.info(I18n.format("Applying %s to %s", corruption.toString(), e.player.toString()));
                            CorruptionRegistry.currentCorruption.put(e.player, corruption);
                        }
                    }
                }
            }
            //Common
            if (CorruptionRegistry.currentCorruption.containsKey(e.player)) {
                //TODO keep an eye out for CME's here
                Set<AbstractCorruption> corruptions = CorruptionRegistry.currentCorruption.get(e.player);
                for (AbstractCorruption corruption : corruptions) {
                    if (corruption != null) corruption.onUpdate(FMLCommonHandler.instance().getEffectiveSide());
                }
            }
            //Client
            if (e.player.worldObj.isRemote) {
                List<EntityFootstep> footsteps = e.player.worldObj.getEntitiesWithinAABB(EntityFootstep.class, e.player.boundingBox.expand(2, 2, 2));

                //TODO scale up chance with corruption
                if (footsteps.size() == 0 && e.player.ticksExisted % 40 == 0) {
                    EntityFootstep footstep = new EntityFootstep(e.player);
                    e.player.worldObj.spawnEntityInWorld(footstep);
                    Corruption.logger.info(I18n.format("Spawned footstep at %s, %s, %s", e.player.posX, e.player.posY, e.player.posZ));
                }
            }
        }
    }
}
