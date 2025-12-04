package com.jonas.gemplugin.listeners;

import com.jonas.gemplugin.GemPlugin;
import com.jonas.gemplugin.gems.Gem;
import com.jonas.gemplugin.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Handles inventory events to enforce one gem per player and apply passive effects
 */
public class InventoryListener implements Listener {
    
    private final GemPlugin plugin;
    
    public InventoryListener(GemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        
        // Delay check to next tick to ensure inventory is updated
        Bukkit.getScheduler().runTask(plugin, () -> {
            checkAndEnforceOneGem(player);
            updatePassiveEffects(player);
        });
    }
    
    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        
        // Delay check to next tick
        Bukkit.getScheduler().runTask(plugin, () -> {
            updatePassiveEffects(player);
        });
    }
    
    /**
     * Check if player has more than one gem and remove extras
     */
    private void checkAndEnforceOneGem(Player player) {
        PlayerInventory inv = player.getInventory();
        ItemStack oldestGem = null;
        int oldestGemSlot = -1;
        long oldestTimestamp = Long.MAX_VALUE;
        
        // First pass: find the oldest gem
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && plugin.getGemManager().isGem(item)) {
                long timestamp = plugin.getGemManager().getGemTimestamp(item);
                if (timestamp < oldestTimestamp) {
                    oldestGem = item;
                    oldestGemSlot = i;
                    oldestTimestamp = timestamp;
                }
            }
        }
        
        // Second pass: remove all gems except the oldest
        if (oldestGem != null) {
            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack item = inv.getItem(i);
                if (item != null && plugin.getGemManager().isGem(item) && i != oldestGemSlot) {
                    inv.setItem(i, null);
                    MessageUtils.sendError(player, "You can only have one gem at a time! The newest gem was removed.");
                }
            }
        }
    }
    
    /**
     * Update passive effects based on offhand gem
     */
    private void updatePassiveEffects(Player player) {
        ItemStack offhand = player.getInventory().getItemInOffHand();
        Gem currentGem = plugin.getGemManager().getActiveGem(player);
        
        // Get all gems and remove their effects first
        for (Gem gem : plugin.getGemManager().getAllGems().values()) {
            gem.removePassiveEffects(player);
        }
        
        // Apply effects if gem is in offhand and enabled
        if (currentGem != null && plugin.getConfigManager().isGemEnabled(currentGem.getName())) {
            currentGem.applyPassiveEffects(player);
        }
    }
}
