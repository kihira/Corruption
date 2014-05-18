package com.kihira.corruption.client.diary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PageData {

    public static final HashMap<String, PageData> pageMap = new HashMap<String, PageData>();

    public final String pageName;
    private final List<String> pageTitles = new ArrayList<String>();
    public final List<String> pageContents = new ArrayList<String>();

    private String tabName = "";
    private String subTitle = "";

    public PageData(String pageName) {
        this.pageName = pageName;
    }

    public PageData setPageContents(int pageNum, String contents, String title) {
        this.setPageContents(pageNum, contents);
        this.setPageTitle(pageNum, title);
        return this;
    }

    public PageData setPageContents(int pageNum, String contents) {
        this.pageContents.add(pageNum, contents);
        return this;
    }

    public PageData setPageTitle(int pageNum, String contents) {
        this.pageTitles.add(pageNum, contents);
        return this;
    }

    public PageData setTabName(String name) {
        this.tabName = name;
        return this;
    }

    public String getPage(int pageNumber) {
        return this.pageContents.get(pageNumber);
    }

    public int getTotalPages() {
        return this.pageContents.size();
    }

    public String getTitle(int pageNum) {
        return this.pageTitles.get(pageNum);
    }

    public String getTabName() {
        return this.tabName;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public PageData setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public static void registerPageData() {
        pageMap.put("contents", new PageData("contents").setPageContents(0, "page.welcome.0", "page.welcome.0.title").setSubTitle("page.welcome.0.subtitle"));
        pageMap.put("colourBlind", new PageData("colourblind").setPageContents(0, "page.colourblind.0", "page.colourblind.0.title").setTabName("page.colourblind.0.tab").setSubTitle("page.colourblind.0.subtitle"));
        pageMap.put("afraidOfTheDark", new PageData("afraidOfTheDark").setPageContents(0, "page.afraidOfTheDark.0", "page.afraidOfTheDark.0.title").setTabName("page.afraidOfTheDark.0.tab").setSubTitle("page.afraidOfTheDark.0.subtitle"));
        pageMap.put("blockTeleport", new PageData("blockTeleport").setPageContents(0, "page.blockTeleport.0", "page.blockTeleport.0.title").setTabName("page.blockTeleport.0.tab").setSubTitle("page.blockTeleport.0.subtitle"));
        pageMap.put("bloodLoss", new PageData("bloodLoss").setPageContents(0, "page.bloodLoss.0", "page.bloodLoss.0.title").setTabName("page.bloodLoss.0.tab").setSubTitle("page.bloodLoss.0.subtitle"));
        pageMap.put("stoneSkin", new PageData("stoneSkin").setPageContents(0, "page.stoneSkin.0", "page.stoneSkin.0.title").setTabName("page.stoneSkin.0.tab").setSubTitle("page.stoneSkin.0.subtitle"));
        pageMap.put("waterAllergy", new PageData("waterAllergy").setPageContents(0, "page.waterAllergy.0", "page.waterAllergy.0.title").setTabName("page.waterAllergy.0.tab").setSubTitle("page.waterAllergy.0.subtitle"));
        pageMap.put("enderCake", new PageData("enderCake").setPageContents(0, "page.enderCake.0", "page.enderCake.0").setTabName("page.enderCake.0.tab").setSubTitle("page.enderCake.0.subtitle"));
    }
}
