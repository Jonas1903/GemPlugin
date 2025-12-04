package com.jonas.gemplugin.managers;

import com.jonas.gemplugin.GemPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Manages the trust system between players
 */
public class TrustManager {
    
    private final GemPlugin plugin;
    private final File trustFile;
    private FileConfiguration trustConfig;
    private final Map<UUID, Set<UUID>> trustMap;
    
    public TrustManager(GemPlugin plugin) {
        this.plugin = plugin;
        this.trustFile = new File(plugin.getDataFolder(), "trusts.yml");
        this.trustMap = new HashMap<>();
        loadTrusts();
    }
    
    /**
     * Load trust data from file
     */
    public void loadTrusts() {
        if (!trustFile.exists()) {
            try {
                trustFile.getParentFile().mkdirs();
                trustFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create trusts.yml: " + e.getMessage());
            }
        }
        
        trustConfig = YamlConfiguration.loadConfiguration(trustFile);
        trustMap.clear();
        
        for (String key : trustConfig.getKeys(false)) {
            try {
                UUID playerUUID = UUID.fromString(key);
                List<String> trustedList = trustConfig.getStringList(key);
                Set<UUID> trusted = new HashSet<>();
                
                for (String trustedStr : trustedList) {
                    try {
                        trusted.add(UUID.fromString(trustedStr));
                    } catch (IllegalArgumentException e) {
                        // Skip invalid UUIDs
                    }
                }
                
                trustMap.put(playerUUID, trusted);
            } catch (IllegalArgumentException e) {
                // Skip invalid UUIDs
            }
        }
    }
    
    /**
     * Save trust data to file
     */
    public void saveTrusts() {
        trustConfig = new YamlConfiguration();
        
        for (Map.Entry<UUID, Set<UUID>> entry : trustMap.entrySet()) {
            List<String> trustedList = new ArrayList<>();
            for (UUID trusted : entry.getValue()) {
                trustedList.add(trusted.toString());
            }
            trustConfig.set(entry.getKey().toString(), trustedList);
        }
        
        try {
            trustConfig.save(trustFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save trusts.yml: " + e.getMessage());
        }
    }
    
    /**
     * Add a trusted player
     */
    public void addTrust(Player player, Player trusted) {
        UUID playerUUID = player.getUniqueId();
        UUID trustedUUID = trusted.getUniqueId();
        
        trustMap.computeIfAbsent(playerUUID, k -> new HashSet<>());
        trustMap.get(playerUUID).add(trustedUUID);
        
        saveTrusts();
    }
    
    /**
     * Remove a trusted player
     */
    public void removeTrust(Player player, Player trusted) {
        UUID playerUUID = player.getUniqueId();
        UUID trustedUUID = trusted.getUniqueId();
        
        if (trustMap.containsKey(playerUUID)) {
            trustMap.get(playerUUID).remove(trustedUUID);
            saveTrusts();
        }
    }
    
    /**
     * Check if a player trusts another player
     */
    public boolean isTrusted(Player player, Player other) {
        UUID playerUUID = player.getUniqueId();
        UUID otherUUID = other.getUniqueId();
        
        if (!trustMap.containsKey(playerUUID)) return false;
        return trustMap.get(playerUUID).contains(otherUUID);
    }
    
    /**
     * Get all trusted players for a player
     */
    public Set<UUID> getTrustedPlayers(Player player) {
        UUID playerUUID = player.getUniqueId();
        return trustMap.getOrDefault(playerUUID, new HashSet<>());
    }
}
