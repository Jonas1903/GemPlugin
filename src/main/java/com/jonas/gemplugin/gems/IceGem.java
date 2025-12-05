package com.jonas.gemplugin.gems;

import com.jonas.gemplugin.GemPlugin;
import com.jonas.gemplugin.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Ice Gem implementation
 * Passive: Speed IV for 3 seconds when touching ice
 * Primary: Creates ice cage, enemies get Slowness II
 */
public class IceGem extends Gem {
    
    private final Map<UUID, BukkitTask> speedTasks = new HashMap<>();
    private final Map<UUID, IceCage> activeCages = new HashMap<>();
    private final Set<Location> allCageBlocks = new HashSet<>();
    
    public IceGem(GemPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "ice";
    }
    
    @Override
    public Component getDisplayName() {
        return Component.text("Ice Gem")
                .color(NamedTextColor.BLUE)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true);
    }
    
    @Override
    public List<Component> getLore() {
        return Arrays.asList(
                Component.text(""),
                Component.text("Passive Abilities:").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                Component.text("• Speed IV for 3s when touching ice").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• Timer resets on ice contact").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text(""),
                Component.text("Primary Ability (Press F):").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                Component.text("• Creates indestructible ice cage").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• 4-block radius").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• Enemies get Slowness II").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• Duration: 20 seconds").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• Cooldown: 60 seconds").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
        );
    }
    
    @Override
    public Material getMaterial() {
        return Material.ICE;
    }
    
    @Override
    public void applyPassiveEffects(Player player) {
        // Passive speed is applied in onPlayerMove
    }
    
    @Override
    public void removePassiveEffects(Player player) {
        player.removePotionEffect(PotionEffectType.SPEED);
        cancelSpeedTask(player);
        removeCage(player);
    }
    
    @Override
    public void activatePrimary(Player player) {
        int duration = plugin.getConfigManager().getDuration("ice", "primary");
        int cooldown = plugin.getConfigManager().getCooldown("ice", "primary");
        
        if (plugin.getCooldownManager().hasCooldown(player, "ice_primary")) {
            int remaining = plugin.getCooldownManager().getRemainingCooldown(player, "ice_primary");
            MessageUtils.sendError(player, "Ability on cooldown! " + remaining + "s remaining");
            return;
        }
        
        createIceCage(player, duration);
        MessageUtils.sendSuccess(player, "Ice Cage created!");
        
        // Set cooldown
        plugin.getCooldownManager().setCooldown(player, "ice_primary", cooldown);
    }
    
    @Override
    public void onPlayerMove(Player player, PlayerMoveEvent event) {
        Location loc = player.getLocation();
        Block blockBelow = loc.clone().subtract(0, 1, 0).getBlock();
        Block blockAt = loc.getBlock();
        
        boolean touchingIce = isIceBlock(blockBelow) || isIceBlock(blockAt);
        
        if (touchingIce) {
            // Reset the 3-second speed timer
            applySpeedEffect(player);
        }
    }
    
    /**
     * Apply Speed IV for 3 seconds, resetting the timer if already active
     */
    private void applySpeedEffect(Player player) {
        // Cancel existing task if any
        cancelSpeedTask(player);
        
        // Apply Speed IV
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 
                60, 3, false, false, true)); // 60 ticks = 3 seconds
        
        // Schedule removal after 3 seconds
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.removePotionEffect(PotionEffectType.SPEED);
            }
            speedTasks.remove(player.getUniqueId());
        }, 60L); // 60 ticks = 3 seconds
        
        speedTasks.put(player.getUniqueId(), task);
    }
    
    /**
     * Cancel the speed removal task
     */
    private void cancelSpeedTask(Player player) {
        BukkitTask task = speedTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }
    
    private boolean isIceBlock(Block block) {
        Material type = block.getType();
        return type == Material.ICE || 
               type == Material.PACKED_ICE || 
               type == Material.BLUE_ICE ||
               type == Material.FROSTED_ICE;
    }
    
    private void createIceCage(Player player, int duration) {
        removeCage(player);
        
        Location center = player.getLocation();
        List<Location> cageBlocks = new ArrayList<>();
        int radius = 4;
        
        // Create spherical cage
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x*x + y*y + z*z);
                    if (distance >= radius - 0.5 && distance <= radius + 0.5) {
                        Location blockLoc = center.clone().add(x, y, z);
                        Block block = blockLoc.getBlock();
                        if (block.getType() == Material.AIR) {
                            cageBlocks.add(blockLoc);
                            allCageBlocks.add(blockLoc);
                            block.setType(Material.HONEY_BLOCK);
                        }
                    }
                }
            }
        }
        
        // Apply slowness to enemies
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int ticksRemaining = duration * 20;
            
            @Override
            public void run() {
                if (!player.isOnline() || ticksRemaining <= 0) {
                    removeCage(player);
                    if (player.isOnline()) {
                        MessageUtils.sendInfo(player, "Ice Cage melted");
                    }
                    return;
                }
                
                // Apply slowness to enemies inside
                for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
                    if (entity instanceof Player) {
                        Player target = (Player) entity;
                        if (target.getUniqueId().equals(player.getUniqueId())) continue;
                        if (plugin.getTrustManager().isTrusted(player, target)) continue;
                        
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 
                                40, 1, false, false, true));
                    } else if (entity instanceof LivingEntity) {
                        LivingEntity target = (LivingEntity) entity;
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 
                                40, 1, false, false, true));
                    }
                }
                
                ticksRemaining -= 20;
            }
        }, 0L, 20L);
        
        activeCages.put(player.getUniqueId(), new IceCage(cageBlocks, task));
    }
    
    private void removeCage(Player player) {
        IceCage cage = activeCages.remove(player.getUniqueId());
        if (cage != null) {
            cage.task.cancel();
            for (Location loc : cage.blocks) {
                allCageBlocks.remove(loc);
                Block block = loc.getBlock();
                if (block.getType() == Material.HONEY_BLOCK) {
                    block.setType(Material.AIR);
                }
            }
        }
    }
    
    /**
     * Check if a block is part of an ice cage
     */
    public boolean isIceCageBlock(Location location) {
        return allCageBlocks.contains(location);
    }
    
    @Override
    public void cleanup(Player player) {
        super.cleanup(player);
        cancelSpeedTask(player);
        removeCage(player);
    }
    
    private static class IceCage {
        final List<Location> blocks;
        final BukkitTask task;
        
        IceCage(List<Location> blocks, BukkitTask task) {
            this.blocks = blocks;
            this.task = task;
        }
    }
}
