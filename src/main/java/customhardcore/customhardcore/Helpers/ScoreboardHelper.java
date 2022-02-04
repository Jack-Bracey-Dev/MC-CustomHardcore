package customhardcore.customhardcore.Helpers;

import customhardcore.customhardcore.Enums.Settings;
import customhardcore.customhardcore.Levelling.PlayerData;
import customhardcore.customhardcore.Levelling.PlayerSave;
import customhardcore.customhardcore.Objects.PlayerSettings;
import customhardcore.customhardcore.PlayerSettings.PlayerSpecificSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import javax.annotation.Nullable;

public class ScoreboardHelper {

    private static void createBoard(Player player) {

        PlayerSettings playerSettings = PlayerSpecificSettings.getPlayerSettings(player);
        if (!playerSettings.getSettings().get(Settings.TOGGLE_SCOREBOARD)) return;

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective(player.getName() + "-CustomHardcore-Main", "dummy",
                ChatColor.translateAlternateColorCodes('&', "&3&lCustom&bHardcore"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        int scorePos = 0;
        for (Player plyr : Bukkit.getServer().getOnlinePlayers()) {
            PlayerData playerData = PlayerSave.getPlayerData(plyr);
            Score plyrScore = objective.getScore(ChatColor.translateAlternateColorCodes('&',
                    String.format("&6%s &f&l%o&4☠ &d&l%s&4❤&a %o⇪", plyr.getName(),
                    playerData.getLives(), Math.round(Math.ceil(plyr.getHealth())),
                            playerData.getLevel())));
            plyrScore.setScore(scorePos);

            scorePos++;
        }

        PlayerData playerData = PlayerSave.getPlayerData(player);

        Score score = objective.getScore(
                ChatColor.translateAlternateColorCodes('&',
                        "&3&l" + playerData.getLevel() + " " +
                        "&a&l" +
                                PlayerSave.calculateLevelProgress("||||||||||||||||||||||||||||||||||||||||",
                                PlayerSave.getPlayerData(player)) +
                                "&3&l " + (playerData.getLevel()+1)));
        score.setScore(scorePos);

        player.setScoreboard(board);
    }

    public static void createOrUpdatePlayerBoard(@Nullable Player player) {
        if (!ConfigurationHelper.isMaxDeathsEnabled())
            return;

        if (player == null) return;

        Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (objective != null)
            updateBoard(player);
        else
            createBoard(player);
    }

    public static void updateBoard(Player player) {
        removeBoard(player);
        createBoard(player);
    }

    public static void removeBoard(Player player) {
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (objective != null) objective.unregister();
    }

    public static void updatePlayerBoards() {
        if (ConfigurationHelper.isMaxDeathsEnabled())
            Bukkit.getServer().getOnlinePlayers().stream().
                    filter(player -> PlayerSpecificSettings.getPlayerSettings(player)
                            .getSettings().get(Settings.TOGGLE_SCOREBOARD))
                    .forEach(ScoreboardHelper::updateBoard);
    }

}
