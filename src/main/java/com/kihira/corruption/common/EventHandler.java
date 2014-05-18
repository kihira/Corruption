package com.kihira.corruption.common;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.corruption.CorruptionRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;

import java.io.File;

public class EventHandler {

    @SubscribeEvent
    //Main corruption event
    public void onLivingDeath(LivingDeathEvent e) {
        if (!e.entityLiving.worldObj.isRemote) {
            if (e.entityLiving instanceof EntityDragon) {
                Corruption.isCorruptionActiveGlobal = false;
                for (Object obj : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
                    EntityPlayer player = (EntityPlayer) obj;
                    CorruptionDataHelper.removeAllCorruptionEffectsForPlayer(player);
                }
                FMLCommonHandler.instance().getMinecraftServerInstance().addChatMessage(new ChatComponentText("The dragon has been killed! This text needs to be rewritten to be fancier!"));
            }
            else if (e.entityLiving instanceof EntityWither && e.source.getEntity() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) e.source.getEntity();
                //Check if they can be corrupted (false if they've already killed it before)
                if (CorruptionDataHelper.canBeCorrupted(player)) {
                    CorruptionDataHelper.removeAllCorruptionEffectsForPlayer(player);
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
        //BlockTeleportCorruption
        if (CorruptionDataHelper.hasCorruptionEffectsForPlayer(e.getPlayer(), "blockTeleport") && !e.block.hasTileEntity(e.blockMetadata)) {
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
        else if (CorruptionDataHelper.getCorruptionForPlayer(e.getPlayer()) > 6000 && e.getPlayer().worldObj.rand.nextInt(FMLEventHandler.CORRUPTION_MAX + 6000) < CorruptionDataHelper.getCorruptionForPlayer(e.getPlayer())) {
            CorruptionDataHelper.addCorruptionEffectForPlayer(e.getPlayer(), "blockTeleport");
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDamage(LivingHurtEvent e) {
        if (e.entityLiving instanceof EntityPlayer && (e.entityLiving.getHealth() - e.ammount <= 6)) {
            EntityPlayer player = (EntityPlayer) e.entityLiving;
            if (CorruptionDataHelper.canBeCorrupted(player) && CorruptionDataHelper.getCorruptionForPlayer(player) > 2000 && !CorruptionDataHelper.hasCorruptionEffectsForPlayer(player, "bloodLoss")) {
                CorruptionDataHelper.addCorruptionEffectForPlayer(player, "bloodLoss");
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload e) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            //Restores original players skins
            File skinBackupFolder = new File("skinbackup");
            if (skinBackupFolder.exists()) {
                File[] skinFiles = skinBackupFolder.listFiles();
                if (skinFiles != null) {
                    for (File skinFile : skinFiles) {
                        String playerName = skinFile.getName().substring(0, skinFile.getName().length() - 4);
                        Corruption.proxy.uncorruptPlayerSkin(playerName);
                        skinFile.delete();
                    }
                }
                skinBackupFolder.delete();
            }
            Corruption.proxy.disableGrayscaleShader();
            //Purge corruption list
            CorruptionRegistry.currentCorruptionClient.clear();
        }
    }
}
