package com.kihira.corruption.common;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.corruption.AbstractCorruption;
import com.kihira.corruption.common.corruption.CorruptionRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.resources.I18n;

public class TickHandler {

    @SubscribeEvent
    public void onServerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.END && e.side == Side.SERVER) {
            //Main corruption tick
            if (Corruption.isCorruptionActiveGlobal && CorruptionDataHelper.canBeCorrupted(e.player)) {
                if (e.player.worldObj.getTotalWorldTime() % 200 == 0) {
                    CorruptionDataHelper.increaseCorruptionForPlayer(e.player, 1);
                    CorruptionDataHelper.currentCorruption.remove(e.player);
                    AbstractCorruption corruption = CorruptionRegistry.getRandomCorruptionEffect(e.player);
                    Corruption.logger.info(I18n.format("Applying %s to %s", corruption.toString(), e.player.toString()));
                    CorruptionDataHelper.currentCorruption.put(e.player, corruption);
                }
                else if (CorruptionDataHelper.currentCorruption.containsKey(e.player)) {
                    CorruptionDataHelper.currentCorruption.get(e.player).onUpdate();
                }
            }
        }
    }
}
