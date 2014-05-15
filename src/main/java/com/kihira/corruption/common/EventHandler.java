package com.kihira.corruption.common;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.corruption.BlockTeleportCorruption;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.BlockEvent;

public class EventHandler {

    @SubscribeEvent
    //Main corruption event
    public void onLivingDeath(LivingDeathEvent e) {
        if (e.entityLiving instanceof EntityDragon && !e.entityLiving.worldObj.isRemote) {
            Corruption.isCorruptionActiveGlobal = false;
            FMLCommonHandler.instance().getMinecraftServerInstance().addChatMessage(new ChatComponentText("The dragon has been killed! This text needs to be rewritten to be fancier!"));
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        if (CorruptionDataHelper.currentCorruption.containsKey(e.getPlayer())) {
            //BlockTeleportCorruption
            if (CorruptionDataHelper.currentCorruption.get(e.getPlayer()).getClass() == BlockTeleportCorruption.class && !e.block.hasTileEntity(e.blockMetadata)) {
                e.world.setBlock(MathHelper.floor_double(e.getPlayer().posX), MathHelper.floor_double(e.getPlayer().posY), MathHelper.floor_double(e.getPlayer().posZ), e.block, e.blockMetadata, 2);
                e.setCanceled(true);
                e.world.setBlockToAir(e.x, e.y, e.z);
            }
        }
    }
}
