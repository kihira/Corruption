package com.kihira.corruption.proxy;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.TextureUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class ClientProxy extends CommonProxy {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void corruptPlayerSkin(AbstractClientPlayer entityPlayer, int oldCorr, int newCorr) {
        ThreadDownloadImageData imageData = entityPlayer.getTextureSkin();
        BufferedImage bufferedImage = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imageData, "bufferedImage"); //TODO need to fix this for obf?

        //Backup old skin
        if (oldCorr == 0) {
            File file = new File("skinbackup");
            file.mkdir();
            File skinFile = new File(file, entityPlayer.getCommandSenderName() + ".png");
            try {
                if (skinFile.exists()) {
                    //If corr is 0 and we already have a skin for this player, load this just incase
                    bufferedImage = ImageIO.read(skinFile);
                }
                else skinFile.createNewFile();
                ImageIO.write(bufferedImage, "PNG", skinFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (bufferedImage != null) {
            for (int i = oldCorr; i <= newCorr; i++) {
                Random rand = new Random(entityPlayer.getCommandSenderName().hashCode() * i);
                int x = rand.nextInt(bufferedImage.getWidth());
                int y = rand.nextInt(bufferedImage.getHeight());
                bufferedImage.setRGB(x, y, new Color(bufferedImage.getRGB(x, y)).darker().getRGB());
            }
        }
        TextureUtil.uploadTextureImage(imageData.getGlTextureId(), bufferedImage);
    }

    @Override
    //TODO fix
    public void uncorruptPlayerSkinPartially(AbstractClientPlayer entityPlayer, int oldCorr, int newCorr) {
        ThreadDownloadImageData imageData = entityPlayer.getTextureSkin();
        BufferedImage bufferedImage = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imageData, "bufferedImage"); //TODO need to fix this for obf?

        //Load old skin
        File file = new File("skinbackup" + File.separator + entityPlayer.getCommandSenderName() + ".png");
        try {
            BufferedImage oldSkin = ImageIO.read(file);
            if (bufferedImage != null) {
                for (int i = newCorr; i <= oldCorr; i++) {
                    Random rand = new Random(entityPlayer.getCommandSenderName().hashCode() * i);
                    int x = rand.nextInt(bufferedImage.getWidth());
                    int y = rand.nextInt(bufferedImage.getHeight());
                    bufferedImage.setRGB(x, y, oldSkin.getRGB(x, y));
                }
            }
            TextureUtil.uploadTextureImage(imageData.getGlTextureId(), bufferedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void uncorruptPlayerSkin(AbstractClientPlayer entityPlayer) {
        entityPlayer.getTextureSkin();
        ThreadDownloadImageData imageData = entityPlayer.getTextureSkin();

        //Load old skin
        File file = new File("skinbackup" + File.separator + entityPlayer.getCommandSenderName() + ".png");
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            imageData.setBufferedImage(bufferedImage);
            TextureUtil.uploadTextureImage(imageData.getGlTextureId(), bufferedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
