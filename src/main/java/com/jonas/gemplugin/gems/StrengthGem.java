package com.jonas.gemplugin.gems;

import com.jonas.gemplugin.GemPlugin;
import com.jonas.gemplugin.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * Strength Gem implementation
 * Passive: Strength II, 1% chance for double damage
 * Primary: Every hit becomes a critical hit (20s)
 */
public class StrengthGem extends Gem {
    
    private final Set<UUID> critModeActive = new HashSet<>();
    private final Random random = new Random();
    
    public StrengthGem(GemPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "strength";
    }
    
    @Override
    public Component getDisplayName() {
        return Component.text("Strength Gem")
                .color(NamedTextColor.RED)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true);
    }
    
    @Override
    public List<Component> getLore() {
        return Arrays.asList(
                Component.text(""),
                Component.text("Passive Abilities:").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                Component.text("• Permanent Strength II").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• 1% chance to deal double damage").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text(""),
                Component.text("Primary Ability (Press F):").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                Component.text("• Every hit becomes critical").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• Duration: 20 seconds").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• Cooldown: 60 seconds").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
        );
    }
    
    @Override
    public Material getMaterial() {
        return Material.REDSTONE;
    }
    
    @Override
    public void applyPassiveEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 
                PotionEffect.INFINITE_DURATION, 1, false, false, true));
    }
    
    @Override
    public void removePassiveEffects(Player player) {
        player.removePotionEffect(PotionEffectType.STRENGTH);
        critModeActive.remove(player.getUniqueId());
    }
    
    @Override
    public void activatePrimary(Player player) {
        int duration = plugin.getConfigManager().getDuration("strength", "primary");
        int cooldown = plugin.getConfigManager().getCooldown("strength", "primary");
        
        if (plugin.getCooldownManager().hasCooldown(player, "strength_primary")) {
            int remaining = plugin.getCooldownManager().getRemainingCooldown(player, "strength_primary");
            MessageUtils.sendError(player, "Ability on cooldown! " + remaining + "s remaining");
            return;
        }
        
        critModeActive.add(player.getUniqueId());
        MessageUtils.sendSuccess(player, "Critical Mode activated!");
        
        // Set cooldown
        plugin.getCooldownManager().setCooldown(player, "strength_primary", cooldown);
        
        // Schedule deactivation
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            critModeActive.remove(player.getUniqueId());
            if (player.isOnline()) {
                MessageUtils.sendInfo(player, "Critical Mode ended");
            }
        }, duration * 20L);
    }
    
    @Override
    public void onPlayerAttack(Player attacker, EntityDamageByEntityEvent event) {
        double baseDamage = event.getDamage();
        
        // Critical hits when primary is active
        if (critModeActive.contains(attacker.getUniqueId())) {
            // Make it a critical hit by multiplying damage by 1.5
            event.setDamage(baseDamage * 1.5);
            // Show critical hit particles
            if (event.getEntity() instanceof org.bukkit.entity.LivingEntity) {
                attacker.getWorld().spawnParticle(
                    org.bukkit.Particle.CRIT, 
                    event.getEntity().getLocation().clone().add(0, 1, 0), 
                    15, 
                    0.3, 0.3, 0.3, 
                    0.1
                );
            }
        } else if (random.nextInt(100) < 1) {
            // 1% chance for double damage (only if not in crit mode)
            event.setDamage(baseDamage * 2);
        }
    }
    
    @Override
    public void cleanup(Player player) {
        super.cleanup(player);
        critModeActive.remove(player.getUniqueId());
    }
}
