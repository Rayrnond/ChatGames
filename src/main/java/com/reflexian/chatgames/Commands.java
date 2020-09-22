package com.reflexian.chatgames;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.File;

public class Commands implements Listener, CommandExecutor {

    public ChatGames main;

    public Commands(ChatGames main) {
        this.main = main;
    }

    public String colorize(String msg) {
        String coloredMsg = "";
        for (int i = 0; i < msg.length(); i++) {
            if(msg.charAt(i) == '&') {
                coloredMsg += '§';
            } else {
                coloredMsg += msg.charAt(i);
            }
        }
        return coloredMsg;
    }

    Long reloadtime;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (command.getName().equalsIgnoreCase("cg")) {

                if (args.length == 0) {
                    player.sendMessage(ChatColor.RED + "That is not a correct argument! (reload)");
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    if (player.hasPermission(main.getConfig().getString("main-commands.reload-permission"))) {
                        File f = new File(main.getDataFolder() + File.separator + "messages.yml");
                        reloadtime = System.currentTimeMillis();
                        main.reloadConfig();
                        YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "messages.yml"));
                        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
                        for (String message : config.getConfigurationSection("").getKeys(false)) {
                            ChatGames.messageData.put(message, config.getString(message));
                        }
                        reloadtime = (((reloadtime) - (System.currentTimeMillis())) * -1);
                        sender.sendMessage(ChatColor.GREEN + "Successfully reloaded the configuration files! " + ChatColor.GRAY + "(" + reloadtime + "ms)");
                        System.out.println(ChatGames.messageData.get("quickmathsNoPermission"));
                        return true;
                    } else {
                        player.sendMessage(colorize(ChatGames.messageData.get("reloadNoPermission")));
                    }
                } else if (args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("debug")) {
                    player.sendMessage(ChatColor.GREEN + "HypixelChatGames " + ChatColor.AQUA + ChatColor.BOLD + "FREE-BETA");
                    player.sendMessage(ChatColor.GRAY + "Version: PUBLIC-DEV1");
                    player.sendMessage(ChatColor.GRAY + "Licensed: Raymond#0001 / Reflexian.com");
                    player.sendMessage(ChatColor.GRAY + "Copyright Reflexian Inc 2020");
                    return true;
                } else {
                    player.sendMessage(ChatColor.RED + "That is not a correct argument! (reload)");
                    return true;
                }
            }

        }
        return true;
    }

}
