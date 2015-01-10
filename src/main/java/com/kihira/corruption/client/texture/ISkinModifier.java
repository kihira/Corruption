package com.kihira.corruption.client.texture;

import net.minecraft.client.entity.AbstractClientPlayer;

import java.awt.image.BufferedImage;

/**
 * Contains an update method that is called every tick when the Texture Modifier is to be applied to an object.
 * Get creative with application effects, you can store little bits of information on the skin with a pixel and take
 * advantage of that to make neat effects for applying the modifier.
 */
public interface ISkinModifier {

    /**
     * Called each tick to apply the modifier.
     *
     * @param imageTexture The current texture
     * @param cleanImage   The texture before the modifier was applied
     */
    BufferedImage Apply(AbstractClientPlayer player, BufferedImage imageTexture, BufferedImage cleanImage, int percentComplete, int oldCorr, int newCorr);

    /**
     * Called each tick to remove the modifier slowly.
     *
     * @param imageTexture The current texture
     * @param cleanImage   The texture before the modifier was applied
     */
    BufferedImage UnApply(AbstractClientPlayer player, BufferedImage imageTexture, BufferedImage cleanImage, int percentComplete, int oldCorr, int newCorr);

}
