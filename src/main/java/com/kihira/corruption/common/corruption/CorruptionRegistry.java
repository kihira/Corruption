package com.kihira.corruption.common.corruption;

import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CorruptionRegistry {

    public static final List<Class<? extends AbstractCorruption>> corruptionList = new ArrayList<Class<? extends AbstractCorruption>>();
    private static final Random rand = new Random();

    public static void registerCorruptionEffect(Class<? extends AbstractCorruption> corruptionClass) {
        if (!corruptionList.contains(corruptionClass)) {
            corruptionList.add(corruptionClass);
        }
        else throw new IllegalArgumentException("The corruption effect " + corruptionClass + " has been registered!");
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
