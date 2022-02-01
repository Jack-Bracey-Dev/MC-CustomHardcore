package customhardcore.customhardcore;

import customhardcore.customhardcore.Enums.Settings;
import customhardcore.customhardcore.Helpers.ConfigurationHelper;
import customhardcore.customhardcore.Helpers.Msg;
import customhardcore.customhardcore.Helpers.PlayerHelper;
import customhardcore.customhardcore.Helpers.ScoreboardHelper;
import customhardcore.customhardcore.Levelling.PlayerSave;
import customhardcore.customhardcore.PlayerSettings.PlayerSpecificSettings;
import customhardcore.customhardcore.SpecialAbilities.SpecialCobbleGen;
import customhardcore.customhardcore.UI.InventoryEvents;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Objects;

public class EventListeners implements Listener {

    @EventHandler
    public void onPlayerDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player)
            PlayerHelper.onDeathEvent(((Player) event.getEntity()));

        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            PlayerSave.addXp(killer, event.getDroppedExp());
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = ((Player) event.getEntity());
            Bukkit.getServer().getScheduler()
                .scheduleSyncDelayedTask(CustomHardcore.getInstance(), ScoreboardHelper::updatePlayerBoards, 5L);
            if (event.getDamage() > player.getHealth()) {
                event.setCancelled(true);
                PlayerHelper.onDeathEvent(player);
            }
        }
    }

    @EventHandler
    public void onPlayerClickSign(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.WARPED_SIGN) &&
                event.getClickedBlock().hasMetadata("finish-line")) {
            Player player = event.getPlayer();
            Msg.send(player, "Congratulations! Let's take you home.", "&2&l");
            Location location = player.getBedSpawnLocation();
            if (location != null)
                player.teleport(location);
            else
                player.teleport(Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerSave.initialisePlayer(event.getPlayer());
        PlayerSpecificSettings.initialisePlayerSettings(event.getPlayer());
        if (ConfigurationHelper.isMaxDeathsEnabled() && PlayerSpecificSettings.getPlayerSettings(event.getPlayer())
                .getSettings().get(Settings.TOGGLE_SCOREBOARD))
            ScoreboardHelper.updatePlayerBoards();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PlayerHelper.leaveEvent(event);
    }

    public void onKicked(PlayerKickEvent event) {
        PlayerHelper.leaveEvent(event);
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player)
            Bukkit.getServer().getScheduler()
                .scheduleSyncDelayedTask(CustomHardcore.getInstance(), ScoreboardHelper::updatePlayerBoards, 5L);
    }

    @EventHandler
    public void onInventoryItemTouched(InventoryClickEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase("Configuration"))
            InventoryEvents.configurationEvent(event);
        if (event.getView().getTitle().equalsIgnoreCase("Point Shop"))
            InventoryEvents.shopEvent(event);
        if (event.getView().getTitle().equalsIgnoreCase("Settings"))
            InventoryEvents.settingEvent(event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerSave.addXp(player, Math.round(event.getBlock().getType().getHardness()));
    }

    @EventHandler
    public void onBlockGen(BlockFormEvent event) {
        if (event.getNewState().getType().equals(Material.COBBLESTONE))
            SpecialCobbleGen.generate(event);
    }

}
