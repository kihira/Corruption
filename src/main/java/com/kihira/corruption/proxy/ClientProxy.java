package com.kihira.corruption.proxy;

import com.kihira.corruption.Corruption;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.I18n;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class ClientProxy extends CommonProxy {

    @Override
    public void corruptPlayerSkin(AbstractClientPlayer entityPlayer, int oldCorr, int newCorr) {
        Random rand = new Random(entityPlayer.getCommandSenderName().hashCode() + newCorr);
        entityPlayer.getTextureSkin();
        ThreadDownloadImageData imageData = entityPlayer.getTextureSkin();
        BufferedImage bufferedImage = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imageData, "bufferedImage"); //TODO need to fix this for obf?

        for (int i = oldCorr; i <= newCorr; i++) {
            if (bufferedImage != null) {
                int x = rand.nextInt(bufferedImage.getWidth());
                int y = rand.nextInt(bufferedImage.getHeight());
                Corruption.logger.info(I18n.format("Changed pixel at %d, %d for %s", x, y, entityPlayer.getCommandSenderName()));
                bufferedImage.setRGB(x, y, new Color(bufferedImage.getRGB(x, y)).darker().getRGB());
                TextureUtil.uploadTextureImage(imageData.getGlTextureId(), bufferedImage);
            }
        }
    }
}
