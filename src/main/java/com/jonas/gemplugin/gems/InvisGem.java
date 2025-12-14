package com.jonas.gemplugin.gems;

import com.jonas.gemplugin.GemPlugin;
import com.jonas.gemplugin.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Invis Gem implementation
 * Passive: Invisibility, Speed II
 * Primary: Full invisibility (hides armor and items) for 15 seconds
 */
public class InvisGem extends Gem {
    
    private final Set<UUID> fullInvisActive = new HashSet<>();
    
    public InvisGem(GemPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "invis";
    }
    
    @Override
    public Component getDisplayName() {
        return Component.text("Invis Gem")
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true);
    }
    
    @Override
    public List<Component> getLore() {
        return Arrays.asList(
                Component.text(""),
                Component.text("Passive Abilities:").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                Component.text("• Permanent Invisibility").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• Permanent Speed II").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text(""),
                Component.text("Primary Ability (Press F):").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                Component.text("• Full invisibility mode").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• Hides armor and items").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• Duration: 15 seconds").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• Cooldown: 45 seconds").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
        );
    }
    
    @Override
    public Material getMaterial() {
        return Material.PHANTOM_MEMBRANE;
    }
    
    @Override
    public void applyPassiveEffects(Player player) {
        applyGemEffect(player, new PotionEffect(PotionEffectType.INVISIBILITY, 
                PotionEffect.INFINITE_DURATION, 0, false, false, true));
        applyGemEffect(player, new PotionEffect(PotionEffectType.SPEED, 
                PotionEffect.INFINITE_DURATION, 1, false, false, true));
    }
    
    @Override
    public void removePassiveEffects(Player player) {
        removeGemEffect(player, PotionEffectType.INVISIBILITY);
        removeGemEffect(player, PotionEffectType.SPEED);
        fullInvisActive.remove(player.getUniqueId());
        
        // Make sure player is visible again
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.showPlayer(plugin, player);
        }
    }
    
    @Override
    public void activatePrimary(Player player) {
        int duration = plugin.getConfigManager().getDuration("invis", "primary");
        int cooldown = plugin.getConfigManager().getCooldown("invis", "primary");
        
        if (plugin.getCooldownManager().hasCooldown(player, "invis_primary")) {
            int remaining = plugin.getCooldownManager().getRemainingCooldown(player, "invis_primary");
            MessageUtils.sendError(player, "Ability on cooldown! " + remaining + "s remaining");
            return;
        }
        
        fullInvisActive.add(player.getUniqueId());
        
        // Hide player from all other players (including armor and items)
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.getUniqueId().equals(player.getUniqueId())) {
                online.hidePlayer(plugin, player);
            }
        }
        
        MessageUtils.sendSuccess(player, "Full Invisibility activated!");
        
        // Set cooldown
        plugin.getCooldownManager().setCooldown(player, "invis_primary", cooldown);
        
        // Schedule deactivation
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            fullInvisActive.remove(player.getUniqueId());
            if (player.isOnline()) {
                // Make player visible again (but still has invisibility potion effect)
                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.showPlayer(plugin, player);
                }
                MessageUtils.sendInfo(player, "Full Invisibility ended");
            }
        }, duration * 20L);
    }
    
    /**
     * Check if full invisibility is active
     */
    public boolean isFullInvisActive(Player player) {
        return fullInvisActive.contains(player.getUniqueId());
    }
    
    @Override
    public void cleanup(Player player) {
        super.cleanup(player);
        fullInvisActive.remove(player.getUniqueId());
        
        // Make sure player is visible again
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.showPlayer(plugin, player);
        }
    }
}
