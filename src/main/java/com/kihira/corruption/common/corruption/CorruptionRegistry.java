package com.kihira.corruption.common.corruption;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.item.ItemDiary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CorruptionRegistry {

    public static final List<String> randomCorruptionList = new ArrayList<String>();
    public static final HashMap<String, ICorruptionEffect> corruptionHashMap = new HashMap<String, ICorruptionEffect>();
    public static final Multimap<String, String> currentCorruptionClient = Multimaps.synchronizedMultimap(HashMultimap.<String, String>create()); //Player Name, Corruption Name
    private static final Random rand = new Random();

    /**
     * Register a corruption that can be applied randomly to the player regardless of current corruption level
     * If you wish to apply context sensitive corruption then you need to do the checks yourself and apply
     * it to the player
     * @param corrName The unique name of the corruption
     */
    public static void registerRandomCorruptionEffect(String corrName) {
        if (!randomCorruptionList.contains(corrName)) {
            randomCorruptionList.add(corrName);
        }
        else throw new IllegalArgumentException("The corruption effect " + corrName + " has been registered!");
    }

    /**
     * Register a corruption so it can be used in game
     * @param corrName A unique name for the corruption
     * @param corruptionEffect The instance of the corruption effect
     */
    public static void registerCorruptionEffect(String corrName, ICorruptionEffect corruptionEffect) {
        if (!corruptionHashMap.containsKey(corrName)) {
            corruptionHashMap.put(corrName, corruptionEffect);
        }
        else throw new IllegalArgumentException("A corruption has already been registered with the name " + corrName);
    }

    //Only directly called on clients (other then from this class)
    @SideOnly(Side.CLIENT)
    public static void addCorruptionEffect(String playerName, String corrName) {
        if (corrName != null) {
            currentCorruptionClient.put(playerName, corrName);
            Corruption.logger.info("Applying " + corrName + " from " + playerName);
            corruptionHashMap.get(corrName).init(playerName, FMLCommonHandler.instance().getEffectiveSide());

            //Add new information tag to diary
            if (playerName.equals(Minecraft.getMinecraft().thePlayer.getCommandSenderName())) {
                EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
                for (int i = 0; i < entityPlayer.inventory.getSizeInventory(); i++) {
                    ItemStack itemStack = entityPlayer.inventory.getStackInSlot(i);
                    if (itemStack != null && itemStack.getItem() instanceof ItemDiary) {
                        NBTTagCompound tagCompound;
                        if (!itemStack.hasTagCompound()) tagCompound = new NBTTagCompound();
                        else tagCompound = itemStack.getTagCompound();
                        tagCompound.setBoolean("NewInformation", true);
                        itemStack.setTagCompound(tagCompound);
                        //entityPlayer.inventory.setInventorySlotContents(i, itemStack);
                        break;
                    }
                }
            }
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
