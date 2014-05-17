package com.kihira.corruption.common.corruption;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

public class BlockTeleportCorruption extends AbstractCorruption {
    public BlockTeleportCorruption() {
        super("blockTeleport");
    }

    @Override
    public void init(String player, Side side) {

    }

    @Override
    public void onUpdate(EntityPlayer player, Side side) {

    }

    @Override
    public void finish(String player, Side side) {

    }

    @Override
    public boolean shouldContinue(EntityPlayer player, Side side) {
        return true;
    }
}
