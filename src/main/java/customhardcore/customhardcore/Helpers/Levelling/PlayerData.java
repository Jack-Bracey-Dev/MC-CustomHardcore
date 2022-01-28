package customhardcore.customhardcore.Helpers.Levelling;

import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData implements Serializable {

    private UUID id;

    private Integer level;

    private Long xp;

    private List<Long> unlocks;

    public PlayerData(UUID id, Integer level, Long xp, List<Long> unlocks) {
        this.id = id;
        this.level = level;
        this.xp = xp;
        this.unlocks = unlocks;
    }

    public PlayerData(Player player) {
        this.id = player.getUniqueId();
        this.xp = 0L;
        this.level = 0;
        this.unlocks = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Long getXp() {
        return xp;
    }

    public void setXp(Long xp) {
        this.xp = xp;
    }

    public List<Long> getUnlocks() {
        if (unlocks == null)
            return new ArrayList<>();
        return unlocks;
    }

    public void addUnlock(Long unlock) {
        if (unlocks == null)
            this.unlocks = new ArrayList<>();
        unlocks.add(unlock);
    }

    public void removeUnlock(Long unlock) {
        if (unlocks == null)
            this.unlocks = new ArrayList<>();
        unlocks.remove(unlock);
    }
}
