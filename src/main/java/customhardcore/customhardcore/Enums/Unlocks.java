package customhardcore.customhardcore.Enums;

public enum Unlocks {
    SPECIAL_COBBLE_GEN("Special Cobble Gen");

    private String displayName;

    Unlocks(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
