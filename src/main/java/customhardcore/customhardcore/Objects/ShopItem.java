package customhardcore.customhardcore.Objects;

import org.bukkit.Material;

public class ShopItem {

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
