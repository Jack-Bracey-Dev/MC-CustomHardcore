package customhardcore.customhardcore.Helpers.UI;

import customhardcore.customhardcore.Helpers.ConfigurationHelper;
import customhardcore.customhardcore.Helpers.Levelling.PlayerData;
import customhardcore.customhardcore.Helpers.Levelling.PlayerSave;
import customhardcore.customhardcore.Helpers.Msg;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class Enums {

    public enum InvUI {
        CONFIGURATION("Configuration", "settings_meta") {
            @Override
            public void fillInventory(Inventory inventory, Player player) {
                inventory.clear();
                UIHelper.addBooleanItem(inventory, ConfigurationHelper.ConfigurationValues.ENABLE_MAX_DEATHS, this);
                UIHelper.addBooleanItem(inventory, ConfigurationHelper.ConfigurationValues.ENABLE_TELEPORT_ON_DEATH, this);
                UIHelper.addLocationItem(inventory, ConfigurationHelper.ConfigurationValues.SIGN_LOCATION, this);
                UIHelper.addLocationItem(inventory, ConfigurationHelper.ConfigurationValues.DEATH_LOCATION, this);

                UIHelper.addKey(inventory, this);
                player.openInventory(inventory);
            }
        },
        SHOP("Point Shop", "shop_meta") {
            @Override
            public void fillInventory(Inventory inventory, Player player) {
                HashMap<String, ShopItem> shopItems = new HashMap<>();

                for (ShopItems value : ShopItems.values())
                    shopItems.put(value.key, value.shopItem);

                shopItems.forEach((key, item) -> UIHelper.addShopItem(inventory, key, item));
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

    public enum ShopItems {
        LIFE("life", new ShopItem("Life", 1, Material.RED_MUSHROOM)) {
            @Override
            public void purchase(Player player) {
                PlayerData playerData = PlayerSave.getPlayerData(player);
                if (playerData.getPoints() >= this.getShopItem().price) {
                    if (player.getStatistic(Statistic.DEATHS) > 0) {
                        player.setStatistic(Statistic.DEATHS, player.getStatistic(Statistic.DEATHS) - 1);
                        PlayerSave.spendPoint(player, this.getShopItem().price);
                        Msg.send(player, "Congratulations on purchasing a life");
                    } else {
                        Msg.send(player, "You do not have any deaths");
                    }
                }
            }
        };

        private String key;
        private ShopItem shopItem;

        ShopItems(String key,ShopItem shopItem) {
            this.key = key;
            this.shopItem = shopItem;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public ShopItem getShopItem() {
            return shopItem;
        }

        public void setShopItem(ShopItem shopItem) {
            this.shopItem = shopItem;
        }

        public abstract void purchase(Player player);

    }

    public static class ShopItem {

        private String displayName;
        private Integer price;
        private Material material;

        public ShopItem(String displayName, Integer price, Material material) {
            this.displayName = displayName;
            this.price = price;
            this.material = material;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }

        public Material getMaterial() {
            return material;
        }

        public void setMaterial(Material material) {
            this.material = material;
        }
    }

}
