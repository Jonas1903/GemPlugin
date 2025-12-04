package com.jonas.gemplugin.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

/**
 * Utility class for sending formatted messages to players
 */
public class MessageUtils {
    
    private static final String PREFIX = "§6[Gems] §r";
    
    /**
     * Send a success message to a player
     */
    public static void sendSuccess(Player player, String message) {
        player.sendMessage(Component.text(PREFIX + "§a" + message));
    }
    
    /**
     * Send an error message to a player
     */
    public static void sendError(Player player, String message) {
        player.sendMessage(Component.text(PREFIX + "§c" + message));
    }
    
    /**
     * Send an info message to a player
     */
    public static void sendInfo(Player player, String message) {
        player.sendMessage(Component.text(PREFIX + "§7" + message));
    }
    
    /**
     * Send a warning message to a player
     */
    public static void sendWarning(Player player, String message) {
        player.sendMessage(Component.text(PREFIX + "§e" + message));
    }
}
