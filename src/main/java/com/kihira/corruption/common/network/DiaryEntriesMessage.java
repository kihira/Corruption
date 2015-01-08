package com.kihira.corruption.common.network;

import com.kihira.corruption.common.CorruptionDataHelper;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class DiaryEntriesMessage implements IMessage {

    public NBTTagCompound tagCompound;

    public DiaryEntriesMessage() {
    }

    public DiaryEntriesMessage(EntityPlayer player) {
        tagCompound = new NBTTagCompound();
        tagCompound.setTag("PageData", CorruptionDataHelper.getDiaryDataForPlayer(player).getTagList("PageData", 8));
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        tagCompound = new NBTTagCompound();
        tagCompound = ByteBufUtils.readTag(byteBuf);
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        ByteBufUtils.writeTag(byteBuf, tagCompound);
    }

    public static class Handler implements IMessageHandler<DiaryEntriesMessage, IMessage> {

        @Override
        public IMessage onMessage(DiaryEntriesMessage message, MessageContext ctx) {
            CorruptionDataHelper.setPageData(message.tagCompound.getTagList("PageData", 8), Minecraft.getMinecraft().thePlayer);

            return null;
        }

    }
}
