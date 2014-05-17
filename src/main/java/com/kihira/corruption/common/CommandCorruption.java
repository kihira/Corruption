package com.kihira.corruption.common;

import com.kihira.corruption.common.corruption.CorruptionRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class CommandCorruption extends CommandBase {

    @Override
    public String getCommandName() {
        return "corruption";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender) {
        return "Pie!";
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args) {
        if (args != null ) {
            if (args.length >= 3 && args[0].equals("set")) {
                if (args[1].equals("corruption")) {
                    int corr;
                    EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(args.length >= 4 ? args[3] : commandSender.getCommandSenderName());
                    if (player != null) {
                        if (args[2].startsWith("+")) {
                            corr = Integer.valueOf(args[2].substring(1)) + CorruptionDataHelper.getCorruptionForPlayer(player);
                        }
                        else if (args[2].startsWith("-")) {
                            corr = Integer.valueOf(args[2].substring(1)) - CorruptionDataHelper.getCorruptionForPlayer(player);
                        }
                        else {
                            corr = Integer.valueOf(args[2]);
                        }
                        CorruptionDataHelper.setCorruptionForPlayer(player, corr);
                        notifyAdmins(commandSender, "%s has set corruption for %s to %s", commandSender.getCommandSenderName(), player.getCommandSenderName(), corr);
                    }
                }
            }
            else if (args.length >= 2 && args[0].equals("effect")) {
                if (CorruptionRegistry.corruptionHashMap.containsKey(args[1])) {
                    EntityPlayer player = commandSender.getEntityWorld().getPlayerEntityByName(commandSender.getCommandSenderName());
                    CorruptionRegistry.addCorruptionEffect(player, args[1]);
                    notifyAdmins(commandSender, "Effect applied!");
                }
            }
            else if (args.length >= 1 && args[0].equals("disable")) {
                EntityPlayer player = commandSender.getEntityWorld().getPlayerEntityByName(args.length >= 2 ? args[1] : commandSender.getCommandSenderName());
                CorruptionDataHelper.setCanBeCorrupted(player, false);
                notifyAdmins(commandSender, "Corrupted disabled!");
            }
            else if (args.length >= 1 && args[0].equals("get")) {
                EntityPlayer player = commandSender.getEntityWorld().getPlayerEntityByName(args.length >= 2 ? args[1] : commandSender.getCommandSenderName());
                notifyAdmins(commandSender, String.valueOf(CorruptionDataHelper.getCorruptionForPlayer(player)));
            }
        }
        else throw new CommandException("Not enough args!");
    }
}
