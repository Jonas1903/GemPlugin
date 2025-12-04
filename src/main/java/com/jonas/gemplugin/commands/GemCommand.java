package com.jonas.gemplugin.commands;

import com.jonas.gemplugin.GemPlugin;
import com.jonas.gemplugin.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the /gem command
 */
public class GemCommand implements CommandExecutor, TabCompleter {
    
    private final GemPlugin plugin;
    private final List<String> gemTypes = Arrays.asList("strength", "fire", "speed", "puff", "ice", "invis");
    
    public GemCommand(GemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("gemplugin.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "give":
                return handleGive(sender, args);
            case "enable":
                return handleEnable(sender, args);
            case "disable":
                return handleDisable(sender, args);
            case "reload":
                return handleReload(sender);
            default:
                sendHelp(sender);
                return true;
        }
    }
    
    private boolean handleGive(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /gem give <player> <gem_type>");
            return true;
        }
        
        String playerName = args[1];
        String gemType = args[2].toLowerCase();
        
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage("§cPlayer '" + playerName + "' not found!");
            return true;
        }
        
        if (!gemTypes.contains(gemType)) {
            sender.sendMessage("§cInvalid gem type! Valid types: " + String.join(", ", gemTypes));
            return true;
        }
        
        ItemStack gem = plugin.getGemManager().createGem(gemType);
        if (gem == null) {
            sender.sendMessage("§cFailed to create gem!");
            return true;
        }
        
        target.getInventory().addItem(gem);
        // Record timestamp when gem is given to player
        plugin.getGemManager().recordGemInInventory(target, gem);
        sender.sendMessage("§aGave " + gemType + " gem to " + target.getName());
        
        if (sender instanceof Player) {
            MessageUtils.sendSuccess((Player) sender, "Gave " + gemType + " gem to " + target.getName());
        }
        
        if (!sender.equals(target)) {
            MessageUtils.sendSuccess(target, "You received a " + gemType + " gem!");
        }
        
        return true;
    }
    
    private boolean handleEnable(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /gem enable <gem_type>");
            return true;
        }
        
        String gemType = args[1].toLowerCase();
        
        if (!gemTypes.contains(gemType)) {
            sender.sendMessage("§cInvalid gem type! Valid types: " + String.join(", ", gemTypes));
            return true;
        }
        
        plugin.getConfigManager().setGemEnabled(gemType, true);
        sender.sendMessage("§aEnabled " + gemType + " gem");
        
        return true;
    }
    
    private boolean handleDisable(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /gem disable <gem_type>");
            return true;
        }
        
        String gemType = args[1].toLowerCase();
        
        if (!gemTypes.contains(gemType)) {
            sender.sendMessage("§cInvalid gem type! Valid types: " + String.join(", ", gemTypes));
            return true;
        }
        
        plugin.getConfigManager().setGemEnabled(gemType, false);
        sender.sendMessage("§aDisabled " + gemType + " gem");
        
        return true;
    }
    
    private boolean handleReload(CommandSender sender) {
        plugin.getConfigManager().reloadConfig();
        sender.sendMessage("§aConfiguration reloaded!");
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== Gem Plugin Commands ===");
        sender.sendMessage("§e/gem give <player> <gem_type> §7- Give a gem to a player");
        sender.sendMessage("§e/gem enable <gem_type> §7- Enable a specific gem");
        sender.sendMessage("§e/gem disable <gem_type> §7- Disable a specific gem");
        sender.sendMessage("§e/gem reload §7- Reload configuration");
        sender.sendMessage("§7Valid gem types: " + String.join(", ", gemTypes));
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("gemplugin.admin")) {
            return new ArrayList<>();
        }
        
        if (args.length == 1) {
            return Arrays.asList("give", "enable", "disable", "reload")
                    .stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("give")) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            } else if (subCommand.equals("enable") || subCommand.equals("disable")) {
                return gemTypes.stream()
                        .filter(s -> s.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return gemTypes.stream()
                    .filter(s -> s.startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
}
