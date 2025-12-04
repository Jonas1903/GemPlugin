package com.jonas.gemplugin.commands;

import com.jonas.gemplugin.GemPlugin;
import com.jonas.gemplugin.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the /trust command
 */
public class TrustCommand implements CommandExecutor {
    
    private final GemPlugin plugin;
    
    public TrustCommand(GemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length != 1) {
            MessageUtils.sendError(player, "Usage: /trust <player>");
            return true;
        }
        
        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        
        if (target == null) {
            MessageUtils.sendError(player, "Player '" + targetName + "' not found!");
            return true;
        }
        
        if (target.getUniqueId().equals(player.getUniqueId())) {
            MessageUtils.sendError(player, "You cannot trust yourself!");
            return true;
        }
        
        plugin.getTrustManager().addTrust(player, target);
        MessageUtils.sendSuccess(player, "You now trust " + target.getName());
        
        return true;
    }
}
