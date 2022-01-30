package customhardcore.customhardcore.Helpers;

import customhardcore.customhardcore.CustomHardcore;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlayerHelper {

    public static void dropAllItems(Player player) {
        World world = player.getWorld();
        List<ItemStack> items = Arrays.stream(player.getInventory().getContents()).filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (items.size() == 0)
            return;

        player.getInventory().clear();
        for(ItemStack item : items)
            world.dropItem(player.getLocation(), item).setPickupDelay(20);
        items.clear();
    }

    public static Optional<Player> checkIfSenderIsPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            Msg.send(sender, "Sender is not a player!", "&4");
            return Optional.empty();
        }

        Player player = ((Player) sender).getPlayer();
        if (player != null)
            return Optional.of(player);
        return Optional.empty();
    }

    public static void banPlayer(Player player, String reason) {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                "ban " + player.getName() + " " + reason);
    }

    public static void onDeathEvent(Player player) {

        player.setStatistic(Statistic.DEATHS, player.getStatistic(Statistic.DEATHS) + 1);
        PlayerHelper.dropAllItems(player);
        player.setHealth(20);
        player.setSaturation(20);
        player.setFoodLevel(20);

        if (ConfigurationHelper.getConfig().getBoolean(ConfigurationHelper.ConfigurationValues.ENABLE_TELEPORT_ON_DEATH.name())) {
            Location location = ConfigurationHelper.getConfig().getLocation(ConfigurationHelper.ConfigurationValues.DEATH_LOCATION.name());
            if (location != null)
                player.teleport(location);
            else
                CustomHardcore.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED +
                        "No death point has been set, use /death_point to set it.");
        } else {
            Location location = player.getBedSpawnLocation();
            if (location != null)
                player.teleport(location);
            else
                player.teleport(Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation());
        }

        if (ConfigurationHelper.getConfig().getBoolean(ConfigurationHelper.ConfigurationValues.ENABLE_MAX_DEATHS.name())) {
            ScoreboardHelper.createOrUpdatePlayerBoard(player);
            int maxDeathsAmount = ConfigurationHelper.getConfig().getInt(ConfigurationHelper.ConfigurationValues.MAX_DEATHS.name());
            if (player.getStatistic(Statistic.DEATHS) >= maxDeathsAmount) {
                player.kickPlayer("KickBanned by CustomHardcore for too many deaths");
                PlayerHelper.banPlayer(player, "KickBanned by CustomHardcore for too many deaths");
            }
        }

        Msg.sendGlobal(String.format("%s died and has been sent to purgatory, how careless...",
                player.getName()), "&4&l");

        ScoreboardHelper.updatePlayerBoards();
        
    }

}
