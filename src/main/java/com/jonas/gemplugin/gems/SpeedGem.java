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
 * Speed Gem implementation
 * Passive: Speed I, Haste III
 * Primary: Haste V for 15 seconds
 */
public class SpeedGem extends Gem {
    
    private final Set<UUID> hasteBoostActive = new HashSet<>();
    
    public SpeedGem(GemPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "speed";
    }
    
    @Override
    public Component getDisplayName() {
        return Component.text("Speed Gem")
                .color(NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true);
    }
    
    @Override
    public List<Component> getLore() {
        return Arrays.asList(
                Component.text(""),
                Component.text("Passive Abilities:").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                Component.text("• Permanent Speed I").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• Permanent Haste III").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text(""),
                Component.text("Primary Ability (Press F):").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                Component.text("• Grants Haste V").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• Duration: 15 seconds").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• Cooldown: 30 seconds").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
        );
    }
    
    @Override
    public Material getMaterial() {
        return Material.SUGAR;
    }
    
    @Override
    public void applyPassiveEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 
                PotionEffect.INFINITE_DURATION, 0, false, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 
                PotionEffect.INFINITE_DURATION, 2, false, false, true));
    }
    
    @Override
    public void removePassiveEffects(Player player) {
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.HASTE);
        hasteBoostActive.remove(player.getUniqueId());
    }
    
    @Override
    public void activatePrimary(Player player) {
        int duration = plugin.getConfigManager().getDuration("speed", "primary");
        int cooldown = plugin.getConfigManager().getCooldown("speed", "primary");
        
        if (plugin.getCooldownManager().hasCooldown(player, "speed_primary")) {
            int remaining = plugin.getCooldownManager().getRemainingCooldown(player, "speed_primary");
            MessageUtils.sendError(player, "Ability on cooldown! " + remaining + "s remaining");
            return;
        }
        
        hasteBoostActive.add(player.getUniqueId());
        
        // Apply Haste V (which overrides the passive Haste III)
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 
                duration * 20, 4, false, false, true));
        
        MessageUtils.sendSuccess(player, "Haste Boost activated!");
        
        // Set cooldown
        plugin.getCooldownManager().setCooldown(player, "speed_primary", cooldown);
        
        // Schedule deactivation
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            hasteBoostActive.remove(player.getUniqueId());
            if (player.isOnline()) {
                // Reapply passive effects
                applyPassiveEffects(player);
                MessageUtils.sendInfo(player, "Haste Boost ended");
            }
        }, duration * 20L);
    }
    
    @Override
    public void cleanup(Player player) {
        super.cleanup(player);
        hasteBoostActive.remove(player.getUniqueId());
    }
}
