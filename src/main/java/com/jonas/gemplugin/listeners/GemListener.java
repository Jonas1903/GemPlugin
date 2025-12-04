package com.jonas.gemplugin.listeners;

import com.jonas.gemplugin.GemPlugin;
import com.jonas.gemplugin.gems.Gem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

/**
 * Handles gem ability activation via offhand swap (F key)
 */
public class GemListener implements Listener {
    
    private final GemPlugin plugin;
    
    public GemListener(GemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onOffhandSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        Gem gem = plugin.getGemManager().getActiveGem(player);
        
        if (gem != null) {
            // Cancel the swap to prevent item switching
            event.setCancelled(true);
            
            // Check if gem is enabled
            if (!plugin.getConfigManager().isGemEnabled(gem.getName())) {
                return;
            }
            
            // Activate primary ability
            gem.activatePrimary(player);
        }
    }
}
