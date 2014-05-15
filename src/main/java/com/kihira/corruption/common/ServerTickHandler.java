package com.kihira.corruption.common;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.corruption.AbstractCorruption;
import com.kihira.corruption.common.corruption.CorruptionRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;

public class ServerTickHandler {

    @SubscribeEvent
    public void onServerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.END && e.side == Side.SERVER) {
            //Main corruption tick
            if (e.player.worldObj.getTotalWorldTime() % 200 == 0 && Corruption.isCorruptionActiveGlobal) {
                if (CorruptionDataHelper.canBeCorrupted(e.player)) {
                    CorruptionDataHelper.increaseCorruptionForPlayer(e.player, 1);
                    CorruptionDataHelper.currentCorruption.remove(e.player);
                    AbstractCorruption corruption = CorruptionRegistry.getRandomCorruptionEffect(e.player);
                    Corruption.logger.info("Applying %s to %s", corruption.toString(), e.player.toString());
                    CorruptionDataHelper.currentCorruption.put(e.player, corruption);
                }
            }
        }
    }
}
