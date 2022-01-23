package customhardcore.customhardcore.Helpers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Msg {

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void send(CommandSender sender, String message, String prefix) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }

    public static void send(Player target, String message) {
        target.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void send(Player target, String message, String prefix) {
        target.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }

    public static void sendGlobal(String message) {
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendGlobal(String message, String prefix) {
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }

}
