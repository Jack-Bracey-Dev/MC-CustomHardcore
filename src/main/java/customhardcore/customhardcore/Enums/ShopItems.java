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
            if (LIFE.canAfford(player, playerData)) {
                if (player.getStatistic(Statistic.DEATHS) > 0) {
                    player.setStatistic(Statistic.DEATHS, player.getStatistic(Statistic.DEATHS) - 1);
                    PlayerSave.spendPoint(player, this.getShopItem().getPrice());
                    Msg.send(player, "Congratulations on purchasing a life");
                } else {
                    Msg.send(player, "You do not have any deaths");
                }
            }
        }
    },
    SPECIAL_COBBLE_GEN("specialcobblegen", new ShopItem("Special Cobble Generator", 8, Material.COBBLESTONE)) {
        @Override
        public void purchase(Player player) {
            PlayerData playerData = PlayerSave.getPlayerData(player);
            if (SPECIAL_COBBLE_GEN.canAfford(player, playerData)) {
                PlayerSave.giveUnlock(playerData, Unlocks.SPECIAL_COBBLE_GEN);
                Msg.send(player, "Congratulations on purchasing special cobblestone, cobblestone generators" +
                        " will now spawn ores and gems when you're close!");
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

    private boolean canAfford(Player player, PlayerData playerData) {
        if (playerData.getPoints() >= this.getShopItem().getPrice())
            return true;
        Msg.send(player, String.format("You cannot afford that item, you only have %o points", playerData.getPoints()));
        return false;
    }

}

