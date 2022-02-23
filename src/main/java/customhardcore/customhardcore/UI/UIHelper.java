package customhardcore.customhardcore.UI;

import customhardcore.customhardcore.Enums.ConfigurationValues;
import customhardcore.customhardcore.Enums.InvUI;
import customhardcore.customhardcore.Enums.Settings;
import customhardcore.customhardcore.Objects.ShopItem;
import customhardcore.customhardcore.Helpers.ConfigurationHelper;
import customhardcore.customhardcore.Helpers.Logger;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class UIHelper {

    public static void createInventoryUI(Player player, InvUI invUI) {
        Inventory inventory = Bukkit.createInventory(player, 36, invUI.title);
        invUI.fillInventory(inventory, player);
    }

    public static void addBooleanItem(Inventory inventory, ConfigurationValues configValue,
                                       InvUI invUI) {
        boolean isSet = ConfigurationHelper.getConfig().getBoolean(configValue.name());
        ItemStack item = createItem(configValue.getDisplayName(), isSet, null, invUI);
        if (item == null) return;
        inventory.addItem(item);
    }

    public static void addBooleanSettingItem(Inventory inventory, Settings setting, Boolean bool) {
        ItemStack item = new ItemStack(bool ? Material.GREEN_CONCRETE : Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            Logger.error(UIHelper.class, "Could not create player setting - meta null");
            return;
        }
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', setting.getDisplayName()));
        item.setItemMeta(meta);
        inventory.addItem(item);
    }

    public static void addLocationItem(Inventory inventory, ConfigurationValues configValue,
                                        InvUI invUI) {
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
        ItemStack item = createItem(configValue.getDisplayName(), isSet, vectorString, invUI);
        if (item == null) return;
        inventory.addItem(item);
    }

    public static void addShopItem(Inventory inventory, String key, ShopItem item) {
        ItemStack itemStack = new ItemStack(item.getMaterial());
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return;
        meta.setDisplayName(item.getDisplayName());
        meta.setLore(Collections.singletonList(String.format("Price: %o points", item.getPrice())));

        meta.getPersistentDataContainer().set(Objects.requireNonNull(NamespacedKey.fromString(key)),
                PersistentDataType.STRING, item.getDisplayName());
        itemStack.setItemMeta(meta);
        inventory.addItem(itemStack);
    }

    public static void addKey(Inventory inventory, InvUI invUI) {
        ItemStack set = createItem("SET", true, null, invUI);
        ItemStack notSet = createItem("NOT SET", false, null, invUI);

        if (set == null || notSet == null) return;

        inventory.setItem(inventory.getSize()-1, notSet);
        inventory.setItem(inventory.getSize()-2, set);
    }

    private static ItemStack createItem(String name, boolean isSet, @Nullable String currentValue, InvUI invUI) {
        if (name == null) {
            Logger.error(UIHelper.class, "Cannot create item with null name");
            return null;
        }

        ItemStack item = new ItemStack(isSet ? Material.GREEN_CONCRETE : Material.BARRIER);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return null;
        meta.getPersistentDataContainer().set(Objects.requireNonNull(NamespacedKey.fromString(invUI.metaHeader)),
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
