package com.kihira.corruption.common.network;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.common.CorruptionDataHelper;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;

public class CorruptionUpdateMessage implements IMessage {

    public String playerName;
    public int newCorruption;

    public CorruptionUpdateMessage() {
    }

    public CorruptionUpdateMessage(String playerName, int newCorruption) {
        this.playerName = playerName;
        this.newCorruption = newCorruption;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        this.playerName = ByteBufUtils.readUTF8String(byteBuf);
        this.newCorruption = byteBuf.readInt();
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        ByteBufUtils.writeUTF8String(byteBuf, playerName);
        byteBuf.writeInt(newCorruption);
    }

    public static class Handler implements IMessageHandler<CorruptionUpdateMessage, IMessage> {

        @Override
        public IMessage onMessage(CorruptionUpdateMessage message, MessageContext ctx) {
            EntityPlayer player = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(message.playerName);
            if (player != null) {
                int newCorr = message.newCorruption;
                int oldCorr = CorruptionDataHelper.getCorruptionForPlayer(player);

                if (newCorr < oldCorr) {
                    Corruption.proxy.uncorruptPlayerSkinPartially((AbstractClientPlayer) player, oldCorr, newCorr);
                }

                if (newCorr == 0) {
                    Corruption.proxy.uncorruptPlayerSkin((AbstractClientPlayer) player);
                } else {
                    Corruption.proxy.corruptPlayerSkin((AbstractClientPlayer) player, oldCorr, newCorr);
                }
                CorruptionDataHelper.setCorruptionForPlayer(player, newCorr);
            }

            return null;
        }

    }
}
