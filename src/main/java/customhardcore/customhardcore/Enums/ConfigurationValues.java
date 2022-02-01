package customhardcore.customhardcore.Enums;

import customhardcore.customhardcore.CustomHardcore;
import customhardcore.customhardcore.Helpers.ConfigurationHelper;
import customhardcore.customhardcore.Helpers.Misc;
import customhardcore.customhardcore.Helpers.Msg;
import customhardcore.customhardcore.Helpers.ScoreboardHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

public enum ConfigurationValues {
    SIGN_LOCATION("End Sign Location", ConfigurationHelper.ConfigurationDataTypes.LOCATION, false, null) {
        @Override
        public void onChange(@Nullable Player player) {
            if (player == null) return;

            Location previousLocation = ConfigurationHelper.getConfig().getLocation(this.name());
            Block block = player.getTargetBlockExact(5);
            if (block != null) {
                Location location = block.getLocation();
                location.add(0, 1, 0);
                location.setDirection(player.getEyeLocation().getDirection().multiply(-1));
                ConfigurationHelper.getConfig().set(this.name(), location);
                ConfigurationHelper.save();
                Misc.createSigns(ConfigurationHelper.getConfig().getLocation(this.name()));
                Misc.removeOrphanedSign(previousLocation);
            }
        }
    },
    DEATH_LOCATION("Purgatory Location", ConfigurationHelper.ConfigurationDataTypes.LOCATION, false, null) {
        @Override
        public void onChange(@Nullable Player player) {
            if (player == null) return;

            CustomHardcore.getInstance().getConfig().set(this.name(), player.getLocation());
            ConfigurationHelper.save();
            Msg.send(player, "Purgatory point set", "&3");
        }
    },
    ENABLE_PLAYER_LIVES("Enable Player Lives", ConfigurationHelper.ConfigurationDataTypes.BOOLEAN, true, false) {
        @Override
        public void onChange(@Nullable Player player) {
            boolean enabled = ConfigurationHelper.toggleBoolean(this);
            if (enabled)
                Bukkit.getServer().getOnlinePlayers().forEach(ScoreboardHelper::createOrUpdatePlayerBoard);
            else
                Bukkit.getServer().getOnlinePlayers().forEach(ScoreboardHelper::removeBoard);
        }
    },
    STARTING_LIVES("Starting number of lives", ConfigurationHelper.ConfigurationDataTypes.INTEGER, true, 3) {
        @Override
        public void onChange(@Nullable Player player) {}
    },
    ENABLE_TELEPORT_ON_DEATH("Enable Teleport On Death", ConfigurationHelper.ConfigurationDataTypes.BOOLEAN, true, true) {
        @Override
        public void onChange(@Nullable Player player) {
            ConfigurationHelper.toggleBoolean(this);
        }
    };

    ConfigurationValues(String displayName,
                        ConfigurationHelper.ConfigurationDataTypes expectedObjectType,
                        Boolean required,
                        Object defaultValue) {
        this.displayName = displayName;
        this.expectedObjectType = expectedObjectType;
        this.required = required;
        this.defaultValue = defaultValue;
    }

    private String displayName;
    private ConfigurationHelper.ConfigurationDataTypes expectedObjectType;
    private Boolean required;
    private Object defaultValue;

    public void setExpectedObjectType(ConfigurationHelper.ConfigurationDataTypes expectedObjectType) {
        this.expectedObjectType = expectedObjectType;
    }

    public ConfigurationHelper.ConfigurationDataTypes getExpectedObjectType() {
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
