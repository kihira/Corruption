package com.kihira.corruption.client.render;

import com.kihira.corruption.client.EntityFootstep;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class EntityFootstepRenderer extends Render {

    private final ResourceLocation footstepTexture = new ResourceLocation("corruption", "footstep.png");

    public EntityFootstepRenderer() {
        this.shadowSize = 0F;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float var8, float partialTickTime) {
        EntityFootstep footstep = (EntityFootstep) entity;
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTranslated(x, y + 0.1F, z - 0.5F);
        GL11.glRotatef(90F, 1F, 0F, 0F);
        Tessellator tessellator = Tessellator.instance;
        float f2 = 0.5F;
        float f3 = 0.0F;
        if (footstep.fadeOutTimer != 40) GL11.glColor4f(0F, 0F, 0F, footstep.fadeOutTimer / 40F);
        float f6 = 0F;
        tessellator.startDrawingQuads();

        this.bindTexture(this.footstepTexture);
        tessellator.addVertexWithUV((double) (f2 - f3), (double) (0.0F), (double) f6, 1, 1);
        tessellator.addVertexWithUV((double) (-f2 - f3), (double) (0.0F), (double) f6, 0, 1);
        tessellator.addVertexWithUV((double) (-f2 - f3), (double) (1F), (double) f6, 0, 0);
        tessellator.addVertexWithUV((double) (f2 - f3), (double) (1F), (double) f6, 1, 0);

        tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1) {
        return this.footstepTexture;
    }
}
