package com.jonas.gemplugin.listeners;

import com.jonas.gemplugin.GemPlugin;
import com.jonas.gemplugin.gems.Gem;
import com.jonas.gemplugin.gems.IceGem;
import com.jonas.gemplugin.utils.MessageUtils;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

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
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockDamage(BlockDamageEvent event) {
        if (event.isCancelled()) return;
        
        // Check if the block is part of an ice cage
        Gem gem = plugin.getGemManager().getGem("ice");
        if (gem instanceof IceGem) {
            IceGem iceGem = (IceGem) gem;
            if (iceGem.isIceCageBlock(event.getBlock().getLocation())) {
                event.setCancelled(true);
                MessageUtils.sendError(event.getPlayer(), "You cannot damage ice cage blocks!");
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.isCancelled()) return;
        
        // Check if any blocks in the explosion are ice cage blocks
        Gem gem = plugin.getGemManager().getGem("ice");
        if (gem instanceof IceGem) {
            IceGem iceGem = (IceGem) gem;
            event.blockList().removeIf(block -> iceGem.isIceCageBlock(block.getLocation()));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (event.isCancelled()) return;
        
        // Check if any blocks being pushed are ice cage blocks
        Gem gem = plugin.getGemManager().getGem("ice");
        if (gem instanceof IceGem) {
            IceGem iceGem = (IceGem) gem;
            for (Block block : event.getBlocks()) {
                if (iceGem.isIceCageBlock(block.getLocation())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (event.isCancelled()) return;
        
        // Check if any blocks being pulled are ice cage blocks
        Gem gem = plugin.getGemManager().getGem("ice");
        if (gem instanceof IceGem) {
            IceGem iceGem = (IceGem) gem;
            for (Block block : event.getBlocks()) {
                if (iceGem.isIceCageBlock(block.getLocation())) {
                    event.setCancelled(true);
                    return;
                }
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
