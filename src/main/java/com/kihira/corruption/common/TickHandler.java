package com.kihira.corruption.common;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.corruption.CorruptionRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;
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
                            String corrName = CorruptionRegistry.getRandomCorruptionEffect(e.player);
                            Corruption.logger.info(I18n.format("Applying %s to %s", corrName, e.player.toString()));
                            CorruptionRegistry.addCorruptionEffect(e.player, corrName);
                        }
                    }
                }
            }
            //Common
            if (CorruptionRegistry.currentCorruption.containsKey(e.player.getCommandSenderName())) {
                Set<String> corruptionNames = CorruptionRegistry.currentCorruption.get(e.player.getCommandSenderName());
                List<String> toRemove = new ArrayList<String>();
                for (String corrName : corruptionNames) {
                    if (CorruptionRegistry.corruptionHashMap.containsKey(corrName)) {
                        if (!CorruptionRegistry.corruptionHashMap.get(corrName).shouldContinue(e.player, FMLCommonHandler.instance().getEffectiveSide())) {
                            toRemove.add(corrName);
                        }
                    }
                }
                //To prevent CME's
                if (!toRemove.isEmpty()) {
                    for (String corrName : toRemove) {
                        CorruptionRegistry.removeCorruptionEffectFromPlayer(e.player.getCommandSenderName(), corrName);
                    }
                }
                //Re populate the set to remove any that may have been removed
                corruptionNames = CorruptionRegistry.currentCorruption.get(e.player.getCommandSenderName());
                for (String corrName : corruptionNames) {
                    if (CorruptionRegistry.corruptionHashMap.containsKey(corrName)) {
                        CorruptionRegistry.corruptionHashMap.get(corrName).onUpdate(e.player, FMLCommonHandler.instance().getEffectiveSide());
                    }
                }
            }
            //Client
            if (e.player.worldObj.isRemote) {
                //TODO scale up chance with corruption
                if (e.player.ticksExisted % 5 == 0) {
                    Corruption.proxy.spawnFootprint(e.player);
                }
            }
        }
    }
}
