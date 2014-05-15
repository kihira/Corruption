package com.kihira.corruption.common;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.corruption.AbstractCorruption;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;

public class CorruptionDataHelper {

    public static final HashMap<EntityPlayer, AbstractCorruption> currentCorruption = new HashMap<EntityPlayer, AbstractCorruption>();

    private static NBTTagCompound getCorruptionDataForPlayer(EntityPlayer entityPlayer) {
        NBTTagCompound persistedTag = entityPlayer.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        NBTTagCompound corruptionData = persistedTag.getCompoundTag("Corruption");

        //Create and save tags if needs be
        if (!persistedTag.hasKey("Corruption")) persistedTag.setTag("Corruption", corruptionData);
        entityPlayer.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persistedTag);

        return corruptionData;
    }

    public static void increaseCorruptionForPlayer(EntityPlayer entityPlayer, int corruptionIncrease) {
        setCorruptionForPlayer(entityPlayer, getCorruptionForPlayer(entityPlayer) + corruptionIncrease);
    }

    public static void setCorruptionForPlayer(EntityPlayer entityPlayer, int corruptionLevel) {
        NBTTagCompound corruptionData = getCorruptionDataForPlayer(entityPlayer);
        corruptionData.setInteger("corruptionLevel", corruptionLevel);
    }

    public static int getCorruptionForPlayer(EntityPlayer entityPlayer) {
        NBTTagCompound corruptionData = getCorruptionDataForPlayer(entityPlayer);
        return corruptionData.getInteger("corruptionLevel");
    }

    public static void setCanBeCorrupted(EntityPlayer entityPlayer, boolean canBeCorrupted) {
        NBTTagCompound corruptionData = getCorruptionDataForPlayer(entityPlayer);
        corruptionData.setBoolean("canBeCorrupted", canBeCorrupted);
    }

    public static boolean canBeCorrupted(EntityPlayer entityPlayer) {
        NBTTagCompound corruptionData = getCorruptionDataForPlayer(entityPlayer);
        if (!corruptionData.hasKey("canBeCorrupted")) {
            if (Corruption.isCorruptionActiveGlobal) setCanBeCorrupted(entityPlayer, true);
        }
        return corruptionData.getBoolean("canBeCorrupted");
    }

}
