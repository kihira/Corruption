package com.kihira.corruption.common;

import com.kihira.corruption.common.corruption.CorruptionRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class CommandCorruption extends CommandBase {

    @Override
    public String getCommandName() {
        return "corruption";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender) {
        return "command.corruption.usage";
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args) {
        if (args != null && args.length > 0) {
            if (args[0].equals("set")) {
                if (args.length >= 2 ) {
                    int corr;
                    EntityPlayer player = getPlayer(commandSender, args.length >= 3 ? args[2] : commandSender.getCommandSenderName());
                    if (player != null) {
                        //Increase
                        if (args[1].startsWith("+")) {
                            corr = parseInt(commandSender, args[1].substring(1)) + CorruptionDataHelper.getCorruptionForPlayer(player);
                        }
                        //Decrease
                        else if (args[1].startsWith("-")) {
                            corr = parseInt(commandSender, args[1].substring(1)) - CorruptionDataHelper.getCorruptionForPlayer(player);
                        }
                        //Set
                        else {
                            corr = parseInt(commandSender, args[1]);
                        }
                        CorruptionDataHelper.setCorruptionForPlayer(player, corr);
                        notifyAdmins(commandSender, "command.corruption.success.set", commandSender.getCommandSenderName(), player.getCommandSenderName(), corr);
                    }
                    else throw new CommandException("command.corruption.usage.set");
                }
                else throw new CommandException("command.corruption.usage.set");
            }
            else if (args[0].equals("effect")) {
                if (args.length >= 2) {
                    EntityPlayer player = getPlayer(commandSender, args.length >= 3 ? args[2] : commandSender.getCommandSenderName());
                    if (CorruptionRegistry.corruptionHashMap.containsKey(args[1]) && player != null) {
                        CorruptionDataHelper.addCorruptionEffectForPlayer(player, args[1]);
                        notifyAdmins(commandSender, "command.corruption.success.effect", commandSender.getCommandSenderName(), args[1], player.getCommandSenderName());
                    }
                    else throw new CommandException("command.corruption.usage.effect");
                }
                else throw new CommandException("command.corruption.usage.effect");
            }
            else if (args[0].equals("disable")) {
                EntityPlayer player = getPlayer(commandSender, args.length >= 2 ? args[1] : commandSender.getCommandSenderName());
                if (player != null) {
                    CorruptionDataHelper.setCanBeCorrupted(player, false);
                    notifyAdmins(commandSender, "command.corruption.success.disable", commandSender.getCommandSenderName(), player.getCommandSenderName());
                }
                else throw new CommandException("command.corruption.usage.disable");
            }
            else if (args[0].equals("enable")) {
                EntityPlayer player = getPlayer(commandSender, args.length >= 2 ? args[1] : commandSender.getCommandSenderName());
                if (player != null) {
                    CorruptionDataHelper.setCanBeCorrupted(player, true);
                    notifyAdmins(commandSender, "command.corruption.success.enable", commandSender.getCommandSenderName(), player.getCommandSenderName());
                }
                else throw new CommandException("command.corruption.usage.enable");
            }
            else if (args[0].equals("get")) {
                EntityPlayer player = getPlayer(commandSender, args.length >= 2 ? args[1] : commandSender.getCommandSenderName());
                if (player != null) {
                    notifyAdmins(commandSender, String.valueOf(CorruptionDataHelper.getCorruptionForPlayer(player)));
                }
                else throw new CommandException("command.corruption.get.usage");
            }
            else if (args[0].equals("clear")) {
                EntityPlayer player = getPlayer(commandSender, args.length >= 2 ? args[1] : commandSender.getCommandSenderName());
                if (player != null) {
                    CorruptionDataHelper.removeAllCorruptionEffectsForPlayer(player);
                    notifyAdmins(commandSender, "command.corruption.success.clear", commandSender.getCommandSenderName(), player.getCommandSenderName());
                }
                else throw new CommandException("command.corruption.usage.clear");
            }
            else throw new CommandException("command.corruption.usage");
        }
        else throw new CommandException("command.corruption.usage");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender commandSender, String[] args) {
        if (args.length == 1) return getListOfStringsMatchingLastWord(args, "set", "effect", "disable", "enable", "get", "clear");
        else if (args.length >= 2) {
            if (args[0].equals("set") && args.length == 3) {
                return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
            }
            else if (args[0].equals("effect")) {
                if (args.length == 2) {
                    return getListOfStringsFromIterableMatchingLastWord(args, CorruptionRegistry.corruptionHashMap.keySet());
                }
                else if (args.length == 3) {
                    return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
                }
            }
            else if (args[0].equals("disable")) {
                return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
            }
            else if (args[0].equals("enable")) {
                return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
            }
            else if (args[0].equals("get")) {
                return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
            }
            else if (args[0].equals("clear")) {
                return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
            }
        }
        return null;
    }
}
