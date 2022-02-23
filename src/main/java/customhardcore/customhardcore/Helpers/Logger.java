package customhardcore.customhardcore.Helpers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logger {

    public static <T> void error(Class<T> clazz, String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                String.format("&4&l%s - %s", clazz.getSimpleName(), message)));
    }

    public static <T, E extends Exception> void error(Class<T> clazz, String message, E exception) {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                        String.format("&4&l%s - &r&4%s", clazz.getSimpleName(), message)),
                ChatColor.translateAlternateColorCodes('&', "&c" + exception.getMessage()));
    }

    public static void info(String message) {
        log("&9" + message);
    }

    private static void log(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(
                ChatColor.translateAlternateColorCodes('&',
                        String.format("&3Custom&bHardcore: &f%s", message)));
    }

}
