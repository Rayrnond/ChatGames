package com.reflexian.chatgames;

import com.reflexian.chatgames.Quickmaths.ChatHandeler.ChatHandeler;
import com.reflexian.chatgames.Quickmaths.Commands.Quickmaths;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

// getLogger().info(messageData.get("noPermission"));
// getLogger().info(main.messageData.get("noPermission"));

public final class ChatGames extends JavaPlugin implements CommandExecutor {

    public static String event;
    public static Integer time;
    public static Integer place;
    public static HashMap<String, String> messageData = new HashMap<String, String>();

    @Override
    public void onEnable() {
        File f = new File(getDataFolder()+File.separator+"messages.yml");
        if (!f.exists()) {
            f.mkdir();
        }
        int pluginId = 8830; // <-- Replace with the id of your plugin!
        MetricsLite metrics = new MetricsLite(this, pluginId);

        setMessage("reloadNoPermission", "&cNot today, nor tomorrow!");
        setMessage("alreadyActivated", "&cThere is already an event going! Wait for it to end!");
        setMessage("quickmathsNoPermission", "&cYou do not have permission.");
        setMessage("quickmathsNoPermissionMultipleArgs", "&cYou do not have permission.");
        setMessage("quickmathsFirst", "&d&lQUICK MATHS! &7First %number% players to answer are cool!");
        setMessage("quickmathsSecond", "&d&lQUICK MATHS! &7Solve: &e%equation%");
        setMessage("quickmathsAnswered", "&d&lQUICK MATHS! &e#%place% %player% &7answered in &e%seconds%s");
        setMessage("quickmathsOver", "&d&lQUICK MATHS OVER! &e%equation% &7= &e%answer%");
        setMessage("quickmathsWrong", "&d&lQUICK MATHS! &cWrong answer, try again!");
        setMessage("quickmathsAnsweredAlready", "&d&lQUICK MATHS! &cYou already answered! Shh.");
        setMessage("quickmathsMinMoreThanMaxError", "&cThe min number is bigger than the max number! Try making min (first number) smaller than the max (second number)!");
        setMessage("quickmathsMinEqualsMaxError", "&cThe min number is equal to the max number! Try making min (first number) smaller than the max (second number)!");
        setMessage("quickmathsMinOrMaxMustBeNumbers", "&cThe min/max number must only contain numbers 0 - 1000000!!!");
        setMessage("quickmathsMustIncludeASlash", "&cThe min/max must contain a slash between min and max. Ex. /quickmaths 1/100");
        setMessage("quickmathsMustIncludeSymbol", "&cThe second arg must contain either +, -, or * !!!");

        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
        for (String message : config.getConfigurationSection("").getKeys(false)) {
            messageData.put(message, config.getString(message));
        }

        getLogger().info("Checking language file ...");
        getLogger().info("Directing plugin files ...");
        event = "";
        getLogger().info("Plugin successfully enabled");
        getCommand("cg").setExecutor(new Commands(this));
        getCommand("quickmaths").setExecutor(new Quickmaths(this));
        getServer().getPluginManager().registerEvents(new ChatHandeler(this), this);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("Could not find PlaceholderAPI! It's recommended you install it!");
        }

        saveDefaultConfig();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void setMessage(String name, String message) {
        File f = new File(getDataFolder()+File.separator+"messages.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
        if (!config.isSet(name)) {
            config.set(name, message);
            try {
                config.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
