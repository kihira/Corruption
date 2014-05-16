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
            if (args.length > 3 && args[0].equals("set")) {
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
            if (args.length >= 2 && args[0].equals("effect")) {
                if (args[1].equals("afraidofthedark")) {
                    EntityPlayer player = commandSender.getEntityWorld().getPlayerEntityByName(commandSender.getCommandSenderName());
                    CorruptionRegistry.addCorruptionEffect(player, "afraidOfTheDark");
                    notifyAdmins(commandSender, "Effect applied!");
                }
            }
        }
        else throw new CommandException("Not enough args!");
    }
}
