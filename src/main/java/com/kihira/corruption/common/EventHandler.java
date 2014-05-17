package com.kihira.corruption.common;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.corruption.CorruptionRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
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
                        for (String corrName : CorruptionRegistry.currentCorruption.get(player.getCommandSenderName())) {
                            CorruptionRegistry.corruptionHashMap.get(corrName).finish(player.getCommandSenderName(), FMLCommonHandler.instance().getEffectiveSide());
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
        if (CorruptionRegistry.currentCorruption.containsKey(e.getPlayer().getCommandSenderName())) {
            Collection<String> corruptions = CorruptionRegistry.currentCorruption.get(e.getPlayer().getCommandSenderName());
            //BlockTeleportCorruption
            if (corruptions.contains("blockTeleport") && !e.block.hasTileEntity(e.blockMetadata)) {
                //Look a few times for a valid block location
                int x, y, z;
                for (int i = 0; i < 5; i++) {
                    x = e.world.rand.nextInt(2 * 8) - 8;
                    y = e.world.rand.nextInt(2 * 3) - 3;
                    z = e.world.rand.nextInt(2 * 8) - 8;
                    if (e.world.isAirBlock(x, y, z)) {
                        e.world.setBlock(x, y, z, e.block, e.blockMetadata, 2);
                        e.setCanceled(true);
                        e.world.setBlockToAir(e.x, e.y, e.z);
                        break;
                    }
                }
            }
        }
    }
}
