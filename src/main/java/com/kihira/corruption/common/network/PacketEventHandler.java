package com.kihira.corruption.common.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import io.netty.buffer.ByteBuf;

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

        }
    }

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent e) {
        ByteBuf payload = e.packet.payload();
        if (payload.readInt() == Packet.CORRUPTION.getID()) {

        }
    }

}
