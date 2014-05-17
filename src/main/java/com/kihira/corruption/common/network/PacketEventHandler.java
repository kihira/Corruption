package com.kihira.corruption.common.network;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.CorruptionDataHelper;
import com.kihira.corruption.common.corruption.CorruptionRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;

public class PacketEventHandler {

    private enum Packet {
        CORRUPTION(0),
        CORRUPTIONEFFECT(1);

        private final int id;

        private Packet(int id) {
            this.id = id;
        }

        public int getID() {
            return this.id;
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent e) {
        ByteBuf payload = e.packet.payload();
        int packetID = payload.readInt();
        if (packetID == Packet.CORRUPTION.getID()) {
            EntityPlayer player = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(ByteBufUtils.readUTF8String(payload));
            if (player != null) {
                int newCorr = payload.readInt();
                int oldCorr = CorruptionDataHelper.getCorruptionForPlayer(player);

                if (newCorr < oldCorr) {
                    Corruption.proxy.uncorruptPlayerSkinPartially((AbstractClientPlayer) player, oldCorr, newCorr);
                }

                if (newCorr == 0) {
                    Corruption.proxy.uncorruptPlayerSkin(player.getCommandSenderName());
                }
                else {
                    Corruption.proxy.corruptPlayerSkin((AbstractClientPlayer) player, oldCorr, newCorr);
                }
                CorruptionDataHelper.setCorruptionForPlayer(player, newCorr);
            }
        }
        else if (packetID == Packet.CORRUPTIONEFFECT.getID()) {
            String playerName = ByteBufUtils.readUTF8String(payload);
            String corrName = ByteBufUtils.readUTF8String(payload);
            boolean shouldAdd = payload.readBoolean();

            if (shouldAdd) CorruptionRegistry.addCorruptionEffect(playerName, corrName);
            else CorruptionRegistry.removeCorruptionEffectFromPlayer(playerName, corrName);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.SERVER)
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent e) {
        ByteBuf payload = e.packet.payload();
        if (payload.readInt() == Packet.CORRUPTION.getID()) {
            Corruption.logger.warn("Received a corruption update server side, this isn't supposed to happen!");
        }
    }

    public static FMLProxyPacket getCorruptionUpdatePacket(String playerName, int newCorruption) {
        ByteBuf byteBuf = Unpooled.buffer();

        byteBuf.writeInt(Packet.CORRUPTION.getID());
        ByteBufUtils.writeUTF8String(byteBuf, playerName);
        byteBuf.writeInt(newCorruption);

        return new FMLProxyPacket(byteBuf, "corruption");
    }

    public static FMLProxyPacket getCorruptionEffectPacket(String playerName, String corrEffect, boolean shouldAdd) {
        ByteBuf byteBuf = Unpooled.buffer();

        byteBuf.writeInt(Packet.CORRUPTIONEFFECT.getID());
        ByteBufUtils.writeUTF8String(byteBuf, playerName);
        ByteBufUtils.writeUTF8String(byteBuf, corrEffect);
        byteBuf.writeBoolean(shouldAdd);

        return new FMLProxyPacket(byteBuf, "corruption");
    }
}
