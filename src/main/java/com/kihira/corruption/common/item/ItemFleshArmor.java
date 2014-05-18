package com.kihira.corruption.common.item;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.CorruptionDataHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

//TODO have a special interface that will then be called each time before we apply corruption
public class ItemFleshArmor extends ItemArmor {

    public ItemFleshArmor(int armorType) {
        super(ArmorMaterial.CLOTH, 4, armorType);
        this.setCreativeTab(Corruption.creativeTab);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return slot == 3 ? "corruption:textures/model/armor/flesh_layer_2.png" : "corruption:textures/model/armor/flesh_layer_1.png";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
        return 16777215;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean requiresMultipleRenderPasses() {
        return false;
    }

    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon(this.getIconString());
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        if (world.getTotalWorldTime() % 400 == 0 && !world.isRemote) {
            if (CorruptionDataHelper.canBeCorrupted(player)) {
                CorruptionDataHelper.decreaseCorruptionForPlayer(player, 10);
                itemStack.damageItem(1, player);
            }
        }
    }
}
