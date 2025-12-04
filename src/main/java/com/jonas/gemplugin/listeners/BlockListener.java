package com.jonas.gemplugin.listeners;

import com.jonas.gemplugin.GemPlugin;
import com.jonas.gemplugin.gems.Gem;
import com.jonas.gemplugin.gems.IceGem;
import com.jonas.gemplugin.utils.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Handles block-related events for gems
 */
public class BlockListener implements Listener {
    
    private final GemPlugin plugin;
    
    public BlockListener(GemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        
        // Check if the block is part of an ice cage
        Gem gem = plugin.getGemManager().getGem("ice");
        if (gem instanceof IceGem) {
            IceGem iceGem = (IceGem) gem;
            if (iceGem.isIceCageBlock(event.getBlock().getLocation())) {
                event.setCancelled(true);
                MessageUtils.sendError(event.getPlayer(), "You cannot break ice cage blocks!");
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        
        // Prevent placing gem items as blocks
        if (plugin.getGemManager().isGem(event.getItemInHand())) {
            event.setCancelled(true);
            MessageUtils.sendError(event.getPlayer(), "You cannot place gem items!");
        }
    }
}
