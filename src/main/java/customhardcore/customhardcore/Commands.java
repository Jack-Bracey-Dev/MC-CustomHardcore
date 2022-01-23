package customhardcore.customhardcore;

import customhardcore.customhardcore.Helpers.ConfigurationHelper;
import customhardcore.customhardcore.Helpers.Misc;
import customhardcore.customhardcore.Helpers.Msg;
import customhardcore.customhardcore.Helpers.PlayerHelper;
import customhardcore.customhardcore.Helpers.ScoreboardHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class Commands implements CommandExecutor {
    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nullable String label,
                             @Nullable String[] args) {
        switch (command.getName()) {
            case "set_sign_to_special":
                setSignToSpecial(sender);
                break;
            case "set_purgatory_point":
                setPurgatoryPoint(sender);
                break;
            case "get_player_deaths":
                getPlayerDeaths(args, sender);
                break;
            case "toggle_max_deaths":
                toggleMaxDeaths(sender);
                break;
            case "set_max_deaths":
                setMaxDeaths(sender, args);
                break;
            case "set_death_counter":
                setPlayerDeaths(sender, args);
                break;
            case "toggle_teleport_on_death":
                toggleTeleportOnDeath(sender);
                break;
        }
        return true;
    }

    private void setPlayerDeaths(CommandSender sender, String[] args) {
        if (args == null || args.length < 2) {
            Msg.send(sender, "This command requires a username, followed with the number of deaths you want to send",
                    "&4");
            return;
        }
        Player target = Bukkit.getServer().getPlayer(args[0]);
        if (target == null) {
            Msg.send(sender, String.format("%s is not online", args[0]), "&4");
            return;
        }

        target.setStatistic(Statistic.DEATHS, Integer.parseInt(args[1]));
        Msg.send(sender, String.format("Successfully set &b%s &3deaths to &b%o",
                target.getName(),
                Integer.parseInt(args[1])), "&3");

        if (ConfigurationHelper.isMaxDeathsEnabled())
            ScoreboardHelper.updatePlayerBoards();
    }

    private void setMaxDeaths(CommandSender sender, @Nullable String[] args) {
        if (args == null || args.length == 0) {
            Msg.send(sender, "This command requires a number as an argument", "&4");
            return;
        }
        ConfigurationHelper.getConfig().set(ConfigurationHelper.ConfigurationValues.MAX_DEATHS.name(),
                Integer.valueOf(args[0]));
        ConfigurationHelper.save();
        Msg.send(sender, "Maximum deaths successfully set to &b" + Integer.valueOf(args[0]), "&3");
        Bukkit.getServer().getOnlinePlayers().forEach(ScoreboardHelper::createOrUpdatePlayerBoard);
    }

    private void toggleMaxDeaths(CommandSender sender) {
        boolean enabled = ConfigurationHelper.getConfig().getBoolean(
                ConfigurationHelper.ConfigurationValues.ENABLE_MAX_DEATHS.name());
        ConfigurationHelper.getConfig().set(ConfigurationHelper.ConfigurationValues.ENABLE_MAX_DEATHS.name(),
                (!enabled));
        Msg.send(sender, "Max deaths set to &b" + !enabled);

        if (!enabled)
            Bukkit.getServer().getOnlinePlayers().forEach(ScoreboardHelper::createOrUpdatePlayerBoard);
        else
            Bukkit.getServer().getOnlinePlayers().forEach(ScoreboardHelper::removeBoard);
    }

    private void getPlayerDeaths(String[] args, CommandSender sender) {
        Optional<Player> optionalPlayer = PlayerHelper.checkIfSenderIsPlayer(sender);
        if (!optionalPlayer.isPresent()) {
            Msg.send(sender, "Must be a player to send this command", "&4");
            return;
        }
        Player player = optionalPlayer.get();

        if (args != null && args.length > 0) {
            String targetUsername = args[0];
            Player targetPlayer = Bukkit.getPlayer(targetUsername);
            if (targetPlayer == null) {
                Msg.send(player, String.format("%s is not currently online", targetUsername), "&2");
                return;
            }
            Msg.send(player, String.format("%s has %o deaths", targetPlayer.getName(),
                    targetPlayer.getStatistic(Statistic.DEATHS)), "&2");
        } else
            Msg.send(player, String.format("You have %o deaths", player.getStatistic(Statistic.DEATHS)), "&2");
    }

    private void setPurgatoryPoint(CommandSender sender) {
        Optional<Player> optionalPlayer = PlayerHelper.checkIfSenderIsPlayer(sender);
        if (!optionalPlayer.isPresent()) {
            Msg.send(sender, "Must be a player to send this command", "&4");
            return;
        }
        Player player = optionalPlayer.get();

        ConfigurationHelper.getConfig().set(ConfigurationHelper.ConfigurationValues.DEATH_LOCATION.name(), player.getLocation());
        CustomHardcore.getInstance().saveConfig();
        Msg.send(player, "Purgatory point set", "&3");
    }

    private void setSignToSpecial(CommandSender sender) {
        Optional<Player> optionalPlayer = PlayerHelper.checkIfSenderIsPlayer(sender);
        if (!optionalPlayer.isPresent()) {
            Msg.send(sender, "Must be a player to send this command", "&4");
            return;
        }
        Player player = optionalPlayer.get();

        Location previousLocation = ConfigurationHelper.getConfig().getLocation(ConfigurationHelper.ConfigurationValues.SIGN_LOCATION.name());
        Block block = player.getTargetBlockExact(5);
        if (block != null) {
            Location location = block.getLocation();
            location.add(0, 1, 0);
            location.setDirection(player.getEyeLocation().getDirection().multiply(-1));
            ConfigurationHelper.getConfig().set(ConfigurationHelper.ConfigurationValues.SIGN_LOCATION.name(), location);
            CustomHardcore.getInstance().saveConfig();
            Misc.createSigns(ConfigurationHelper.getConfig().getLocation(ConfigurationHelper.ConfigurationValues.SIGN_LOCATION.name()));
            Misc.removeOrphanedSign(previousLocation);
        }
    }

    private void toggleTeleportOnDeath(CommandSender sender) {
        boolean enabled = ConfigurationHelper.getConfig().getBoolean(
                ConfigurationHelper.ConfigurationValues.ENABLE_TELEPORT_ON_DEATH.name());
        ConfigurationHelper.getConfig().set(ConfigurationHelper.ConfigurationValues.ENABLE_TELEPORT_ON_DEATH.name(), (!enabled));
        ConfigurationHelper.save();
        Msg.send(sender, "Teleport on death set to &b" + !enabled);
    }
}
