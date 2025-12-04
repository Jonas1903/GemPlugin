package com.jonas.gemplugin.listeners;

import com.jonas.gemplugin.GemPlugin;
import com.jonas.gemplugin.gems.Gem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player events for passive gem abilities
 */
public class PlayerListener implements Listener {
    
    private final GemPlugin plugin;
    
    public PlayerListener(GemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Gem gem = plugin.getGemManager().getActiveGem(player);
        
        if (gem != null && plugin.getConfigManager().isGemEnabled(gem.getName())) {
            gem.onPlayerMove(player, event);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Gem gem = plugin.getGemManager().getActiveGem(attacker);
            
            if (gem != null && plugin.getConfigManager().isGemEnabled(gem.getName())) {
                // Check trust system
                if (event.getEntity() instanceof Player) {
                    Player victim = (Player) event.getEntity();
                    if (plugin.getTrustManager().isTrusted(attacker, victim)) {
                        return; // Don't apply gem effects to trusted players
                    }
                }
                
                gem.onPlayerAttack(attacker, event);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDamaged(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();
            Gem gem = plugin.getGemManager().getActiveGem(victim);
            
            if (gem != null && plugin.getConfigManager().isGemEnabled(gem.getName())) {
                // Check trust system
                if (event.getDamager() instanceof Player) {
                    Player attacker = (Player) event.getDamager();
                    if (plugin.getTrustManager().isTrusted(victim, attacker)) {
                        return; // Don't apply gem effects from trusted players
                    }
                }
                
                gem.onPlayerDamaged(victim, event);
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Clean up any active gem effects
        Gem gem = plugin.getGemManager().getActiveGem(player);
        if (gem != null) {
            gem.cleanup(player);
        }
        
        // Clear cooldowns
        plugin.getCooldownManager().clearAllCooldowns(player);
        
        // Clear gem timestamps
        plugin.getGemManager().clearPlayerGemTimestamps(player);
    }
}
