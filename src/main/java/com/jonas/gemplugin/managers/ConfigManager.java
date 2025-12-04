package com.jonas.gemplugin.managers;

import com.jonas.gemplugin.GemPlugin;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Manages plugin configuration
 */
public class ConfigManager {
    
    private final GemPlugin plugin;
    private FileConfiguration config;
    
    public ConfigManager(GemPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    /**
     * Load the configuration file
     */
    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    /**
     * Reload the configuration
     */
    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    /**
     * Get a cooldown value in seconds
     */
    public int getCooldown(String gemType, String abilityType) {
        return config.getInt("cooldowns." + gemType + "." + abilityType, 0);
    }
    
    /**
     * Get a duration value in seconds
     */
    public int getDuration(String gemType, String abilityType) {
        return config.getInt("durations." + gemType + "." + abilityType, 0);
    }
    
    /**
     * Check if a gem is enabled
     */
    public boolean isGemEnabled(String gemType) {
        return config.getBoolean("enabled-gems." + gemType, true);
    }
    
    /**
     * Set a gem's enabled state
     */
    public void setGemEnabled(String gemType, boolean enabled) {
        config.set("enabled-gems." + gemType, enabled);
        plugin.saveConfig();
    }
    
    /**
     * Get the underlying config
     */
    public FileConfiguration getConfig() {
        return config;
    }
}
