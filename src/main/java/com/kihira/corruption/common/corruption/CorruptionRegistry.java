package com.kihira.corruption.common.corruption;

import com.google.common.collect.HashMultimap;
import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.CorruptionDataHelper;
import com.kihira.corruption.common.network.PacketEventHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CorruptionRegistry {

    public static final List<String> randomCorruptionList = new ArrayList<String>();
    public static final HashMap<String, AbstractCorruption> corruptionHashMap = new HashMap<String, AbstractCorruption>();
    public static final HashMultimap<String, String> currentCorruption = HashMultimap.create(); //Player Name, Corruption Name
    private static final Random rand = new Random();

    /**
     * Register a corruption that can be applied randomly to the player regardless of current corruption level
     * If you wish to apply context sensitive corruption then you need to do the checks yourself and apply
     * it to the player
     * @param corrName
     */
    public static void registerRandomCorruptionEffect(String corrName) {
        if (!randomCorruptionList.contains(corrName)) {
            randomCorruptionList.add(corrName);
        }
        else throw new IllegalArgumentException("The corruption effect " + corrName + " has been registered!");
    }

    //This should only be called on server
    public static void addCorruptionEffect(EntityPlayer player, String corrName) {
        addCorruptionEffect(player.getCommandSenderName(), corrName);

        //Check if we need to unlock data about corruption
        if (!CorruptionDataHelper.hasPageDataUnlocked(corruptionHashMap.get(corrName).getPageDataName(), player)) {
            CorruptionDataHelper.unlockPageData(corruptionHashMap.get(corrName).getPageDataName(), player);
        }

        FMLProxyPacket packet = PacketEventHandler.getCorruptionEffectPacket(player.getCommandSenderName(), corrName, true);
        Corruption.eventChannel.sendToAll(packet);
    }

    //Only directly called on clients (other then from this class)
    public static void addCorruptionEffect(String playerName, String corrName) {
        if (corrName != null) {
            currentCorruption.put(playerName, corrName);
            Corruption.logger.info("Applying " + corrName + " from " + playerName);
            corruptionHashMap.get(corrName).init(playerName, FMLCommonHandler.instance().getEffectiveSide());
        }
    }

    public static void removeCorruptionEffectFromPlayer(String playerName, String corrName) {
        if (corrName != null) {
            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                FMLProxyPacket packet = PacketEventHandler.getCorruptionEffectPacket(playerName, corrName, false);
                Corruption.eventChannel.sendToAll(packet);
            }
            currentCorruption.remove(playerName, corrName);
            Corruption.logger.info("Removing " + corrName + " from " + playerName);
            corruptionHashMap.get(corrName).finish(playerName, FMLCommonHandler.instance().getEffectiveSide());
        }
    }

    public static String getRandomCorruptionEffect(EntityPlayer entityPlayer) {
        String corrName;
        if (randomCorruptionList.size() == 0) corrName = null;
        else if (randomCorruptionList.size() == 1) corrName = randomCorruptionList.get(0);
        else corrName = randomCorruptionList.get(rand.nextInt(randomCorruptionList.size()));
        return corrName;
    }
}
