package customhardcore.customhardcore.Enums;

public enum Settings {
    TOGGLE_SCOREBOARD("Toggle Scoreboard", true);

    private String displayName;
    private Boolean defaultValue;

    Settings(String displayName, Boolean defaultValue) {
        this.displayName = displayName;
        this.defaultValue = defaultValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Boolean defaultValue) {
        this.defaultValue = defaultValue;
    }
}
