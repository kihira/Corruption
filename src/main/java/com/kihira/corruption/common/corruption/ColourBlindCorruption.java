package com.kihira.corruption.common.corruption;

import com.google.common.collect.HashMultiset;
import com.kihira.corruption.Corruption;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

public class ColourBlindCorruption extends AbstractCorruption {

    private final HashMultiset<String> playerCount = HashMultiset.create();

    public ColourBlindCorruption() {
        super("colourBlind");
    }

    @Override
    public void init(String player, Side side) {
        if (side == Side.CLIENT && FMLClientHandler.instance().getClientPlayerEntity().getCommandSenderName().equals(player)) {
            Corruption.proxy.enableGrayscaleShader();
        }
        else if (side.isServer()) {
            this.playerCount.add(player);
        }
    }

    @Override
    public void onUpdate(EntityPlayer player, Side side) {
        if (side.isServer()) {
            this.playerCount.add(player.getCommandSenderName());
        }
    }

    @Override
    public void finish(String player, Side side) {
        if (side == Side.CLIENT && FMLClientHandler.instance().getClientPlayerEntity().getCommandSenderName().equals(player)) {
            Corruption.proxy.disableGrayscaleShader();
        }
        else if (side.isServer()) {
            this.playerCount.remove(player);
        }
    }

    @Override
    public boolean shouldContinue(EntityPlayer player, Side side) {
        return this.playerCount.count(player.getCommandSenderName()) < 1000;
    }
}
