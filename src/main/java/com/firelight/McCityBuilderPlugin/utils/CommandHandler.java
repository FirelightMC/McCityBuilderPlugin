package com.firelight.McCityBuilderPlugin.utils;

import com.firelight.McCityBuilderPlugin.McCityBuilderPlugin;
import com.foxxite.fxcore.config.Config;
import com.foxxite.fxcore.config.Language;
import com.foxxite.fxcore.misc.Common;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler implements TabExecutor {

    private final McCityBuilderPlugin plugin;
    private final FileConfiguration config;
    private final Language language;

    public CommandHandler(final McCityBuilderPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.language = plugin.getLanguage();
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (command.getName().equalsIgnoreCase("command")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                if (args.length > 0) {
                    switch (args[0]) {
                        case "reload":
                            if (player.hasPermission("plugin.reload")) {
                                this.plugin.reloadConfig();
                                this.language.reloadLanguage();
                                player.sendMessage(this.language.getMessage("reload"));
                            } else {
                                player.sendMessage(this.language.getMessage("no-perms"));
                            }
                            break;
                        default:
                            player.sendMessage(this.language.getMessage("unknown-command"));
                            break;
                    }
                    return true;
                }
            } else {
                sender.sendMessage(Common.colorize("&cYou have to be a player to use this command."));
                return true;
            }
        }
        return false;
    }

    private ArrayList<String> getSubCommands() {
        final ArrayList<String> returns = new ArrayList<>();
        returns.add("command1");
        returns.add("command2");
        returns.add("command3");
        returns.add("reload");

        returns.sort(String::compareToIgnoreCase);
        return returns;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] agrs) {

        if (agrs.length > 0) {
            ArrayList<String> autoComplete = new ArrayList<>();
            int activeArg = 0;

            if (agrs.length == 1) {

                activeArg = 0;
                if (agrs[0].length() == 0) {
                    return this.getSubCommands();
                }

                autoComplete = this.getSubCommands();

            }

            final ArrayList<String> returnList = new ArrayList<>();

            //Intelligent Auto Complete
            for (final String subCommand : autoComplete) {
                //Check if args contain subcommand, ignore case
                if (subCommand.startsWith(agrs[activeArg]) || subCommand.toLowerCase().startsWith(agrs[activeArg])) {
                    returnList.add(subCommand);
                }
            }

            return returnList;
        }

        return null;
    }

}
