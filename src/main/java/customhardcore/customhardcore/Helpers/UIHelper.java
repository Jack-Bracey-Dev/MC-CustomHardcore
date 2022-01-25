package customhardcore.customhardcore.Helpers;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UIHelper {

    public static final String SETTING_META_HEADER = "settings_meta";

    public static void createInventoryUI(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 36, "Configuration");
        updateInventoryUI(player, inventory);
    }

    public static void updateInventoryUI(Player player, Inventory inventory) {
        inventory.clear();
        addBooleanItem(inventory, ConfigurationHelper.ConfigurationValues.ENABLE_MAX_DEATHS);
        addBooleanItem(inventory, ConfigurationHelper.ConfigurationValues.ENABLE_TELEPORT_ON_DEATH);
        addLocationItem(inventory, ConfigurationHelper.ConfigurationValues.SIGN_LOCATION);
        addLocationItem(inventory, ConfigurationHelper.ConfigurationValues.DEATH_LOCATION);

        addKey(inventory);
        player.openInventory(inventory);
    }

    private static void addBooleanItem(Inventory inventory, ConfigurationHelper.ConfigurationValues configValue) {
        boolean isSet = ConfigurationHelper.getConfig().getBoolean(configValue.name());
        ItemStack item = createItem(configValue.getDisplayName(), isSet, null);
        if (item == null) return;
        inventory.addItem(item);
    }

    private static void addLocationItem(Inventory inventory, ConfigurationHelper.ConfigurationValues configValue) {
        boolean isSet = ConfigurationHelper.getConfig().isSet(configValue.name());
        Location location = ConfigurationHelper.getConfig().getLocation(configValue.name());
        String vectorString = "";
        if (location != null) {
            Vector vector = location.toVector();
            vector.setX(Math.round(vector.getX()));
            vector.setY(Math.round(vector.getY()));
            vector.setZ(Math.round(vector.getZ()));
            vectorString = String.format("%s %s %s", vector.getX(), vector.getY(), vector.getZ());
        }
        ItemStack item = createItem(configValue.getDisplayName(), isSet, vectorString);
        if (item == null) return;
        inventory.addItem(item);
    }

    private static void addKey(Inventory inventory) {
        ItemStack set = createItem("SET", true, null);
        ItemStack notSet = createItem("NOT SET", false, null);

        if (set == null || notSet == null) return;

        inventory.setItem(inventory.getSize()-1, notSet);
        inventory.setItem(inventory.getSize()-2, set);
    }

    private static ItemStack createItem(String name, boolean isSet, @Nullable String currentValue) {
        if (name == null) {
            Logger.error("Cannot create item with null name");
            return null;
        }

        ItemStack item = new ItemStack(isSet ? Material.GREEN_CONCRETE : Material.RED_CONCRETE);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return null;
        meta.getPersistentDataContainer().set(Objects.requireNonNull(NamespacedKey.fromString(SETTING_META_HEADER)),
                PersistentDataType.STRING, name);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                (isSet ? "&a" : "&c") + name));
        if (currentValue != null) {
            List<String> lore = meta.getLore();
            if (lore == null)
                lore = new ArrayList<>();
            lore.add(currentValue);
            meta.setLore(lore);
        }

        item.setItemMeta(meta);
        return item;
    }

}
