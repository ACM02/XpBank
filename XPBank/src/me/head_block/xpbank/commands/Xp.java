package me.head_block.xpbank.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.head_block.xpbank.Main;
import me.head_block.xpbank.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class Xp implements CommandExecutor {

	public Xp (Main plugin) {
		plugin.getCommand("xpbank").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			asPlayer((Player) sender, args);
		} else {
			asConsole(sender, args);
		}

		return false;
	}

	private void asPlayer(Player sender, String[] args) {
		switch (args.length) {
		case 0:
			if (!Main.xps.containsKey(sender.getUniqueId().toString())) {
				Main.xps.put(sender.getUniqueId().toString(), 0);
			}
			sender.sendMessage(ChatColor.YELLOW + "You have " + Main.xps.get(sender.getUniqueId().toString()) + " experience points in the bank. (Enough for level " + 
					Utils.getMaxLevel(sender, Main.xps.get(sender.getUniqueId().toString())) + ")");
			break;
		case 1: 
			switch(args[0]) {
			case "help":
				//ToDo
				break;
			default:
				sender.sendMessage(ChatColor.RED + "Improper usage. Try /xpbank help");
				break;
			}
			break;
		case 2:
			switch(args[0]) {
			case "deposit":
				int amount = 1;
				if (!args[1].equals("all")) {
					try {
						amount = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Invalid amount");
						break;
					}
				}
				if (amount < 1) {
					sender.sendMessage(ChatColor.RED + "Invalid amount");
					break;
				}
				int playerTotalXp = Utils.totalXp(sender.getLevel(), Utils.xpPointsInBar(sender.getExp(), sender.getExpToLevel()));
				if (args[1].equals("all")) amount = playerTotalXp;
				if (amount > playerTotalXp) {
					sender.sendMessage(ChatColor.RED + "You only have " + playerTotalXp + " xp");
					break;
				}
				int newTotal = playerTotalXp - amount;
				int newLevel = Utils.level(newTotal);
				float newXp = Utils.xp(newTotal, newLevel);
				if (newXp == 1) {
					newLevel++;
					newXp = 0;
				}
				sender.setLevel(newLevel);
				sender.setExp(newXp);
				if (!Main.xps.containsKey(sender.getUniqueId().toString())) {
					Main.xps.put(sender.getUniqueId().toString(), 0);
				}
				int oldBal = Main.xps.get(sender.getUniqueId().toString());
				Main.xps.put(sender.getUniqueId().toString(), oldBal + amount);
				sender.sendMessage(ChatColor.GREEN + "Xp deposited. New balance: " + Main.xps.get(sender.getUniqueId().toString()));
				break;
				
			case "withdraw": 
				amount = 0;
				if (!Main.xps.containsKey(sender.getUniqueId().toString())) {
					Main.xps.put(sender.getUniqueId().toString(), 0);
				}
				if (args[1].equalsIgnoreCase("all")) {
					amount = Main.xps.get(sender.getUniqueId().toString());
				} else {
					try {
						amount = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Invalid amount");
						break;
					}
				}
				if (amount > Main.xps.get(sender.getUniqueId().toString())) {
					sender.sendMessage(ChatColor.RED + "You only have " + Main.xps.get(sender.getUniqueId().toString()) + " xp in the bank");
					break;
				}
				newTotal = Utils.totalXp(sender.getLevel(), Utils.xpPointsInBar(sender.getExp(), sender.getExpToLevel())) + amount;
				newLevel = Utils.level(newTotal);
				newXp = Utils.xp(newTotal, newLevel);
				if (newXp == 1) {
					newLevel++;
					newXp = 0;
				}
				sender.setLevel(newLevel);
				sender.setExp(newXp);
				oldBal = Main.xps.get(sender.getUniqueId().toString());
				Main.xps.put(sender.getUniqueId().toString(), oldBal - amount);
				sender.sendMessage(ChatColor.GREEN + "Xp withdrawn. New balance: " + Main.xps.get(sender.getUniqueId().toString()));
				break;
			default:
				sender.sendMessage(ChatColor.RED + "Improper usage. Try /xpbank <deposit/withdraw> <amount>");
				break;
			}
			break;
			
		case 3: 
			switch(args[0]) {
			case "deposit":
				int amount;
				try {
					amount = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.RED + "Invalid amount");
					break;
				}
				if (amount < 1) {
					sender.sendMessage(ChatColor.RED + "Invalid amount");
					break;
				}
				int playerTotalXp = Utils.totalXp(sender.getLevel(), Utils.xpPointsInBar(sender.getExp(), sender.getExpToLevel()));
				switch (args[2]) {
				case "levels":
					if (sender.getLevel() < amount) {
						sender.sendMessage(ChatColor.RED + "You don't have that many levels");
						break;
					}
					int xpToLose = Utils.totalXp(sender.getLevel()) - Utils.totalXp(sender.getLevel() - amount);
					int newTotal = playerTotalXp - xpToLose;
					int newLevel = Utils.level(newTotal);
					float newXp = Utils.xp(newTotal, newLevel);
					if (newXp == 1) {
						newLevel++;
						newXp = 0;
					}
					sender.setLevel(newLevel);
					sender.setExp(newXp);
					if (!Main.xps.containsKey(sender.getUniqueId().toString())) {
						Main.xps.put(sender.getUniqueId().toString(), 0);
					}
					int oldBal = Main.xps.get(sender.getUniqueId().toString());
					Main.xps.put(sender.getUniqueId().toString(), oldBal + xpToLose);
					sender.sendMessage(ChatColor.GREEN + "Xp deposited. New balance: " + Main.xps.get(sender.getUniqueId().toString()));
					break;
					
				case "points":
					if (amount > playerTotalXp) {
						sender.sendMessage(ChatColor.RED + "You only have " + playerTotalXp + " xp");
						break;
					}
					newTotal = playerTotalXp - amount;
					newLevel = Utils.level(newTotal);
					newXp = Utils.xp(newTotal, newLevel);
					if (newXp == 1) {
						newLevel++;
						newXp = 0;
					}
					sender.setLevel(newLevel);
					sender.setExp(newXp);
					if (!Main.xps.containsKey(sender.getUniqueId().toString())) {
						Main.xps.put(sender.getUniqueId().toString(), 0);
					}
					oldBal = Main.xps.get(sender.getUniqueId().toString());
					Main.xps.put(sender.getUniqueId().toString(), oldBal + amount);
					sender.sendMessage(ChatColor.GREEN + "Xp deposited. New balance: " + Main.xps.get(sender.getUniqueId().toString()));
					break;
				default:
					sender.sendMessage(ChatColor.RED + "Improper usage. Try /xpbank <deposit/withdraw> <amount> <levels/points>");
					break;
				}
				break;
				
			case "withdraw": 
				try {
					amount = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.RED + "Invalid amount");
					break;
				}
				if (!Main.xps.containsKey(sender.getUniqueId().toString())) {
					Main.xps.put(sender.getUniqueId().toString(), 0);
				}
				switch(args[2]) {
				case "levels":
					int xpToAdd = Utils.totalXp(sender.getLevel() + amount) - Utils.totalXp(sender.getLevel());
					if (xpToAdd > Main.xps.get(sender.getUniqueId().toString())) {
						sender.sendMessage(ChatColor.RED + "You only have enough xp for " + Utils.getMaxLevel(sender, Main.xps.get(sender.getUniqueId().toString())) + " levels");
						break;
					}
					int newTotal = Utils.totalXp(sender.getLevel(), Utils.xpPointsInBar(sender.getExp(), sender.getExpToLevel())) + xpToAdd;
					int newLevel = Utils.level(newTotal);
					float newXp = Utils.xp(newTotal, newLevel);
					if (newXp == 1) {
						newLevel++;
						newXp = 0;
					}
					sender.setLevel(newLevel);
					sender.setExp(newXp);
					int oldBal = Main.xps.get(sender.getUniqueId().toString());
					Main.xps.put(sender.getUniqueId().toString(), oldBal - xpToAdd);
					sender.sendMessage(ChatColor.GREEN + "Xp withdrawn. New balance: " + Main.xps.get(sender.getUniqueId().toString()));
					break;
				case "points":
					if (amount > Main.xps.get(sender.getUniqueId().toString())) {
						sender.sendMessage(ChatColor.RED + "You only have " + Main.xps.get(sender.getUniqueId().toString()) + " xp in the bank");
						break;
					}
					newTotal = Utils.totalXp(sender.getLevel(), Utils.xpPointsInBar(sender.getExp(), sender.getExpToLevel())) + amount;
					newLevel = Utils.level(newTotal);
					newXp = Utils.xp(newTotal, newLevel);
					if (newXp == 1) {
						newLevel++;
						newXp = 0;
					}
					sender.setLevel(newLevel);
					sender.setExp(newXp);
					oldBal = Main.xps.get(sender.getUniqueId().toString());
					Main.xps.put(sender.getUniqueId().toString(), oldBal - amount);
					sender.sendMessage(ChatColor.GREEN + "Xp withdrawn. New balance: " + Main.xps.get(sender.getUniqueId().toString()));
					break;
				default:
					sender.sendMessage(ChatColor.RED + "Improper usage. Try /xpbank <deposit/withdraw> <amount> <levels/points>");
					break;
				}
				break;
			default:
				sender.sendMessage(ChatColor.RED + "Improper usage. Try /xpbank <deposit/withdraw> <amount>");
				break;
			}
			break;
		default:
			sender.sendMessage(ChatColor.RED + "Improper usage. Try /xpbank help");
			break;
		}
	}
	
	private void asConsole(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		
	}

}
