package com.kihira.corruption;

import com.kihira.corruption.client.CreativeTabCorruption;
import com.kihira.corruption.client.diary.PageData;
import com.kihira.corruption.common.CommandCorruption;
import com.kihira.corruption.common.EventHandler;
import com.kihira.corruption.common.FMLEventHandler;
import com.kihira.corruption.common.GuiHandler;
import com.kihira.corruption.common.block.BlockEnderCake;
import com.kihira.corruption.common.corruption.*;
import com.kihira.corruption.common.item.ItemDiary;
import com.kihira.corruption.common.item.ItemFleshArmor;
import com.kihira.corruption.common.network.CorruptionEffectMessage;
import com.kihira.corruption.common.network.CorruptionUpdateMessage;
import com.kihira.corruption.common.network.DiaryEntriesMessage;
import com.kihira.corruption.proxy.CommonProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(name = "Corruption", modid = "corruption")
public class Corruption {

    public static final CreativeTabCorruption creativeTab = new CreativeTabCorruption();
    public static final BlockEnderCake blockEnderCake = new BlockEnderCake();
    public static final ItemDiary itemDiary = new ItemDiary();
    public static final Item itemFleshArmourHelmet = new ItemFleshArmor(0).setUnlocalizedName("fleshHelmet").setTextureName("corruption:flesh_helmet");
    public static final Item itemFleshArmourChest = new ItemFleshArmor(1).setUnlocalizedName("fleshChest").setTextureName("corruption:flesh_chestplate");
    public static final Item itemFleshArmourLegs = new ItemFleshArmor(2).setUnlocalizedName("fleshLegs").setTextureName("corruption:flesh_leggings");
    public static final Item itemFleshArmourBoots = new ItemFleshArmor(3).setUnlocalizedName("fleshBoots").setTextureName("corruption:flesh_boots");
    public static final Logger logger = LogManager.getLogger("Corruption");
    public static final String CATEGORY_CORRUPTION = "corruption";
    @SidedProxy(clientSide = "com.kihira.corruption.proxy.ClientProxy", serverSide = "com.kihira.corruption.proxy.CommonProxy")
    public static CommonProxy proxy;
    @Mod.Instance
    public static Corruption instance;
    public static SimpleNetworkWrapper networkWrapper;
    public static boolean isCorruptionActiveGlobal = true;
    public static boolean isDebugMode;
    public static boolean isEnabledBlockTeleportCorr;
    public static boolean isEnabledStoneSkinCorr;
    public static boolean isEnabledWaterAllergyCorr;
    public static boolean isEnabledColourBlindCorr;
    public static boolean isEnabledAfraidOfTheDarkCorr;
    public static boolean isEnabledBloodLossCorr;
    public static boolean isEnabledGluttonyCorr;
    public static ColourBlindCorruption colourBlindCorruption;
    public static GluttonyCorruption gluttonyCorruption;
    public static StoneSkinCorruption stoneSkinCorruption;
    public static WaterAllergyCorruption waterAllergyCorruption;
    public static BlockTeleportCorruption blockTeleportCorruption;
    public static Configuration config;

    public static boolean disableCorrOnDragonDeath;
    public static boolean disableCorrOnWitherDeath;
    public static int corrRemovedOnDeath;
    public static int corrSpeed;

    public static void setDiableCorruption() {
        Property prop = config.get(Configuration.CATEGORY_GENERAL, "Disable Corruption", false);
        prop.set(true);
        config.save();
        isCorruptionActiveGlobal = false;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        loadGeneralConfig(e.getSuggestedConfigurationFile());
        registerCorruptionEffects();
        registerBlocks();
        registerItems();
        registerRecipes();
        registerNetworking();

        FMLCommonHandler.instance().bus().register(new FMLEventHandler());
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

        proxy.registerRenderers();
        PageData.registerPageData();
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent e) {
        e.registerServerCommand(new CommandCorruption());
    }

    private void loadGeneralConfig(File file) {
        config = new Configuration(file);
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
        prop = config.get(CATEGORY_CORRUPTION, "Enable Gluttony Corruption Effect", true);
        isEnabledGluttonyCorr = prop.getBoolean(true);

        prop = config.get(Configuration.CATEGORY_GENERAL, "Disable corruption on dragon death", true);
        prop.comment = "When the dragon is killed, corruption is disabled for ALL players no matter when they play";
        disableCorrOnDragonDeath = prop.getBoolean(true);
        prop = config.get(Configuration.CATEGORY_GENERAL, "Disable corruption on wither death", false);
        prop.comment = "If a player kills a wither, corruption is disabled for THAT PLAYER ONLY";
        disableCorrOnWitherDeath = prop.getBoolean(false);
        prop = config.get(Configuration.CATEGORY_GENERAL, "Corruption removed on death", 0);
        prop.comment = "If a player dies, this amount of corruption will be removed (set to 0 to disable)";
        corrRemovedOnDeath = prop.getInt();
        prop = config.get(Configuration.CATEGORY_GENERAL, "Corruption Speed", 200);
        prop.comment = "How often in ticks to apply corruption. ModJam speed is 20, normal speed is 200";
        corrSpeed = prop.getInt();

        prop = config.get(Configuration.CATEGORY_GENERAL, "Disable Corruption", false);
        prop.comment = "DO NOT CHANGE THIS >=(";
        isCorruptionActiveGlobal = !prop.getBoolean(false);

        prop = config.get(Configuration.CATEGORY_GENERAL, "Debug Mode", false);
        prop.comment = "Debug mode is used to display player's corruption above their heads could be useful for development";
        isDebugMode = prop.getBoolean(false);

        if (config.hasChanged()) config.save();
    }

