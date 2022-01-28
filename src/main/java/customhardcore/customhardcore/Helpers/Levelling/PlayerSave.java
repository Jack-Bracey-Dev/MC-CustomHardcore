package customhardcore.customhardcore.Helpers.Levelling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import customhardcore.customhardcore.CustomHardcore;
import customhardcore.customhardcore.Helpers.Logger;
import customhardcore.customhardcore.Helpers.Msg;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerSave {

    private static final CustomHardcore instance = CustomHardcore.getInstance();
    public static HashMap<UUID, PlayerData> players;

    public static PlayerData getPlayer(UUID playerId) {
        if (players == null)
            players = new HashMap<>();
        if (!players.containsKey(playerId)) {
            PlayerData playerData = getOrCreatePlayerSave(instance.getServer().getPlayer(playerId));
            addPlayer(playerId, playerData);
        }
        return players.get(playerId);
    }

    public static void addPlayer(UUID playerId, PlayerData playerData) {
        if (players == null)
            players = new HashMap<>();
        if (!players.containsKey(playerId))
            players.put(playerId, playerData);
    }

    public static void removePlayer(UUID playerId) {
        if (players == null)
            players = new HashMap<>();
        players.remove(playerId);
    }

    public static PlayerData getOrCreatePlayerSave(Player player) {
        String folderString = instance.getDataFolder().getAbsolutePath()+"/PlayerSaves";
        if (!new File(folderString).exists()) {
            if (!new File(folderString).mkdirs()) {
                Logger.error("getOrCreatePlayerSave - Could not create folder at: " + folderString);
                return null;
            }
        }

        File playerDataFile = new File(String.format("%s\\%s.json", folderString, player.getUniqueId()));
        if (!new File(playerDataFile.getAbsolutePath()).exists()) {
            try {
                if (!playerDataFile.createNewFile())
                    Logger.error(String.format("Failed to create player data store for %s (%s)", player.getName(),
                            player.getUniqueId()));

                PlayerData playerData = new PlayerData(player);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(playerDataFile))) {
                    String json = serializeToJson(playerData);
                    if (json == null) {
                        Logger.error("Failed to serialize json to save file");
                        return null;
                    }
                    writer.write(json);
                }
            } catch (IOException e) {
                Logger.error(String.format("Failed to create player data store for %s (%s)", player.getName(),
                        player.getUniqueId()), e);
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(playerDataFile))) {
            String values = reader.lines().collect(Collectors.joining(System.lineSeparator()));

            //Load data from json
            PlayerData playerData = deserializeFromJson(values);
            addPlayer(player.getUniqueId(), playerData);
            if (playerData != null)
                return playerData;
            Logger.error(String.format("Failed to get player data for %s (%s)", player.getName(), player.getUniqueId()));
            return null;
        } catch (IOException e) {
            Logger.error(String.format("Error when getting player data for %s (%s)", player.getName(), player.getUniqueId()));
            return null;
        }
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
            String json = serializeToJson(Objects.requireNonNull(playerData));
            if (json == null) {
                Logger.error("Failed to serialize player data to json save");
                return;
            }
            writer.write(json);
        } catch (IOException e) {
            if (playerData != null)
                Logger.error(String.format("Failed to save player data for id: %s", playerData.getId()), e);
            else
                Logger.error("Failed to save player data", e);
        }
    }

    private static Long getNextLevelXpAmount(int level) {
        //No logic behind this, just a prototype
        return Math.round((level * 1000) * 1.25);
    }

    private static String serializeToJson(@Nonnull PlayerData playerData) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(playerData);
        } catch (JsonProcessingException e) {
            Logger.error("PlayerSave failed to serialize player data to string", e);
            return null;
        }
    }

    private static PlayerData deserializeFromJson(String values) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(values, PlayerData.class);
        } catch (JsonProcessingException e) {
            Logger.error("PlayerSave failed to deserialize player data");
            return null;
        }
    }

    public static String calculateLevelProgress(String progressBar, PlayerData playerData) {
        int barLength = progressBar.length();
        Long previousLevel = getNextLevelXpAmount(playerData.getLevel()-1);
        Long nextLevel = getNextLevelXpAmount(playerData.getLevel());
        long firstCalc = nextLevel - previousLevel;

        long percentage = Math.round(((double) playerData.getXp() / firstCalc) * 100);
        int progressPoint = Math.round(barLength * percentage);
        return insertStringIntoString(progressBar, "&4", progressPoint);
    }

    private static String insertStringIntoString(String original, String insert, int progressPoint) {
        String start = original.substring(0, progressPoint);
        String end = original.substring(progressPoint+1);
        return String.format("%s%s%s", start, insert, end);
    }

}
