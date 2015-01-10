package com.kihira.corruption.common.network;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.client.texture.CorruptionSkinModifier;
import com.kihira.corruption.common.CorruptionDataHelper;
import com.kihira.corruption.proxy.ClientProxy;
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

                ClientProxy clientProxy = (ClientProxy) Corruption.proxy;

                if (newCorr < oldCorr) {
                    clientProxy.skinHelper.unapplySkinModifierToPlayer((AbstractClientPlayer) player, new CorruptionSkinModifier(), 0, oldCorr, newCorr);
                }

                if (newCorr == 0) {
                    clientProxy.skinHelper.restoreDefaultPlayerSkin((AbstractClientPlayer) player);
                } else {
                    clientProxy.skinHelper.applySkinModifierToPlayer((AbstractClientPlayer) player, new CorruptionSkinModifier(), 0, oldCorr, newCorr);
                }
                CorruptionDataHelper.setCorruptionForPlayer(player, newCorr);
            }

            return null;
        }

    }
}
