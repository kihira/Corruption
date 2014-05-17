package com.kihira.corruption.common.corruption;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

public abstract class AbstractCorruption {

    public final String name;

    public AbstractCorruption(String corruptionName) {
        this.name = corruptionName;

        if (!CorruptionRegistry.corruptionHashMap.containsKey(corruptionName)) {
            CorruptionRegistry.corruptionHashMap.put(corruptionName, this);
        }
        else throw new IllegalArgumentException(I18n.format("A corruption effect with the name %s has already been registered!", corruptionName));
    }

    public abstract void init(String player, Side side);

    public abstract void onUpdate(EntityPlayer player, Side side);

    public abstract void finish(String player, Side side);

    public abstract boolean shouldContinue(EntityPlayer player, Side side);
}
