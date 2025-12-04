package com.jonas.gemplugin;

import com.jonas.gemplugin.commands.GemCommand;
import com.jonas.gemplugin.commands.TrustCommand;
import com.jonas.gemplugin.commands.UntrustCommand;
import com.jonas.gemplugin.listeners.GemListener;
import com.jonas.gemplugin.listeners.InventoryListener;
import com.jonas.gemplugin.listeners.PlayerListener;
import com.jonas.gemplugin.managers.ConfigManager;
import com.jonas.gemplugin.managers.CooldownManager;
import com.jonas.gemplugin.managers.GemManager;
import com.jonas.gemplugin.managers.TrustManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for GemPlugin
 */
public class GemPlugin extends JavaPlugin {
    
    private ConfigManager configManager;
    private CooldownManager cooldownManager;
    private TrustManager trustManager;
    private GemManager gemManager;
    
    @Override
    public void onEnable() {
        // Initialize managers
        configManager = new ConfigManager(this);
        cooldownManager = new CooldownManager(this);
        trustManager = new TrustManager(this);
        gemManager = new GemManager(this);
        
        // Register commands
        registerCommands();
        
        // Register listeners
        registerListeners();
        
        getLogger().info("GemPlugin has been enabled!");
    }
    
    @Override
    public void onDisable() {
        // Cleanup
        if (cooldownManager != null) {
            cooldownManager.cleanup();
        }
        
        if (trustManager != null) {
            trustManager.saveTrusts();
        }
        
        getLogger().info("GemPlugin has been disabled!");
    }
    
    private void registerCommands() {
        GemCommand gemCommand = new GemCommand(this);
        getCommand("gem").setExecutor(gemCommand);
        getCommand("gem").setTabCompleter(gemCommand);
        
        getCommand("trust").setExecutor(new TrustCommand(this));
        getCommand("untrust").setExecutor(new UntrustCommand(this));
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new GemListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
    }
    
    // Getter methods for managers
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
    
    public TrustManager getTrustManager() {
        return trustManager;
    }
    
    public GemManager getGemManager() {
        return gemManager;
    }
}
