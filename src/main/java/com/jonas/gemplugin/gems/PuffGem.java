package com.jonas.gemplugin.gems;

import com.jonas.gemplugin.GemPlugin;
import com.jonas.gemplugin.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Puff Gem implementation
 * Passive: Double jump every 5s, 5% chance to negate damage
 * Primary: Dash forward 10 blocks
 */
public class PuffGem extends Gem {
    
    private final Set<UUID> hasDoubleJump = new HashSet<>();
    private final Map<UUID, Long> lastJumpTime = new HashMap<>();
    private final Random random = new Random();
    
    public PuffGem(GemPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "puff";
    }
    
    @Override
    public Component getDisplayName() {
        return Component.text("Puff Gem")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true);
    }
    
    @Override
    public List<Component> getLore() {
        return Arrays.asList(
                Component.text(""),
                Component.text("Passive Abilities:").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                Component.text("• Double jump every 5 seconds").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• 5% chance to negate damage").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text(""),
                Component.text("Primary Ability (Press F):").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
                Component.text("• Dash forward 10 blocks").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("• Cooldown: 20 seconds").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
        );
    }
    
    @Override
    public Material getMaterial() {
        return Material.FEATHER;
    }
    
    @Override
    public void applyPassiveEffects(Player player) {
        hasDoubleJump.add(player.getUniqueId());
        lastJumpTime.put(player.getUniqueId(), 0L);
        // Allow flight for double jump (but not flying)
        if (player.getGameMode() != org.bukkit.GameMode.CREATIVE && 
            player.getGameMode() != org.bukkit.GameMode.SPECTATOR) {
            player.setAllowFlight(true);
        }
    }
    
    @Override
    public void removePassiveEffects(Player player) {
        hasDoubleJump.remove(player.getUniqueId());
        lastJumpTime.remove(player.getUniqueId());
        // Remove flight ability if not in creative/spectator
        if (player.getGameMode() != org.bukkit.GameMode.CREATIVE && 
            player.getGameMode() != org.bukkit.GameMode.SPECTATOR) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }
    
    @Override
    public void activatePrimary(Player player) {
        int cooldown = plugin.getConfigManager().getCooldown("puff", "primary");
        
        if (plugin.getCooldownManager().hasCooldown(player, "puff_primary")) {
            int remaining = plugin.getCooldownManager().getRemainingCooldown(player, "puff_primary");
            MessageUtils.sendError(player, "Ability on cooldown! " + remaining + "s remaining");
            return;
        }
        
        // Dash forward
        Vector direction = player.getLocation().getDirection().normalize();
        direction.multiply(2.5); // Adjust multiplier for 10 block dash
        direction.setY(0.3); // Add slight upward component
        player.setVelocity(direction);
        
        MessageUtils.sendSuccess(player, "Dash activated!");
        
        // Set cooldown
        plugin.getCooldownManager().setCooldown(player, "puff_primary", cooldown);
    }
    
    @Override
    public void onPlayerDamaged(Player player, EntityDamageByEntityEvent event) {
        // 5% chance to negate damage
        if (random.nextInt(100) < 5) {
            event.setCancelled(true);
            MessageUtils.sendSuccess(player, "Damage negated!");
        }
    }
    
    /**
     * Handle double jump
     */
    public boolean tryDoubleJump(Player player) {
        if (!hasDoubleJump.contains(player.getUniqueId())) {
            return false;
        }
        
        long now = System.currentTimeMillis();
        long lastJump = lastJumpTime.getOrDefault(player.getUniqueId(), 0L);
        int doubleJumpCooldown = plugin.getConfigManager().getCooldown("puff", "double-jump") * 1000;
        
        if (now - lastJump < doubleJumpCooldown) {
            return false;
        }
        
        // Check if player is in air (not on ground)
        if (!player.isOnGround()) {
            Vector velocity = player.getVelocity();
            velocity.setY(0.6); // Double jump boost
            player.setVelocity(velocity);
            lastJumpTime.put(player.getUniqueId(), now);
            return true;
        }
        
        return false;
    }
    
    @Override
    public void cleanup(Player player) {
        super.cleanup(player);
        hasDoubleJump.remove(player.getUniqueId());
        lastJumpTime.remove(player.getUniqueId());
    }
}
