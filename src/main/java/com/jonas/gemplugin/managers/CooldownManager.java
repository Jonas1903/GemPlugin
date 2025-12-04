package com.jonas.gemplugin.managers;

import com.jonas.gemplugin.GemPlugin;
import com.jonas.gemplugin.utils.CooldownDisplay;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages cooldowns for gem abilities
 */
public class CooldownManager {
    
    private final GemPlugin plugin;
    private final Map<UUID, Map<String, Long>> cooldowns;
    private final Map<UUID, BukkitTask> displayTasks;
    
    public CooldownManager(GemPlugin plugin) {
        this.plugin = plugin;
        this.cooldowns = new HashMap<>();
        this.displayTasks = new HashMap<>();
    }
    
    /**
     * Set a cooldown for a player
     */
    public void setCooldown(Player player, String abilityKey, int seconds) {
        UUID uuid = player.getUniqueId();
        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>());
        cooldowns.get(uuid).put(abilityKey, System.currentTimeMillis() + (seconds * 1000L));
        
        // Start display task
        startDisplayTask(player, abilityKey, seconds);
    }
    
    /**
     * Check if a player has a cooldown
     */
    public boolean hasCooldown(Player player, String abilityKey) {
        UUID uuid = player.getUniqueId();
        if (!cooldowns.containsKey(uuid)) return false;
        
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (!playerCooldowns.containsKey(abilityKey)) return false;
        
        long expiry = playerCooldowns.get(abilityKey);
        if (System.currentTimeMillis() >= expiry) {
            playerCooldowns.remove(abilityKey);
            return false;
        }
        
        return true;
    }
    
    /**
     * Get remaining cooldown time in seconds
     */
    public int getRemainingCooldown(Player player, String abilityKey) {
        UUID uuid = player.getUniqueId();
        if (!cooldowns.containsKey(uuid)) return 0;
        
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (!playerCooldowns.containsKey(abilityKey)) return 0;
        
        long expiry = playerCooldowns.get(abilityKey);
        long remaining = expiry - System.currentTimeMillis();
        
        if (remaining <= 0) {
            playerCooldowns.remove(abilityKey);
            return 0;
        }
        
        return (int) Math.ceil(remaining / 1000.0);
    }
    
    /**
     * Clear a specific cooldown
     */
    public void clearCooldown(Player player, String abilityKey) {
        UUID uuid = player.getUniqueId();
        if (cooldowns.containsKey(uuid)) {
            cooldowns.get(uuid).remove(abilityKey);
        }
        stopDisplayTask(player);
    }
    
    /**
     * Clear all cooldowns for a player
     */
    public void clearAllCooldowns(Player player) {
        UUID uuid = player.getUniqueId();
        cooldowns.remove(uuid);
        stopDisplayTask(player);
    }
    
    /**
     * Start the cooldown display task
     */
    private void startDisplayTask(Player player, String abilityKey, int maxSeconds) {
        stopDisplayTask(player);
        
        UUID uuid = player.getUniqueId();
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline()) {
                stopDisplayTask(player);
                return;
            }
            
            int remaining = getRemainingCooldown(player, abilityKey);
            if (remaining <= 0) {
                CooldownDisplay.clearXPBar(player);
                stopDisplayTask(player);
                return;
            }
            
            CooldownDisplay.updateXPBar(player, remaining, maxSeconds);
        }, 0L, 20L); // Run every second (20 ticks)
        
        displayTasks.put(uuid, task);
    }
    
    /**
     * Stop the cooldown display task
     */
    private void stopDisplayTask(Player player) {
        UUID uuid = player.getUniqueId();
        BukkitTask task = displayTasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
    }
    
    /**
     * Extract gem name from ability key
     */
    private String getGemName(String abilityKey) {
        String[] parts = abilityKey.split("_");
        if (parts.length > 0) {
            return parts[0].substring(0, 1).toUpperCase() + parts[0].substring(1);
        }
        return "Gem";
    }
    
    /**
     * Cleanup on plugin disable
     */
    public void cleanup() {
        for (BukkitTask task : displayTasks.values()) {
            task.cancel();
        }
        displayTasks.clear();
        cooldowns.clear();
    }
}
