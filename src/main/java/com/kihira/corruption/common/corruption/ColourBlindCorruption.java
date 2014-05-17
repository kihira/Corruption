package com.kihira.corruption.common.corruption;

import com.kihira.corruption.Corruption;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

public class ColourBlindCorruption extends AbstractCorruption {

    public ColourBlindCorruption() {
        super("colourBlind");
    }

    @Override
    public void init(String player, Side side) {
        if (side == Side.CLIENT && FMLClientHandler.instance().getClientPlayerEntity().getCommandSenderName().equals(player)) {
            Corruption.proxy.enableGrayscaleShader();
        }
    }

    @Override
    public void onUpdate(EntityPlayer player, Side side) {

    }

    @Override
    public void finish(String player, Side side) {
        if (side == Side.CLIENT && FMLClientHandler.instance().getClientPlayerEntity().getCommandSenderName().equals(player)) {
            Corruption.proxy.disableGrayscaleShader();
        }
    }

    @Override
    public boolean shouldContinue(EntityPlayer player, Side side) {
        return true;
    }
}
