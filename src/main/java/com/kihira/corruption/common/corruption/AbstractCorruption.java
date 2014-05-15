package com.kihira.corruption.common.corruption;

import net.minecraft.entity.player.EntityPlayer;

public abstract class AbstractCorruption {

    public final EntityPlayer thePlayer;

    public AbstractCorruption(EntityPlayer entityPlayer) {
        this.thePlayer = entityPlayer;
    }

    public abstract void init();

    public abstract void onUpdate();

    public abstract void finish();
}
