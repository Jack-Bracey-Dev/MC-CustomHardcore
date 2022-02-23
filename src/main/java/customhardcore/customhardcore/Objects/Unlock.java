package customhardcore.customhardcore.Objects;

import customhardcore.customhardcore.Enums.Unlocks;
import customhardcore.customhardcore.Generic.DatabaseObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

public class Unlock extends DatabaseObject implements Serializable {

    public Integer id;

    private Unlocks unlock;

    private Integer playerDataId;

    public Unlock() {
    }

    public Unlock(Unlocks unlock, Integer playerDataId) {
        this.unlock = unlock;
        this.playerDataId = playerDataId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Unlocks getUnlock() {
        return unlock;
    }

    public void setUnlock(Unlocks unlock) {
        this.unlock = unlock;
    }

    public Integer getPlayerDataId() {
        return playerDataId;
    }

    public void setPlayerDataId(Integer playerDataId) {
        this.playerDataId = playerDataId;
    }

    @Override
    public List<Field> getIgnoredFields() {
        return null;
    }

}
