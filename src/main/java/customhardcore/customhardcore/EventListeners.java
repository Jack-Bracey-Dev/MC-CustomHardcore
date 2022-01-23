package customhardcore.customhardcore;

import customhardcore.customhardcore.Helpers.ConfigurationHelper;
import customhardcore.customhardcore.Helpers.Msg;
import customhardcore.customhardcore.Helpers.PlayerHelper;
import customhardcore.customhardcore.Helpers.ScoreboardHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Objects;

public class EventListeners implements Listener {

    @EventHandler
    public void onPlayerDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player)
            PlayerHelper.onDeathEvent(((Player) event.getEntity()));
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = ((Player) event.getEntity());
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
        if (ConfigurationHelper.isMaxDeathsEnabled())
            ScoreboardHelper.updatePlayerBoards();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        System.out.println("Removing board for " + event.getPlayer().getName());
        ScoreboardHelper.removeBoard(event.getPlayer());
        
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(CustomHardcore.getInstance(), new Runnable() {

                @Override
                public void run() {
                    ScoreboardHelper.updatePlayerBoards();
                }
        }, 10L);
        
    }

}
