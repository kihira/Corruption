package com.kihira.corruption.common.block;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.client.particle.EntityFXEnder;
import com.kihira.corruption.common.CorruptionDataHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockCake;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.Random;

public class BlockEnderCake extends BlockCake {

    @SideOnly(Side.CLIENT)
    private IIcon innerIcon;
    @SideOnly(Side.CLIENT)
    private IIcon topIcon;
    @SideOnly(Side.CLIENT)
    private IIcon bottomIcon;

    public BlockEnderCake() {
        super();
        this.setCreativeTab(CreativeTabs.tabFood);
        this.setHardness(0.5F);
        this.setStepSound(soundTypeCloth);
        this.setBlockName("endercake");
        this.disableStats();
        this.setBlockTextureName("corruption:cake");
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        this.doNoms(world, player, x, y, z);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        this.doNoms(world, entityPlayer, x, y, z);
        return true;
    }

    private void doNoms(World world, EntityPlayer player, int x, int y, int z) {
        if (player.canEat(false) && CorruptionDataHelper.canBeCorrupted(player)) {
            player.getFoodStats().addStats(2, 0.1F);
            CorruptionDataHelper.decreaseCorruptionForPlayer(player, 10);
            int l = world.getBlockMetadata(x, y, z) + 1;

            if (l >= 6) {
                world.setBlockToAir(x, y, z);
            }
            else {
                world.setBlockMetadataWithNotify(x, y, z, l, 2);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.topIcon : (side == 0 ? this.bottomIcon : (meta > 0 && side == 4 ? this.innerIcon : this.blockIcon));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon(this.getTextureName() + "_side");
        this.innerIcon = iconRegister.registerIcon(this.getTextureName() + "_inner");
        this.topIcon = iconRegister.registerIcon(this.getTextureName() + "_top");
        this.bottomIcon = iconRegister.registerIcon(this.getTextureName() + "_bottom");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        for (int i = world.getBlockMetadata(x, y, z); i < 7; i++) {
            double d0 = (double) ((float) x + random.nextFloat());
            double d1 = (double) ((float) y + random.nextFloat() / 2);
            double d2 = (double) ((float) z + random.nextFloat());
            double d3 = ((double) random.nextFloat() - 0.5D) * 1.5D;
            double d4 = ((double) random.nextFloat() - 0.5D) * 1.5D;
            double d5 = ((double) random.nextFloat() - 0.5D) * 1.5D;
            Minecraft.getMinecraft().effectRenderer.addEffect(new EntityFXEnder(world, d0, d1, d2, d3, d4, d5));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Item getItem(World world, int x, int y, int z) {
        return Item.getItemFromBlock(Corruption.blockEnderCake);
    }
}
