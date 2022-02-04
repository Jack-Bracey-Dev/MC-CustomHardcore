package customhardcore.customhardcore.Objects;

import customhardcore.customhardcore.Enums.Settings;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.HashMap;

public class PlayerSettings implements Serializable {

    private String id;
    private HashMap<Settings, Boolean> settings;

    public PlayerSettings(Player player) {
        this.id = player.getUniqueId().toString();
        HashMap<Settings, Boolean> map = new HashMap<>();
        for (Settings value : Settings.values())
            map.put(value, value.getDefaultValue());
        this.settings = map;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<Settings, Boolean> getSettings() {
        return settings;
    }

    public void setSettings(HashMap<Settings, Boolean> settings) {
        this.settings = settings;
    }

    public void toggleSetting(Settings setting) {
        Boolean enabled = getSettings().get(setting);
        settings.replace(setting, !enabled);
    }
}
