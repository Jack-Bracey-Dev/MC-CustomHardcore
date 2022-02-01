package customhardcore.customhardcore.Enums;

import customhardcore.customhardcore.Objects.PlayerSettings;
import customhardcore.customhardcore.Objects.ShopItem;
import customhardcore.customhardcore.PlayerSettings.PlayerSpecificSettings;
import customhardcore.customhardcore.UI.UIHelper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public enum InvUI {
    CONFIGURATION("Configuration", "settings_meta") {
        @Override
        public void fillInventory(Inventory inventory, Player player) {
            inventory.clear();
            UIHelper.addBooleanItem(inventory, ConfigurationValues.ENABLE_PLAYER_LIVES, this);
            UIHelper.addBooleanItem(inventory, ConfigurationValues.ENABLE_TELEPORT_ON_DEATH, this);
            UIHelper.addLocationItem(inventory, ConfigurationValues.SIGN_LOCATION, this);
            UIHelper.addLocationItem(inventory, ConfigurationValues.DEATH_LOCATION, this);

            UIHelper.addKey(inventory, this);
            player.openInventory(inventory);
        }
    },
    SHOP("Point Shop", "shop_meta") {
        @Override
        public void fillInventory(Inventory inventory, Player player) {
            HashMap<String, ShopItem> shopItems = new HashMap<>();

            for (ShopItems value : ShopItems.values())
                shopItems.put(value.getKey(), value.getShopItem());

            shopItems.forEach((key, item) -> UIHelper.addShopItem(inventory, key, item));
            player.openInventory(inventory);
        }
    },
    SETTINGS("Settings", "settings_meta") {
        @Override
        public void fillInventory(Inventory inventory, Player player) {
            PlayerSettings settings = PlayerSpecificSettings.getPlayerSettings(player);
            HashMap<Settings, Boolean> map = settings.getSettings();

            inventory.clear();
            map.forEach((setting, bool) -> UIHelper.addBooleanSettingItem(inventory, setting, bool));

            player.openInventory(inventory);
        }
    };

    public final String title;
    public final String metaHeader;

    public abstract void fillInventory(Inventory inventory, Player player);

    InvUI(String title, String metaHeader) {
        this.title = title;
        this.metaHeader = metaHeader;
    }
}
