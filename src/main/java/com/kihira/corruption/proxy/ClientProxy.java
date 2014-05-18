package com.kihira.corruption.proxy;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.client.EntityFootstep;
import com.kihira.corruption.client.particle.EntityFXBlood;
import com.kihira.corruption.client.render.EntityFootstepRenderer;
import com.kihira.corruption.common.CorruptionDataHelper;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    private final ResourceLocation stoneSkinTexture = new ResourceLocation("corruption", "stoneskin.png");
    private final ResourceLocation shader = new ResourceLocation("corruption", "grayscale.json");
    private final HashMap<EntityPlayer, EntityFootstep> footsteps = new HashMap<EntityPlayer, EntityFootstep>();


    @Override
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityFootstep.class, new EntityFootstepRenderer());
    }

    @Override
    public void corruptPlayerSkin(AbstractClientPlayer entityPlayer, int oldCorr, int newCorr) {
        ThreadDownloadImageData imageData = entityPlayer.getTextureSkin();
        BufferedImage bufferedImage = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imageData, "bufferedImage", "field_110560_d", "bpj.g");

        //Backup old skin
        if (oldCorr == 0) {
            this.backupPlayerSkin(entityPlayer); //TODO better way to detect when a backup is needed
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
                }
                else {
                    color = new Color(bufferedImage.getRGB(x, y)).darker();
                }
                bufferedImage.setRGB(x, y, color.getRGB());
            }
            TextureUtil.uploadTextureImage(imageData.getGlTextureId(), bufferedImage);
        }
    }

    @Override
    public void uncorruptPlayerSkinPartially(AbstractClientPlayer entityPlayer, int oldCorr, int newCorr) {
        oldCorr = oldCorr / 30;
        newCorr = newCorr / 30;
        ThreadDownloadImageData imageData = entityPlayer.getTextureSkin();
        BufferedImage bufferedImage = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imageData, "bufferedImage", "field_110560_d", "bpj.g");
        BufferedImage oldSkin = this.getOriginalPlayerSkin(entityPlayer);

        if (bufferedImage != null) {
            for (int i = newCorr; i <= oldCorr; i++) {
                Random rand = new Random(entityPlayer.getCommandSenderName().hashCode() * i);
                int x = rand.nextInt(bufferedImage.getWidth());
                int y = rand.nextInt(bufferedImage.getHeight());
                bufferedImage.setRGB(x, y, oldSkin.getRGB(x, y));
            }
        }
        TextureUtil.uploadTextureImage(imageData.getGlTextureId(), bufferedImage);
    }

    @Override
    public void uncorruptPlayerSkin(String playerName) {
        AbstractClientPlayer player = (AbstractClientPlayer) Minecraft.getMinecraft().theWorld.getPlayerEntityByName(playerName);
        if (player == null) {
            player = new EntityOtherPlayerMP(Minecraft.getMinecraft().theWorld, new GameProfile(playerName, playerName));
        }
        ThreadDownloadImageData imageData = player.getTextureSkin();
        BufferedImage bufferedImage = this.getOriginalPlayerSkin(player);

        //Load old skin
        if (bufferedImage != null) {
            imageData.setBufferedImage(bufferedImage);
            TextureUtil.uploadTextureImage(imageData.getGlTextureId(), bufferedImage);
            System.out.println("Restored " + playerName + " skin");
        }
    }

    @Override
    public void unstonifyPlayerSkin(String playerName) {
        this.uncorruptPlayerSkin(playerName);
        AbstractClientPlayer player = (AbstractClientPlayer) Minecraft.getMinecraft().theWorld.getPlayerEntityByName(playerName);
        if (player == null) {
            player = new EntityOtherPlayerMP(Minecraft.getMinecraft().theWorld, new GameProfile(playerName, playerName));
        }
        this.corruptPlayerSkin(player, 0, CorruptionDataHelper.getCorruptionForPlayer(player));
    }

    @Override
    public void stonifyPlayerSkin(AbstractClientPlayer entityPlayer, int amount) {
        ThreadDownloadImageData imageData = entityPlayer.getTextureSkin();
        BufferedImage bufferedImage = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imageData, "bufferedImage", "field_110560_d", "bpj.g");
        Random rand = new Random();

        if (bufferedImage != null) {
            try {
                BufferedImage stoneSkin = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(this.stoneSkinTexture).getInputStream());
                for (int i = 0; i < amount; i++) {
                    int x = rand.nextInt(bufferedImage.getWidth());
                    int y = rand.nextInt(bufferedImage.getHeight());
                    bufferedImage.setRGB(x, y, stoneSkin.getRGB(x, y));
                }
                TextureUtil.uploadTextureImage(imageData.getGlTextureId(), bufferedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void spawnFootprint(EntityPlayer player) {
        if (player.fallDistance == 0 && !player.worldObj.isAirBlock((int) player.posX, (int) player.boundingBox.minY - 1, (int) player.posZ)) {
            if ((this.footsteps.containsKey(player) && this.footsteps.get(player).getDistanceToEntity(player) > 1.4) || !this.footsteps.containsKey(player)) {
                EntityFootstep footstep = new EntityFootstep(player);
                player.worldObj.spawnEntityInWorld(footstep);
                this.footsteps.put(player, footstep);
                Corruption.logger.debug(I18n.format("Spawned footstep at %s, %s, %s for %s", player.posX, player.posY, player.posZ, player));
            }
        }
    }

    @Override
    public void enableGrayscaleShader() {
        if (OpenGlHelper.shadersSupported) {
            EntityRenderer entityRenderer = Minecraft.getMinecraft().entityRenderer;
            if (entityRenderer.theShaderGroup != null) {
                entityRenderer.theShaderGroup.deleteShaderGroup();
            }

            try {
                entityRenderer.theShaderGroup = new ShaderGroup(Minecraft.getMinecraft().getResourceManager(), Minecraft.getMinecraft().getFramebuffer(), this.shader);
                entityRenderer.theShaderGroup.createBindFramebuffers(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void backupPlayerSkin(AbstractClientPlayer entityPlayer) {
        ThreadDownloadImageData imageData = entityPlayer.getTextureSkin();
        BufferedImage bufferedImage = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imageData, "bufferedImage", "field_110560_d", "bpj.g");

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
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }
}
