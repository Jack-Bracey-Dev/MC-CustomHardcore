package customhardcore.customhardcore.Helpers.Levelling;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;

public class PlayerData implements ConfigurationSerializable {

    private String id;

    private Integer level;

    private Integer xp;

    private List<Integer> unlocks;

    public PlayerData(String id, Integer level, Integer xp, List<Integer> unlocks) {
        this.id = id;
        this.level = level;
        this.xp = xp;
        this.unlocks = unlocks;
    }

    public PlayerData(Player player) {
        this.id = player.getUniqueId().toString();
        this.xp = 0;
        this.level = 0;
        this.unlocks = new ArrayList<>();
    }

    public PlayerData() {
    }

    public PlayerData(Map<String, Object> map) {
        this.id = (String) map.get("id");
        this.xp = (Integer) map.get("xp");
        this.level = (Integer) map.get("level");
        List<Integer> unlocks = new ArrayList<>();
        if (map.get("unlocks") instanceof List)
            unlocks = (List<Integer>) map.get("unlocks");
        this.unlocks = unlocks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getXp() {
        return xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public void addXp(Integer xp) {
        this.xp = (this.xp + xp);
    }

    public List<Integer> getUnlocks() {
        if (unlocks == null)
            return new ArrayList<>();
        return unlocks;
    }

    public void addUnlock(Integer unlock) {
        if (unlocks == null)
            this.unlocks = new ArrayList<>();
        unlocks.add(unlock);
    }

    public void removeUnlock(Integer unlock) {
        if (unlocks == null)
            this.unlocks = new ArrayList<>();
        unlocks.remove(unlock);
    }

    public UUID getUuid() {
        return UUID.fromString(this.id);
    }

    public void setUnlocks(List<Integer> unlocks) {
        this.unlocks = unlocks;
    }

    @Override
    @Nonnull
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("xp", this.xp);
        map.put("level", this.level);
        map.put("unlocks", this.unlocks);
        return map;
    }

}
