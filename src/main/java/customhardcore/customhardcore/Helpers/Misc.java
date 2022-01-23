package customhardcore.customhardcore.Helpers;

import customhardcore.customhardcore.CustomHardcore;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.metadata.FixedMetadataValue;

public class Misc {

    public static void createSigns(Location location) {
        if (location != null) {
            World world = Bukkit.getWorld("world");
            if (world != null) {
                Block block = world.getBlockAt(location);
                block.setType(Material.WARPED_SIGN);
                Sign sign = (Sign) world.getBlockAt(location).getState();
                sign.getData().setData((byte) 4);
                sign.setGlowingText(true);
                sign.setLine(0, ChatColor.translateAlternateColorCodes('&', "&4&l&k------------"));
                sign.setLine(1, ChatColor.translateAlternateColorCodes('&', "&4&l&k|     &3&lWell      &4&l&k|"));
                sign.setLine(2, ChatColor.translateAlternateColorCodes('&', "&4&l&k|     &3&lDone     &4&l&k|"));
                sign.setLine(3, ChatColor.translateAlternateColorCodes('&', "&4&l&k------------"));
                sign.update();
                sign.setMetadata("finish-line", new FixedMetadataValue(CustomHardcore.getInstance(), true));
            }
        } else {
            Msg.sendGlobal("No sign locations set!", "&4&l");
        }
    }

    public static void removeOrphanedSign(Location location) {
        if (location == null) return;
        World world = Bukkit.getWorld("world");
        if (world == null) return;
        world.getBlockAt(location).setType(Material.AIR);
    }

}
