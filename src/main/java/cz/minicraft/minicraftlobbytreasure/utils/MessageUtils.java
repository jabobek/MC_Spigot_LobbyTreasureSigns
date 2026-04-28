package cz.minicraft.minicraftlobbytreasure.utils;

import cz.minicraft.minicraftlobbytreasure.MinicraftLobbyTreasure;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageUtils {

    public static void sendMessage(CommandSender sender, String message) {
        String prefix = MinicraftLobbyTreasure.getInstance().getConfigManager().getLang().getString("prefix");
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }

    public static void sendMessageWithoutPrefix(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
