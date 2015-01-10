package com.kihira.corruption.client.texture;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Based off of the texture helper class from tails, at least in the sense that it modifies player's skins.
 */
public class SkinHelper {

    public HashMap<UUID, BufferedImage> defaultPlayerSkins = new HashMap<UUID, BufferedImage>();
    public List<UUID> steveSkinPlayers = new ArrayList<UUID>();

    public void applySkinModifierToPlayer(AbstractClientPlayer player, ISkinModifier textureModifier, int percentComplete, int oldCorr, int newCorr) {
        createBackup(player);
        BufferedImage bufferedImage = kihira.foxlib.client.TextureHelper.getPlayerSkinAsBufferedImage(player);

        bufferedImage = textureModifier.Apply(player, bufferedImage, getOriginalPlayerSkin(player), percentComplete, oldCorr, newCorr);

        uploadPlayerSkin(player, bufferedImage);
    }


    // Player Skin Backups

    public void unapplySkinModifierToPlayer(AbstractClientPlayer player, ISkinModifier textureModifier, int percentComplete, int oldCorr, int newCorr) {
        createBackup(player);
        BufferedImage bufferedImage = kihira.foxlib.client.TextureHelper.getPlayerSkinAsBufferedImage(player);

        bufferedImage = textureModifier.UnApply(player, bufferedImage, getOriginalPlayerSkin(player), percentComplete, oldCorr, newCorr);

        uploadPlayerSkin(player, bufferedImage);
    }

    private void uploadPlayerSkin(AbstractClientPlayer player, BufferedImage bufferedImage) {
        TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
        ITextureObject textureObject = texturemanager.getTexture(player.getLocationSkin());

        if (textureObject == null) {
            textureObject = new ThreadDownloadImageData(null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[]{StringUtils.stripControlCodes(player.getCommandSenderName())}), AbstractClientPlayer.locationStevePng, new ImageBufferDownload());
            texturemanager.loadTexture(player.getLocationSkin(), textureObject);
        }

        TextureUtil.uploadTextureImage(textureObject.getGlTextureId(), bufferedImage);
    }

    public void createBackup(AbstractClientPlayer player) {
        // Player skin location is null. It's a steve.
        if (!defaultPlayerSkins.containsKey(player.getGameProfile().getId()) && !steveSkinPlayers.contains(player.getGameProfile().getId())) {
            if (!player.func_152123_o()) {
                steveSkinPlayers.add(player.getGameProfile().getId());
                return;
            }
            if (kihira.foxlib.client.TextureHelper.getPlayerSkinAsBufferedImage(player) != null) {
                defaultPlayerSkins.put(player.getGameProfile().getId(), kihira.foxlib.client.TextureHelper.getPlayerSkinAsBufferedImage(player));
                uploadPlayerSkin(player, defaultPlayerSkins.get(player.getGameProfile().getId()));
            }
        }
    }

    public void restoreDefaultPlayerSkin(AbstractClientPlayer player) {
        UUID id = player.getGameProfile().getId();
        ResourceLocation playerSkinLocation = null;
        if (defaultPlayerSkins.containsKey(id)) {
            BufferedImage playerSkin = defaultPlayerSkins.get(id);
            Map map = Minecraft.getMinecraft().func_152342_ad().func_152788_a(player.getGameProfile());
            if (map.containsKey(MinecraftProfileTexture.Type.SKIN))
                playerSkinLocation = Minecraft.getMinecraft().func_152342_ad().func_152792_a((MinecraftProfileTexture) map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
        } else {
            //Steve Skin
            playerSkinLocation = AbstractClientPlayer.locationStevePng;
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(playerSkinLocation);
    }

    public BufferedImage getOriginalPlayerSkin(AbstractClientPlayer entityPlayer) {
        return defaultPlayerSkins.containsKey(entityPlayer.getGameProfile().getId()) ? defaultPlayerSkins.get(entityPlayer.getGameProfile().getId()) : null;
    }


}
