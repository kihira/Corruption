package com.kihira.corruption.common.item;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.network.PacketEventHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;

public class ItemDiary extends ItemBook {

    //TODO have enchanting effect thingy if there is a new entry
    public ItemDiary() {
        super();
        this.setCreativeTab(Corruption.creativeTab);
        this.setTextureName("corruption:diary");
        this.setUnlocalizedName("diary");
        this.setMaxStackSize(8);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            Corruption.eventChannel.sendTo(PacketEventHandler.getDiaryDataPacket(player), (EntityPlayerMP) player);
        }
        if (itemStack.hasTagCompound()) {
            itemStack.getTagCompound().setBoolean("NewInformation", false);
        }
        player.openGui(Corruption.instance, 0, world, 0, 0, 0);
        return itemStack;
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(item, 1, 0));
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtTagCompound.setBoolean("CheatBook", true);
        ItemStack itemStack = new ItemStack(item, 1, 0);
        itemStack.setTagCompound(nbtTagCompound);
        list.add(itemStack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        if (itemStack.hasTagCompound()) {
            if (itemStack.getTagCompound().getBoolean("CheatBook")) {
                list.add("Cheat Book");
                list.add("Unlocks all information");
            }
            else if (itemStack.getTagCompound().getBoolean("NewInformation")) {
                list.add("New information available");
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack itemStack, int pass) {
        if (pass == 0) {
            if (itemStack.hasTagCompound() && (itemStack.getTagCompound().getBoolean("CheatBook") || itemStack.getTagCompound().getBoolean("NewInformation"))) {
                return true;
            }
        }
        return false;
    }
}
