package com.kihira.corruption.common.network;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.CorruptionDataHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.resources.I18n;
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
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent e) {
        ByteBuf payload = e.packet.payload();
        if (payload.readInt() == Packet.CORRUPTION.getID()) {
            EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(ByteBufUtils.readUTF8String(payload));
            int newCorruption = payload.readInt();
            CorruptionDataHelper.setCorruptionForPlayer(player, newCorruption);
            Corruption.logger.info(I18n.format("Updated %s corruption to %d", player.getCommandSenderName(), newCorruption));
        }
    }

    @SubscribeEvent
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
