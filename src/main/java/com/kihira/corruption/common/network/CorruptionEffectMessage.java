package com.kihira.corruption.common.network;

import com.kihira.corruption.common.corruption.CorruptionRegistry;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class CorruptionEffectMessage implements IMessage {

    public String playerName;
    public String corrEffect;
    public boolean shouldAdd;
    public boolean allEffects;

    public CorruptionEffectMessage() {
    }

    public CorruptionEffectMessage(String playerName, String corrEffect, boolean shouldAdd, boolean allEffects) {
        this.playerName = playerName;
        this.corrEffect = corrEffect;
        this.shouldAdd = shouldAdd;
        this.allEffects = allEffects;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        this.playerName = ByteBufUtils.readUTF8String(byteBuf);
        this.corrEffect = ByteBufUtils.readUTF8String(byteBuf);
        this.shouldAdd = byteBuf.readBoolean();
        this.allEffects = byteBuf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        ByteBufUtils.writeUTF8String(byteBuf, playerName);
        ByteBufUtils.writeUTF8String(byteBuf, corrEffect);
        byteBuf.writeBoolean(shouldAdd);
        byteBuf.writeBoolean(allEffects);
    }

    public static class Handler implements IMessageHandler<CorruptionEffectMessage, IMessage> {

        @Override
        public IMessage onMessage(CorruptionEffectMessage message, MessageContext ctx) {
            if (message.shouldAdd) {
                if (message.allEffects) {
                    for (String name : CorruptionRegistry.corruptionHashMap.keySet()) {
                        CorruptionRegistry.addCorruptionEffect(message.playerName, name);
                    }
                } else {
                    CorruptionRegistry.addCorruptionEffect(message.playerName, message.corrEffect);
                }
            } else {
                if (message.allEffects) {
                    for (String name : CorruptionRegistry.corruptionHashMap.keySet()) {
                        CorruptionRegistry.removeCorruptionEffect(message.playerName, name);
                    }
                } else {
                    CorruptionRegistry.removeCorruptionEffect(message.playerName, message.corrEffect);
                }
            }

            return null;
        }

    }
}
