package com.kihira.corruption.common.corruption;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.kihira.corruption.common.FMLEventHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class GluttonyCorruption implements ICorruptionEffect {

    public final Multiset<String> playerCount = HashMultiset.create();

    @Override
    public void init(String player, Side side) {
        this.playerCount.add(player);
    }

    @Override
    public void onUpdate(EntityPlayer player, Side side) {
        if (player.worldObj.getTotalWorldTime() % 40 == 0 && side.isServer()) {
            InventoryPlayer inventoryPlayer = player.inventory;
            player.addPotionEffect(new PotionEffect(Potion.hunger.id, 40));
            for (int i = 0; i < inventoryPlayer.getSizeInventory(); i++) {
                ItemStack itemStack = inventoryPlayer.getStackInSlot(i);
                if (itemStack != null && itemStack.getItem() instanceof ItemFood) {
                    inventoryPlayer.setInventorySlotContents(i, itemStack.onFoodEaten(player.worldObj, player).stackSize <= 0 ? null : itemStack);
                    player.worldObj.playSoundAtEntity(player, "random.eat", 0.5F + 0.5F * (float) player.getRNG().nextInt(2), (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.2F + 1.0F);
                    this.playerCount.add(player.getCommandSenderName());
                    break;
                }
            }
            if (player.worldObj.getTotalWorldTime() % 80 == 0) this.playerCount.add(player.getCommandSenderName());
        }
    }

    @Override
    public void finish(String player, Side side) {
        this.playerCount.setCount(player, 0);
    }

    @Override
    public boolean shouldContinue(EntityPlayer player, Side side) {
        return this.playerCount.count(player.getCommandSenderName()) < FMLEventHandler.CORRUPTION_MAX / 200;
    }

    @Override
    public String getPageDataName() {
        return "gluttony";
    }

    @Override
    public boolean canApply(EntityPlayer player) {
        return true;
    }
}
