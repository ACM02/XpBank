package me.head_block.xpbank.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.head_block.xpbank.Main;
import me.head_block.xpbank.ui.MainMenu;
import me.head_block.xpbank.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class Xp implements CommandExecutor {

	public static final String EXCEEDS_HOLD_LIMIT = ChatColor.RED + "Invalid amount. That exceeds the maximum xp that can be held. (" + Main.MAX_LEVEL_HELD + " levels/" + Main.MAX_XP_HELD + " points)";
	public static final String EXCEEDS_STORE_LIMIT_TARGET = ChatColor.RED + "Invalid amount. That exceeds the maximum xp that can be held by the target. (" + Main.MAX_LEVEL_HELD + " levels/" + Main.MAX_XP_HELD + " points)";
	public static final String EXCEEDS_STORE_LIMIT = ChatColor.RED + "Invalid amount. That exceeds the maximum xp that can be stored. (" + Main.MAX_LEVEL_STORED + " levels/" + Main.MAX_XP_STORED + " points)";
	public static final String HELP_MESSAGE = ChatColor.GREEN + "------------/xpbank help------------\n" 
			+ ChatColor.YELLOW
			+ "/xpbank - Tells you how much xp you have stored\n"
			+ "/xpbank xpheld - Tells you how much xp you are holding\n"
			+ "/xpbank xpstored - Tells you how much xp you have stored\n"
			+ "/xpbank deposit <amount> - Deposits <amount> xp points\n"
			+ "/xpbank deposit <amount> <levels/points> - Deposits <amount> points or levels\n"
			+ "/xpbank deposit max - Deposits all xp up to the max (" + Main.MAX_XP_STORED +  " points)\n"
			+ "/xpbank withdraw <amount> - Withdraws <amount> xp points\n"
			+ "/xpbank withdraw <amount> <levels/points> - Withdraws <amount> points or levels\n"
			+ "/xpbank withdraw max - Withdraws all xp up to the max (" + Main.MAX_XP_HELD + " points)\n"
			+ "/xpbank pay <player> <amount> - Pays <player> the specified amount"; 
	public static final String ADMIN_HELP_MESSAGE = ChatColor.GREEN + "------------/xpbank admin help------------\n"
			+ ChatColor.YELLOW
			+ "/xpbank set <player> <amount> - Sets <player>'s balance to <amount>\n"
			+ "/xpbank add <player> <amount> - Adds <amount> to <player>'s balance\n"
			+ "/xpbank remove <player> <amount> - Removes <amount> from <player>'s balance capping out at 0\n"; 
	public static final String NO_PERMISSION_MESSAGE = Main.NO_PERM_MESSAGE;
	
	public Xp (Main plugin) {
		plugin.getCommand("xpbank").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) asPlayer((Player) sender, args);
		else asConsole(sender, args);
		return false;
	}

	@SuppressWarnings("deprecation")
	private void asPlayer(Player sender, String[] args) {
		if (!sender.hasPermission("xpbank.use")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use the xp bank");
			return;
		}
		switch (args.length) {
		case 0:
			if (Main.GUI_ENABLED) {
				MainMenu.openForPlayer(sender);
				break;
			} else {
				checkBalInstance(sender);
				sender.sendMessage(ChatColor.YELLOW + "You have " + Main.xps.get(sender.getUniqueId().toString()) + " experience points in the bank. (Enough for level " + 
						Utils.getMaxLevel(sender, Main.xps.get(sender.getUniqueId().toString())) + ")");
				break;
			}
		case 1: 
			switch(args[0]) {
			case "help":
				sender.sendMessage(HELP_MESSAGE);
			case "adminhelp":
				if (!sender.hasPermission("xpbank.admin")) {
					sender.sendMessage(NO_PERMISSION_MESSAGE);
				} else {
					sender.sendMessage(ADMIN_HELP_MESSAGE);
				}
				break;
			case "xpheld":
				sender.sendMessage(ChatColor.YELLOW + "You are holding " + Utils.totalXp(sender) + " xp");
				break;
			case "xpstored":
				sender.sendMessage(ChatColor.YELLOW + "You have " + Main.xps.get(sender.getUniqueId().toString()) + " experience points in the bank. (Enough for level " + 
						Utils.getMaxLevel(sender, Main.xps.get(sender.getUniqueId().toString())) + ")");
				break;
			default:
				sender.sendMessage(Main.IMPROPER_USE_MESSAGE + "/xpbank help for help");
				break;
			}
			break;
			
			
		case 2:
			switch(args[0]) {
			case "deposit":
				int amount = 1;
				if (!args[1].equals("max")) {
					amount = getAmount(args[1], sender);
					if (amount == -1) break;
				}
				checkBalInstance(sender);
				int playerTotalXp = Utils.totalXp(sender);
				if (playerTotalXp == 0) {
					sender.sendMessage(ChatColor.RED + "You don't have any XP to deposit");
					break;
				}
				if (args[1].equals("max")) {
					if (Main.xps.get(sender.getUniqueId().toString()) >= Main.MAX_XP_STORED) {
						sender.sendMessage(EXCEEDS_STORE_LIMIT);
						break;
					}
					amount = playerTotalXp;
					if (Main.xps.get(sender.getUniqueId().toString()) + amount >= Main.MAX_XP_STORED) {
						amount = Main.MAX_XP_STORED - Main.xps.get(sender.getUniqueId().toString());
					}
				}
				if ((long) Main.xps.get(sender.getUniqueId().toString()) + (long) amount > Main.MAX_XP_STORED) {
					sender.sendMessage(EXCEEDS_STORE_LIMIT);
					break;
				}
				if (amount > playerTotalXp) {
					sender.sendMessage(ChatColor.RED + "You only have " + playerTotalXp + " xp");
					break;
				}
				removeXp(amount, sender, playerTotalXp);
				int oldBal = Main.xps.get(sender.getUniqueId().toString());
				Main.xps.put(sender.getUniqueId().toString(), oldBal + amount);
				sender.sendMessage(ChatColor.GREEN + "Xp deposited. New balance: " + Main.xps.get(sender.getUniqueId().toString()));
				break;
				
			case "withdraw": 
				amount = 0;
				checkBalInstance(sender);
				if (Main.xps.get(sender.getUniqueId().toString()) == 0) {
					sender.sendMessage(ChatColor.RED + "You have no XP to withdraw");
					break;
				}
				playerTotalXp = Utils.totalXp(sender);
				if (args[1].equalsIgnoreCase("max")) {
					if (playerTotalXp >= Main.MAX_XP_HELD) {
						sender.sendMessage(EXCEEDS_HOLD_LIMIT);
						break;
					}
					amount = Main.xps.get(sender.getUniqueId().toString());
					if (playerTotalXp + amount >= Main.MAX_XP_HELD) {
						amount = Main.MAX_XP_HELD - playerTotalXp;
					}
				} else {
					amount = getAmount(args[1], sender);
					if (amount == -1) break;
				}
				if ((long) playerTotalXp + (long) amount > Main.MAX_XP_HELD) {
					sender.sendMessage(EXCEEDS_HOLD_LIMIT);
					break;
				}
				if (amount > Main.xps.get(sender.getUniqueId().toString())) {
					sender.sendMessage(ChatColor.RED + "You only have " + Main.xps.get(sender.getUniqueId().toString()) + " xp in the bank");
					break;
				}
				addXp(amount, sender, playerTotalXp);
				oldBal = Main.xps.get(sender.getUniqueId().toString());
				Main.xps.put(sender.getUniqueId().toString(), oldBal - amount);
				sender.sendMessage(ChatColor.GREEN + "Xp withdrawn. New balance: " + Main.xps.get(sender.getUniqueId().toString()));
				break;
			case "get":
				if (!sender.hasPermission("xpbank.admin")) {
					sender.sendMessage(NO_PERMISSION_MESSAGE);
					break;
				}
				OfflinePlayer offline = Bukkit.getOfflinePlayer(args[1]);
				if (offline == null) {
					sender.sendMessage(ChatColor.RED + "Player not found");
				} else if (!Main.xps.containsKey(offline.getUniqueId().toString())) {
					sender.sendMessage(ChatColor.RED + "Player has no balance");
				} else if (Main.xps.containsKey(offline.getUniqueId().toString())) {
					sender.sendMessage(ChatColor.YELLOW + offline.getName() + "'s stored xp is: " + Main.xps.get(offline.getUniqueId().toString()));
				}
				break;
			default:
				sender.sendMessage(Main.IMPROPER_USE_MESSAGE + "/xpbank <deposit/withdraw> <amount>");
				break;
			}
			break;
			
		case 3: 
			
			
			switch(args[0]) {
			case "deposit":
				int amount = getAmount(args[1], sender);
				if (amount == -1) break;
				checkBalInstance(sender);
				int playerTotalXp = Utils.totalXp(sender);
				if (playerTotalXp == 0) {
					sender.sendMessage(ChatColor.RED + "You don't have any XP to deposit");
					break;
				}
				switch (args[2]) {
				case "levels":
					if ((long) Main.xps.get(sender.getUniqueId().toString()) + (long) Utils.totalXp(amount) > Main.MAX_XP_STORED) {
						sender.sendMessage(EXCEEDS_STORE_LIMIT);
						break;
					}
					if (sender.getLevel() < amount) {
						sender.sendMessage(ChatColor.RED + "You don't have that many levels");
						break;
					}
					int xpToLose = Utils.totalXp(sender.getLevel()) - Utils.totalXp(sender.getLevel() - amount);
					removeXp(xpToLose, sender, playerTotalXp);
					int oldBal = Main.xps.get(sender.getUniqueId().toString());
					Main.xps.put(sender.getUniqueId().toString(), oldBal + xpToLose);
					sender.sendMessage(ChatColor.GREEN + "Xp deposited. New balance: " + Main.xps.get(sender.getUniqueId().toString()));
					break;
					
				case "points":
					if ((long) Main.xps.get(sender.getUniqueId().toString()) + (long) amount > Main.MAX_XP_STORED) {
						sender.sendMessage(EXCEEDS_STORE_LIMIT);
						break;
					}
					if (amount > playerTotalXp) {
						sender.sendMessage(ChatColor.RED + "You only have " + playerTotalXp + " xp");
						break;
					}
					removeXp(amount, sender, playerTotalXp);
					oldBal = Main.xps.get(sender.getUniqueId().toString());
					Main.xps.put(sender.getUniqueId().toString(), oldBal + amount);
					sender.sendMessage(ChatColor.GREEN + "Xp deposited. New balance: " + Main.xps.get(sender.getUniqueId().toString()));
					break;
				default:
					sender.sendMessage(Main.IMPROPER_USE_MESSAGE + "/xpbank <deposit/withdraw> <amount> <levels/points>");
					break;
				}
				break;
			case "withdraw": 
				amount = getAmount(args[1], sender);
				if (amount == -1) break;
				checkBalInstance(sender);
				if (Main.xps.get(sender.getUniqueId().toString()) == 0) {
					sender.sendMessage(ChatColor.RED + "You have no XP to withdraw");
					break;
				}
				playerTotalXp = Utils.totalXp(sender);
				switch(args[2]) {
				case "levels":
					int xpToAdd = Utils.totalXp(sender.getLevel() + amount) - Utils.totalXp(sender.getLevel());
					if ((long) playerTotalXp + (long) xpToAdd > Main.MAX_XP_HELD) {
						sender.sendMessage(EXCEEDS_HOLD_LIMIT);
						break;
					}
					if (xpToAdd > Main.xps.get(sender.getUniqueId().toString())) {
						sender.sendMessage(ChatColor.RED + "You only have enough xp for " + Utils.getMaxLevel(sender, Main.xps.get(sender.getUniqueId().toString())) + " levels");
						break;
					}
					addXp(xpToAdd, sender, playerTotalXp);
					int oldBal = Main.xps.get(sender.getUniqueId().toString());
					Main.xps.put(sender.getUniqueId().toString(), oldBal - xpToAdd);
					sender.sendMessage(ChatColor.GREEN + "Xp withdrawn. New balance: " + Main.xps.get(sender.getUniqueId().toString()));
					break;
				case "points":
					if ((long) playerTotalXp + (long) amount > Main.MAX_XP_HELD) {
						sender.sendMessage(EXCEEDS_HOLD_LIMIT);
						break;
					}
					if (amount > Main.xps.get(sender.getUniqueId().toString())) {
						sender.sendMessage(ChatColor.RED + "You only have " + Main.xps.get(sender.getUniqueId().toString()) + " xp in the bank");
						break;
					}
					addXp(amount, sender, playerTotalXp);
					oldBal = Main.xps.get(sender.getUniqueId().toString());
					Main.xps.put(sender.getUniqueId().toString(), oldBal - amount);
					sender.sendMessage(ChatColor.GREEN + "Xp withdrawn. New balance: " + Main.xps.get(sender.getUniqueId().toString()));
					break;
				default:
					sender.sendMessage(Main.IMPROPER_USE_MESSAGE + "/xpbank <deposit/withdraw> <amount> <levels/points>");
					break;
				}
				break;
			case "pay":
				amount = getAmount(args[2], sender);
				if (amount == -1) break;
				checkBalInstance(sender);
				if (amount > Main.xps.get(sender.getUniqueId().toString())) {
					sender.sendMessage(ChatColor.RED + "You don't have enough xp to do that! (Balance: " + Main.xps.get(sender.getUniqueId().toString()) + ")");
					break;
				}
				OfflinePlayer offline = Bukkit.getOfflinePlayer(args[1]);
				if (offline != null) {
					if (Main.xps.containsKey(offline.getUniqueId().toString())) {
						if ((long) Main.xps.get(offline.getUniqueId().toString()) + (long) amount > Main.MAX_XP_STORED) {
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
			case "set":
				if (!sender.hasPermission("xpbank.admin")) {
					sender.sendMessage(NO_PERMISSION_MESSAGE);
					break;
				}
				amount = getAmount(args[2], sender);
				if (amount == -1) break;
				if (amount > Main.MAX_XP_STORED) {
					sender.sendMessage(EXCEEDS_STORE_LIMIT_TARGET);
					break;
				}
				offline = Bukkit.getOfflinePlayer(args[1]);
				if (offline == null) {
					sender.sendMessage(ChatColor.RED + "Player not found");
				}
				if (Main.xps.containsKey(offline.getUniqueId().toString())) {
					Main.xps.put(offline.getUniqueId().toString(), amount);
					if (offline.isOnline()) {
						Player online = offline.getPlayer();
						online.sendMessage(ChatColor.YELLOW + "Your balance has been set to " + amount + " by an admin");
					}
					sender.sendMessage(ChatColor.YELLOW + offline.getName() + "'s balance is now " + Main.xps.get(offline.getUniqueId().toString()));
					break;
				} else {
					if (offline.isOnline()) {
						Main.xps.put(offline.getUniqueId().toString(), amount);
						Player online = offline.getPlayer();
						online.sendMessage(ChatColor.YELLOW + "Your balance has been set to " + amount + " by an admin");
						sender.sendMessage(ChatColor.YELLOW + offline.getName() + "'s balance is now " + Main.xps.get(offline.getUniqueId().toString()));
						break;
					} else {
						sender.sendMessage(ChatColor.RED + "Player not found");
						break;
					}
				}
			case "add":
				if (!sender.hasPermission("xpbank.admin")) {
					sender.sendMessage(NO_PERMISSION_MESSAGE);
					break;
				}
				amount = getAmount(args[2], sender);
				if (amount == -1) break;
				if (amount > Main.MAX_XP_STORED) {
					sender.sendMessage(EXCEEDS_STORE_LIMIT_TARGET);
					break;
				}
				offline = Bukkit.getOfflinePlayer(args[1]);
				if (offline == null) {
					sender.sendMessage(ChatColor.RED + "Player not found");
				}
				if (Main.xps.containsKey(offline.getUniqueId().toString())) {
					Main.xps.put(offline.getUniqueId().toString(), amount + Main.xps.get(offline.getUniqueId().toString()));
					if (offline.isOnline()) {
						Player online = offline.getPlayer();
						online.sendMessage(ChatColor.YELLOW + "Your balance has been set to " + Main.xps.get(offline.getUniqueId().toString()) + " by an admin");
					}
					sender.sendMessage(ChatColor.YELLOW + offline.getName() + "'s balance is now " + Main.xps.get(offline.getUniqueId().toString()));
					break;
				} else {
					if (offline.isOnline()) {
						Main.xps.put(offline.getUniqueId().toString(), amount);
						Player online = offline.getPlayer();
						online.sendMessage(ChatColor.YELLOW + "Your balance has been set to " + Main.xps.get(offline.getUniqueId().toString()) + " by an admin");
						sender.sendMessage(ChatColor.YELLOW + offline.getName() + "'s balance is now " + Main.xps.get(offline.getUniqueId().toString()));
						break;
					} else {
						sender.sendMessage(ChatColor.RED + "Player not found");
						break;
					}
				}
			case "remove":
				if (!sender.hasPermission("xpbank.admin")) {
					sender.sendMessage(NO_PERMISSION_MESSAGE);
					break;
				}
				amount = getAmount(args[2], sender);
				if (amount == -1) break;
				offline = Bukkit.getOfflinePlayer(args[1]);
				if (offline == null) {
					sender.sendMessage(ChatColor.RED + "Player not found");
				}
				if (!Main.xps.containsKey(offline.getUniqueId().toString())) {
					if (offline.isOnline()) {
						Main.xps.put(offline.getUniqueId().toString(), 0);
						Player online = offline.getPlayer();
						online.sendMessage(ChatColor.YELLOW + "Your balance has been set to " + Main.xps.get(offline.getUniqueId().toString()) + " by an admin");
						sender.sendMessage(ChatColor.YELLOW + offline.getName() + "'s balance is now " + Main.xps.get(offline.getUniqueId().toString()));
						break;
					} else {
						sender.sendMessage(ChatColor.RED + "Player not found");
						break;
					}
				} else { 
					if (Main.xps.get(offline.getUniqueId().toString()) - amount < 0) {
						amount = Main.xps.get(offline.getUniqueId().toString());
					}
					Main.xps.put(offline.getUniqueId().toString(), Main.xps.get(offline.getUniqueId().toString()) - amount);
					if (offline.isOnline()) {
						Player online = offline.getPlayer();
						online.sendMessage(ChatColor.YELLOW + "Your balance has been set to " + Main.xps.get(offline.getUniqueId().toString()) + " by an admin");
					}
					sender.sendMessage(ChatColor.YELLOW + offline.getName() + "'s balance is now " + Main.xps.get(offline.getUniqueId().toString()));
					break;
				}
			default:
				sender.sendMessage(Main.IMPROPER_USE_MESSAGE + "/xpbank <deposit/withdraw> <amount>");
				break;
			}
			break;
			
			
		default:
			sender.sendMessage(Main.IMPROPER_USE_MESSAGE + "/xpbank help for help");
			
			break;
		}
	}
	
	private int getAmount(String string, Player p) {
		try {
			int toReturn =Integer.parseInt(string);
			if (toReturn > 0) return toReturn;
			else {
				p.sendMessage(ChatColor.RED + "Invalid amount (Must be greater than zero)");
				return -1;
			}
		} catch (NumberFormatException e) {
			p.sendMessage(ChatColor.RED + "Invalid amount");
			return -1;
		}
	}

	/**
	 * Checks to see if a player has a balance, if not, creates a balance for the user
	 * @param p The player to check
	 */
	private void checkBalInstance(OfflinePlayer p) {
		if (!Main.xps.containsKey(p.getUniqueId().toString())) {
			Main.xps.put(p.getUniqueId().toString(), 0);
		}
	}

	private void asConsole(CommandSender sender, String[] args) {
		
		
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
