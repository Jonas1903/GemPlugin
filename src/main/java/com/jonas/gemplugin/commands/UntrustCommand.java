package com.jonas.gemplugin.commands;

import com.jonas.gemplugin.GemPlugin;
import com.jonas.gemplugin.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the /untrust command
 */
public class UntrustCommand implements CommandExecutor {
    
    private final GemPlugin plugin;
    
    public UntrustCommand(GemPlugin plugin) {
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
            MessageUtils.sendError(player, "Usage: /untrust <player>");
            return true;
        }
        
        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        
        if (target == null) {
            MessageUtils.sendError(player, "Player '" + targetName + "' not found!");
            return true;
        }
        
        plugin.getTrustManager().removeTrust(player, target);
        MessageUtils.sendSuccess(player, "You no longer trust " + target.getName());
        
        return true;
    }
}
