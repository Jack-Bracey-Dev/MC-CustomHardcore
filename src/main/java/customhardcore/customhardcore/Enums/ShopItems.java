package customhardcore.customhardcore.Enums;

import customhardcore.customhardcore.Helpers.Msg;
import customhardcore.customhardcore.Levelling.PlayerData;
import customhardcore.customhardcore.Levelling.PlayerSave;
import customhardcore.customhardcore.Objects.ShopItem;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public enum ShopItems {
    LIFE("life", new ShopItem("Life", 1, Material.RED_MUSHROOM)) {
        @Override
        public void purchase(Player player) {
            PlayerData playerData = PlayerSave.getPlayerData(player);
            if (playerData.getPoints() >= this.getShopItem().getPrice()) {
                if (player.getStatistic(Statistic.DEATHS) > 0) {
                    player.setStatistic(Statistic.DEATHS, player.getStatistic(Statistic.DEATHS) - 1);
                    PlayerSave.spendPoint(player, this.getShopItem().getPrice());
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

