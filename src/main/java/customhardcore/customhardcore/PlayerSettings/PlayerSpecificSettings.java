package customhardcore.customhardcore.PlayerSettings;

import customhardcore.customhardcore.CustomHardcore;
import customhardcore.customhardcore.Enums.Settings;
import customhardcore.customhardcore.Generic.FileHandler;
import customhardcore.customhardcore.Helpers.Logger;
import customhardcore.customhardcore.Objects.PlayerSettings;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class PlayerSpecificSettings extends FileHandler {

    private static final CustomHardcore instance = CustomHardcore.getInstance();
    private static final String FOLDER = "PlayerSettings";
    public static HashMap<UUID, PlayerSettings> players;

    public static void initialisePlayerSettings(Player player) {
        String settingsDir = getSaveFileDir(instance, player,FOLDER);
        if (!(new File(settingsDir).exists()))
            createNewSettings(settingsDir, player);

        PlayerSettings settings = getPlayerSettingsFromFile(settingsDir);
        addPlayer(player, settings);
    }

    private static void createNewSettings(String fileString, Player player) {
        File settingsFile = createFile(fileString);
        if (settingsFile == null)
            return;
        PlayerSettings playerSettings = new PlayerSettings(player);
        if (!writeToFile(playerSettings, settingsFile))
            Logger.error(PlayerSpecificSettings.class, "Failed to write player data to file");
    }

    private static void addPlayer(Player player, PlayerSettings settings) {
        if (players == null)
            players = new HashMap<>();
        players.put(player.getUniqueId(), settings);
    }

    public static void removePlayer(Player player) {
        save(player, getPlayerSettings(player));
        players.remove(player.getUniqueId());
    }

    public static void save(Player player, PlayerSettings playerSettings) {
        writeToFile(playerSettings, new File(getSaveFileDir(instance, player, FOLDER)));
    }

    public static PlayerSettings getPlayerSettings(Player player) {
        return players.get(player.getUniqueId());
    }

    private static PlayerSettings getPlayerSettingsFromFile(String fileString) {
        return readFromFile(fileString, PlayerSettings.class);
    }

    public static void toggleSetting(Player player, Settings setting) {
        PlayerSettings settings = getPlayerSettings(player);
        settings.toggleSetting(setting);
    }

}

