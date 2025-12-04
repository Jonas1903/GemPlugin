package com.jonas.gemplugin.gems;

import com.jonas.gemplugin.GemPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

/**
 * Abstract base class for all gems
 */
public abstract class Gem {
    
    protected final GemPlugin plugin;
    
    public Gem(GemPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Get the gem's name
     */
    public abstract String getName();
    
    /**
     * Get the gem's display name
     */
    public abstract Component getDisplayName();
    
    /**
     * Get the gem's lore (ability descriptions)
     */
    public abstract List<Component> getLore();
    
    /**
     * Get the material for this gem
     */
    public abstract Material getMaterial();
    
    /**
     * Apply passive effects to a player
     */
    public abstract void applyPassiveEffects(Player player);
    
    /**
     * Remove passive effects from a player
     */
    public abstract void removePassiveEffects(Player player);
    
    /**
     * Activate the gem's primary ability
     */
    public abstract void activatePrimary(Player player);
    
    /**
     * Handle player attack events (for passive abilities)
     */
    public void onPlayerAttack(Player attacker, EntityDamageByEntityEvent event) {
        // Override in subclasses if needed
    }
    
    /**
     * Handle player move events (for passive abilities)
     */
    public void onPlayerMove(Player player, PlayerMoveEvent event) {
        // Override in subclasses if needed
    }
    
    /**
     * Handle player being attacked (for passive abilities)
     */
    public void onPlayerDamaged(Player player, EntityDamageByEntityEvent event) {
        // Override in subclasses if needed
    }
    
    /**
     * Cleanup when gem is removed or player logs out
     */
    public void cleanup(Player player) {
        removePassiveEffects(player);
    }
}
