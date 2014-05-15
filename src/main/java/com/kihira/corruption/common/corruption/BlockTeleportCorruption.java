package com.kihira.corruption.common.corruption;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

public class BlockTeleportCorruption extends AbstractCorruption {

    public BlockTeleportCorruption(EntityPlayer entityPlayer) {
        super(entityPlayer);
    }

    @Override
    public void onUpdate(Side side) {}

    @Override
    public void finish() {}
}
