package com.kihira.corruption.proxy;

import com.kihira.corruption.Corruption;
import com.kihira.corruption.client.EntityFootstep;
import com.kihira.corruption.client.particle.EntityFXBlood;
import com.kihira.corruption.client.render.EntityFootstepRenderer;
import com.kihira.corruption.client.texture.SkinHelper;
import com.kihira.corruption.common.CorruptionDataHelper;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.foxlib.client.TextureHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
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
    public SkinHelper skinHelper = new SkinHelper();

    @Override
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityFootstep.class, new EntityFootstepRenderer());
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
}
