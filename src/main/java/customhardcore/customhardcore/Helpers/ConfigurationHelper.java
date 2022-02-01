package customhardcore.customhardcore.Helpers;

import customhardcore.customhardcore.CustomHardcore;
import customhardcore.customhardcore.Enums.ConfigurationValues;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;

public class ConfigurationHelper {

    private static final CustomHardcore instance = CustomHardcore.getInstance();

    public static Integer getInt(ConfigurationValues configurationValue) {
        return getConfig().getInt(configurationValue.name());
    }

    public static boolean getBoolean(ConfigurationValues configurationValue) {
        return getConfig().getBoolean(configurationValue.name());
    }

    public enum ConfigurationDataTypes {
        LOCATION, BOOLEAN, INTEGER
    }

    public static void checkAndSetConfig() {
        instance.saveConfig();
        Arrays.stream(ConfigurationValues.values()).filter(ConfigurationValues::getRequired).forEach(val -> {
            if (!instance.getConfig().contains(val.name()))
                instance.getConfig().set(val.name(), val.getDefaultValue());
            instance.saveConfig();
        });
    }

    public static boolean isMaxDeathsEnabled() {
        return getConfig().getBoolean(ConfigurationValues.ENABLE_PLAYER_LIVES.name());
    }

    public static void save() {
        instance.saveConfig();
    }

    public static FileConfiguration getConfig() {
        return instance.getConfig();
    }

    public static boolean toggleBoolean(ConfigurationValues value) {
        boolean enabled = getConfig().getBoolean(value.name());
        getConfig().set(value.name(), !enabled);
        save();
        return !enabled;
    }

}
