package customhardcore.customhardcore.UI;

import customhardcore.customhardcore.Enums.ConfigurationValues;
import customhardcore.customhardcore.Enums.InvUI;
import customhardcore.customhardcore.Enums.Settings;
import customhardcore.customhardcore.Enums.ShopItems;
import customhardcore.customhardcore.Helpers.Logger;
import customhardcore.customhardcore.Helpers.PlayerHelper;
import customhardcore.customhardcore.Helpers.ScoreboardHelper;
import customhardcore.customhardcore.Objects.PlayerSettings;
import customhardcore.customhardcore.PlayerSettings.PlayerSpecificSettings;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class InventoryEvents {

    public static void configurationEvent(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null)
            return;

        if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("SET") ||
                event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("NOT SET")) return;

        String name = event.getCurrentItem().getItemMeta().getPersistentDataContainer()
                .get(Objects.requireNonNull(NamespacedKey.fromString(InvUI.CONFIGURATION.metaHeader)),
                        PersistentDataType.STRING);

        if (name == null) {
            Logger.error("Inventory item doesn't have metadata for name");
            return;
        }

        ConfigurationValues value = ConfigurationValues.getConfigValueByString(name);

        if (value == null) {
            Logger.error("Could not get configuration value from onInventoryItemTouched");
            return;
        }

        value.onChange(event.getWhoClicked() instanceof Player ? (Player) event.getWhoClicked() : null);
        reloadInventory(event, InvUI.CONFIGURATION);
    }

    public static void shopEvent(InventoryClickEvent event) {
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            Logger.error("shopEvent not an instance of a player");
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null)
            return;

        Optional<ShopItems> shopItemsOptional = Arrays.stream(ShopItems.values()).filter(shopItem ->
                shopItem.getShopItem().getDisplayName().equalsIgnoreCase(event.getCurrentItem().getItemMeta().getDisplayName()))
                .findFirst();

        if (!shopItemsOptional.isPresent()) {
            Logger.error("shopEvent selected item is not a valid shop item (" +
                    event.getCurrentItem().getItemMeta().getDisplayName() + ")");
            return;
        }

        ShopItems shopItemsValue = shopItemsOptional.get();
        shopItemsValue.purchase(player);
        ScoreboardHelper.updateBoard(player);
        player.closeInventory();
    }

    public static void settingEvent(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player))
            return;

        Player player = (Player) event.getWhoClicked();

        ItemStack item = event.getCurrentItem();
        if (item == null)
            return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return;

        Optional<Settings> settingsOptional = Arrays.stream(Settings.values())
                .filter(setting -> meta.getDisplayName().toLowerCase()
                        .contains(setting.getDisplayName().toLowerCase())).findFirst();

        if (!settingsOptional.isPresent())
            return;

        Settings setting = settingsOptional.get();
        PlayerSpecificSettings.toggleSetting(player, setting);

        if (setting.equals(Settings.TOGGLE_SCOREBOARD)) {
            PlayerSettings playerSettings = PlayerSpecificSettings.getPlayerSettings(player);
            if (playerSettings.getSettings().get(setting))
                ScoreboardHelper.createOrUpdatePlayerBoard(player);
            else
                ScoreboardHelper.removeBoard(player);
        }

        if (setting.equals(Settings.TOGGLE_PVP) && (!PlayerHelper.checkPlayerCanTogglePvP(player))) PlayerSpecificSettings.toggleSetting(player, setting);
        

        reloadInventory(event, InvUI.SETTINGS);
    }

    private static void reloadInventory(InventoryClickEvent event, InvUI invUI) {
        if (event.getInventory().getViewers().size() > 0) {
            List<HumanEntity> entities = event.getInventory().getViewers().stream()
                    .filter(entity -> (entity instanceof Player)).collect(Collectors.toList());
            if (entities.size() > 0)
                entities.forEach(entity -> {
                    Player plyr = (Player) entity;
                    invUI.fillInventory(event.getInventory(), plyr);
                });
        }
    }

}

