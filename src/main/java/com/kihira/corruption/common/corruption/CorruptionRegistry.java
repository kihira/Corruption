package com.kihira.corruption.common.corruption;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CorruptionRegistry {

    public static final List<Class<? extends AbstractCorruption>> corruptionList = new ArrayList<Class<? extends AbstractCorruption>>();
    public static final Multimap<EntityPlayer, AbstractCorruption> currentCorruption = HashMultimap.create();
    private static final Random rand = new Random();

    /**
     * Register a corruption that can be applied randomly to the player regardless of current corruption level
     * If you wish to apply context sensitive corruption then you need to do the checks yourself and apply
     * it to the player
     * @param corruptionClass
     */
    public static void registerRandomCorruptionEffect(Class<? extends AbstractCorruption> corruptionClass) {
        if (!corruptionList.contains(corruptionClass)) {
            corruptionList.add(corruptionClass);
        }
        else throw new IllegalArgumentException("The corruption effect " + corruptionClass + " has been registered!");
    }

    public static void addCorruptionEffect(EntityPlayer player, AbstractCorruption corruption) {
        if (corruption != null && !currentCorruption.containsEntry(player, corruption)) {
            currentCorruption.put(player, corruption);
        }
    }

    public static AbstractCorruption getRandomCorruptionEffect(EntityPlayer entityPlayer) {
        Class<? extends AbstractCorruption> clazz;
        if (corruptionList.size() == 0) return null;
        else if (corruptionList.size() == 1) clazz = corruptionList.get(0);
        else clazz = corruptionList.get(rand.nextInt(corruptionList.size()));

        try {
            return clazz.getConstructor(EntityPlayer.class).newInstance(entityPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
