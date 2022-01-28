package customhardcore.customhardcore.Helpers.Levelling;

import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData implements Serializable {

    private String id;

    private Integer level;

    private Integer xp;

    private Integer nextLevelXp;

    private Integer points;

    private List<Integer> unlocks;

    public PlayerData(String id, Integer level, Integer xp, Integer nextLevelXp, Integer points,
                      List<Integer> unlocks) {
        this.id = id;
        this.level = level;
        this.xp = xp;
        this.nextLevelXp = nextLevelXp;
        this.points = points;
        this.unlocks = unlocks;
    }

    public PlayerData(Player player) {
        this.id = player.getUniqueId().toString();
        this.xp = 0;
        this.level = 1;
        this.points = 0;
        this.nextLevelXp = PlayerSave.getNextLevelXpAmount(1);
        this.unlocks = new ArrayList<>();
    }

    public PlayerData() {
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

    public Integer getNextLevelXp() {
        return nextLevelXp;
    }

    public void setNextLevelXp(Integer nextLevelXp) {
        this.nextLevelXp = nextLevelXp;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public void increaseLevel() {
        this.level++;
        this.points++;
        this.nextLevelXp = PlayerSave.getNextLevelXpAmount(this.level);
    }

    public boolean usePoint(Integer cost) {
        if (this.points < cost)
            return false;
        this.points -= cost;
        return true;
    }
}
