package com.kihira.corruption.proxy;

import net.minecraft.client.entity.AbstractClientPlayer;

public class CommonProxy {

    public void registerRenderers() {}

    public void corruptPlayerSkin(AbstractClientPlayer entityPlayer, int oldCorr, int newCorr) {}

    public void uncorruptPlayerSkin(AbstractClientPlayer entityPlayer) {}

    public void uncorruptPlayerSkinPartially(AbstractClientPlayer entityPlayer, int oldCorr, int newCorr) {}
}
