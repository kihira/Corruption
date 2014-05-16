package com.kihira.corruption.common.corruption;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

//TODO Remember this is a conditional corruption effect
public class AfraidOfTheDarkCorruption extends AbstractCorruption {

    public AfraidOfTheDarkCorruption(EntityPlayer entityPlayer) {
        super(entityPlayer);
    }

    @Override
    public void onUpdate(Side side) {
        if (side == Side.CLIENT && this.thePlayer.worldObj.getTotalWorldTime() % (MathHelper.getRandomIntegerInRange(this.thePlayer.getRNG(), 600, 1200)) == 0) {
            if (this.thePlayer.worldObj.getBlockLightValue((int) this.thePlayer.posX, (int) this.thePlayer.posY, (int) this.thePlayer.posZ) <= 8) {
                String sound;
                switch (this.thePlayer.getRNG().nextInt(4)) {
                    case (0):
                        sound = (thePlayer.worldObj.provider.dimensionId == -1 ? "mob.ghast.moan" : "mob.zombie.say");
                        break;
                    case (1):
                        sound = "creeper.primed";
                        break;
                    case (2):
                        sound = "fireworks.blast";
                        break;
                    case (3):
                        sound = "mob.endermen.scream";
                        break;
                    default:
                        sound = "mob.endermen.stare";
                }
                this.thePlayer.worldObj.playSoundAtEntity(this.thePlayer, sound, this.thePlayer.getRNG().nextFloat(), (this.thePlayer.worldObj.rand.nextFloat() - this.thePlayer.worldObj.rand.nextFloat()) * 0.2F + 1.0F);
            }
        }
        //TODO add in one where the player might hear footsteps over time
    }

    @Override
    public void finish() {

    }
}
