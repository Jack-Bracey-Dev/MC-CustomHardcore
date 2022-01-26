package customhardcore.customhardcore;

import customhardcore.customhardcore.Helpers.ConfigurationHelper;
import customhardcore.customhardcore.Helpers.Logger;
import customhardcore.customhardcore.Helpers.Misc;
import customhardcore.customhardcore.Helpers.ScoreboardHelper;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class CustomHardcore extends JavaPlugin {
    private static CustomHardcore instance;

    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;

    @Override
    public void onEnable() {
        instance = this;

        ConfigurationHelper.checkAndSetConfig();

        if (!setupEconomy() ) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED +
                    "Could not setup vault economy, shutting down CustomHardcore.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!setupPermissions()) Logger.error("setupPermissions for vault failed.");
        if (!setupChat()) Logger.error("setupChat for Vault failed.");

        Misc.createSigns(getConfig().getLocation(ConfigurationHelper.ConfigurationValues.SIGN_LOCATION.name()));

        Bukkit.getPluginManager().registerEvents(new EventListeners(), this);
        enableCommands();

        if (Bukkit.getOnlinePlayers().size() > 0 && ConfigurationHelper.isMaxDeathsEnabled())
            for (Player player : Bukkit.getOnlinePlayers())
                ScoreboardHelper.createOrUpdatePlayerBoard(player);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            Logger.error("Can't find vault");
            return false;
        }

        ServicesManager serviceManager = getServer().getServicesManager();
        serviceManager.register(Economy.class, );

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Logger.error("Can't find economy RegisteredServiceProvider");
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp == null) return false;
        chat = rsp.getProvider();
        return true;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) return false;
        perms = rsp.getProvider();
        return true;
    }

    private void enableCommands() {
        List<String> commands = Arrays.asList("get_player_deaths","set_max_deaths","set_death_counter","open_config");
        commands.forEach(command -> Objects.requireNonNull(getCommand(command)).setExecutor(new Commands()));
    }

    @Override
    public void onDisable() {
        if (Bukkit.getServer().getOnlinePlayers().size() > 0)
            Bukkit.getServer().getOnlinePlayers().forEach(ScoreboardHelper::removeBoard);
    }

    public static CustomHardcore getInstance() {
        return instance;
    }

}
