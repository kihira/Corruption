package com.kihira.corruption.common.block;

import com.kihira.corruption.common.CorruptionDataHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockCake;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.Random;

//TODO make this better
public class BlockEnderCake extends BlockCake {

    public BlockEnderCake() {
        super();
        this.setCreativeTab(CreativeTabs.tabFood);
        this.setHardness(0.5F);
        this.setStepSound(soundTypeCloth);
        this.setBlockName("endercake");
        this.disableStats();
        this.setBlockTextureName("cake");
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        if (player.canEat(false) && CorruptionDataHelper.canBeCorrupted(player)) {
            player.getFoodStats().addStats(2, 0.1F);
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
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        for (int l = 0; l < 4; ++l) {
            for (int i = world.getBlockMetadata(x, y, z); i < 7; i++) {
                double d0 = (double) ((float) x + random.nextFloat());
                double d1 = (double) ((float) y + random.nextFloat() / 2);
                double d2 = (double) ((float) z + random.nextFloat());
                double d3 = ((double) random.nextFloat() - 0.5D) * 1.5D;
                double d4 = ((double) random.nextFloat() - 0.5D) * 1.5D;
                double d5 = ((double) random.nextFloat() - 0.5D) * 1.5D;
                world.spawnParticle("portal", d0, d1, d2, d3, d4, d5);
            }
        }
    }
}
