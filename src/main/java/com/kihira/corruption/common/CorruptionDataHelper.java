package com.kihira.corruption.common;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.network.PacketEventHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class CorruptionDataHelper {

    public static NBTTagCompound getDiaryDataForPlayer(EntityPlayer entityPlayer) {
        NBTTagCompound corruptionData = getCorruptionDataForPlayer(entityPlayer);
        NBTTagCompound diaryData = corruptionData.getCompoundTag("Diary");

        //Create and save tags if needs be
        if (!corruptionData.hasKey("Diary")) corruptionData.setTag("Diary", diaryData);

        return diaryData;
    }

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

    public static void decreaseCorruptionForPlayer(EntityPlayer entityPlayer, int corruptionDecrease) {
        setCorruptionForPlayer(entityPlayer, Math.max(0, getCorruptionForPlayer(entityPlayer) - corruptionDecrease));
    }

    public static void setCorruptionForPlayer(EntityPlayer entityPlayer, int newCorruption) {
        NBTTagCompound corruptionData = getCorruptionDataForPlayer(entityPlayer);
        corruptionData.setInteger("corruptionLevel", newCorruption);

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            FMLProxyPacket packet = PacketEventHandler.getCorruptionUpdatePacket(entityPlayer.getCommandSenderName(), newCorruption);
            Corruption.eventChannel.sendToDimension(packet, entityPlayer.worldObj.provider.dimensionId);
        }
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
