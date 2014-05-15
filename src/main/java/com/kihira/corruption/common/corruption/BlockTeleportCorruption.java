package com.kihira.corruption.common.corruption;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

public class BlockTeleportCorruption extends AbstractCorruption {

    private short activeCount = 0;

    public BlockTeleportCorruption(EntityPlayer entityPlayer) {
        super(entityPlayer);
    }

    @Override
    public void onUpdate(Side side) {
        if (side == Side.SERVER) {
            if (activeCount > 200) {
                CorruptionRegistry.currentCorruption.remove(this.thePlayer, this);
            }
        }
        this.activeCount++;
    }

    @Override
    public void finish() {}
}
