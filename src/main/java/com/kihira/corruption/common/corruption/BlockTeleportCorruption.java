package com.kihira.corruption.common.corruption;

import com.google.common.collect.HashMultiset;
import com.kihira.corruption.common.CorruptionDataHelper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

public class BlockTeleportCorruption implements ICorruptionEffect {

    private final HashMultiset<String> blocksBroken = HashMultiset.create();

    @Override
    public void init(String player, Side side) {
        this.blocksBroken.add(player);
    }

    @Override
    public void onUpdate(EntityPlayer player, Side side) {

    }

    @Override
    public void finish(String player, Side side) {
        this.blocksBroken.remove(player, this.blocksBroken.count(player));
    }

    @Override
    public boolean shouldContinue(EntityPlayer player, Side side) {
        return (CorruptionDataHelper.getCorruptionForPlayer(player) < this.blocksBroken.count(player.getCommandSenderName()) * 400);
    }

    @Override
    public String getPageDataName() {
        return "blockTeleport";
    }

    @Override
    public boolean canApply(EntityPlayer player) {
        return true;
    }
}
