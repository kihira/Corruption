package com.kihira.corruption.common;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.network.PacketEventHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class FMLEventHandler {

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e) {
        //Synchronise players corruption
        List players = e.player.worldObj.playerEntities;

        for (Object obj : players) {
            EntityPlayer player = (EntityPlayer) obj;
            Corruption.eventChannel.sendTo(PacketEventHandler.getCorruptionUpdatePacket(player.getCommandSenderName(), CorruptionDataHelper.getCorruptionForPlayer(player)), (EntityPlayerMP) e.player);
        }
    }

    @SubscribeEvent
    public void onPlayerChangeDimenion(PlayerEvent.PlayerChangedDimensionEvent e) {
        //Synchronise players corruption
        List players = MinecraftServer.getServer().worldServerForDimension(e.toDim).playerEntities;

        for (Object obj : players) {
            EntityPlayer player = (EntityPlayer) obj;
            Corruption.eventChannel.sendTo(PacketEventHandler.getCorruptionUpdatePacket(player.getCommandSenderName(), CorruptionDataHelper.getCorruptionForPlayer(player)), (EntityPlayerMP) e.player);
        }
    }
}
