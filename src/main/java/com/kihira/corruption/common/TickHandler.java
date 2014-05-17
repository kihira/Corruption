package com.kihira.corruption.common;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.corruption.CorruptionRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;

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
                    //5 second
                    //TODO: reduce this time for modjam only?
                    if (e.player.worldObj.getTotalWorldTime() % 100 == 0) {
                        CorruptionDataHelper.increaseCorruptionForPlayer(e.player, 1);

                        //24 hours
                        if (e.player.worldObj.rand.nextInt(17280) < CorruptionDataHelper.getCorruptionForPlayer(e.player)) {
                            String corrName = CorruptionRegistry.getRandomCorruptionEffect(e.player);
                            CorruptionRegistry.addCorruptionEffect(e.player, corrName);
                        }
                    }
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
                    }

                }
            }
            //Common
            if (CorruptionRegistry.currentCorruption.containsKey(e.player.getCommandSenderName())) {
                Set<String> corruptionNames = CorruptionRegistry.currentCorruption.get(e.player.getCommandSenderName());
                for (String corrName : corruptionNames) {
                    if (CorruptionRegistry.corruptionHashMap.containsKey(corrName)) {
                        CorruptionRegistry.corruptionHashMap.get(corrName).onUpdate(e.player, FMLCommonHandler.instance().getEffectiveSide());
                    }
                }
            }
            //Client
            if (e.player.worldObj.isRemote) {
                //TODO scale up chance with corruption
                if (e.player.ticksExisted % 2 == 0) {
                    Corruption.proxy.spawnFootprint(e.player);
                }
            }
        }
    }
}
