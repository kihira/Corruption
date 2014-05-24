package com.kihira.corruption.client.gui.button;

import com.kihira.corruption.client.gui.GuiDiary;
import com.kihira.corruption.common.corruption.CorruptionRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@SideOnly(Side.CLIENT)
public class GuiButtonTab extends GuiButton {

    private final String corrName;

    public GuiButtonTab(int id, int x, int y, String tabName, String corrName) {
        super(id, x, y, 63, 15, tabName);
        this.corrName = corrName;
    }

    @Override
    public void drawButton(Minecraft minecraft, int width, int height) {
        if (this.visible) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            minecraft.getTextureManager().bindTexture(GuiDiary.bookGuiTextures);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 51, 193, 63, 15);
            int colour = 14737632;
            if (this.corrName != null && CorruptionRegistry.currentCorruptionClient.containsEntry(Minecraft.getMinecraft().thePlayer.getCommandSenderName(), this.corrName)) colour = new Color(205, 4, 2).getRGB();
            this.drawCenteredString(minecraft.fontRenderer, I18n.format(this.displayString), this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, colour);
        }
    }
}
