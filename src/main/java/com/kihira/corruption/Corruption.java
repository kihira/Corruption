package com.kihira.corruption;

import com.kihira.corruption.common.EventHandler;
import com.kihira.corruption.common.TickHandler;
import com.kihira.corruption.common.corruption.BlockTeleportCorruption;
import com.kihira.corruption.common.corruption.CorruptionRegistry;
import com.kihira.corruption.common.corruption.StoneSkinCorruption;
import com.kihira.corruption.common.corruption.WaterAllergyCorruption;
import com.kihira.corruption.common.network.PacketEventHandler;
import com.kihira.corruption.proxy.CommonProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(name = "Corruption", modid = "corruption")
public class Corruption {

    @SidedProxy(clientSide = "com.kihira.corruption.proxy.ClientProxy", serverSide = "com.kihira.corruption.proxy.ClientProxy")
    public static CommonProxy proxy;

    public static boolean isCorruptionActiveGlobal = true;

    public static boolean isEnabledBlockTeleportCorr;
    public static boolean isEnabledStoneskinCorr;
    public static boolean isEnabledWaterAllergyCorr;

    public static final Logger logger = LogManager.getLogger("Corruption");
    public static final FMLEventChannel eventChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel("corruption");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        loadGeneralConfig(e.getSuggestedConfigurationFile());
        registerCorruptionEffects();

        FMLCommonHandler.instance().bus().register(new TickHandler());
        MinecraftForge.EVENT_BUS.register(new EventHandler());

        eventChannel.register(new PacketEventHandler());
    }

    private void loadGeneralConfig(File file) {
        Configuration config = new Configuration(file);
        Property prop;

        config.load();

        prop = config.get(Configuration.CATEGORY_GENERAL, "Enable Block Teleport Corruption Effect", true);
        isEnabledBlockTeleportCorr = prop.getBoolean(true);
        prop = config.get(Configuration.CATEGORY_GENERAL, "Enable Stone Skin Corruption Effect", true);
        isEnabledStoneskinCorr = prop.getBoolean(true);
        prop = config.get(Configuration.CATEGORY_GENERAL, "Enable Water Allergy Corruption Effect", true);
        isEnabledWaterAllergyCorr = prop.getBoolean(true);

        if (config.hasChanged()) config.save();
    }

    private void registerCorruptionEffects() {
        CorruptionRegistry.registerCorruptionEffect(BlockTeleportCorruption.class);
        CorruptionRegistry.registerCorruptionEffect(WaterAllergyCorruption.class);
        CorruptionRegistry.registerCorruptionEffect(StoneSkinCorruption.class);
    }
}
