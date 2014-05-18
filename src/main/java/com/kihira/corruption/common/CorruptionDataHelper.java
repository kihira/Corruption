package com.kihira.corruption.common;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.corruption.CorruptionRegistry;
import com.kihira.corruption.common.network.PacketEventHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.ArrayList;
import java.util.List;

public class CorruptionDataHelper {

    public static List<String> getCorruptionEffectsForPlayer(EntityPlayer entityPlayer) {
        NBTTagCompound corruptionData = getCorruptionDataForPlayer(entityPlayer);
        NBTTagList corrEffects = corruptionData.getTagList("CorruptionEffects", 8);
        List<String> effects = new ArrayList<String>();
        if (corrEffects != null && corrEffects.tagCount() > 0) {
            for (int i = 0; i < corrEffects.tagCount(); i++) {
                effects.add(corrEffects.getStringTagAt(i));
            }
        }
        return effects;
    }

    public static boolean hasCorruptionEffectsForPlayer(EntityPlayer entityPlayer, String name) {
        NBTTagCompound corruptionData = getCorruptionDataForPlayer(entityPlayer);
        NBTTagList corrEffects = corruptionData.getTagList("CorruptionEffects", 8);
        if (corrEffects != null && corrEffects.tagCount() > 0) {
            for (int i = 0; i < corrEffects.tagCount(); i++) {
                if (name.equals(corrEffects.getStringTagAt(i))) return true;
            }
        }
        return false;
    }

    public static void addCorruptionEffectForPlayer(EntityPlayer entityPlayer, String name) {
        NBTTagCompound corruptionData = getCorruptionDataForPlayer(entityPlayer);
        NBTTagList corrEffects = corruptionData.getTagList("CorruptionEffects", 8);
        corrEffects.appendTag(new NBTTagString(name));
        corruptionData.setTag("CorruptionEffects", corrEffects);

        //Check if we need to unlock data about corruption
        if (!hasPageDataUnlocked(CorruptionRegistry.corruptionHashMap.get(name).getPageDataName(), entityPlayer)) {
            unlockPageData(CorruptionRegistry.corruptionHashMap.get(name).getPageDataName(), entityPlayer);
        }

        FMLProxyPacket packet = PacketEventHandler.getCorruptionEffectPacket(entityPlayer.getCommandSenderName(), name, true, false);
        Corruption.eventChannel.sendToAll(packet);
    }

    public static void removeCorruptionEffectForPlayer(EntityPlayer entityPlayer, String name) {
        NBTTagCompound corruptionData = getCorruptionDataForPlayer(entityPlayer);
        NBTTagList corrEffects = corruptionData.getTagList("CorruptionEffects", 8);
        if (corrEffects != null && corrEffects.tagCount() > 0) {
            NBTTagList copy = (NBTTagList) corrEffects.copy();
            for (int i = 0; i < copy.tagCount(); i++) {
                if (name.equals(copy.getStringTagAt(i))) {
                    corrEffects.removeTag(i);
                    break;
                }
            }
        }
        corruptionData.setTag("CorruptionEffects", corrEffects);

        FMLProxyPacket packet = PacketEventHandler.getCorruptionEffectPacket(entityPlayer.getCommandSenderName(), name, false, false);
        Corruption.eventChannel.sendToAll(packet);
    }

    public static void removeAllCorruptionEffectsForPlayer(EntityPlayer entityPlayer) {
        NBTTagCompound corruptionData = getCorruptionDataForPlayer(entityPlayer);
        corruptionData.setTag("CorruptionEffects", new NBTTagList());

        FMLProxyPacket packet = PacketEventHandler.getCorruptionEffectPacket(entityPlayer.getCommandSenderName(), "", false, true);
        Corruption.eventChannel.sendToAll(packet);
    }


    public static NBTTagCompound getDiaryDataForPlayer(EntityPlayer entityPlayer) {
        NBTTagCompound corruptionData = getCorruptionDataForPlayer(entityPlayer);
        NBTTagCompound diaryData = corruptionData.getCompoundTag("Diary");

        //Create and save tags if needs be
        if (!corruptionData.hasKey("Diary")) {
            NBTTagList pageData = new NBTTagList();
            pageData.appendTag(new NBTTagString("contents"));
            pageData.appendTag(new NBTTagString("enderCake"));
            diaryData.setTag("PageData", pageData);
            corruptionData.setTag("Diary", diaryData);
        }

        return diaryData;
    }

    public static boolean hasPageDataUnlocked(String pageDataName, EntityPlayer entityPlayer) {
        NBTTagCompound diaryData = getDiaryDataForPlayer(entityPlayer);
        NBTTagList pageData = diaryData.getTagList("PageData", 8);
        if (pageData != null && pageData.tagCount() > 0) {
            for (int i = 0; i < pageData.tagCount(); i++) {
                String pageName = pageData.getStringTagAt(i);
                if (pageName.equals(pageDataName)) return true;
            }
        }
        return false;
    }

    public static void setPageData(NBTTagList pageData, EntityPlayer entityPlayer) {
        NBTTagCompound diaryData = getDiaryDataForPlayer(entityPlayer);
        if (pageData != null) {
            diaryData.setTag("PageData", pageData);

            if (Side.SERVER == FMLCommonHandler.instance().getEffectiveSide()) {
                Corruption.eventChannel.sendTo(PacketEventHandler.getDiaryDataPacket(entityPlayer), (EntityPlayerMP) entityPlayer);
            }
        }
    }

    public static void unlockPageData(String pageDataName, EntityPlayer entityPlayer) {
        NBTTagCompound diaryData = getDiaryDataForPlayer(entityPlayer);
        if (diaryData.hasKey("PageData")) {
            NBTTagList pageData = diaryData.getTagList("PageData", 8);
            pageData.appendTag(new NBTTagString(pageDataName));
            diaryData.setTag("PageData", pageData);

            if (Side.SERVER == FMLCommonHandler.instance().getEffectiveSide()) {
                Corruption.eventChannel.sendTo(PacketEventHandler.getDiaryDataPacket(entityPlayer), (EntityPlayerMP) entityPlayer);
            }
        }
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
