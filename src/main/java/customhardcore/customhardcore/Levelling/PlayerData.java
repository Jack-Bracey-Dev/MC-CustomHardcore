package customhardcore.customhardcore.Levelling;

import customhardcore.customhardcore.Enums.ConfigurationValues;
import customhardcore.customhardcore.Enums.Unlocks;
import customhardcore.customhardcore.Helpers.ConfigurationHelper;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PlayerData implements Serializable {

    private String id;

    private Integer level;

    private Integer xp;

    private Integer nextLevelXp;

    private Integer points;

    private List<Unlocks> unlocks;

    private Integer lives;

    private Date lastCombatTime;

    public PlayerData(Player player) {
        this.id = player.getUniqueId().toString();
        this.xp = 0;
        this.level = 1;
        this.points = 0;
        this.nextLevelXp = PlayerSave.getNextLevelXpAmount(1);
        this.unlocks = new ArrayList<>();
        this.lives = ConfigurationHelper.getInt(ConfigurationValues.STARTING_LIVES);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, -10);
        this.lastCombatTime = calendar.getTime();
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

    public List<Unlocks> getUnlocks() {
        if (unlocks == null)
            return new ArrayList<>();
        return unlocks;
    }

    public void addUnlock(Unlocks unlock) {
        if (unlocks == null)
            this.unlocks = new ArrayList<>();
        unlocks.add(unlock);
    }

    public void removeUnlock(Unlocks unlock) {
        if (unlocks == null)
            this.unlocks = new ArrayList<>();
        unlocks.remove(unlock);
    }

    public UUID getUuid() {
        return UUID.fromString(this.id);
    }

    public void setUnlocks(List<Unlocks> unlocks) {
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

    public Integer getLives() {
        return lives;
    }

    public void setLives(Integer lives) {
        this.lives = lives;
    }

    public void usePoint(Integer cost) {
        if (this.points < cost)
            return;
        this.points -= cost;
    }

    public void increaseLevel() {
        this.level++;
        this.points++;
        this.nextLevelXp = PlayerSave.getNextLevelXpAmount(this.level);
    }

    public void setLastCombatTime(Date attackTime) {
        this.lastCombatTime = attackTime;
    }

    public Date getLastCombatTime() {
        return lastCombatTime;
    }

    public static PlayerData checkMissingElements(PlayerData playerData) {
        boolean hasMissingValues = false;
        if (playerData.getLevel() == null) {
            playerData.setLevel(0);
            hasMissingValues = true;
        }
        if (playerData.getXp() == null) {
            playerData.setXp(0);
            hasMissingValues = true;
        }
        if (playerData.getNextLevelXp() == null) {
            playerData.setNextLevelXp(PlayerSave.getNextLevelXpAmount(1));
            hasMissingValues = true;
        }
        if (playerData.getPoints() == null) {
            playerData.setPoints(0);
            hasMissingValues = true;
        }
        if (playerData.getUnlocks() == null) {
            playerData.setUnlocks(new ArrayList<>());
            hasMissingValues = true;
        }
        if (playerData.getLives() == null) {
            playerData.setLives(ConfigurationHelper.getInt(ConfigurationValues.STARTING_LIVES));
            hasMissingValues = true;
        }
        if (playerData.getLastCombatTime() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.MINUTE, -10);
            playerData.lastCombatTime = calendar.getTime();
            hasMissingValues = true;
        }

        return hasMissingValues ? playerData : null;
    }

    public void removeLife() {
        this.lives--;
    }
}
