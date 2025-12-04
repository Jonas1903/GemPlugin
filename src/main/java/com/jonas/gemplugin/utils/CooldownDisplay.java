package com.jonas.gemplugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

/**
 * Utility class for displaying cooldowns to players
 */
public class CooldownDisplay {
    
    /**
     * Update the XP bar to show cooldown progress
     */
    public static void updateXPBar(Player player, int secondsRemaining, int maxSeconds) {
        if (maxSeconds <= 0) {
            player.setLevel(0);
            player.setExp(0);
            return;
        }
        
        float progress = (float) secondsRemaining / maxSeconds;
        player.setExp(Math.max(0, Math.min(1, progress)));
        player.setLevel(secondsRemaining);
    }
    
    /**
     * Clear the XP bar cooldown display
     */
    public static void clearXPBar(Player player) {
        player.setLevel(0);
        player.setExp(0);
    }
    
    /**
     * Update the scoreboard timer on the right side
     */
    public static void updateScoreboard(Player player, String gemName, int secondsRemaining) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;
        
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
            scoreboard = manager.getNewScoreboard();
            player.setScoreboard(scoreboard);
        }
        
        Objective objective = scoreboard.getObjective("gemCooldown");
        if (objective == null) {
            objective = scoreboard.registerNewObjective(
                "gemCooldown", 
                Criteria.DUMMY, 
                net.kyori.adventure.text.Component.text("§6Gem Cooldown")
            );
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        
        // Clear old scores
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }
        
        // Add new score
        String displayText = "§e" + gemName + ": §c" + secondsRemaining + "s";
        Score score = objective.getScore(displayText);
        score.setScore(secondsRemaining);
    }
    
    /**
     * Clear the scoreboard timer
     */
    public static void clearScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("gemCooldown");
        if (objective != null) {
            objective.unregister();
        }
    }
}
