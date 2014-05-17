package com.kihira.corruption.common.corruption;

import com.kihira.corruption.Corruption;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

public abstract class AbstractCorruption {

    public final String name;

    public AbstractCorruption(String corruptionName) {
        this.name = corruptionName;

        if (!CorruptionRegistry.corruptionHashMap.containsKey(corruptionName)) {
            CorruptionRegistry.corruptionHashMap.put(corruptionName, this);
        }
        else Corruption.logger.error(new IllegalArgumentException("A corruption effect with the name " + corruptionName + " has already been registered!"));
    }

    public abstract void init(String player, Side side);

    public abstract void onUpdate(EntityPlayer player, Side side);

    public abstract void finish(String player, Side side);

    public abstract boolean shouldContinue(EntityPlayer player, Side side);
}
