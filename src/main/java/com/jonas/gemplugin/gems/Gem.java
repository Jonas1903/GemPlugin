package com.jonas.gemplugin.gems;

import com.jonas.gemplugin.GemPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract base class for all gems
 */
public abstract class Gem {
    
    protected final GemPlugin plugin;
    
    /**
     * Tracks expiry times for gem-applied effects to prevent removing unrelated effects
     * Key: UUID of player
     * Value: Map of PotionEffectType to expiry time in milliseconds
     */
    protected static final Map<UUID, Map<PotionEffectType, Long>> gemAppliedEffectExpiry = new ConcurrentHashMap<>();
    
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
        cleanupGemEffects(player);
    }
    
    /**
     * Applies a gem effect to the player without overriding stronger existing effects.
     * Tracks the effect expiry time for proper cleanup.
     * 
     * @param player The player to apply the effect to
     * @param effect The potion effect to apply
     * @return true if the effect was applied, false otherwise
     */
    protected boolean applyGemEffect(Player player, PotionEffect effect) {
        // Attempt to add the effect without overriding stronger effects
        boolean applied = player.addPotionEffect(effect, false);
        
        if (applied) {
            UUID playerId = player.getUniqueId();
            PotionEffectType effectType = effect.getType();
            
            // Calculate expiry time in milliseconds
            long expiryMillis;
            if (effect.getDuration() == PotionEffect.INFINITE_DURATION) {
                expiryMillis = Long.MAX_VALUE;
            } else {
                expiryMillis = System.currentTimeMillis() + (effect.getDuration() * 50L);
            }
            
            // Store the expiry time
            gemAppliedEffectExpiry.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>())
                    .put(effectType, expiryMillis);
            
            // Set PDC marker for gem-applied effects
            PersistentDataContainer pdc = player.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(plugin, "gem_effect_" + effectType.getKey().getKey());
            pdc.set(key, PersistentDataType.LONG, expiryMillis);
        }
        
        return applied;
    }
    
    /**
     * Removes a gem-applied effect from the player only if it was applied by a gem
     * and has not been replaced by a non-gem effect.
     * 
     * @param player The player to remove the effect from
     * @param effectType The type of potion effect to remove
     */
    protected void removeGemEffect(Player player, PotionEffectType effectType) {
        UUID playerId = player.getUniqueId();
        Map<PotionEffectType, Long> playerEffects = gemAppliedEffectExpiry.get(playerId);
        
        if (playerEffects != null && playerEffects.containsKey(effectType)) {
            long gemExpiryTime = playerEffects.get(effectType);
            long currentTime = System.currentTimeMillis();
            
            // Check if the gem effect is still active (not expired)
            PotionEffect currentEffect = player.getPotionEffect(effectType);
            if (currentEffect != null) {
                long currentEffectExpiry;
                if (currentEffect.getDuration() == PotionEffect.INFINITE_DURATION) {
                    currentEffectExpiry = Long.MAX_VALUE;
                } else {
                    currentEffectExpiry = currentTime + (currentEffect.getDuration() * 50L);
                }
                
                // Only remove if the current effect matches our gem effect expiry (within a tolerance)
                // This prevents removing effects that were applied by other sources
                if (Math.abs(currentEffectExpiry - gemExpiryTime) < 1000) {
                    player.removePotionEffect(effectType);
                }
            } else {
                // Effect already gone, just clean up tracking
            }
            
            // Clean up tracking
            playerEffects.remove(effectType);
            if (playerEffects.isEmpty()) {
                gemAppliedEffectExpiry.remove(playerId);
            }
            
            // Remove PDC marker
            PersistentDataContainer pdc = player.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(plugin, "gem_effect_" + effectType.getKey().getKey());
            pdc.remove(key);
        }
    }
    
    /**
     * Cleans up all gem-applied effects for a player.
     * This should be called when a player logs out or when all gem effects need to be removed.
     * 
     * @param player The player to cleanup effects for
     */
    protected void cleanupGemEffects(Player player) {
        UUID playerId = player.getUniqueId();
        Map<PotionEffectType, Long> playerEffects = gemAppliedEffectExpiry.remove(playerId);
        
        if (playerEffects != null) {
            for (PotionEffectType effectType : playerEffects.keySet()) {
                // Remove PDC marker
                PersistentDataContainer pdc = player.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(plugin, "gem_effect_" + effectType.getKey().getKey());
                pdc.remove(key);
            }
        }
    }
}
