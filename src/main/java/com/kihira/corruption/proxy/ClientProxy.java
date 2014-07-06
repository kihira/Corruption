package com.kihira.corruption.proxy;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.client.EntityFootstep;
import com.kihira.corruption.client.particle.EntityFXBlood;
import com.kihira.corruption.client.render.EntityFootstepRenderer;
import com.kihira.corruption.common.CorruptionDataHelper;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.foxlib.client.TextureHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.ThreadDownloadImageData;
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
import java.util.Random;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    private final ResourceLocation stoneSkinTexture = new ResourceLocation("corruption", "stoneskin.png");
    private final ResourceLocation shader = new ResourceLocation("corruption", "grayscale.json");
    private final HashMap<EntityPlayer, EntityFootstep> footsteps = new HashMap<EntityPlayer, EntityFootstep>();

    private void uploadPlayerSkin(AbstractClientPlayer player, BufferedImage bufferedImage) {
        ThreadDownloadImageData imageData = AbstractClientPlayer.getDownloadImageSkin(player.getLocationSkin(), player.getCommandSenderName());
        TextureHelper.uploadTexture(imageData, bufferedImage);
    }

    @Override
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityFootstep.class, new EntityFootstepRenderer());
    }

    @Override
    public void corruptPlayerSkin(AbstractClientPlayer entityPlayer, int oldCorr, int newCorr) {
        BufferedImage bufferedImage = TextureHelper.getPlayerSkinAsBufferedImage((EntityPlayerSP) entityPlayer);

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
            this.uploadPlayerSkin(entityPlayer, bufferedImage);
        }
        else System.out.println("Noooo");
    }

    @Override
    public void uncorruptPlayerSkinPartially(AbstractClientPlayer entityPlayer, int oldCorr, int newCorr) {
        oldCorr = oldCorr / 30;
        newCorr = newCorr / 30;
        BufferedImage bufferedImage = TextureHelper.getPlayerSkinAsBufferedImage((EntityPlayerSP) entityPlayer);
        BufferedImage oldSkin = this.getOriginalPlayerSkin(entityPlayer);

        if (bufferedImage != null && oldSkin != null) {
            for (int i = newCorr; i <= oldCorr; i++) {
                Random rand = new Random(entityPlayer.getCommandSenderName().hashCode() * i);
                int x = rand.nextInt(bufferedImage.getWidth());
                int y = rand.nextInt(bufferedImage.getHeight());
                bufferedImage.setRGB(x, y, oldSkin.getRGB(x, y));
            }
        }
        this.uploadPlayerSkin(entityPlayer, bufferedImage);
    }

    @Override
    public void uncorruptPlayerSkin(AbstractClientPlayer entityPlayer) {
        TextureHelper.restoreOriginalTexture(entityPlayer.getLocationSkin());
    }

    @Override
    public void unstonifyPlayerSkin(AbstractClientPlayer entityPlayer) {
        this.uncorruptPlayerSkin(entityPlayer);
        this.corruptPlayerSkin(entityPlayer, 0, CorruptionDataHelper.getCorruptionForPlayer(entityPlayer));
    }

    @Override
    public void stonifyPlayerSkin(AbstractClientPlayer entityPlayer, int amount) {
        BufferedImage bufferedImage = TextureHelper.getPlayerSkinAsBufferedImage((EntityPlayerSP) entityPlayer);
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
                this.uploadPlayerSkin(entityPlayer, bufferedImage);
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
        BufferedImage bufferedImage = TextureHelper.getPlayerSkinAsBufferedImage((EntityPlayerSP) entityPlayer);

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
            if (file.exists()) {
                bufferedImage = ImageIO.read(file);
            }
            //Load skin from Mojang servers
            else {
                //Minecraft.getMinecraft().getTextureManager().getTexture(entityPlayer.).loadTexture(Minecraft.getMinecraft().getResourceManager());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }
}
