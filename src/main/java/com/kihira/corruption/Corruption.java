package com.kihira.corruption;

import com.kihira.corruption.common.EventHandler;
import com.kihira.corruption.common.ServerTickHandler;
import com.kihira.corruption.common.corruption.BlockTeleportCorruption;
import com.kihira.corruption.common.corruption.CorruptionRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(name = "Corruption", modid = "corruption")
public class Corruption {

    public static boolean isCorruptionActiveGlobal = true;

    public static final Logger logger = LogManager.getLogger("Corruption");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        FMLCommonHandler.instance().bus().register(new ServerTickHandler());
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        CorruptionRegistry.registerCorruptionEffect(BlockTeleportCorruption.class);
    }
}
