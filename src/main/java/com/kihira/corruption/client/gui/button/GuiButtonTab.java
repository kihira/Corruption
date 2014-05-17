package com.kihira.corruption.client.gui.button;

import com.kihira.corruption.client.gui.GuiDiary;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

public class GuiButtonTab extends GuiButton{

    public GuiButtonTab(int id, int x, int y, String par4Str) {
        super(id, x, y, 63, 15, par4Str);
    }

    @Override
    public void drawButton(Minecraft minecraft, int width, int height) {
        if (this.visible) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            minecraft.getTextureManager().bindTexture(GuiDiary.bookGuiTextures);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 51, 193, 63, 15);
            this.drawCenteredString(minecraft.fontRenderer, I18n.format(this.displayString), this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, 14737632);
        }
    }
}
