package com.jonas.gemplugin.managers;

import com.jonas.gemplugin.GemPlugin;
import com.jonas.gemplugin.gems.*;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages gem creation and identification
 */
public class GemManager {
    
    private final GemPlugin plugin;
    private final NamespacedKey gemKey;
    private final Map<String, Gem> gems;
    
    public GemManager(GemPlugin plugin) {
        this.plugin = plugin;
        this.gemKey = new NamespacedKey(plugin, "gem_type");
        this.gems = new HashMap<>();
        
        registerGems();
    }
    
    /**
     * Register all gem types
     */
    private void registerGems() {
        gems.put("strength", new StrengthGem(plugin));
        gems.put("fire", new FireGem(plugin));
        gems.put("speed", new SpeedGem(plugin));
        gems.put("puff", new PuffGem(plugin));
        gems.put("ice", new IceGem(plugin));
        gems.put("invis", new InvisGem(plugin));
    }
    
    /**
     * Create a gem item
     */
    public ItemStack createGem(String gemType) {
        Gem gem = gems.get(gemType.toLowerCase());
        if (gem == null) return null;
        
        ItemStack item = new ItemStack(gem.getMaterial());
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.displayName(gem.getDisplayName());
            meta.lore(gem.getLore());
            meta.getPersistentDataContainer().set(gemKey, PersistentDataType.STRING, gemType.toLowerCase());
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * Check if an item is a gem
     */
    public boolean isGem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(gemKey, PersistentDataType.STRING);
    }
    
    /**
     * Get the gem type from an item
     */
    public String getGemType(ItemStack item) {
        if (!isGem(item)) return null;
        
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(gemKey, PersistentDataType.STRING);
    }
    
    /**
     * Get a gem instance by type
     */
    public Gem getGem(String gemType) {
        return gems.get(gemType.toLowerCase());
    }
    
    /**
     * Get the gem a player is currently holding in offhand
     */
    public Gem getActiveGem(Player player) {
        ItemStack offhand = player.getInventory().getItemInOffHand();
        if (!isGem(offhand)) return null;
        
        String gemType = getGemType(offhand);
        return getGem(gemType);
    }
    
    /**
     * Check if a player has a gem in their offhand
     */
    public boolean hasActiveGem(Player player) {
        return getActiveGem(player) != null;
    }
    
    /**
     * Get all registered gem types
     */
    public Map<String, Gem> getAllGems() {
        return new HashMap<>(gems);
    }
    
    /**
     * Get the gem key for persistent data
     */
    public NamespacedKey getGemKey() {
        return gemKey;
    }
}
