package customhardcore.customhardcore.Helpers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logger {

    public static void error(String message) {
        log("&4" + message);
    }

    public static void info(String message) {
        log("&9" + message);
    }

    public static void log(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(
                ChatColor.translateAlternateColorCodes('&',
                        String.format("&3Custom&bHardcore: &f%s", message)));
    }

}
