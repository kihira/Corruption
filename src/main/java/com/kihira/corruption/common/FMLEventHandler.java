package com.kihira.corruption.common;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.corruption.AbstractCorruption;
import com.kihira.corruption.common.corruption.CorruptionRegistry;
import com.kihira.corruption.common.network.PacketEventHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import java.util.Collection;
import java.util.List;

public class FMLEventHandler {

    public static final int CORRUPTION_MAX = 17280;

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.END) {
            //Main corruption tick
            //Server
            if (e.side == Side.SERVER) {
                if (Corruption.isCorruptionActiveGlobal && CorruptionDataHelper.canBeCorrupted(e.player)) {
                    if (e.player.worldObj.getTotalWorldTime() % Corruption.corrSpeed == 0) {
                        CorruptionDataHelper.increaseCorruptionForPlayer(e.player, 1);

                        //24 hours
                        if (e.player.worldObj.rand.nextInt(CORRUPTION_MAX * CorruptionDataHelper.getCorruptionEffectsForPlayer(e.player).size()) < CorruptionDataHelper.getCorruptionForPlayer(e.player)) {
                            String corrName = CorruptionRegistry.getRandomCorruptionEffect(e.player);
                            if (!CorruptionDataHelper.hasCorruptionEffectsForPlayer(e.player, corrName)) {
                                CorruptionDataHelper.addCorruptionEffectForPlayer(e.player, corrName);
                            }
                        }
                    }
                    List<String> corrEffects = CorruptionDataHelper.getCorruptionEffectsForPlayer(e.player);
                    if (!corrEffects.isEmpty()) {
                        for (String corrEffect : corrEffects) {
                            AbstractCorruption corruption = CorruptionRegistry.corruptionHashMap.get(corrEffect);
                            if (corruption != null) {
                                if (corruption.shouldContinue(e.player, FMLCommonHandler.instance().getEffectiveSide())) {
                                    corruption.onUpdate(e.player, FMLCommonHandler.instance().getEffectiveSide());
                                }
                                else {
                                    corruption.finish(e.player.getCommandSenderName(), FMLCommonHandler.instance().getEffectiveSide());
                                    CorruptionDataHelper.removeCorruptionEffectForPlayer(e.player, corrEffect);
                                }
                            }
                            else {
                                CorruptionDataHelper.removeCorruptionEffectForPlayer(e.player, corrEffect);
                            }
                        }
                    }
                    //AfraidOfTheDark
                    if (e.player.worldObj.getTotalWorldTime() % 200 == 0 && e.player.worldObj.getBlockLightValue((int) e.player.posX, (int) e.player.posY, (int) e.player.posZ) <= 8) {
                        if (CorruptionDataHelper.getCorruptionForPlayer(e.player) > 3000 && e.player.worldObj.rand.nextInt(CORRUPTION_MAX) < CorruptionDataHelper.getCorruptionForPlayer(e.player)) {
                            CorruptionDataHelper.addCorruptionEffectForPlayer(e.player, "afraidOfTheDark");
                        }
                    }

                }
                //Removing corruption
                if ((!Corruption.isCorruptionActiveGlobal || !CorruptionDataHelper.canBeCorrupted(e.player)) && CorruptionDataHelper.getCorruptionForPlayer(e.player) > 0 && e.player.worldObj.getTotalWorldTime() % 10 == 0) {
                    CorruptionDataHelper.decreaseCorruptionForPlayer(e.player, 300);
                }
            }
            //Client
            else if (e.player.worldObj.isRemote) {
                //Do corruption client side
                if (CorruptionRegistry.currentCorruptionClient.containsKey(e.player.getCommandSenderName())) {
                    Collection<String> corruptionNames = CorruptionRegistry.currentCorruptionClient.get(e.player.getCommandSenderName());
                    for (String corrName : corruptionNames) {
                        if (CorruptionRegistry.corruptionHashMap.containsKey(corrName)) {
                            CorruptionRegistry.corruptionHashMap.get(corrName).onUpdate(e.player, FMLCommonHandler.instance().getEffectiveSide());
                        }
                    }
                }
                //Footprint
                if (CorruptionDataHelper.canBeCorrupted(e.player) && e.player.worldObj.rand.nextInt(1800) < CorruptionDataHelper.getCorruptionForPlayer(e.player) && e.player.ticksExisted % 2 == 0) {
                    Corruption.proxy.spawnFootprint(e.player);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (Corruption.corrSpeed == 10) e.player.addChatComponentMessage(new ChatComponentText("[Corruption] Please note that this server is running at ModJam speed (20x faster then normal!) so you can see the full effects of the mod. You can change this in your config"));

        Corruption.eventChannel.sendTo(PacketEventHandler.getDiaryDataPacket(e.player), (EntityPlayerMP) e.player);
    }

}
