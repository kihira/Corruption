package com.kihira.corruption.client.diary;

import java.util.HashMap;

public class PageData {

    public static final HashMap<String, PageData> pageMap = new HashMap<String, PageData>();

    public final String pageName;

    public PageData(String pageName) {
        this.pageName = pageName;
        pageMap.put(pageName, this);
    }

    public String getPage(int pageNumber) {
        return "Cheese"; //TODO
    }

    public int getTotalPages() {
        return 1;
    }

    public String getTitle() {
        return this.pageName;
    }

    public String getTabName() {
        return this.pageName;
    }
}
