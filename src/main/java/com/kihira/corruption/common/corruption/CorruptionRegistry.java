package com.kihira.corruption.common.corruption;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.kihira.corruption.Corruption;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CorruptionRegistry {

    public static final List<String> randomCorruptionList = new ArrayList<String>();
    public static final HashMap<String, AbstractCorruption> corruptionHashMap = new HashMap<String, AbstractCorruption>();
    public static final Multimap<String, String> currentCorruptionClient = Multimaps.synchronizedMultimap(HashMultimap.<String, String>create()); //Player Name, Corruption Name
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

    //Only directly called on clients (other then from this class)
    @SideOnly(Side.CLIENT)
    public static void addCorruptionEffect(String playerName, String corrName) {
        if (corrName != null) {
            currentCorruptionClient.put(playerName, corrName);
            Corruption.logger.info("Applying " + corrName + " from " + playerName);
            corruptionHashMap.get(corrName).init(playerName, FMLCommonHandler.instance().getEffectiveSide());
        }
    }

    @SideOnly(Side.CLIENT)
    public static void removeCorruptionEffect(String playerName, String corrName) {
        currentCorruptionClient.remove(playerName, corrName);
        Corruption.logger.info("Removing " + corrName + " from " + playerName);
        corruptionHashMap.get(corrName).finish(playerName, FMLCommonHandler.instance().getEffectiveSide());
    }

    public static String getRandomCorruptionEffect(EntityPlayer entityPlayer) {
        String corrName;
        if (randomCorruptionList.size() == 0) corrName = null;
        else if (randomCorruptionList.size() == 1) corrName = randomCorruptionList.get(0);
        else corrName = randomCorruptionList.get(rand.nextInt(randomCorruptionList.size()));
        return corrName;
    }
}
