package customhardcore.customhardcore.Helpers.UI;

import customhardcore.customhardcore.Helpers.ConfigurationHelper;
import customhardcore.customhardcore.Helpers.Logger;
import customhardcore.customhardcore.Helpers.ScoreboardHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
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
                .get(Objects.requireNonNull(NamespacedKey.fromString(Enums.InvUI.CONFIGURATION.metaHeader)),
                        PersistentDataType.STRING);

        if (name == null) {
            Logger.error("Inventory item doesn't have metadata for name");
            return;
        }

        ConfigurationHelper.ConfigurationValues value = ConfigurationHelper.ConfigurationValues
                .getConfigValueByString(name);

        if (value == null) {
            Logger.error("Could not get configuration value from onInventoryItemTouched");
            return;
        }

        value.onChange(event.getWhoClicked() instanceof Player ? (Player) event.getWhoClicked() : null);
        if (event.getInventory().getViewers().size() > 0) {
            List<HumanEntity> entities = event.getInventory().getViewers().stream()
                    .filter(entity -> (entity instanceof Player)).collect(Collectors.toList());
            if (entities.size() > 0)
                entities.forEach(entity -> {
                    Player player = (Player) entity;
                    Enums.InvUI.CONFIGURATION.fillInventory(event.getInventory(), player);
                });
        }
    }

    public static void shopEvent(InventoryClickEvent event) {
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player))
            return;

        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null)
            return;

        Optional<Enums.ShopItems> shopItemsOptional = Arrays.stream(Enums.ShopItems.values()).filter(shopItem ->
                shopItem.getShopItem().getDisplayName().equalsIgnoreCase(event.getCurrentItem().getItemMeta().getDisplayName()))
                .findFirst();

        if (!shopItemsOptional.isPresent())
            return;

        Enums.ShopItems shopItemsValue = shopItemsOptional.get();
        shopItemsValue.purchase(player);
        ScoreboardHelper.updateBoard(player);
        player.closeInventory();
    }

}
