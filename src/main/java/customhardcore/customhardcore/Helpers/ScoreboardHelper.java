package customhardcore.customhardcore.Helpers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import javax.annotation.Nullable;

public class ScoreboardHelper {

    private static void createBoard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective(player.getName() + "-CustomHardcore-Main", "dummy",
                ChatColor.translateAlternateColorCodes('&', "&3&lCustom&bHardcore"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score dummyScore1 = objective.getScore(ChatColor.DARK_BLUE + "-=-=-=-=-=-=-=-");
        dummyScore1.setScore(4);

        int maxDeaths = ConfigurationHelper.getConfig().getInt(ConfigurationHelper.ConfigurationValues.MAX_DEATHS.name());
        int playerDeaths = player.getStatistic(Statistic.DEATHS);

        Score score = objective.getScore(ChatColor.AQUA + "Deaths Remaining: " + (maxDeaths-playerDeaths));
        score.setScore(3);

        Score score2 = objective.getScore(ChatColor.GREEN + "Deaths: " + playerDeaths);
        score2.setScore(2);

        Score score3 = objective.getScore(ChatColor.AQUA + "Max Deaths: " + maxDeaths);
        score3.setScore(1);

        player.setScoreboard(board);
    }

    public static void createOrUpdatePlayerBoard(@Nullable Player player) {
        if (player == null) return;

        Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (objective != null)
            updateBoard(player);
        else
            createBoard(player);
    }

    public static void updateBoard(Player player) {
        Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (objective == null) {
            createBoard(player);
            return;
        }

        removeBoard(player);
        createBoard(player);
    }

    public static void removeBoard(Player player) {
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (objective != null) objective.unregister();
    }

}
