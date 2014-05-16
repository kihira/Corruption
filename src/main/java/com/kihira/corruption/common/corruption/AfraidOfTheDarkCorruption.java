package com.kihira.corruption.common.corruption;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

//TODO Remember this is a conditional corruption effect
public class AfraidOfTheDarkCorruption extends AbstractCorruption {

    public AfraidOfTheDarkCorruption() {
        super("afraidOfTheDark");
    }

    @Override
    public void init(EntityPlayer player, Side side) {

    }

    @Override
    public void onUpdate(EntityPlayer player, Side side) {
        if (side == Side.CLIENT && player.worldObj.getTotalWorldTime() % (MathHelper.getRandomIntegerInRange(player.getRNG(), 600, 1200)) == 0) {
            if (player.worldObj.getBlockLightValue((int) player.posX, (int) player.posY, (int) player.posZ) <= 8) {
                String sound;
                switch (player.getRNG().nextInt(4)) {
                    case (0):
                        sound = (player.worldObj.provider.dimensionId == -1 ? "mob.ghast.moan" : "mob.zombie.say");
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
                player.worldObj.playSoundAtEntity(player, sound, player.getRNG().nextFloat(), (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.2F + 1.0F);
            }
        }
        //TODO add in one where the player might hear footsteps over time
    }

    @Override
    public void finish(EntityPlayer player, Side side) {

    }
}
