package customhardcore.customhardcore;

import customhardcore.customhardcore.Helpers.ConfigurationHelper;
import customhardcore.customhardcore.Helpers.Logger;
import customhardcore.customhardcore.Helpers.Misc;
import customhardcore.customhardcore.Helpers.ScoreboardHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class CustomHardcore extends JavaPlugin {
    private static CustomHardcore instance;

    @Override
    public void onEnable() {
        instance = this;
        if (getServer().isHardcore())
            Logger.info("&4should not be used on hardcore servers");
        else
            Logger.info("&astarted");

        ConfigurationHelper.checkAndSetConfig();
        Misc.createSigns(getConfig().getLocation(ConfigurationHelper.ConfigurationValues.SIGN_LOCATION.name()));

        Bukkit.getPluginManager().registerEvents(new EventListeners(), this);
        enableCommands();

        if (Bukkit.getOnlinePlayers().size() > 0 && ConfigurationHelper.isMaxDeathsEnabled())
            for (Player player : Bukkit.getOnlinePlayers())
                ScoreboardHelper.createOrUpdatePlayerBoard(player);
    }

    private void enableCommands() {
        List<String> commands = Arrays.asList("get_player_deaths","set_max_deaths","set_death_counter","open_config",
                "points_shop");
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
