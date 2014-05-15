package com.kihira.corruption.common.network;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.CorruptionDataHelper;
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
        CORRUPTION(0);

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
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent e) {
        ByteBuf payload = e.packet.payload();
        if (payload.readInt() == Packet.CORRUPTION.getID()) {
            EntityPlayer player = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(ByteBufUtils.readUTF8String(payload));
            if (player != null) {
                int newCorr = payload.readInt();
                Corruption.proxy.corruptPlayerSkin((AbstractClientPlayer) player, CorruptionDataHelper.getCorruptionForPlayer(player), newCorr);
                CorruptionDataHelper.setCorruptionForPlayer(player, newCorr);
            }
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

}
