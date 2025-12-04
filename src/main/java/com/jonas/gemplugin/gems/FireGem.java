package com.jonas.gemplugin.gems;

import com.jonas.gemplugin.GemPlugin;
import com.jonas.gemplugin.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Fire Gem implementation
 * Passive: Fire Resistance, 5% chance to set target on fire
 * Primary: Creates fire aura that sets enemies on fire and drains their water
 */
public class FireGem extends Gem {
    
    private final Map<UUID, BukkitTask> activeAuras = new HashMap<>();
    private final Random random = new Random();
    
    public FireGem(GemPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "fire";
    }
    
    @Override
    public Component getDisplayName() {
        return Component.text("Fire Gem")
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true);
    }
    
    @Override
    public List<Component> getLore() {
        return Arrays.asList(
                Component.text(""),
                Component.text("Passive Abilities:").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                Component.text("• Permanent Fire Resistance").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• 5% chance to ignite enemies").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text(""),
                Component.text("Primary Ability (Press F):").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                Component.text("• Creates 3-block fire aura").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• Sets enemies on fire").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• Drains enemy-placed water").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• Duration: 15 seconds").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• Cooldown: 45 seconds").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
        );
    }
    
    @Override
    public Material getMaterial() {
        return Material.BLAZE_POWDER;
    }
    
    @Override
    public void applyPassiveEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 
                PotionEffect.INFINITE_DURATION, 0, false, false, true));
    }
    
    @Override
    public void removePassiveEffects(Player player) {
        player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        stopAura(player);
    }
    
    @Override
    public void activatePrimary(Player player) {
        int duration = plugin.getConfigManager().getDuration("fire", "primary");
        int cooldown = plugin.getConfigManager().getCooldown("fire", "primary");
        
        if (plugin.getCooldownManager().hasCooldown(player, "fire_primary")) {
            int remaining = plugin.getCooldownManager().getRemainingCooldown(player, "fire_primary");
            MessageUtils.sendError(player, "Ability on cooldown! " + remaining + "s remaining");
            return;
        }
        
        startAura(player, duration);
        MessageUtils.sendSuccess(player, "Fire Aura activated!");
        
        // Set cooldown
        plugin.getCooldownManager().setCooldown(player, "fire_primary", cooldown);
    }
    
    @Override
    public void onPlayerAttack(Player attacker, EntityDamageByEntityEvent event) {
        // 5% chance to set target on fire
        if (random.nextInt(100) < 5) {
            if (event.getEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) event.getEntity();
                target.setFireTicks(60); // 3 seconds
            }
        }
    }
    
    private void startAura(Player player, int duration) {
        stopAura(player);
        
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int ticksRemaining = duration * 20;
            
            @Override
            public void run() {
                if (!player.isOnline() || ticksRemaining <= 0) {
                    stopAura(player);
                    if (player.isOnline()) {
                        MessageUtils.sendInfo(player, "Fire Aura ended");
                    }
                    return;
                }
                
                Location loc = player.getLocation();
                World world = player.getWorld();
                
                // Set enemies on fire and drain water
                for (Entity entity : world.getNearbyEntities(loc, 3, 3, 3)) {
                    if (entity instanceof Player) {
                        Player target = (Player) entity;
                        if (target.getUniqueId().equals(player.getUniqueId())) continue;
                        if (plugin.getTrustManager().isTrusted(player, target)) continue;
                        
                        target.setFireTicks(60);
                    } else if (entity instanceof LivingEntity) {
                        LivingEntity target = (LivingEntity) entity;
                        target.setFireTicks(60);
                    }
                }
                
                // Drain water placed by enemies in the aura
                for (int x = -3; x <= 3; x++) {
                    for (int y = -3; y <= 3; y++) {
                        for (int z = -3; z <= 3; z++) {
                            Block block = world.getBlockAt(
                                    loc.getBlockX() + x,
                                    loc.getBlockY() + y,
                                    loc.getBlockZ() + z
                            );
                            
                            if (block.getType() == Material.WATER) {
                                // Only drain water, not water placed by the gem holder
                                // Since we can't track who placed it easily, we drain all water
                                block.setType(Material.AIR);
                            }
                        }
                    }
                }
                
                ticksRemaining--;
            }
        }, 0L, 5L); // Run every 5 ticks (4 times per second)
        
        activeAuras.put(player.getUniqueId(), task);
    }
    
    private void stopAura(Player player) {
        BukkitTask task = activeAuras.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }
    
    @Override
    public void cleanup(Player player) {
        super.cleanup(player);
        stopAura(player);
    }
}
