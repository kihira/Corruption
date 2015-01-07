package com.kihira.corruption.client.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Based off of the texture helper class from tails, at least in the sense that it modifies player's skins.
 */
public class TextureHelper {

    public void applySkinModifierToPlayer(AbstractClientPlayer player, ISkinModifier textureModifier, int oldCorr, int newCorr) {
        BufferedImage bufferedImage = kihira.foxlib.client.TextureHelper.getPlayerSkinAsBufferedImage(player);

        if (!hasBackup(player))
            backupPlayerSkin(player);

        bufferedImage = textureModifier.Apply(player, bufferedImage, getOriginalPlayerSkin(player), oldCorr, newCorr);

        uploadPlayerSkin(player, bufferedImage);

        throw new NoSuchMethodError("applyTextureModifierToPlayer has yet to be implemented!");
    }

    private void uploadPlayerSkin(AbstractClientPlayer player, BufferedImage bufferedImage) {
        TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
        ITextureObject textureObject = texturemanager.getTexture(player.getLocationSkin());

        if (textureObject == null) {
            textureObject = new ThreadDownloadImageData(null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[]{StringUtils.stripControlCodes(player.getCommandSenderName())}), AbstractClientPlayer.locationStevePng, new ImageBufferDownload());
            texturemanager.loadTexture(player.getLocationSkin(), textureObject);
        }

        kihira.foxlib.client.TextureHelper.uploadTexture(textureObject, bufferedImage);
    }

    public boolean hasBackup(AbstractClientPlayer player) {
        return new File("skinbackup" + File.separator + player.getCommandSenderName() + ".png").exists();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void backupPlayerSkin(AbstractClientPlayer entityPlayer) {
        BufferedImage bufferedImage = kihira.foxlib.client.TextureHelper.getPlayerSkinAsBufferedImage((EntityPlayerSP) entityPlayer);

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
