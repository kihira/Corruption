package com.kihira.corruption.proxy;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.client.EntityFootstep;
import com.kihira.corruption.client.particle.EntityFXBlood;
import com.kihira.corruption.client.render.EntityFootstepRenderer;
import com.kihira.corruption.common.CorruptionDataHelper;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    private final ResourceLocation stoneSkinTexture = new ResourceLocation("corruption", "stoneskin.png");
    private final ResourceLocation shader = new ResourceLocation("corruption", "grayscale.json");
    private final HashMap<EntityPlayer, EntityFootstep> footsteps = new HashMap<EntityPlayer, EntityFootstep>();

    //TODO This holds the corrupted image of the players skin so we don't constantly reload it from minecrafts cache (which is vanilla)
    private final WeakHashMap<ResourceLocation, BufferedImage> playerSkinCurrent = new WeakHashMap<ResourceLocation, BufferedImage>();

    //TODO add in a way to check if player skin has refreshed (such as updating to custom skin thanks to slow skin servers)
    private BufferedImage getBufferedImageSkin(ResourceLocation playerSkin) {
        BufferedImage bufferedImage = null;
        InputStream inputStream = null;

        if (this.playerSkinCurrent.containsKey(playerSkin)) {
            return this.playerSkinCurrent.get(playerSkin);
        } else {
            try {
                inputStream = Minecraft.getMinecraft().getResourceManager().getResource(playerSkin).getInputStream();
                bufferedImage = ImageIO.read(inputStream);
                playerSkinCurrent.put(playerSkin, bufferedImage);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return bufferedImage;
        }
    }

    private ResourceLocation getPlayerSkinResourceLocation(GameProfile gameProfile) {
        ResourceLocation resourceLocation = AbstractClientPlayer.locationStevePng;
        if (gameProfile != null) {
            Minecraft minecraft = Minecraft.getMinecraft();
            Map map = minecraft.func_152342_ad().func_152788_a(gameProfile);

            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                resourceLocation = minecraft.func_152342_ad().func_152792_a((MinecraftProfileTexture)map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            }
        }
        return resourceLocation;
    }

    private void uploadPlayerSkin(ResourceLocation resourceLocation, BufferedImage bufferedImage) {
        //Get the texture ID for the skin and upload it ourselves
        ITextureObject itextureobject = Minecraft.getMinecraft().getTextureManager().getTexture(resourceLocation);
        if (itextureobject != null) {
            this.playerSkinCurrent.put(resourceLocation, bufferedImage);
            TextureUtil.uploadTextureImage(itextureobject.getGlTextureId(), bufferedImage);
        }
    }

    @Override
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityFootstep.class, new EntityFootstepRenderer());
    }

    @Override
    public void corruptPlayerSkin(AbstractClientPlayer entityPlayer, int oldCorr, int newCorr) {
        ResourceLocation playerSkin = this.getPlayerSkinResourceLocation(entityPlayer.getGameProfile());
        BufferedImage bufferedImage = this.getBufferedImageSkin(playerSkin);

        if (!this.hasBackup(entityPlayer)) {
            this.backupPlayerSkin(entityPlayer);
        }

        if (bufferedImage != null) {
            for (int i = oldCorr; i <= newCorr; i++) {
                Random rand = new Random(entityPlayer.getCommandSenderName().hashCode() * i);
                int x = rand.nextInt(bufferedImage.getWidth());
                int y = rand.nextInt(bufferedImage.getHeight());
                Color color;
                //Eyes
                if (y == 12 && (x == 9 || x == 10 || x == 13 || x == 14 || x == 41 || x == 42 || x == 45 || x == 46)) {
                    color = new Color(204, 0, 250);
                } else {
                    color = new Color(bufferedImage.getRGB(x, y)).darker();
                }
                bufferedImage.setRGB(x, y, color.getRGB());
            }
            this.uploadPlayerSkin(playerSkin, bufferedImage);
        }
        else System.out.println("Noooo");
    }

    @Override
    public void uncorruptPlayerSkinPartially(AbstractClientPlayer entityPlayer, int oldCorr, int newCorr) {
        oldCorr = oldCorr / 30;
        newCorr = newCorr / 30;
        ResourceLocation playerSkin = this.getPlayerSkinResourceLocation(entityPlayer.getGameProfile());
        BufferedImage bufferedImage = this.getBufferedImageSkin(playerSkin);
        BufferedImage oldSkin = this.getOriginalPlayerSkin(entityPlayer);

        if (bufferedImage != null && oldSkin != null) {
            for (int i = newCorr; i <= oldCorr; i++) {
                Random rand = new Random(entityPlayer.getCommandSenderName().hashCode() * i);
                int x = rand.nextInt(bufferedImage.getWidth());
                int y = rand.nextInt(bufferedImage.getHeight());
                bufferedImage.setRGB(x, y, oldSkin.getRGB(x, y));
            }
        }
        this.uploadPlayerSkin(playerSkin, bufferedImage);
    }

    @Override
    public void uncorruptPlayerSkin(AbstractClientPlayer entityPlayer) {
        ResourceLocation playerSkin = this.getPlayerSkinResourceLocation(entityPlayer.getGameProfile());
        BufferedImage oldSkin = this.getOriginalPlayerSkin(entityPlayer);

        //Load old skin
        if (oldSkin != null) {
            this.uploadPlayerSkin(playerSkin, oldSkin);
            System.out.println("Restored " + entityPlayer.getCommandSenderName() + " skin");
        }
    }

    @Override
    public void unstonifyPlayerSkin(AbstractClientPlayer entityPlayer) {
        this.uncorruptPlayerSkin(entityPlayer);
        this.corruptPlayerSkin(entityPlayer, 0, CorruptionDataHelper.getCorruptionForPlayer(entityPlayer));
    }

    @Override
    public void stonifyPlayerSkin(AbstractClientPlayer entityPlayer, int amount) {
        ResourceLocation playerSkin = this.getPlayerSkinResourceLocation(entityPlayer.getGameProfile());
        BufferedImage bufferedImage = this.getBufferedImageSkin(playerSkin);
        Random rand = new Random();
        InputStream inputStream = null;

        if (!this.hasBackup(entityPlayer)) {
            this.backupPlayerSkin(entityPlayer);
        }

        if (bufferedImage != null) {
            try {
                inputStream = Minecraft.getMinecraft().getResourceManager().getResource(this.stoneSkinTexture).getInputStream();
                BufferedImage stoneSkin = ImageIO.read(inputStream);
                for (int i = 0; i < amount; i++) {
                    int x = rand.nextInt(bufferedImage.getWidth());
                    int y = rand.nextInt(bufferedImage.getHeight());
                    bufferedImage.setRGB(x, y, stoneSkin.getRGB(x, y));
                }
                this.uploadPlayerSkin(playerSkin, bufferedImage);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    @Override
    public void spawnFootprint(EntityPlayer player) {
        if (player.fallDistance == 0 && !player.isInWater() && !player.worldObj.isAirBlock((int) player.posX, (int) player.boundingBox.minY - 1, (int) player.posZ)) {
            if ((this.footsteps.containsKey(player) && this.footsteps.get(player).getDistanceToEntity(player) > 1.4) || !this.footsteps.containsKey(player)) {
                EntityFootstep footstep = new EntityFootstep(player);
                player.worldObj.spawnEntityInWorld(footstep);
                this.footsteps.put(player, footstep);
            }
        }
    }

    @Override
    public void enableGrayscaleShader() {
        if (OpenGlHelper.shadersSupported) {
            Minecraft mc = Minecraft.getMinecraft();
            EntityRenderer entityRenderer = mc.entityRenderer;
            if (entityRenderer.theShaderGroup != null) {
                entityRenderer.theShaderGroup.deleteShaderGroup();
            }

            try {
                entityRenderer.theShaderGroup = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), this.shader);
                entityRenderer.theShaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            } catch (IOException ioexception) {
                Corruption.logger.warn("Failed to load shader: ", ioexception);
            }
        }
    }

    @Override
    public void disableGrayscaleShader() {
        if (OpenGlHelper.shadersSupported) {
            EntityRenderer entityRenderer = Minecraft.getMinecraft().entityRenderer;
            if (entityRenderer.getShaderGroup() != null) {
                entityRenderer.getShaderGroup().deleteShaderGroup();
            }
            entityRenderer.theShaderGroup = null;
        }
    }

    @Override
    public void spawnBloodParticle(EntityPlayer player) {
        for (float i = player.getHealth(); i >= 0; i--) {
            EntityFXBlood blood = new EntityFXBlood(player);
            Minecraft.getMinecraft().effectRenderer.addEffect(blood);
        }
    }

    //TODO retire this backup system and just use resource location instead?
    public boolean hasBackup(AbstractClientPlayer player) {
        return new File("skinbackup" + File.separator + player.getCommandSenderName() + ".png").exists();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void backupPlayerSkin(AbstractClientPlayer entityPlayer) {
        ResourceLocation playerSkin = this.getPlayerSkinResourceLocation(entityPlayer.getGameProfile());
        BufferedImage bufferedImage = this.getBufferedImageSkin(playerSkin);

        File file = new File("skinbackup");
        file.mkdir();
        File skinFile = new File(file, entityPlayer.getCommandSenderName() + ".png");
        try {
/*                if (skinFile.exists()) {
                    //If corr is 0 and we already have a skin for this player, load this just incase
                    bufferedImage = ImageIO.read(skinFile);
                }*/
            skinFile.createNewFile();
            if (bufferedImage != null) ImageIO.write(bufferedImage, "PNG", skinFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage getOriginalPlayerSkin(AbstractClientPlayer entityPlayer) {
        File file = new File("skinbackup" + File.separator + entityPlayer.getCommandSenderName() + ".png");
        BufferedImage bufferedImage = null;
        ResourceLocation playerSkin = this.getPlayerSkinResourceLocation(entityPlayer.getGameProfile());

        try {
            if (file.exists()) {
                bufferedImage = ImageIO.read(file);
            }
            //Load skin from Mojang servers
            else {
                Minecraft.getMinecraft().getTextureManager().getTexture(playerSkin).loadTexture(Minecraft.getMinecraft().getResourceManager());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }
}
