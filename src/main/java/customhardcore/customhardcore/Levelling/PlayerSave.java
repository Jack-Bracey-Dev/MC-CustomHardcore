package customhardcore.customhardcore.Levelling;

import customhardcore.customhardcore.CustomHardcore;
import customhardcore.customhardcore.Enums.Unlocks;
import customhardcore.customhardcore.Generic.FileHandler;
import customhardcore.customhardcore.Helpers.Logger;
import customhardcore.customhardcore.Helpers.Msg;
import customhardcore.customhardcore.Helpers.ScoreboardHelper;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static customhardcore.customhardcore.Levelling.PlayerData.checkMissingElements;

public class PlayerSave extends FileHandler {

    private static final CustomHardcore instance = CustomHardcore.getInstance();
    public static HashMap<UUID, PlayerData> players;
    private static final String FOLDER = "PlayerSaves";

    public static void initialisePlayer(@Nonnull Player player) {
        String fileString = getSaveFileDir(instance, player, FOLDER);
        if (!new File(fileString).exists())
            createNewSave(fileString, player);

        addPlayer(player, getPlayerSave(fileString));
    }

    private static void createNewSave(String fileString, Player player) {
        File playerFile = createFile(fileString);
        if (playerFile == null)
            return;
        PlayerData playerData = new PlayerData(player);
        if (!writeToFile(playerData, playerFile))
            Logger.error("Failed to write player data to file");
    }

    private static PlayerData getPlayerSave(String fileString) {
        return readFromFile(fileString, PlayerData.class);
    }

    public static PlayerData getPlayerData(Player player) {
        return players.get(player.getUniqueId());
    }

    public static void removePlayer(Player player) {
        save(getPlayerData(player), player);
        players.remove(player.getUniqueId());
    }

    public static void addPlayer(Player player, PlayerData playerData) {
        if (players == null)
            players = new HashMap<>();
        players.put(player.getUniqueId(), playerData);
    }

    private static void save(PlayerData playerData, Player player) {
        if (player == null) {
            Logger.error("Failed to save player file - player is null");
            return;
        }
        writeToFile(playerData, new File(getSaveFileDir(instance, player, FOLDER)));
    }

    public static PlayerData calculateLevel(Player player) {
        PlayerData playerData = getPlayerData(player);
        if (playerData == null) {
            Logger.error(String.format("Could not get player data for %s (%s)", player.getName(), player.getUniqueId()));
            return null;
        }

        Integer nextLevelXp = getNextLevelXpAmount(playerData.getLevel());
        if (playerData.getXp() > nextLevelXp)
            playerData.setLevel(player.getLevel() + 1);
        return playerData;
    }

    public static Integer getNextLevelXpAmount(int level) {
        return Math.toIntExact(Math.round((level * 10000) * 1.25));
    }

    public static String calculateLevelProgress(String progressBar, PlayerData playerData) {
        int barLength = progressBar.length();
        Integer previousLevel = getNextLevelXpAmount(playerData.getLevel()-1);
        Integer nextLevel = getNextLevelXpAmount(playerData.getLevel());
        long firstCalc = nextLevel - previousLevel;
        double percentage = ((double) playerData.getXp() / firstCalc);
        if (percentage == 0)
            return insertStringIntoString(progressBar, 0);
        int progressPoint = Math.toIntExact(Math.round(barLength * percentage));
        return insertStringIntoString(progressBar, progressPoint);
    }

    private static String insertStringIntoString(String original, int progressPoint) {
        String start = original.substring(0, progressPoint);
        String end = original.substring(progressPoint+1);
        return String.format("%s%s%s", start, "&4&l", end);
    }

    public static void addXp(Player player, Integer xp) {
        PlayerData playerData = getPlayerData(player);
        if (playerData == null)
            return;
        playerData.addXp(xp);

        if (playerData.getXp() >= playerData.getNextLevelXp()) {
            playerData.increaseLevel();
            Msg.sendGlobal(String.format("%s just reached level %o", player.getName(), playerData.getLevel()));
        }
        players.replace(player.getUniqueId(), playerData);
        ScoreboardHelper.createOrUpdatePlayerBoard(player);
    }

    public static void spendPoint(Player player, Integer cost) {
        PlayerData playerData = getPlayerData(player);
        playerData.usePoint(cost);
    }

    public static void giveUnlock(PlayerData playerData, Unlocks unlock) {
        playerData.addUnlock(unlock);
        players.replace(playerData.getUuid(), playerData);
    }

    public static void checkForNewElements() {
        String folderString = getSaveFolderDir(instance, FOLDER);
        Logger.info("Checking save files for missing elements in: " + folderString);
        File folder = new File(folderString);
        if (!folder.exists())
            return;
        File[] files = folder.listFiles();
        if (files == null || files.length <= 0)
            return;

        Arrays.stream(files)
                .filter(file -> file.getName().toLowerCase().contains(".json"))
                .map(file -> readFromFile(file.getAbsolutePath(), PlayerData.class))
                .filter(Objects::nonNull)
                .forEach(playerData -> {
                    PlayerData newPlayerData = PlayerData.checkMissingElements(playerData);
                    Logger.info(String.format("Does %s need updating? %s", playerData.getId(), newPlayerData != null ? "Yes" : "No"));
                    if (newPlayerData != null && newPlayerData.getUuid() != null) {
                        File file = new File(getSaveFileDir(instance, newPlayerData.getUuid(), FOLDER));
                        writeToFile(newPlayerData, file);
                    }
                });
    }
}
