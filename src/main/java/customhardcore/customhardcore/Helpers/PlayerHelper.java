package customhardcore.customhardcore.Helpers;

import customhardcore.customhardcore.CustomHardcore;
import customhardcore.customhardcore.Enums.ConfigurationValues;
import customhardcore.customhardcore.Levelling.PlayerData;
import customhardcore.customhardcore.Levelling.PlayerSave;
import customhardcore.customhardcore.PlayerSettings.PlayerSpecificSettings;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

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

        if (ConfigurationHelper.getConfig().getBoolean(ConfigurationValues.ENABLE_TELEPORT_ON_DEATH.name())) {
            Location location = ConfigurationHelper.getConfig().getLocation(ConfigurationValues.DEATH_LOCATION.name());
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

        if (ConfigurationHelper.getBoolean(ConfigurationValues.ENABLE_PLAYER_LIVES)) {
            ScoreboardHelper.createOrUpdatePlayerBoard(player);
            PlayerData playerData = PlayerSave.getPlayerData(player);
            playerData.removeLife();
            PlayerSave.replacePlayer(playerData);
            if (playerData.getLives() <= 0) {
                playerData = new PlayerData(player);
                PlayerSave.replacePlayer(playerData);
                Msg.send(player, "&4&lYou have ran out of lives and so have had your progress reset, " +
                        "be more careful next time");
            }
        }

        Msg.sendGlobal(String.format("%s died and has been sent to purgatory, how careless...",
                player.getName()), "&4&l");

        ScoreboardHelper.updatePlayerBoards();
        
    }

    public static <T extends PlayerEvent> void leaveEvent(T event) {
        PlayerSave.removePlayer(event.getPlayer());
        PlayerSpecificSettings.removePlayer(event.getPlayer());
        ScoreboardHelper.removeBoard(event.getPlayer());
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(CustomHardcore.getInstance(), ScoreboardHelper::updatePlayerBoards, 10L);
    }

}
