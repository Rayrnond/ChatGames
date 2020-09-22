package com.reflexian.chatgames.Quickmaths.Commands;

import com.connorlinfoot.titleapi.TitleAPI;
import com.reflexian.chatgames.ChatGames;
import com.reflexian.chatgames.Quickmaths.ChatHandeler.ChatHandeler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Quickmaths implements CommandExecutor {

    public ChatGames main;

    public Quickmaths(ChatGames main) {
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


    public static int RandomNumber;
    public static int RandomNumber2;
    public static int TheEquation;
    public static String equationType;
    public static Integer onlinePlayers = null;
    public String[] exq2;
    public static long timer;

    public void randomNumber(int min, int max) {
        if (min > max) {
            RandomNumber = (int)(Math.random() * (min - max + 1) + max);
            RandomNumber2 = (int)(Math.random() * (min - max + 1) + max);
        } else if (min < max) {
            RandomNumber = (int)(Math.random() * (max - min + 1) + min);
            RandomNumber2 = (int)(Math.random() * (max - min + 1) + min);
        } else if (min == max) {
            RandomNumber = (int)(Math.random() * (100 - 1 + 1) + 1);
            RandomNumber2 = (int)(Math.random() * (100 - 1 + 1) + 1);
        }
        if (equationType.isEmpty()) {
            return;
        } else if (equationType == "+") {
            TheEquation = RandomNumber + RandomNumber2;
        } else if (equationType == "-") {
            TheEquation = RandomNumber - RandomNumber2;
        } else if (equationType == "x") {
            TheEquation = RandomNumber * RandomNumber2;
        }
    }

    public void randomEquation() {
        int RandomEquation = (int)(Math.random() * (3 - 1 + 1) + 1);
        if (RandomEquation == 1) {
            equationType = "+";
        } else if (RandomEquation == 2) {
            equationType = "-";
        } else if (RandomEquation == 3) {
            equationType = "x";
        }
    }

    public void title() {
        //if (!main.getConfig().getBoolean("quickmaths.show-titles")) {
        //    return;
        //}
        List<String> titles = main.getConfig().getStringList("quickmaths.titles");
        List<String> subtitles = main.getConfig().getStringList("quickmaths.subtitles");
        int test = titles.size();
        for (Player all : Bukkit.getServer().getOnlinePlayers()) {
            for (int i = 0; i < titles.size(); i++) {
                int finalI = i;
                Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
                    public void run() {
                        if (finalI == (titles.size() -1)) {
                            TitleAPI.sendTitle(all, 0, 10, 10, titles.get(finalI), subtitles.get(finalI));
                        } else if (finalI == 0) {
                            TitleAPI.sendTitle(all, 10, 10, 0, titles.get(finalI), subtitles.get(finalI));
                        } else {
                            TitleAPI.sendTitle(all,0,10,0, titles.get(finalI), subtitles.get(finalI));
                        }
                    }
                }, (main.getConfig().getInt("quickmaths.delay") + (i * main.getConfig().getInt("quickmaths.delay"))));
            }
        }

    }

    public void quickmathsDisable(Integer seconds) {
        ChatGames.event = "";

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
            public void run() {
                String overMessage = ChatGames.messageData.get("quickmathsOver");
                overMessage = overMessage.replace("%equation%", String.valueOf(RandomNumber + equationType + RandomNumber2)).replace("%answer%", String.valueOf(TheEquation));
                Bukkit.broadcastMessage(colorize(overMessage));
                ChatGames.event = "";
                endCommands();
            }
        }, (main.getConfig().getInt("quickmaths.default-timer") * 20));
    }

    public void endCommands() {
        List<String> wrongcommands = main.getConfig().getStringList("quickmaths.ended-commands");
        if (!(wrongcommands.size() == 0)) {
            for (String wrongcommand : wrongcommands) {
                new BukkitRunnable() {
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), wrongcommand);
                    }
                }.runTask(main);
            }
        }
    }

    public void enableQuickMaths(Integer min, Integer max, String type, Integer time) {
        quickmathsDisable(main.getConfig().getInt("default-timer"));
        ChatGames.place = 1;
        ChatGames.time = time;
        ChatGames.event = "quickmaths";
        ChatHandeler.playerAnswered.clear();
        timer = System.currentTimeMillis();

        for (Player all : Bukkit.getOnlinePlayers()) {

            all.sendMessage(" ");
            if (main.getConfig().getBoolean("quickmaths.enable-player-amount")) {
                if (Bukkit.getServer().getOnlinePlayers().size() > 9) {
                    onlinePlayers = Integer.parseInt(String.valueOf(Bukkit.getServer().getOnlinePlayers().size())) - 2;
                } else if (Bukkit.getServer().getOnlinePlayers().size() > 1) {
                    onlinePlayers = Integer.parseInt(String.valueOf(Bukkit.getServer().getOnlinePlayers().size())) - 1;
                } else {
                    onlinePlayers = 1;
                }
            }

            if (min ==0 && max == 0)  {
                randomEquation();
                randomNumber(main.getConfig().getInt("quickmaths.default-min"), main.getConfig().getInt("quickmaths.default-max"));
                title();
                String quickmathsFirst = ChatGames.messageData.get("quickmathsFirst");
                if (main.getConfig().getBoolean("quickmaths.enable-player-amount")) {
                    quickmathsFirst = quickmathsFirst.replace("%number%", "" + onlinePlayers + "");
                } else {
                    quickmathsFirst = quickmathsFirst.replace("%number%", "" + 10 + "");
                }
                String quickmathsEquation = ChatGames.messageData.get("quickmathsSecond").replace("%equation%", RandomNumber + equationType + RandomNumber2);
                Bukkit.broadcastMessage(colorize(quickmathsFirst));
                Bukkit.broadcastMessage(colorize(quickmathsEquation));
                return;

            } else if (type.equalsIgnoreCase("")) {
                randomEquation();
                randomNumber(min, max);
                title();
                String quickmathsFirst = ChatGames.messageData.get("quickmathsFirst");
                if (main.getConfig().getBoolean("quickmaths.enable-player-amount")) {
                    quickmathsFirst = quickmathsFirst.replace("%number%", "" + onlinePlayers + "");
                } else {
                    quickmathsFirst = quickmathsFirst.replace("%number%", "" + 10 + "");
                }
                String quickmathsEquation = ChatGames.messageData.get("quickmathsSecond").replace("%equation%", RandomNumber + equationType + RandomNumber2);
                Bukkit.broadcastMessage(colorize(quickmathsFirst));
                Bukkit.broadcastMessage(colorize(quickmathsEquation));
                return;
            } else if (!type.equalsIgnoreCase("")) {
                if (type.equalsIgnoreCase("+")) {
                    equationType = "+";
                } else if (type.equalsIgnoreCase("-")) {
                    equationType = "-";
                } else if (type.equals("*") || type.equalsIgnoreCase("x")) {
                    equationType = "x";
                }
                randomNumber(min, max);
                title();
                String quickmathsFirst = ChatGames.messageData.get("quickmathsFirst");
                if (main.getConfig().getBoolean("quickmaths.enable-player-amount")) {
                    quickmathsFirst = quickmathsFirst.replace("%number%", "" + onlinePlayers + "");
                } else {
                    quickmathsFirst = quickmathsFirst.replace("%number%", "" + 10 + "");
                }
                String quickmathsEquation = ChatGames.messageData.get("quickmathsSecond").replace("%equation%", RandomNumber + equationType + RandomNumber2);
                Bukkit.broadcastMessage(colorize(quickmathsFirst));
                Bukkit.broadcastMessage(colorize(quickmathsEquation));
                return;
            }
        }



    }


    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {

        Player player = (Player) sender;

        if (ChatGames.event.equals("quickmaths") || ChatGames.event.equals("scrambler") || ChatGames.event.equals("randomnumber")) {
            player.sendMessage(colorize(ChatGames.messageData.get("alreadyActivated")));
            return true;
        }

        if (args.length == 0) {
            if (!player.hasPermission(main.getConfig().getString("quickmaths.command-permission-multi"))) {
                player.sendMessage(colorize(ChatGames.messageData.get("quickmathsNoPermissionMultipleArgs")));
                return true;
            } else {
                enableQuickMaths(0, 0, "", main.getConfig().getInt("quickmaths.default-timer"));
                return true;
            }

        }

        if (args[0].contains("/")) {
            final String exq = args[0];
            exq2 = exq.split("\\/");


            if (exq2.length == 0 || exq2.length == 1) {
                player.sendMessage(colorize(ChatGames.messageData.get("quickmathsMustIncludeASlash")));
                return true;
            } else if (exq2.length == 2) {
                if (exq2[0].equals("")) {
                    player.sendMessage(colorize(ChatGames.messageData.get("quickmathsMustIncludeASlash")));
                    return true;
                }
            }

            try {
                if (Integer.parseInt(exq2[0]) > 1000000) {
                    player.sendMessage(colorize(ChatGames.messageData.get("quickmathsMinOrMaxMustBeNumbers")));
                    return true;
                } else if (Integer.parseInt(exq2[1]) > 1000000) {
                    player.sendMessage(colorize(ChatGames.messageData.get("quickmathsMinOrMaxMustBeNumbers")));
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(colorize(ChatGames.messageData.get("quickmathsMinOrMaxMustBeNumbers")));
                return true;
            }

            if (!exq2[0].matches("[0-9]+") && exq.length() < 6) {
                player.sendMessage(colorize(ChatGames.messageData.get("quickmathsMinOrMaxMustBeNumbers")));
                return true;
            }

            if (Integer.parseInt(exq2[0]) > Integer.parseInt(exq2[1])) {
                player.sendMessage(colorize(ChatGames.messageData.get("quickmathsMinMoreThanMaxError")));
                return true;
            } else if (Integer.parseInt(exq2[0]) == Integer.parseInt(exq2[1])) {
                player.sendMessage(colorize(ChatGames.messageData.get("quickmathsMinEqualsMaxError")));
                return true;
            } else if (Integer.parseInt(exq2[0]) > 1000000) {
                player.sendMessage(colorize(ChatGames.messageData.get("quickmathsMinOrMaxMustBeNumbers")));
                return true;
            } else if (Integer.parseInt(exq2[1]) > 1000000) {
                player.sendMessage(colorize(ChatGames.messageData.get("quickmathsMinOrMaxMustBeNumbers")));
                return true;
            }
            //enableQuickMaths(Integer.parseInt(exq2[0]), Integer.parseInt(exq2[1]), "", main.getConfig().getInt("quickmaths.default-timer"));
            //return true;
        } else if (!args[0].contains("\\/")){
            player.sendMessage(colorize(ChatGames.messageData.get("quickmathsMustIncludeASlash")));
            return true;
        }

        if (args.length == 1) {
            enableQuickMaths(Integer.parseInt(exq2[0]), Integer.parseInt(exq2[1]), "", main.getConfig().getInt("quickmaths.default-timer"));
            return true;
        }


        if (args.length == 2) {
            if (args[1].equals("*") || args[1].equals("x") || args[1].equals("+") || args[1].equals("-")) {
                enableQuickMaths(Integer.parseInt(exq2[0]), Integer.parseInt(exq2[1]), args[1], main.getConfig().getInt("quickmaths.default-timer"));
                return true;
            } else {
                player.sendMessage(colorize(ChatGames.messageData.get("quickmathsMustIncludeSymbol")));
                return true;
            }
        } else if (args.length == 3) {
            if (args[1].equals("*") || args[1].equals("x") || args[1].equals("+") || args[1].equals("-")) {
                if (!args[2].matches("[0-9]+") && args[2].length() < 3) {
                    player.sendMessage(colorize("&cNo!"));
                } else {
                    player.sendMessage(colorize("&bYes!"));
                }
                if (args[1].equals("*") || args[1].equals("x") || args[1].equals("+") || args[1].equals("-")) {
                    enableQuickMaths(Integer.parseInt(exq2[0]), Integer.parseInt(exq2[1]), args[1], main.getConfig().getInt("quickmaths.default-timer"));
                    return true;
                } else {
                    player.sendMessage(colorize(ChatGames.messageData.get("quickmathsMustIncludeSymbol")));
                    return true;
                }
            } else {
                player.sendMessage(colorize(ChatGames.messageData.get("quickmathsMustIncludeSymbol")));
                return true;
            }
        }

        return true;
    }






}
