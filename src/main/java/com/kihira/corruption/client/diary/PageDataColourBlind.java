package com.kihira.corruption.client.diary;

public class PageDataColourBlind extends PageData {

    public PageDataColourBlind() {
        super("colourBlind");
    }

    public String getPage(int pageNumber) {
        return "Dooooooom";
    }

    public int getTotalPages() {
        return 1;
    }

    public String getTitle() {
        return "Colour Blind";
    }

    public String getTabName() {
        return "Colour Blind";
    }
}
