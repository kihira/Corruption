package com.kihira.corruption.common;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.corruption.CorruptionRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;

import java.io.File;
import java.util.Iterator;

public class EventHandler {

    @SubscribeEvent
    //Main corruption event
    public void onLivingDeath(LivingDeathEvent e) {
        if (!e.entityLiving.worldObj.isRemote) {
            if (Corruption.disableCorrOnDragonDeath && (e.entity instanceof EntityDragon || e.entity instanceof EntityDragonPart)) {
                Corruption.setDiableCorruption();
                for (Object obj : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
                    EntityPlayer player = (EntityPlayer) obj;
                    CorruptionDataHelper.removeAllCorruptionEffectsForPlayer(player);
                }
                FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().sendChatMsg(new ChatComponentText("chat.corruption.end.dragon").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_RED).setItalic(true)));
            }
            else if (Corruption.disableCorrOnWitherDeath && e.entityLiving instanceof EntityWither && e.source.getEntity() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) e.source.getEntity();
                //Check if they can be corrupted (false if they've already killed it before)
                if (CorruptionDataHelper.canBeCorrupted(player)) {
                    CorruptionDataHelper.removeAllCorruptionEffectsForPlayer(player);
                    CorruptionDataHelper.setCanBeCorrupted(player, false);
                    CorruptionDataHelper.setCorruptionForPlayer(player, 0);
                    player.addChatComponentMessage(new ChatComponentText("chat.corruption.end.wither").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_RED).setItalic(true)));
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
        if (!e.world.isRemote) {
            if (CorruptionDataHelper.hasCorruptionEffectsForPlayer(e.getPlayer(), "blockTeleport") && !e.block.hasTileEntity(e.blockMetadata)) {
                //Look a few times for a valid block location
                int x, y, z;
                for (int i = 0; i < 10; i++) {
                    x = (int) (e.getPlayer().posX + e.world.rand.nextInt(2 * 8) - 8);
                    y = (int) (e.getPlayer().posY + e.world.rand.nextInt(2 * 3) - 3);
                    z = (int) (e.getPlayer().posZ + e.world.rand.nextInt(2 * 8) - 8);
                    if (e.world.isAirBlock(x, y, z)) {
                        e.world.setBlock(x, y, z, e.block, e.blockMetadata, 2);
                        e.setCanceled(true);
                        e.world.setBlockToAir(e.x, e.y, e.z);
                        Corruption.blockTeleportCorruption.blocksBroken.add(e.getPlayer().getCommandSenderName());
                        break;
                    }
                }
            }
            else if (CorruptionDataHelper.getCorruptionForPlayer(e.getPlayer()) > 6000 && e.getPlayer().worldObj.rand.nextInt(FMLEventHandler.CORRUPTION_MAX + 6000) < CorruptionDataHelper.getCorruptionForPlayer(e.getPlayer())) {
                CorruptionDataHelper.addCorruptionEffectForPlayer(e.getPlayer(), "blockTeleport");
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (CorruptionDataHelper.hasCorruptionEffectsForPlayer(e.entityPlayer, "blockTeleport") && e.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && e.entityPlayer.worldObj.getBlock(e.x, e.y, e.z) instanceof BlockContainer) {
            World world = e.entityPlayer.worldObj;
            //Teleporting chests!
            //Look a few times for a valid block location
            int x, y, z;
            for (int i = 0; i < 5; i++) {
                x = (int) (e.entityPlayer.posX + world.rand.nextInt(2 * 8) - 8);
                y = (int) (e.entityPlayer.posY + world.rand.nextInt(2 * 3) - 3);
                z = (int) (e.entityPlayer.posZ + world.rand.nextInt(2 * 8) - 8);
                if (y > 0 && world.isAirBlock(x, y, z)) {
                    Block block = world.getBlock(e.x, e.y, e.z);
                    int meta = world.getBlockMetadata(e.x, e.y, e.z);
                    NBTTagCompound tagCompound = new NBTTagCompound();

                    if (world.setBlock(x, y, z, block, meta, 3)) {
                        TileEntity tileEntity = world.getTileEntity(e.x, e.y, e.z);
                        if (tileEntity != null) {
                            tileEntity.writeToNBT(tagCompound);
                            tileEntity.invalidate();

                            world.setTileEntity(x, y, z, ((ITileEntityProvider) block).createNewTileEntity(world, meta));
                            tileEntity = world.getTileEntity(x, y, z);

                            if (tileEntity != null) {
                                NBTTagCompound nbttagcompound = new NBTTagCompound();
                                tileEntity.writeToNBT(nbttagcompound);
                                Iterator iterator = tagCompound.func_150296_c().iterator();

                                while (iterator.hasNext()) {
                                    String s = (String)iterator.next();
                                    NBTBase nbtbase = tagCompound.getTag(s);

                                    if (!s.equals("x") && !s.equals("y") && !s.equals("z")) {
                                        nbttagcompound.setTag(s, nbtbase.copy());
                                    }
                                }

                                tileEntity.readFromNBT(nbttagcompound);
                                tileEntity.markDirty();
                            }
                        }

                        e.setResult(Event.Result.DENY);
                        e.entityPlayer.worldObj.func_147480_a(e.x, e.y, e.z, false);
                        Corruption.blockTeleportCorruption.blocksBroken.add(e.entityPlayer.getCommandSenderName());
                        break;
                    }
                }
            }
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
        //Server
        else {
            //Reset certain corruption
            Corruption.blockTeleportCorruption.blocksBroken.clear();
            Corruption.waterAllergyCorruption.playerCount.clear();
            Corruption.gluttonyCorruption.playerCount.clear();
            Corruption.colourBlindCorruption.playerCount.clear();
            Corruption.stoneSkinCorruption.playerCount.clear();
        }
    }
}
