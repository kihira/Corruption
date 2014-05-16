package com.kihira.corruption.proxy;

import com.kihira.corruption.client.EntityFootstep;
import com.kihira.corruption.client.render.EntityFootstepRenderer;
import cpw.mods.fml.client.registry.RenderingRegistry;
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

    @Override
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityFootstep.class, new EntityFootstepRenderer());
    }

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
                Color color;
                //Eyes
                if (y == 12 && (x == 9 || x == 10 || x == 13 || x == 14)) {
                    color = new Color(204, 0, 250);
                }
                else if (y == 12 && (x == 41 || x == 42 || x == 45 || x == 46)) {
                    color = new Color(204, 0, 250);
                }
                else {
                    color = new Color(bufferedImage.getRGB(x, y)).darker();
                }
                bufferedImage.setRGB(x, y, color.getRGB());
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
