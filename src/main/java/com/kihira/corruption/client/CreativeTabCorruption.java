package com.kihira.corruption.client;

import com.kihira.corruption.Corruption;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabCorruption extends CreativeTabs {

    public CreativeTabCorruption() {
        super("corruption");
    }

    @Override
    public Item getTabIconItem() {
        return Corruption.itemDiary;
    }
}
