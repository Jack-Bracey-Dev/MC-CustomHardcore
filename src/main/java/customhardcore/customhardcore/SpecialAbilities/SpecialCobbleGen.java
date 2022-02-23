package customhardcore.customhardcore.SpecialAbilities;

import customhardcore.customhardcore.CustomHardcore;
import customhardcore.customhardcore.Enums.Unlocks;
import customhardcore.customhardcore.Objects.PlayerData;
import customhardcore.customhardcore.Levelling.PlayerSave;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockFormEvent;

import java.util.Random;

public class SpecialCobbleGen {

    public static void generate(BlockFormEvent event) {
        World world = CustomHardcore.getInstance().getServer().getWorld("world");
        if (world == null)
            return;

        boolean hasUnlock = world.getNearbyEntities(event.getBlock().getLocation(), 15, 15, 15)
                .stream().anyMatch(entity -> {
                    if (!(entity instanceof Player))
                        return false;
                    Player player = (Player) entity;
                    PlayerData playerData = PlayerSave.getPlayerData(player);
                    return playerData.getUnlocks().contains(Unlocks.SPECIAL_COBBLE_GEN);
                });

        if (hasUnlock) {
            Material material = getBlockType();
            event.getNewState().setType(material);
        }
    }

    private static Material getBlockType() {
        if (getRandomNumber(0, 100) > 90) {
            int random = getRandomNumber(0, 100);
            if (random <= 80)
                return Material.COAL_ORE;
            else if (random <= 90)
                return Material.IRON_ORE;
            else if(random <= 96)
                return Material.GOLD_ORE;
            else
                return Material.DIAMOND_ORE;
        } else
            return Material.COBBLESTONE;
    }

    private static int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max-min)+min;
    }

}
