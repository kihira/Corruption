/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package com.kihira.corruption.client.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class StoneSkinModifier implements ISkinModifier {

    private final ResourceLocation stoneSkinTexture = new ResourceLocation("corruption", "stoneskin.png");

    @Override
    public BufferedImage Apply(AbstractClientPlayer player, BufferedImage bufferedImage, BufferedImage cleanImage, int percentComplete, int oldCorr, int newCorr) {
        Random rand = new Random();
        InputStream inputStream = null;

        if (bufferedImage != null) {
            try {
                inputStream = Minecraft.getMinecraft().getResourceManager().getResource(this.stoneSkinTexture).getInputStream();
                BufferedImage stoneSkin = ImageIO.read(inputStream);

                for (int i = 0; i < percentComplete; i++) {
                    int x = rand.nextInt(bufferedImage.getWidth());
                    int y = rand.nextInt(bufferedImage.getHeight());
                    bufferedImage.setRGB(x, y, stoneSkin.getRGB(x, y));
                }
                return bufferedImage;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }

        return null;
    }

    @Override
    public BufferedImage UnApply(AbstractClientPlayer player, BufferedImage imageTexture, BufferedImage cleanImage, int percentComplete, int oldCorr, int newCorr) {
        return null;
    }
}
