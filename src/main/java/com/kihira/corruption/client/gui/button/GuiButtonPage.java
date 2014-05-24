package com.kihira.corruption.client.gui.button;

import com.kihira.corruption.client.gui.GuiDiary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiButtonPage extends GuiButton {

    private final boolean hasNextPage;

    public GuiButtonPage(int par1, int par2, int par3, boolean par4) {
        super(par1, par2, par3, 23, 13, "");
        this.hasNextPage = par4;
    }

    @Override
    public void drawButton(Minecraft minecraft, int width, int height) {
        if (this.visible) {
            boolean flag = width >= this.xPosition && height >= this.yPosition && width < this.xPosition + this.width && height < this.yPosition + this.height;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            minecraft.getTextureManager().bindTexture(GuiDiary.bookGuiTextures);
            int k = 0;
            int l = 192;

            if (flag) {
                k += 23;
            }
            if (!this.hasNextPage) {
                l += 13;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, k, l, 23, 13);
        }
    }
}
