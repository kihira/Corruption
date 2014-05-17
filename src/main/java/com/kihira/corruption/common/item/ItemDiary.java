package com.kihira.corruption.common.item;

import com.kihira.corruption.Corruption;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDiary extends ItemBook {

    public ItemDiary() {
        super();
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setTextureName("corruption:diary");
        this.setUnlocalizedName("diary");
        this.setMaxStackSize(8);
    }

    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        player.openGui(Corruption.instance, 0, world, 0, 0, 0);
        return itemStack;
    }
}
