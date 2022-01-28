package customhardcore.customhardcore.Helpers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logger {

    public static void error(String message) {
        log("&4" + message);
    }

    public static <E extends Exception> void error(String message, E exception) {
        Bukkit.getServer().getConsoleSender().sendMessage("&4" + message, exception.getMessage());
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
