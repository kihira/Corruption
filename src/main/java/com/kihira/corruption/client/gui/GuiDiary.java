package com.kihira.corruption.client.gui;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.kihira.corruption.client.diary.PageData;
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
        //Corruption Pages
        int yOffset = 15;
        int id = 0;
        for (Map.Entry<String, PageData> page : this.pageData.entrySet()) {
            if (!page.getKey().equals("contents")) {
                this.buttonPageMapping.put(id, page.getKey());
                this.buttonPageTabs.add(id, new GuiButtonTab(id, this.width / 2 + 69, 14 + yOffset, page.getValue().getTabName()));
                this.buttonList.add(this.buttonPageTabs.get(id));
                id++;
                yOffset += 15;
            }
        }
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
        }
        this.loadPageData("contents");
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
            String title = this.currentPageData.getTitle();

            int i1 = this.fontRendererObj.getStringWidth(title);
            this.fontRendererObj.drawString(EnumChatFormatting.BOLD + title, k + 36 + (104 - i1) / 2, 28, 0);

            s = I18n.format("book.pageIndicator", this.currPage + 1, this.currentPageData.getTotalPages());
            s1 = "";

            if (this.currPage >= 0 && this.currPage < this.currentPageData.getTotalPages()) {
                s1 = this.currentPageData.getPage(this.currPage);
            }

            l = this.fontRendererObj.getStringWidth(s);
            this.fontRendererObj.drawString(s, k - l + this.diaryWidth - 44, 16, 0);
            this.fontRendererObj.drawSplitString(s1, k + 36, 40, 116, 0);
        }

        super.drawScreen(par1, par2, par3);
    }
}
