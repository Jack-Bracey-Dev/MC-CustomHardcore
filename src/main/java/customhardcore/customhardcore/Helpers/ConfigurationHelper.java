package customhardcore.customhardcore.Helpers;

import customhardcore.customhardcore.CustomHardcore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

public class ConfigurationHelper {

    public enum ConfigurationValues {
        SIGN_LOCATION("End Sign Location", ConfigurationDataTypes.LOCATION, false, null) {
            @Override
            public void onChange(@Nullable Player player) {
                if (player == null) return;

                Location previousLocation = ConfigurationHelper.getConfig().getLocation(ConfigurationHelper.ConfigurationValues.SIGN_LOCATION.name());
                Block block = player.getTargetBlockExact(5);
                if (block != null) {
                    Location location = block.getLocation();
                    location.add(0, 1, 0);
                    location.setDirection(player.getEyeLocation().getDirection().multiply(-1));
                    getConfig().set(ConfigurationHelper.ConfigurationValues.SIGN_LOCATION.name(), location);
                    save();
                    CustomHardcore.getInstance().saveConfig();
                    Misc.createSigns(ConfigurationHelper.getConfig().getLocation(ConfigurationHelper.ConfigurationValues.SIGN_LOCATION.name()));
                    Misc.removeOrphanedSign(previousLocation);
                }
            }
        },
        DEATH_LOCATION("Purgatory Location", ConfigurationDataTypes.LOCATION, false, null) {
            @Override
            public void onChange(@Nullable Player player) {
                if (player == null) return;

                getConfig().set(ConfigurationHelper.ConfigurationValues.DEATH_LOCATION.name(), player.getLocation());
                save();
                Msg.send(player, "Purgatory point set", "&3");
            }
        },
        ENABLE_MAX_DEATHS("Enable Max Deaths", ConfigurationDataTypes.BOOLEAN, true, false) {
            @Override
            public void onChange(@Nullable Player player) {
                boolean enabled = toggleBoolean(this);
                if (enabled)
                    Bukkit.getServer().getOnlinePlayers().forEach(ScoreboardHelper::createOrUpdatePlayerBoard);
                else
                    Bukkit.getServer().getOnlinePlayers().forEach(ScoreboardHelper::removeBoard);
            }
        },
        MAX_DEATHS("Number Of Max Deaths", ConfigurationDataTypes.INTEGER, true, 3) {
            @Override
            public void onChange(@Nullable Player player) {}
        },
        ENABLE_TELEPORT_ON_DEATH("Enable Teleport On Death", ConfigurationDataTypes.BOOLEAN, true, true) {
            @Override
            public void onChange(@Nullable Player player) {
                toggleBoolean(this);
            }
        };

        ConfigurationValues(String displayName,
                            ConfigurationDataTypes expectedObjectType,
                            Boolean required,
                            Object defaultValue) {
            this.displayName = displayName;
            this.expectedObjectType = expectedObjectType;
            this.required = required;
            this.defaultValue = defaultValue;
        }

        private String displayName;
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

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public static ConfigurationValues getConfigValueByString(final String value) {
            Optional<ConfigurationValues> optional = Arrays.stream(ConfigurationValues.values())
                    .filter(val -> val.name().toLowerCase().contains(value.toLowerCase()) ||
                            val.getDisplayName().toLowerCase().contains(value.toLowerCase()))
                    .findFirst();
            return optional.orElse(null);
        }

        public abstract void onChange(@Nullable Player player);

    }

    private static final CustomHardcore instance = CustomHardcore.getInstance();

    public enum ConfigurationDataTypes {
        LOCATION, BOOLEAN, INTEGER
    }

    public static void checkAndSetConfig() {
        save();
        Arrays.stream(ConfigurationValues.values()).filter(val -> val.required).forEach(val -> {
            if (!instance.getConfig().contains(val.name()))
                instance.getConfig().set(val.name(), val.defaultValue);
            save();
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

    public static boolean toggleBoolean(ConfigurationValues value) {
        boolean enabled = getConfig().getBoolean(value.name());
        getConfig().set(value.name(), !enabled);
        save();
        return !enabled;
    }

}
