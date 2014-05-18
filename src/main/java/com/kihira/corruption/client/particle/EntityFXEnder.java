package com.kihira.corruption.client.particle;

import net.minecraft.client.particle.EntityPortalFX;
import net.minecraft.world.World;

public class EntityFXEnder extends EntityPortalFX {

    public EntityFXEnder(World par1World, double par2, double par4, double par6, double par8, double par10, double par12) {
        super(par1World, par2, par4, par6, par8, par10, par12);
        this.particleRed = 0.05F;
        this.particleGreen = 0.3F;
        this.particleBlue = 0.2F;
    }
}