    private void registerCorruptionEffects() {

        if (isEnabledWaterAllergyCorr) {
            waterAllergyCorruption = new WaterAllergyCorruption();
            CorruptionRegistry.registerCorruptionEffect("waterAllergy", waterAllergyCorruption);
            CorruptionRegistry.registerRandomCorruptionEffect("waterAllergy");
        }
        if (isEnabledStoneSkinCorr) {
            stoneSkinCorruption = new StoneSkinCorruption();
            CorruptionRegistry.registerCorruptionEffect("stoneSkin", stoneSkinCorruption);
            CorruptionRegistry.registerRandomCorruptionEffect("stoneSkin");
        }
        if (isEnabledColourBlindCorr) {
            colourBlindCorruption = new ColourBlindCorruption();
            CorruptionRegistry.registerCorruptionEffect("colourBlind", colourBlindCorruption);
            CorruptionRegistry.registerRandomCorruptionEffect("colourBlind");
        }
        if (isEnabledGluttonyCorr) {
            gluttonyCorruption = new GluttonyCorruption();
            CorruptionRegistry.registerCorruptionEffect("gluttony", gluttonyCorruption);
            CorruptionRegistry.registerRandomCorruptionEffect("gluttony");
        }
        if (isEnabledAfraidOfTheDarkCorr) {
            CorruptionRegistry.registerCorruptionEffect("afraidOfTheDark", new AfraidOfTheDarkCorruption());
        }
        if (isEnabledBlockTeleportCorr) {
            blockTeleportCorruption = new BlockTeleportCorruption();
            CorruptionRegistry.registerCorruptionEffect("blockTeleport", blockTeleportCorruption);
        }
        if (isEnabledBloodLossCorr) {
            CorruptionRegistry.registerCorruptionEffect("bloodLoss", new BloodLossCorruption());
        }
    }

    private void registerBlocks() {
        GameRegistry.registerBlock(blockEnderCake, "blockEnderCake");
    }

    private void registerItems() {
        GameRegistry.registerItem(itemDiary, "itemDiary");
        GameRegistry.registerItem(itemFleshArmourHelmet, "itemFleshArmourHelmet");
        GameRegistry.registerItem(itemFleshArmourChest, "itemFleshArmourChest");
        GameRegistry.registerItem(itemFleshArmourLegs, "itemFleshArmourLegs");
        GameRegistry.registerItem(itemFleshArmourBoots, "itemFleshArmourBoots");
    }

    private void registerRecipes() {
        GameRegistry.addRecipe(new ItemStack(blockEnderCake, 1), "EPE", "BEB", "CCC", 'A', Items.milk_bucket, 'B', Items.sugar, 'C', Items.wheat, 'E', Items.egg, 'P', Items.ender_pearl);
        GameRegistry.addRecipe(new ItemStack(itemFleshArmourHelmet, 1), "FFF", "FLF", "   ", 'F', Items.rotten_flesh, 'L', Items.leather_helmet);
        GameRegistry.addRecipe(new ItemStack(itemFleshArmourChest, 1), "FFF", "FLF", "FFF", 'F', Items.rotten_flesh, 'L', Items.leather_helmet);
        GameRegistry.addRecipe(new ItemStack(itemFleshArmourLegs, 1), "FFF", "FLF", "F F", 'F', Items.rotten_flesh, 'L', Items.leather_helmet);
        GameRegistry.addRecipe(new ItemStack(itemFleshArmourBoots, 1), "   ", "F F", "FLF", 'F', Items.rotten_flesh, 'L', Items.leather_helmet);
        GameRegistry.addRecipe(new ItemStack(itemDiary, 1), "BL ", "P  ", "   ", 'B', Items.book, 'L', Items.leather, 'P', Items.paper);
    }

    private void registerNetworking() {
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("Corruption");
        networkWrapper.registerMessage(CorruptionUpdateMessage.Handler.class, CorruptionUpdateMessage.class, 0, Side.CLIENT);
        networkWrapper.registerMessage(CorruptionEffectMessage.Handler.class, CorruptionEffectMessage.class, 1, Side.CLIENT);
        networkWrapper.registerMessage(DiaryEntriesMessage.Handler.class, DiaryEntriesMessage.class, 2, Side.CLIENT);
    }
}
