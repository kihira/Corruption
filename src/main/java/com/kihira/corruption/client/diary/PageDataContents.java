package com.kihira.corruption.client.diary;

public class PageDataContents extends PageData {

    public PageDataContents() {
        super("contents");
    }

    public String getPage(int pageNumber) {
        return "Welcome to your guide to Corruption";
    }

    public int getTotalPages() {
        return 1;
    }

    public String getTitle() {
        return "Contents";
    }
}
