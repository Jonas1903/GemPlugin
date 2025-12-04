package com.jonas.gemplugin.gems;

import com.jonas.gemplugin.GemPlugin;
import com.jonas.gemplugin.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Astra Gem implementation
 * Passive: Cycles invisibility every 5 seconds (5s invis -> 5s visible -> repeat)
 * Primary: Fires a beam that deals 3.5 hearts of damage
 */
public class AstraGem extends Gem {
    
    private final Map<UUID, BukkitTask> passiveTasks = new HashMap<>();
    private final Set<UUID> currentlyInvisible = new HashSet<>();
    
    public AstraGem(GemPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "astra";
    }
    
    @Override
    public Component getDisplayName() {
        return Component.text("Astra Gem")
                .color(NamedTextColor.LIGHT_PURPLE)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true);
    }
    
    @Override
    public List<Component> getLore() {
        return Arrays.asList(
                Component.text(""),
                Component.text("Passive Abilities:").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                Component.text("• Cycles invisibility every 5s").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• 5s invisible -> 5s visible -> repeat").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text(""),
                Component.text("Primary Ability (Press F):").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                Component.text("• Fires a damaging beam").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• Deals 3.5 hearts of damage").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• 5 block range, 1 block wide").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• Cooldown: 30 seconds").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
        );
    }
    
    @Override
    public Material getMaterial() {
        return Material.AMETHYST_SHARD;
    }
    
    @Override
    public void applyPassiveEffects(Player player) {
        // Start the invisibility cycle
        startInvisibilityCycle(player);
    }
    
    @Override
    public void removePassiveEffects(Player player) {
        stopInvisibilityCycle(player);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        currentlyInvisible.remove(player.getUniqueId());
    }
    
    @Override
    public void activatePrimary(Player player) {
        int cooldown = plugin.getConfigManager().getCooldown("astra", "primary");
        
        if (plugin.getCooldownManager().hasCooldown(player, "astra_primary")) {
            int remaining = plugin.getCooldownManager().getRemainingCooldown(player, "astra_primary");
            MessageUtils.sendError(player, "Ability on cooldown! " + remaining + "s remaining");
            return;
        }
        
        // Fire the beam
        fireBeam(player);
        
        MessageUtils.sendSuccess(player, "Beam fired!");
        
        // Set cooldown
        plugin.getCooldownManager().setCooldown(player, "astra_primary", cooldown);
    }
    
    /**
     * Start the invisibility cycle (5s on, 5s off)
     */
    private void startInvisibilityCycle(Player player) {
        stopInvisibilityCycle(player);
        
        int invisDuration = plugin.getConfigManager().getDuration("astra", "passive-invis");
        int cycleDuration = plugin.getConfigManager().getDuration("astra", "passive-cycle");
        
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            boolean isInvisPhase = true; // Start with invisibility
            int ticksUntilSwitch = invisDuration * 20;
            
            @Override
            public void run() {
                if (!player.isOnline()) {
                    stopInvisibilityCycle(player);
                    return;
                }
                
                // Check if still holding the gem
                Gem activeGem = plugin.getGemManager().getActiveGem(player);
                if (!(activeGem instanceof AstraGem)) {
                    stopInvisibilityCycle(player);
                    return;
                }
                
                ticksUntilSwitch--;
                
                if (ticksUntilSwitch <= 0) {
                    // Switch phase
                    isInvisPhase = !isInvisPhase;
                    
                    if (isInvisPhase) {
                        // Apply invisibility
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 
                                invisDuration * 20 + 10, 0, false, false, true));
                        currentlyInvisible.add(player.getUniqueId());
                        ticksUntilSwitch = invisDuration * 20;
                    } else {
                        // Remove invisibility
                        player.removePotionEffect(PotionEffectType.INVISIBILITY);
                        currentlyInvisible.remove(player.getUniqueId());
                        ticksUntilSwitch = cycleDuration * 20;
                    }
                }
            }
        }, 0L, 1L); // Run every tick for precise timing
        
        passiveTasks.put(player.getUniqueId(), task);
        
        // Apply initial invisibility
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 
                invisDuration * 20 + 10, 0, false, false, true));
        currentlyInvisible.add(player.getUniqueId());
    }
    
    /**
     * Stop the invisibility cycle
     */
    private void stopInvisibilityCycle(Player player) {
        BukkitTask task = passiveTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }
    
    /**
     * Fire a beam that deals damage
     */
    private void fireBeam(Player player) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();
        World world = player.getWorld();
        
        double range = 5.0;
        double beamWidth = 1.0;
        
        // Raycast to find entities
        RayTraceResult result = world.rayTrace(
                eyeLocation,
                direction,
                range,
                FluidCollisionMode.NEVER,
                true,
                beamWidth,
                entity -> {
                    if (!(entity instanceof LivingEntity)) return false;
                    if (entity.getUniqueId().equals(player.getUniqueId())) return false;
                    
                    // Check trust system
                    if (entity instanceof Player) {
                        Player target = (Player) entity;
                        if (plugin.getTrustManager().isTrusted(player, target)) {
                            return false;
                        }
                    }
                    
                    return true;
                }
        );
        
        // Draw beam particles
        drawBeamParticles(eyeLocation, direction, range);
        
        // Deal damage if hit
        if (result != null && result.getHitEntity() instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) result.getHitEntity();
            target.damage(7.0); // 3.5 hearts = 7.0 damage points
            
            // Play hit effect
            Location hitLoc = result.getHitPosition().toLocation(world);
            world.spawnParticle(Particle.EXPLOSION, hitLoc, 1, 0, 0, 0, 0);
            world.playSound(hitLoc, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.5f);
        }
    }
    
    /**
     * Draw particle effects for the beam
     */
    private void drawBeamParticles(Location start, Vector direction, double range) {
        World world = start.getWorld();
        
        // Draw particles along the beam path
        for (double d = 0; d <= range; d += 0.3) {
            Location particleLoc = start.clone().add(direction.clone().multiply(d));
            world.spawnParticle(Particle.END_ROD, particleLoc, 1, 0, 0, 0, 0);
            world.spawnParticle(Particle.DRAGON_BREATH, particleLoc, 2, 0.1, 0.1, 0.1, 0.01);
        }
        
        // Play sound
        world.playSound(start, Sound.ENTITY_ENDER_DRAGON_SHOOT, 0.5f, 1.8f);
    }
    
    @Override
    public void cleanup(Player player) {
        super.cleanup(player);
        stopInvisibilityCycle(player);
        currentlyInvisible.remove(player.getUniqueId());
    }
}
