package com.kihira.corruption.common;

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
        if (args != null && args.length > 3) {
            if (args[0].equals("set")) {
                if (args[1].equals("corruption")) {
                    int corr;
                    EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(args[3]);
                    if (player == null) player = commandSender.getEntityWorld().getPlayerEntityByName(commandSender.getCommandSenderName());
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
        else throw new CommandException("Not enough args!");
    }
}
