package com.kihira.corruption.common.corruption;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

public abstract class AbstractCorruption {

    public final EntityPlayer thePlayer;

    public AbstractCorruption(EntityPlayer entityPlayer) {
        this.thePlayer = entityPlayer;
    }

    public abstract void onUpdate(Side side);

    public abstract void finish();
}
