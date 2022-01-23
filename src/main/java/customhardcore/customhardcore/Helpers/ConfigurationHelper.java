package customhardcore.customhardcore.Helpers;

import customhardcore.customhardcore.CustomHardcore;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;

public class ConfigurationHelper {

    public enum ConfigurationValues {
        SIGN_LOCATION(ConfigurationDataTypes.LOCATION, false, null),
        DEATH_LOCATION(ConfigurationDataTypes.LOCATION, false, null),
        ENABLE_MAX_DEATHS(ConfigurationDataTypes.BOOLEAN, true, false),
        MAX_DEATHS(ConfigurationDataTypes.INTEGER, true, 3),
        ENABLE_TELEPORT_ON_DEATH(ConfigurationDataTypes.BOOLEAN, true, true);

        ConfigurationValues(ConfigurationDataTypes expectedObjectType,
                            Boolean required,
                            Object defaultValue) {
            this.expectedObjectType = expectedObjectType;
            this.required = required;
            this.defaultValue = defaultValue;
        }

        private ConfigurationDataTypes expectedObjectType;
        private Boolean required;
        private Object defaultValue;

        public void setExpectedObjectType(ConfigurationDataTypes expectedObjectType) {
            this.expectedObjectType = expectedObjectType;
        }

        public ConfigurationDataTypes getExpectedObjectType() {
            return expectedObjectType;
        }

        public Boolean getRequired() {
            return required;
        }

        public void setRequired(Boolean required) {
            this.required = required;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
        }
    }

    private static final CustomHardcore instance = CustomHardcore.getInstance();

    public enum ConfigurationDataTypes {
        LOCATION, BOOLEAN, INTEGER
    }

    public static void checkAndSetConfig() {
        instance.saveConfig();
        Arrays.stream(ConfigurationValues.values()).filter(val -> val.required).forEach(val -> {
            if (!instance.getConfig().contains(val.name()))
                instance.getConfig().set(val.name(), val.defaultValue);
            instance.saveConfig();
        });
    }

    public static boolean isMaxDeathsEnabled() {
        return getConfig().getBoolean(ConfigurationHelper.ConfigurationValues.ENABLE_MAX_DEATHS.name());
    }

    public static void save() {
        instance.saveConfig();
    }

    public static FileConfiguration getConfig() {
        return instance.getConfig();
    }

}
