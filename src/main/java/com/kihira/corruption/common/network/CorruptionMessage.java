package com.kihira.corruption.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class CorruptionMessage implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<CorruptionMessage, IMessage> {

        @Override
        public IMessage onMessage(CorruptionMessage message, MessageContext ctx) {
            return null;
        }

    }
}
