package me.head_block.xpbank.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.head_block.xpbank.Main;
import me.head_block.xpbank.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class Xp implements CommandExecutor {

	public static final String EXCEEDS_HOLD_LIMIT = ChatColor.RED + "Invalid amount. That exceeds the maximum xp that can be stored. (" + Main.MAX_LEVEL + " levels/" + Main.MAX_XP + " points)";
	public static final String EXCEEDS_STORE_LIMIT_TARGET = ChatColor.RED + "Invalid amount. That exceeds the maximum xp that can be held by the target. (" + Main.MAX_LEVEL + " levels/" + Main.MAX_XP + " points)";
	public static final String EXCEEDS_STORE_LIMIT = ChatColor.RED + "Invalid amount. That exceeds the maximum xp that can be stored. (" + Main.MAX_LEVEL + " levels/" + Main.MAX_XP + " points)";
	
	
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

	@SuppressWarnings("deprecation")
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
				int playerTotalXp = Utils.totalXp(sender);
				if (args[1].equals("all")) amount = playerTotalXp;
				if (amount > playerTotalXp) {
					sender.sendMessage(ChatColor.RED + "You only have " + playerTotalXp + " xp");
					break;
				}
				if ((long) Main.xps.get(sender.getUniqueId().toString()) + (long) amount > Main.MAX_XP) {
					sender.sendMessage(EXCEEDS_STORE_LIMIT);
					break;
				}
				removeXp(amount, sender, playerTotalXp);
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
				playerTotalXp = Utils.totalXp(sender);
				if ((long) playerTotalXp + (long) amount > Main.MAX_XP) {
					sender.sendMessage(EXCEEDS_STORE_LIMIT);
					break;
				}
				addXp(amount, sender, playerTotalXp);
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
				int playerTotalXp = Utils.totalXp(sender);
				switch (args[2]) {
				case "levels":
					if (sender.getLevel() < amount) {
						sender.sendMessage(ChatColor.RED + "You don't have that many levels");
						break;
					}
					if ((long) Main.xps.get(sender.getUniqueId().toString()) + (long) Utils.totalXp(amount) > Main.MAX_XP) {
						sender.sendMessage(EXCEEDS_STORE_LIMIT);
						break;
					}
					int xpToLose = Utils.totalXp(sender.getLevel()) - Utils.totalXp(sender.getLevel() - amount);
					removeXp(xpToLose, sender, playerTotalXp);
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
					if ((long) Main.xps.get(sender.getUniqueId().toString()) + (long) amount > Main.MAX_XP) {
						sender.sendMessage(EXCEEDS_STORE_LIMIT);
						break;
					}
					removeXp(amount, sender, playerTotalXp);
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
				playerTotalXp = Utils.totalXp(sender);
				switch(args[2]) {
				case "levels":
					int xpToAdd = Utils.totalXp(sender.getLevel() + amount) - Utils.totalXp(sender.getLevel());
					if (xpToAdd > Main.xps.get(sender.getUniqueId().toString())) {
						sender.sendMessage(ChatColor.RED + "You only have enough xp for " + Utils.getMaxLevel(sender, Main.xps.get(sender.getUniqueId().toString())) + " levels");
						break;
					}
					if ((long) playerTotalXp + (long) xpToAdd > Main.MAX_XP) {
						sender.sendMessage(EXCEEDS_HOLD_LIMIT);
						break;
					}
					addXp(xpToAdd, sender, playerTotalXp);
					int oldBal = Main.xps.get(sender.getUniqueId().toString());
					Main.xps.put(sender.getUniqueId().toString(), oldBal - xpToAdd);
					sender.sendMessage(ChatColor.GREEN + "Xp withdrawn. New balance: " + Main.xps.get(sender.getUniqueId().toString()));
					break;
				case "points":
					if (amount > Main.xps.get(sender.getUniqueId().toString())) {
						sender.sendMessage(ChatColor.RED + "You only have " + Main.xps.get(sender.getUniqueId().toString()) + " xp in the bank");
						break;
					}
					if ((long) playerTotalXp + (long) amount > Main.MAX_XP) {
						sender.sendMessage(EXCEEDS_HOLD_LIMIT);
						break;
					}
					addXp(amount, sender, playerTotalXp);
					oldBal = Main.xps.get(sender.getUniqueId().toString());
					Main.xps.put(sender.getUniqueId().toString(), oldBal - amount);
					sender.sendMessage(ChatColor.GREEN + "Xp withdrawn. New balance: " + Main.xps.get(sender.getUniqueId().toString()));
					break;
				default:
					sender.sendMessage(ChatColor.RED + "Improper usage. Try /xpbank <deposit/withdraw> <amount> <levels/points>");
					break;
				}
				break;
			case "pay":
				try {
					amount = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.RED + "Invalid amount");
					break;
				}
				if (amount < 1) {
					sender.sendMessage(ChatColor.RED + "Invalid amount");
					break;
				}
				if (!Main.xps.containsKey(sender.getUniqueId().toString())) {
					Main.xps.put(sender.getUniqueId().toString(), 0);
				}
				if (amount > Main.xps.get(sender.getUniqueId().toString())) {
					sender.sendMessage(ChatColor.RED + "You don't have enough xp to do that! (Balance: " + Main.xps.get(sender.getUniqueId().toString()) + ")");
					break;
				}
				OfflinePlayer offline = Bukkit.getOfflinePlayer(args[1]);
				if (offline != null) {
					if (Main.xps.containsKey(offline.getUniqueId().toString())) {
						if ((long) Main.xps.get(offline.getUniqueId().toString()) + (long) amount > Main.MAX_XP) {
							sender.sendMessage(EXCEEDS_STORE_LIMIT_TARGET);
							break;
						}
					}
					if (Main.xps.containsKey(offline.getUniqueId().toString())) {
						Main.xps.put(offline.getUniqueId().toString(), Main.xps.get(offline.getUniqueId().toString()) + amount);
						Main.xps.put(sender.getUniqueId().toString(), Main.xps.get(sender.getUniqueId().toString()) - amount);
						if (offline.isOnline()) {
							Player online = offline.getPlayer();
							online.sendMessage(ChatColor.YELLOW + sender.getName() + " has sent you " + amount + " xp. New balance: " + Main.xps.get(offline.getUniqueId().toString()));
						}
						sender.sendMessage(ChatColor.YELLOW + "Transfer complete. New balance: " + Main.xps.get(sender.getUniqueId().toString()));
					} else {
						if (offline.isOnline()) {
							Main.xps.put(offline.getUniqueId().toString(), amount);
							Main.xps.put(sender.getUniqueId().toString(), Main.xps.get(sender.getUniqueId().toString()) - amount);
							Player online = offline.getPlayer();
							online.sendMessage(ChatColor.YELLOW + sender.getName() + " has sent you " + amount + " xp. Try /xpbank to see your balance");
							sender.sendMessage(ChatColor.YELLOW + "Transfer complete. New balance: " + Main.xps.get(sender.getUniqueId().toString()));
						} else {
							sender.sendMessage(ChatColor.RED + "Player not found");
							break;
						}
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Player not found");
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
	
	@SuppressWarnings("unused")
	private void addXp(int xp, Player p) {
		int newTotal = Utils.totalXp(p) + xp;
		int newLevel = Utils.level(newTotal);
		float newXp = Utils.xp(newTotal, newLevel);
		if (newXp == 1) {
			newLevel++;
			newXp = 0;
		}
		p.setLevel(newLevel);
		p.setExp(newXp);
	}
	
	@SuppressWarnings("unused")
	private void removeXp(int xp, Player p) {
		int newTotal = Utils.totalXp(p) - xp;
		int newLevel = Utils.level(newTotal);
		float newXp = Utils.xp(newTotal, newLevel);
		if (newXp == 1) {
			newLevel++;
			newXp = 0;
		}
		p.setLevel(newLevel);
		p.setExp(newXp);
	}

	@SuppressWarnings("unused")
	private void addLevels(int levels, Player p) {
		int xpToAdd = Utils.totalXp(p.getLevel() + levels) - Utils.totalXp(p.getLevel());
		int newTotal = Utils.totalXp(p) + xpToAdd;
		int newLevel = Utils.level(newTotal);
		float newXp = Utils.xp(newTotal, newLevel);
		if (newXp == 1) {
			newLevel++;
			newXp = 0;
		}
		p.setLevel(newLevel);
		p.setExp(newXp);
	}

	@SuppressWarnings("unused")
	private void removeLevels(int levels, Player p) {
		int xpToLose = Utils.totalXp(p.getLevel()) - Utils.totalXp(p.getLevel() - levels);
		int newTotal = Utils.totalXp(p) - xpToLose;
		int newLevel = Utils.level(newTotal);
		float newXp = Utils.xp(newTotal, newLevel);
		if (newXp == 1) {
			newLevel++;
			newXp = 0;
		}
		p.setLevel(newLevel);
		p.setExp(newXp);
	}
	
	private void addXp(int xp, Player p, int playerTotalXp) {
		int newTotal = playerTotalXp + xp;
		int newLevel = Utils.level(newTotal);
		float newXp = Utils.xp(newTotal, newLevel);
		if (newXp == 1) {
			newLevel++;
			newXp = 0;
		}
		p.setLevel(newLevel);
		p.setExp(newXp);
	}
	
	private void removeXp(int xp, Player p, int playerTotalXp) {
		int newTotal = playerTotalXp - xp;
		int newLevel = Utils.level(newTotal);
		float newXp = Utils.xp(newTotal, newLevel);
		if (newXp == 1) {
			newLevel++;
			newXp = 0;
		}
		p.setLevel(newLevel);
		p.setExp(newXp);
	}

	@SuppressWarnings("unused")
	private void addLevels(int levels, Player p, int playerTotalXp) {
		int xpToAdd = Utils.totalXp(p.getLevel() + levels) - Utils.totalXp(p.getLevel());
		int newTotal = Utils.totalXp(p) + xpToAdd;
		int newLevel = Utils.level(newTotal);
		float newXp = Utils.xp(newTotal, newLevel);
		if (newXp == 1) {
			newLevel++;
			newXp = 0;
		}
		p.setLevel(newLevel);
		p.setExp(newXp);
	}

	@SuppressWarnings("unused")
	private void removeLevels(int levels, Player p, int playerTotalXp) {
		int xpToLose = Utils.totalXp(p.getLevel()) - Utils.totalXp(p.getLevel() - levels);
		int newTotal = playerTotalXp - xpToLose;
		int newLevel = Utils.level(newTotal);
		float newXp = Utils.xp(newTotal, newLevel);
		if (newXp == 1) {
			newLevel++;
			newXp = 0;
		}
		p.setLevel(newLevel);
		p.setExp(newXp);
	}

}
