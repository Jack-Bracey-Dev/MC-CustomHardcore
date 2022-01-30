package customhardcore.customhardcore.Helpers.Levelling;

import customhardcore.customhardcore.CustomHardcore;
import customhardcore.customhardcore.Helpers.Logger;
import customhardcore.customhardcore.Helpers.Msg;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerSave {

    private static final CustomHardcore instance = CustomHardcore.getInstance();
    public static HashMap<UUID, PlayerData> players;

    public static PlayerData getPlayer(Player player) {
        if (players == null)
            players = new HashMap<>();
        if (!players.containsKey(player.getUniqueId())) {
            PlayerData playerData = getOrCreatePlayerSave(instance.getServer().getPlayer(player.getUniqueId()));
            addPlayer(player, playerData);
        }
        return players.get(player.getUniqueId());
    }

    public static void addPlayer(Player player, PlayerData playerData) {
        if (players == null)
            players = new HashMap<>();
        if (!players.containsKey(player.getUniqueId()))
            players.put(player.getUniqueId(), playerData);
    }

    public static void removePlayer(Player player) {
        if (player == null) {
            Logger.error("WTF HAPPENED?!");
            return;
        }
        PlayerData playerData = getPlayer(player);
        if (playerData == null) {
            Logger.error("Could not remove player data from hashmap on quit");
            return;
        }
        save(playerData);
        players.remove(playerData.getUuid());
    }

    public static PlayerData getOrCreatePlayerSave(Player player) {
        String folderString = instance.getDataFolder().getAbsolutePath()+"/PlayerSaves";
        if (!new File(folderString).exists()) {
            if (!new File(folderString).mkdirs()) {
                Logger.error("getOrCreatePlayerSave - Could not create folder at: " + folderString);
                return null;
            }
        }

        File playerDataFile = new File(String.format("%s\\%s.yml", folderString, player.getUniqueId()));
        if (!new File(playerDataFile.getAbsolutePath()).exists()) {
            try {
                if (!playerDataFile.createNewFile())
                    Logger.error(String.format("Failed to create player data store for %s (%s)", player.getName(),
                            player.getUniqueId()));

                PlayerData playerData = new PlayerData(player);
                YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(playerDataFile);
                yamlConfiguration.set(player.getName(), playerData);
                yamlConfiguration.save(playerDataFile);
            } catch (IOException e) {
                Logger.error(String.format("Failed to create player data store for %s (%s)", player.getName(),
                        player.getUniqueId()), e);
            }
        }

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(playerDataFile);
        PlayerData playerData = yamlConfiguration.getSerializable(player.getName(), PlayerData.class);
        addPlayer(player, playerData);
        if (playerData != null)
            return playerData;
        Logger.error(String.format("Failed to get player data for %s (%s)", player.getName(), player.getUniqueId()));
        return null;
    }

    public static PlayerData calculateLevel(Player player) {
        PlayerData playerData = getOrCreatePlayerSave(player);
        if (playerData == null) {
            Logger.error(String.format("Could not get player data for %s (%s)", player.getName(), player.getUniqueId()));
            return null;
        }

        Long nextLevelXp = getNextLevelXpAmount(playerData.getLevel());
        if (playerData.getXp() > nextLevelXp) {
            playerData.setLevel(player.getLevel() + 1);
            save(playerData);
        }
        return playerData;
    }

    private static void save(PlayerData playerData) {
        if (playerData == null) {
            Logger.info("Unable to save player data as the player data is null");
            return;
        }

        File saveFile = new File(instance.getDataFolder().getAbsolutePath()+"/PlayerSaves"+playerData.getId());
        if (!saveFile.exists())
            playerData = getOrCreatePlayerSave(instance.getServer().getPlayer(playerData.getId()));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
            new Yaml().dump(playerData, writer);
        } catch (IOException e) {
            if (playerData != null)
                Logger.error(String.format("Failed to save player data for id: %s", playerData.getId()), e);
            else
                Logger.error("Failed to save player data", e);
        }
    }

    private static Long getNextLevelXpAmount(int level) {
        //No logic behind this, just a prototype
        return Math.round((level * 10000) * 1.25);
    }

    public static String calculateLevelProgress(String progressBar, PlayerData playerData) {
        int barLength = progressBar.length();
        Long previousLevel = getNextLevelXpAmount(playerData.getLevel()-1);
        Long nextLevel = getNextLevelXpAmount(playerData.getLevel());
        long firstCalc = nextLevel - previousLevel;

        long percentage = Math.round(((double) playerData.getXp() / firstCalc) * 100);
        int progressPoint = Math.round(barLength * percentage);
        return insertStringIntoString(progressBar, progressPoint);
    }

    private static String insertStringIntoString(String original, int progressPoint) {
        String start = original.substring(0, progressPoint);
        String end = original.substring(progressPoint+1);
        return String.format("%s%s%s", start, "&4", end);
    }

    public static void addXp(Player player, Integer xp) {
        PlayerData playerData = getPlayer(player);
        Msg.send(player, players.size() + "");
        if (playerData == null)
            return;
        playerData.addXp(xp);
        players.replace(player.getUniqueId(), playerData);
    }

}
