package com.reflexian.chatgames.Quickmaths.ChatHandeler;

import com.reflexian.chatgames.ChatGames;
import com.reflexian.chatgames.Quickmaths.Commands.Quickmaths;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

public class ChatHandeler implements Listener {

    public static HashMap<Player, Boolean> playerAnswered = new HashMap<Player, Boolean>();

    public ChatGames main;

    public ChatHandeler(ChatGames main) {
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

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        Player player = e.getPlayer();
        String message = e.getMessage();

        long timerfull = (Quickmaths.timer/100 - System.currentTimeMillis()/100) * -1;

        if (ChatGames.event.equalsIgnoreCase("quickmaths")) {


            if (message.matches(".*[a-z].*")) {
                e.setMessage(message.replaceAll("\\d+", ""));
                return;
            } else if (playerAnswered.containsKey(player)) {
                e.setCancelled(true);
                player.sendMessage(colorize(ChatGames.messageData.get("quickmathsAnsweredAlready")));
                return;
            } else {
                if (Integer.parseInt(message) != Quickmaths.TheEquation) {
                    List<String> wrongcommands = main.getConfig().getStringList("quickmaths.wrong-commands");
                    if (!(wrongcommands.size() == 0)) {
                        for (String wrongcommand : wrongcommands) {
                            new BukkitRunnable() {
                                public void run() {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), wrongcommand.replace("%player%", String.valueOf(player.getDisplayName())));
                                }
                            }.runTask(main);
                        }
                    }
                    e.setCancelled(true);
                    player.sendMessage(colorize(ChatGames.messageData.get("quickmathsWrong")));
                    return;


                } else {
                    List<String> rightcommands = main.getConfig().getStringList("quickmaths.correct-commands");
                    if (!(rightcommands.size() == 0)) {
                        for (String rightcommand : rightcommands) {
                            new BukkitRunnable() {
                                public void run() {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rightcommand.replace("%player%", String.valueOf(player.getDisplayName())).replace("%place%", String.valueOf(ChatGames.place - 1)));
                                }
                            }.runTask(main);
                        }
                    }
                    e.setCancelled(true);
                    playerAnswered.put(player, true);
                    String answeredMessage = ChatGames.messageData.get("quickmathsAnswered");
                    answeredMessage = answeredMessage.replace("%place%", String.valueOf(ChatGames.place)).replace("%player%", String.valueOf(player.getDisplayName())).replace("%seconds%", String.valueOf((double)timerfull/10));
                    Bukkit.broadcastMessage(colorize(answeredMessage));
                    ChatGames.place ++;
                    return;
                }

            }
        }

    }


}
