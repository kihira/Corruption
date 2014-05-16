package com.kihira.corruption.common;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.corruption.AbstractCorruption;
import com.kihira.corruption.common.corruption.BlockTeleportCorruption;
import com.kihira.corruption.common.corruption.CorruptionRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.BlockEvent;

import java.util.Collection;

public class EventHandler {

    @SubscribeEvent
    //Main corruption event
    public void onLivingDeath(LivingDeathEvent e) {
        if (!e.entityLiving.worldObj.isRemote) {
            if (e.entityLiving instanceof EntityDragon) {
                Corruption.isCorruptionActiveGlobal = false;
                CorruptionRegistry.currentCorruption.clear();
                FMLCommonHandler.instance().getMinecraftServerInstance().addChatMessage(new ChatComponentText("The dragon has been killed! This text needs to be rewritten to be fancier!"));
            }
            else if (e.entityLiving instanceof EntityWither && e.source.getEntity() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) e.source.getEntity();
                //Check if they can be corrupted (false if they've already killed it before)
                if (CorruptionDataHelper.canBeCorrupted(player)) {
                    if (CorruptionRegistry.currentCorruption.containsKey(player)) {
                        for (AbstractCorruption corruption : CorruptionRegistry.currentCorruption.get(player)) {
                            corruption.finish();
                        }
                        CorruptionRegistry.currentCorruption.removeAll(player);
                    }
                    CorruptionDataHelper.setCanBeCorrupted(player, false);
                    CorruptionDataHelper.setCorruptionForPlayer(player, 0);
                    player.addChatComponentMessage(new ChatComponentText("As the wither screams out its last breath, you feel a weight lifted from your entire body and soul"));
                }
            }
            else if (e.entityLiving instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) e.entityLiving;
                CorruptionDataHelper.decreaseCorruptionForPlayer(player, Corruption.corrRemovedOnDeath);
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        if (CorruptionRegistry.currentCorruption.containsKey(e.getPlayer())) {
            //BlockTeleportCorruption
            Collection<AbstractCorruption> corruptions = CorruptionRegistry.currentCorruption.get(e.getPlayer());
            for (AbstractCorruption corruption : corruptions) {
                if (corruption.getClass() == BlockTeleportCorruption.class && !e.block.hasTileEntity(e.blockMetadata)) {
                    e.world.setBlock(MathHelper.floor_double(e.getPlayer().posX), MathHelper.floor_double(e.getPlayer().posY), MathHelper.floor_double(e.getPlayer().posZ), e.block, e.blockMetadata, 2);
                    e.setCanceled(true);
                    e.world.setBlockToAir(e.x, e.y, e.z);
                }
            }
        }
    }
}
