package com.kihira.corruption.client.texture;

import net.minecraft.client.entity.AbstractClientPlayer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CorruptionTextureModifier implements ISkinModifier {

    @Override
    public BufferedImage Apply(AbstractClientPlayer player, BufferedImage bufferedImage, BufferedImage cleanImage, int oldCorr, int newCorr) {
        if (bufferedImage != null) {
            for (int i = oldCorr; i <= newCorr; i++) {
                Random rand = new Random(player.getCommandSenderName().hashCode() * i);
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
            return bufferedImage;
        } else System.out.println("Buffered image is null.");
        return null;
    }

}
