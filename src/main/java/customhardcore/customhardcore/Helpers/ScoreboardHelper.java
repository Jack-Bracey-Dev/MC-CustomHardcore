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

        int scorePos = 1;
        for (Player plyr : Bukkit.getServer().getOnlinePlayers()) {
            Score plyrScore = objective.getScore(ChatColor.translateAlternateColorCodes('&',
                    String.format("&a%s %o&4☠ &a%s&4&4❤&a", plyr.getName(),
                    plyr.getStatistic(Statistic.DEATHS), Math.round(plyr.getHealth()))));
            plyrScore.setScore(scorePos);

            scorePos++;
        }

        Score scoreInfo = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&n&bPlayer Deaths"));
        scoreInfo.setScore(scorePos);

        Score scoreHeader = objective.getScore(ChatColor.DARK_BLUE + "-=-=-=-=-=-=-=-");
        scoreHeader.setScore(scorePos+1);

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

    public static void updatePlayerBoards() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            updateBoard(player);
        }
    }

}
