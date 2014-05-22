package com.kihira.corruption.common.corruption;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

public interface ICorruptionEffect {

    public void init(String player, Side side);

    public void onUpdate(EntityPlayer player, Side side);

    public void finish(String player, Side side);

    public boolean shouldContinue(EntityPlayer player, Side side);

    public String getPageDataName();

    public boolean canApply(EntityPlayer player);
}
