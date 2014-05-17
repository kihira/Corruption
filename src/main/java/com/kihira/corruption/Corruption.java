package com.kihira.corruption;

import com.kihira.corruption.common.CommandCorruption;
import com.kihira.corruption.common.EventHandler;
import com.kihira.corruption.common.TickHandler;
import com.kihira.corruption.common.block.BlockEnderCake;
import com.kihira.corruption.common.corruption.*;
import com.kihira.corruption.common.network.PacketEventHandler;
import com.kihira.corruption.proxy.CommonProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(name = "Corruption", modid = "corruption")
public class Corruption {

    @SidedProxy(clientSide = "com.kihira.corruption.proxy.ClientProxy", serverSide = "com.kihira.corruption.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static boolean isCorruptionActiveGlobal = true;

    public static boolean isEnabledBlockTeleportCorr;
    public static boolean isEnabledStoneSkinCorr;
    public static boolean isEnabledWaterAllergyCorr;
    public static boolean isEnabledColourBlindCorr;
    public static boolean isEnabledAfraidOfTheDarkCorr;
    public static boolean isEnabledBloodLossCorr;

    public static final BlockEnderCake blockEnderCake = new BlockEnderCake();

    public static final Logger logger = LogManager.getLogger("Corruption");
    public static final FMLEventChannel eventChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel("corruption");

    public static final String CATEGORY_CORRUPTION = "corruption";

    public static boolean disableCorrOnDragonDeath;
    public static boolean disableCorrOnWitherDeath;
    public static int corrRemovedOnDeath;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        loadGeneralConfig(e.getSuggestedConfigurationFile());
        registerCorruptionEffects();
        registerBlocks();

        FMLCommonHandler.instance().bus().register(new TickHandler());
        MinecraftForge.EVENT_BUS.register(new EventHandler());

        eventChannel.register(new PacketEventHandler());

        proxy.registerRenderers();
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent e) {
        e.registerServerCommand(new CommandCorruption());
    }

    private void loadGeneralConfig(File file) {
        Configuration config = new Configuration(file);
        Property prop;

        config.load();

        prop = config.get(CATEGORY_CORRUPTION, "Enable Block Teleport Corruption Effect", true);
        isEnabledBlockTeleportCorr = prop.getBoolean(true);
        prop = config.get(CATEGORY_CORRUPTION, "Enable Stone Skin Corruption Effect", true);
        isEnabledStoneSkinCorr = prop.getBoolean(true);
        prop = config.get(CATEGORY_CORRUPTION, "Enable Water Allergy Corruption Effect", true);
        isEnabledWaterAllergyCorr = prop.getBoolean(true);
        prop = config.get(CATEGORY_CORRUPTION, "Enable Colour Blind Corruption Effect", true);
        isEnabledColourBlindCorr = prop.getBoolean(true);
        prop = config.get(CATEGORY_CORRUPTION, "Enable Afraid of the Dark Corruption Effect", true);
        isEnabledAfraidOfTheDarkCorr = prop.getBoolean(true);
        prop = config.get(CATEGORY_CORRUPTION, "Enable Blood Loss Corruption Effect", true);
        isEnabledBloodLossCorr = prop.getBoolean(true);

        prop = config.get(Configuration.CATEGORY_GENERAL, "Disable corruption on dragon death", true);
        prop.comment = "When the dragon is killed, corruption is disabled for ALL players no matter when they play";
        disableCorrOnDragonDeath = prop.getBoolean(true);
        prop = config.get(Configuration.CATEGORY_GENERAL, "Disable corruption on wither death", false);
        prop.comment = "If a player kills a wither, corruption is disabled for THAT PLAYER ONLY";
        disableCorrOnWitherDeath = prop.getBoolean(false);
        prop = config.get(Configuration.CATEGORY_GENERAL, "Corruptioned removed on death", 0);
        prop.comment = "If a player dies, this amount of corruption will be removed (set to 0 to disable)";
        corrRemovedOnDeath = prop.getInt();

        if (config.hasChanged()) config.save();
    }

    private void registerCorruptionEffects() {

        if (isEnabledBlockTeleportCorr) {
            new BlockTeleportCorruption();
        }
        if (isEnabledWaterAllergyCorr) {
            new WaterAllergyCorruption();
            CorruptionRegistry.registerRandomCorruptionEffect("waterAllergy");
        }
        if (isEnabledStoneSkinCorr) {
            new StoneSkinCorruption();
            CorruptionRegistry.registerRandomCorruptionEffect("stoneSkin");
        }
        if (isEnabledColourBlindCorr) {
            new ColourBlindCorruption();
            CorruptionRegistry.registerRandomCorruptionEffect("colourBlind");
        }
        if (isEnabledAfraidOfTheDarkCorr) {
            new AfraidOfTheDarkCorruption();
        }
        if (isEnabledBloodLossCorr) {
            new BloodLossCorruption();
        }
    }

    private void registerBlocks() {
        GameRegistry.registerBlock(blockEnderCake, "blockEnderCake");
    }
}
