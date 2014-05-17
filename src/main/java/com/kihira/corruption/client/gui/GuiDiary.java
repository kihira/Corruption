package com.kihira.corruption.client.gui;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.kihira.corruption.client.diary.PageData;
import com.kihira.corruption.client.gui.button.GuiButtonPage;
import com.kihira.corruption.client.gui.button.GuiButtonTab;
import com.kihira.corruption.common.CorruptionDataHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class GuiDiary extends GuiScreen {

    private final EntityPlayer player;
    private final int diaryWidth;
    private final int diaryHeight;
    private GuiButtonTab buttonContents;
    private GuiButtonPage buttonNextPage;
    private GuiButtonPage buttonPreviousPage;
    private List<GuiButtonTab> buttonPageTabs = new ArrayList<GuiButtonTab>();
    private HashMap<String, PageData> pageData = new HashMap<String, PageData>();
    private BiMap<Integer, String> buttonPageMapping = HashBiMap.create();
    private PageData currentPageData;
    private int currPage;
    public static final ResourceLocation bookGuiTextures = new ResourceLocation("corruption", "book.png");

    public GuiDiary(EntityPlayer player) {
        this.player = player;
        this.diaryWidth = 192;
        this.diaryHeight = 192;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        this.buttonList.clear();
        this.loadDiaryData();

        //Contents Tab Button
        this.buttonList.add(this.buttonContents = new GuiButtonTab(50, this.width / 2 + 69, 14, "Contents"));
        //Page Buttons
        this.buttonList.add(this.buttonNextPage = new GuiButtonPage(51, this.width / 2 + 120, 154, true));
        this.buttonList.add(this.buttonPreviousPage = new GuiButtonPage(52, this.width / 2 + 38, 154, false));
        //Corruption Pages
        int yOffset = 15;
        int id = 0;
        for (Map.Entry<String, PageData> page : this.pageData.entrySet()) {
            if (!page.getKey().equals("contents") && page.getValue() != null) {
                this.buttonPageMapping.put(id, page.getKey());
                this.buttonPageTabs.add(id, new GuiButtonTab(id, this.width / 2 + 69, 14 + yOffset, page.getValue().getTabName()));
                this.buttonList.add(this.buttonPageTabs.get(id));
                id++;
                yOffset += 15;
            }
        }

        this.updateButtons();
    }

    private void updateButtons() {
        this.buttonNextPage.visible = this.currPage < this.currentPageData.getTotalPages() - 1;
        this.buttonPreviousPage.visible = this.currPage > 0;
    }

    private void loadDiaryData() {
        NBTTagCompound tagCompound = CorruptionDataHelper.getDiaryDataForPlayer(this.player);
        if (tagCompound.hasKey("PageData")) {
            NBTTagList pageData = tagCompound.getTagList("PageData", 8);
            if (pageData != null && pageData.tagCount() > 0) {
                for (int i = 0; i < pageData.tagCount(); i++) {
                    String pageName = pageData.getStringTagAt(i);
                    this.pageData.put(pageName, PageData.pageMap.get(pageName));
                }
            }
        }
        else {
            this.pageData.put("contents", PageData.pageMap.get("contents"));
            this.pageData.put("colourBlind", PageData.pageMap.get("colourBlind"));
            this.pageData.put("afraidOfTheDark", PageData.pageMap.get("afraidOfTheDark"));
            this.pageData.put("blockTeleport", PageData.pageMap.get("blockTeleport"));
            this.pageData.put("bloodLoss", PageData.pageMap.get("bloodLoss"));
            this.pageData.put("stoneSkin", PageData.pageMap.get("stoneSkin"));
            this.pageData.put("waterAllergy", PageData.pageMap.get("waterAllergy"));
        }
        if (this.currentPageData == null) this.loadPageData("contents");
    }

    private void loadPageData(String pageName) {
        this.currentPageData = this.pageData.get(pageName);
        this.currPage = 0;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            //contents
            if (button.id == 50) {
                this.loadPageData("contents");
            }
            //Page
            else if (button.id < this.pageData.size()) {
                this.loadPageData(this.buttonPageMapping.get(button.id));
            }
        }
        this.updateButtons();
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(bookGuiTextures);
        int k = (this.width - this.diaryWidth) / 2;
        this.drawTexturedModalRect(k, 2, 0, 0, this.diaryWidth, this.diaryHeight);
        String s;
        String s1;
        int l;

        if (this.currentPageData != null) {
            String title = I18n.format(this.currentPageData.getTitle(this.currPage));

            int i1 = this.fontRendererObj.getStringWidth(title);
            this.fontRendererObj.drawString(EnumChatFormatting.BOLD + title, k + 36 + (104 - i1) / 2, 28, 0);

            title = I18n.format(this.currentPageData.getSubTitle());
            i1 = this.fontRendererObj.getStringWidth(title);
            GL11.glPushMatrix();
            GL11.glTranslatef(this.width * 0.25F, 40 * 0.5F, 0F);
            GL11.glScalef(0.5F, 0.5F, 0F);
            this.fontRendererObj.drawString(EnumChatFormatting.ITALIC + title, k + 36 + (114 - i1) / 2, 40, 0);
            GL11.glPopMatrix();

            s = I18n.format("book.pageIndicator", this.currPage + 1, this.currentPageData.getTotalPages());
            s1 = "";

            if (this.currPage >= 0 && this.currPage < this.currentPageData.getTotalPages()) {
                s1 = this.currentPageData.getPage(this.currPage);
            }

            l = this.fontRendererObj.getStringWidth(s);
            this.fontRendererObj.drawString(s, k - l + this.diaryWidth - 44, 16, 0);
            this.fontRendererObj.drawSplitString(I18n.format(s1), k + 36, 48, 116, 0);
        }

        super.drawScreen(par1, par2, par3);
    }
}
