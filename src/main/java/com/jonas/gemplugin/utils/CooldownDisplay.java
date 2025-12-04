package com.jonas.gemplugin.utils;

import org.bukkit.entity.Player;

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
    

}
