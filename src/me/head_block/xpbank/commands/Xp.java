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
import me.head_block.xpbank.utils.XpBPlayer;
import net.md_5.bungee.api.ChatColor;

public class Xp implements CommandExecutor {

	public static final String HELP_MESSAGE = ChatColor.GRAY + "------------ " + ChatColor.YELLOW +  "/xpbank help" + ChatColor.GRAY +  " ------------\n" 
			+ ChatColor.GRAY
			+ "/xpbank - " + ChatColor.AQUA + "Tells you how much xp you have stored or opens the XpBank GUI\n"
			+ ChatColor.GRAY + "/xpbank xpheld - " + ChatColor.AQUA + "Tells you how much xp you are holding\n"
			+ ChatColor.GRAY + "/xpbank xpstored - " + ChatColor.AQUA + "Tells you how much xp you have stored\n"
			+ ChatColor.GRAY + "/xpbank totalxp - " + ChatColor.AQUA + "Tells you how much xp you have stored and held together\n"
			+ ChatColor.GRAY + "/xpbank deposit <amount> - " + ChatColor.AQUA + "Deposits <amount> xp points\n"
			+ ChatColor.GRAY + "/xpbank deposit <amount> <levels/points> - " + ChatColor.AQUA + "Deposits <amount> points or levels\n"
			+ ChatColor.GRAY + "/xpbank deposit max - " + ChatColor.AQUA + "Deposits all xp up to the max (" + "%MAX_XP_STORED%" +  " points)\n"
			+ ChatColor.GRAY + "/xpbank withdraw <amount> - " + ChatColor.AQUA + "Withdraws <amount> xp points\n"
			+ ChatColor.GRAY + "/xpbank withdraw <amount> <levels/points> - " + ChatColor.AQUA + "Withdraws <amount> points or levels\n"
			+ ChatColor.GRAY + "/xpbank withdraw max - " + ChatColor.AQUA + "Withdraws all xp up to the max (" + "%MAX_XP_HELD%" + " points)\n"
			+ ChatColor.GRAY + "/xpbank pay <player> <amount> - " + ChatColor.AQUA + "Pays <player> the specified amount"; 
	public static final String ADMIN_HELP_MESSAGE = ChatColor.GRAY + "------------ " + ChatColor.YELLOW + "/xpbank admin help" + ChatColor.GRAY +   " ------------\n"
			+ ChatColor.GRAY + "/xpbank set <player> <amount> - " + ChatColor.AQUA + "Sets <player>'s balance to <amount>\n"
			+ ChatColor.GRAY + "/xpbank add <player> <amount> - " + ChatColor.AQUA + "Adds <amount> to <player>'s balance\n"
			+ ChatColor.GRAY + "/xpbank remove <player> <amount> - " + ChatColor.AQUA + "Removes <amount> from <player>'s balance capping out at 0\n"
			+ ChatColor.GRAY + "/xpbank reload - " + ChatColor.AQUA + "Reloads config values. (Will not enable/disable commands)"; 
	
	public static String INFO_MESSAGE;
	
	public Xp (Main plugin) {
		plugin.getCommand("xpbank").setExecutor(this);
		
		INFO_MESSAGE = ChatColor.GRAY + "------------ " + ChatColor.YELLOW +  "/xpbank info" + ChatColor.GRAY +  " ------------\n"
				+ ChatColor.GRAY + "Plugin version: " + ChatColor.AQUA + Main.instance.getDescription().getVersion() + ChatColor.GRAY + " (Newest version: " + ChatColor.AQUA + Main.newestVersion + ChatColor.GRAY + ")\n"
				+ "Spigot page: " + ChatColor.AQUA + "https://www.spigotmc.org/resources/xpbank.101132/ \n"
				+ ChatColor.GRAY + "Wiki page: " + ChatColor.AQUA + "https://github.com/ACM02/XpBank/wiki";
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) asPlayer(new XpBPlayer((Player) sender), args);
		else asConsole(sender, args);
		return false;
	}

	private void asPlayer(XpBPlayer sender, String[] args) {
		if (!sender.hasPermission("xpbank.use")) {
			sender.sendPlaceholderMessage(Main.NO_PERM_MESSAGE);
			return;
		}
		switch (args.length) {
		case 0:		// ---------------------- No Arguments ----------------------
			handle0Args(sender);
			break;
		case 1:		// ---------------------- 1 Argument ----------------------
			handle1Arg(sender, args[0]);
			break;
		case 2:		// ---------------------- 2 Arguments ----------------------
			handle2Args(sender, args);
			break;
		case 3:		// ---------------------- 3 Arguments ----------------------
			handle3Args(sender, args);
			break;
		default:
			sender.sendPlaceholderMessage(Main.IMPROPER_USE_MESSAGE + "/xpbank help for help");
			break;
		}
	}
	
	private void handle0Args(XpBPlayer sender) {
		if (Main.GUI_ENABLED) {
			MainMenu.openForPlayer(sender);
		} else {
			//sender.checkBalInstance();
			sender.sendPlaceholderMessage(Main.XP_STORED_MESSAGE);
			//sender.sendMessage(Utils.replacePlaceholders(Main.XP_STORED_MESSAGE, sender));
		}
	}
	
	private void handle1Arg(XpBPlayer sender, String arg) {
		switch(arg) {
		case "help":	// /xpbank help
			sender.sendPlaceholderMessage(HELP_MESSAGE);
			break;
		case "adminhelp":	// /xpbank adminhelp
			if (!sender.hasPermission("xpbank.admin")) {
				sender.sendPlaceholderMessage(Main.NO_PERM_MESSAGE);
			} else {
				sender.sendMessage(ADMIN_HELP_MESSAGE);
			}
			break;
		case "xpheld":	// /xpbank xpheld
			sender.sendPlaceholderMessage(Main.XP_HELD_MESSAGE);
			break;
		case "xpstored":	// /xpbank xpstored
			sender.sendPlaceholderMessage(Main.XP_STORED_MESSAGE);
			break;
		case "totalxp":		// /xpbank totalxp
			sender.sendPlaceholderMessage(Main.TOTAL_XP_MESSAGE);
			break;
		case "reload":		// /xpbank reload
			if (!sender.hasPermission("xpbank.admin")) {
				sender.sendPlaceholderMessage(Main.NO_PERM_MESSAGE);
			} else {
				Main.reloadPlugin();
				sender.sendMessage(ChatColor.GREEN + "Reloaded successfully.");
			}
			break;
		case "info":		// /xpbank info
			if (!sender.hasPermission("xpbank.admin")) {
				sender.sendPlaceholderMessage(Main.NO_PERM_MESSAGE);
			} else {
				sender.sendPlaceholderMessage(ChatColor.GRAY + "------------ " + ChatColor.YELLOW +  "/xpbank info" + ChatColor.GRAY +  " ------------\n"
						+ ChatColor.GRAY + "Plugin version: " + ChatColor.AQUA + Main.instance.getDescription().getVersion() + ChatColor.GRAY + " (Newest version: " + ChatColor.AQUA + Main.newestVersion + ChatColor.GRAY + ")\n"
						+ "Spigot page: " + ChatColor.AQUA + "https://www.spigotmc.org/resources/xpbank.101132/ \n"
						+ ChatColor.GRAY + "Wiki page: " + ChatColor.AQUA + "https://github.com/ACM02/XpBank/wiki");
			}
			break;
		default:
			sender.sendPlaceholderMessage(Main.IMPROPER_USE_MESSAGE + "/xpbank help for help");
			break;
		}
	}
	
	@SuppressWarnings("deprecation")
	private void handle2Args(XpBPlayer sender, String[] args) {
		switch(args[0]) {
		case "deposit":		// /xpbank deposit <amount>/max
			int amount = 1;
			if (!args[1].equals("max")) {
				amount = getAmount(args[1], sender);
				if (amount == -1) break;
			}
			//checkBalInstance(sender);
			
			int playerTotalXp = sender.totalXp(); //Utils.totalXp(sender);
			if (playerTotalXp == 0) {
				//sender.sendMessage(Utils.replacePlaceholders(Main.NO_XP_DEPOSIT_MESSAGE, sender));
				sender.sendPlaceholderMessage(Main.NO_XP_DEPOSIT_MESSAGE);
				break;
			}
			if (args[1].equals("max")) {
				if (sender.getStoredXp() >= Main.MAX_XP_STORED) { // Main.xps.get(sender.getUniqueId().toString())
					//sender.sendMessage(Utils.replacePlaceholders(Main.EXCEEDS_STORE_LIMIT));
					sender.sendPlaceholderMessage(Main.EXCEEDS_STORE_LIMIT);
					break;
				}
				amount = playerTotalXp;
				if (sender.getStoredXp() + amount >= Main.MAX_XP_STORED) {
					amount = Main.MAX_XP_STORED - sender.getStoredXp(); // Main.xps.get(sender.getUniqueId().toString());
				}
			}
			if ((long) sender.getStoredXp() + (long) amount > Main.MAX_XP_STORED) {
				//sender.sendMessage(Utils.replacePlaceholders(Main.EXCEEDS_STORE_LIMIT));
				sender.sendPlaceholderMessage(Main.EXCEEDS_STORE_LIMIT);
				break;
			}
			if (amount > playerTotalXp) {
				sender.sendMessage(ChatColor.RED + "You only have " + playerTotalXp + " xp");
				break;
			}
			sender.removeXp(amount); // removeXp(amount, sender, playerTotalXp);
			int oldBal = sender.getStoredXp();
			sender.setStoredXp(oldBal + amount);	//Main.xps.put(sender.getUniqueId().toString(), oldBal + amount);
			sender.sendPlaceholderMessage(Main.DEPOSIT_MESSAGE);
			//sender.sendMessage(Utils.replacePlaceholders(Main.DEPOSIT_MESSAGE, sender));
			break;
			
		case "withdraw": 		// /xpbank withdraw <amount>/max
			amount = 0;
			//checkBalInstance(sender);
			if (sender.getStoredXp() == 0) {
				//sender.sendMessage(Utils.replacePlaceholders(Main.NO_XP_WITHDRAW_MESSAGE, sender));
				sender.sendPlaceholderMessage(Main.NO_XP_WITHDRAW_MESSAGE);
				break;
			}
			playerTotalXp = sender.totalXp(); // Utils.totalXp(sender);
			if (args[1].equalsIgnoreCase("max")) {
				if (playerTotalXp >= Main.MAX_XP_HELD) {
					//sender.sendMessage(Utils.replacePlaceholders(Main.EXCEEDS_HOLD_LIMIT));
					sender.sendPlaceholderMessage(Main.EXCEEDS_HOLD_LIMIT);
					break;
				}
				amount = sender.getStoredXp(); // Main.xps.get(sender.getUniqueId().toString());
				if (playerTotalXp + amount >= Main.MAX_XP_HELD) {
					amount = Main.MAX_XP_HELD - playerTotalXp;
				}
			} else {
				amount = getAmount(args[1], sender);
				if (amount == -1) break;
			}
			if ((long) playerTotalXp + (long) amount > Main.MAX_XP_HELD) {
				//sender.sendMessage(Utils.replacePlaceholders(Main.EXCEEDS_HOLD_LIMIT));
				sender.sendPlaceholderMessage(Main.EXCEEDS_HOLD_LIMIT);
				break;
			}
			if (amount > sender.getStoredXp()) {
				//sender.sendMessage(Utils.replacePlaceholders(ChatColor.RED + "You only have " + "%XP_HELD%" + " xp in the bank", sender));
				sender.sendPlaceholderMessage(ChatColor.RED + "You only have " + "%XP_HELD%" + " xp in the bank");
				break;
			}
			sender.addXp(amount);
			//addXp(amount, sender);
			oldBal = sender.getStoredXp(); // Main.xps.get(sender.getUniqueId().toString());
			sender.setStoredXp(oldBal - amount); // Main.xps.put(sender.getUniqueId().toString(), oldBal - amount);
			sender.sendPlaceholderMessage(Main.WITHDRAW_MESSAGE);
			//sender.sendMessage(Utils.replacePlaceholders(Main.WITHDRAW_MESSAGE, sender));
			break;
		case "get":		// /xpbank get <player>
			if (!sender.hasPermission("xpbank.admin")) {
				//sender.sendMessage(Utils.replacePlaceholders(Main.NO_PERM_MESSAGE));
				sender.sendPlaceholderMessage(Main.NO_PERM_MESSAGE);
				break;
			}
			OfflinePlayer offline = Bukkit.getOfflinePlayer(args[1]);
			if (offline == null) {
				sender.sendPlaceholderMessage(Main.PLAYER_NOT_FOUND_MESSAGE);
				//sender.sendMessage(Utils.replacePlaceholders(Main.PLAYER_NOT_FOUND_MESSAGE));
			} else if (!Main.xps.containsKey(offline.getUniqueId().toString())) {
				sender.sendMessage(ChatColor.RED + "Player has no balance");
			} else if (Main.xps.containsKey(offline.getUniqueId().toString())) {
				sender.sendPlaceholderMessage(ChatColor.YELLOW + offline.getName() + "'s stored xp is: " + "%XP_STORED%");
				//sender.sendMessage(Utils.replacePlaceholders(ChatColor.YELLOW + offline.getName() + "'s stored xp is: " + "%XP_STORED%", offline));
			}
			break;
		default:
			sender.sendPlaceholderMessage(Main.IMPROPER_USE_MESSAGE + "/xpbank <deposit/withdraw> <amount>");
			//sender.sendMessage(Utils.replacePlaceholders(Main.IMPROPER_USE_MESSAGE + "/xpbank <deposit/withdraw> <amount>"));
			break;
		}		
	}
	
	@SuppressWarnings("deprecation")
	private void handle3Args(XpBPlayer sender, String[] args) {
		switch(args[0]) {
		case "deposit":		// /xpbank deposit <amount> levels/points
			int amount = getAmount(args[1], sender);
			if (amount == -1) break;
			//checkBalInstance(sender);
			int playerTotalXp = sender.totalXp(); // Utils.totalXp(sender);
			if (playerTotalXp == 0) {
				sender.sendPlaceholderMessage(Main.NO_XP_DEPOSIT_MESSAGE);
				//sender.sendMessage(Utils.replacePlaceholders(Main.NO_XP_DEPOSIT_MESSAGE, sender));
				break;
			}
			switch (args[2]) {
			case "levels":		// /xpbank deposit <amount> levels
				if ((long) sender.getStoredXp() + (long) Utils.totalXp(amount) > Main.MAX_XP_STORED) {
					//sender.sendMessage(Utils.replacePlaceholders(Main.EXCEEDS_STORE_LIMIT));
					sender.sendPlaceholderMessage(Main.EXCEEDS_STORE_LIMIT);
					break;
				}
				if (sender.getLevel() < amount) {
					sender.sendMessage(ChatColor.RED + "You don't have that many levels");
					break;
				}
				int xpToLose = Utils.totalXp(sender.getLevel()) - Utils.totalXp(sender.getLevel() - amount);
				sender.removeXp(xpToLose);
				//removeXp(xpToLose, sender, playerTotalXp);
				int oldBal = sender.getStoredXp(); // Main.xps.get(sender.getUniqueId().toString());
				sender.setStoredXp(oldBal + xpToLose);
				//Main.xps.put(sender.getUniqueId().toString(), oldBal + xpToLose);
				sender.sendPlaceholderMessage(Main.DEPOSIT_MESSAGE);
				//sender.sendMessage(Utils.replacePlaceholders(Main.DEPOSIT_MESSAGE, sender));
				break;
				
			case "points":		// /xpbank deposit <amount> points
				if ((long) sender.getStoredXp() + (long) amount > Main.MAX_XP_STORED) {
					//sender.sendMessage(Utils.replacePlaceholders(Main.EXCEEDS_STORE_LIMIT));
					sender.sendPlaceholderMessage(Main.EXCEEDS_STORE_LIMIT);
					break;
				}
				if (amount > playerTotalXp) {
					sender.sendMessage(ChatColor.RED + "You only have " + playerTotalXp + " xp");
					break;
				}
				sender.removeXp(amount);
				//removeXp(amount, sender, playerTotalXp);
				oldBal = sender.getStoredXp(); // Main.xps.get(sender.getUniqueId().toString());
				sender.setStoredXp(oldBal + amount);
				//Main.xps.put(sender.getUniqueId().toString(), oldBal + amount);
				sender.sendPlaceholderMessage(Main.DEPOSIT_MESSAGE);
				//sender.sendMessage(Utils.replacePlaceholders(Main.DEPOSIT_MESSAGE, sender));
				break;
			default:
				//sender.sendMessage(Utils.replacePlaceholders(Main.IMPROPER_USE_MESSAGE + "/xpbank <deposit/withdraw> <amount> <levels/points>"));
				sender.sendPlaceholderMessage(Main.IMPROPER_USE_MESSAGE + "/xpbank <deposit/withdraw> <amount> <levels/points>");
				break;
			}
			break;
		case "withdraw": 		// /xpbank withdraw <amount> levels/points
			amount = getAmount(args[1], sender);
			if (amount == -1) break;
			//checkBalInstance(sender);
			if (sender.getStoredXp() == 0) {
				//sender.sendMessage(Utils.replacePlaceholders(Main.NO_XP_WITHDRAW_MESSAGE, sender));
				sender.sendPlaceholderMessage(Main.NO_XP_WITHDRAW_MESSAGE);
				break;
			}
			playerTotalXp = sender.totalXp(); // Utils.totalXp(sender);
			switch(args[2]) {
			case "levels": 		// /xpbank withdraw <amount> levels
				int xpToAdd = Utils.totalXp(sender.getLevel() + amount) - Utils.totalXp(sender.getLevel());
				if ((long) playerTotalXp + (long) xpToAdd > Main.MAX_XP_HELD) {
					//sender.sendMessage(Utils.replacePlaceholders(Main.EXCEEDS_HOLD_LIMIT));
					sender.sendPlaceholderMessage(Main.EXCEEDS_HOLD_LIMIT);
					break;
				}
				if (xpToAdd > sender.getStoredXp()) {
					sender.sendMessage(ChatColor.RED + "You only have enough xp for " + Utils.getMaxLevel(sender, Main.xps.get(sender.getUniqueId().toString())) + " levels");
					break;
				}
				sender.addXp(xpToAdd);
				//addXp(xpToAdd, sender);
				int oldBal = sender.getStoredXp(); // Main.xps.get(sender.getUniqueId().toString());
				sender.setStoredXp(oldBal - xpToAdd);
				//Main.xps.put(sender.getUniqueId().toString(), oldBal - xpToAdd);
				sender.sendMessage(Main.WITHDRAW_MESSAGE);
				//sender.sendMessage(Utils.replacePlaceholders(Main.WITHDRAW_MESSAGE, sender));
				break;
			case "points": 		// /xpbank withdraw <amount> points
				if ((long) playerTotalXp + (long) amount > Main.MAX_XP_HELD) {
					sender.sendPlaceholderMessage(Main.EXCEEDS_HOLD_LIMIT);
					break;
				}
				if (amount > Main.xps.get(sender.getUniqueId().toString())) {
					//sender.sendMessage(Utils.replacePlaceholders(ChatColor.RED + "You only have " + "%XP_STORED%" + " xp in the bank", sender));
					sender.sendPlaceholderMessage(ChatColor.RED + "You only have " + "%XP_STORED%" + " xp in the bank");
					break;
				}
				sender.addXp(amount);
				//addXp(amount, sender);
				oldBal = sender.getStoredXp(); // Main.xps.get(sender.getUniqueId().toString());
				//Main.xps.put(sender.getUniqueId().toString(), oldBal - amount);
				sender.setStoredXp(oldBal - amount);
				sender.sendPlaceholderMessage(Main.WITHDRAW_MESSAGE);
				//sender.sendMessage(Utils.replacePlaceholders(Main.WITHDRAW_MESSAGE, sender));
				break;
			default:
				//sender.sendMessage(Utils.replacePlaceholders(Main.IMPROPER_USE_MESSAGE + "/xpbank <deposit/withdraw> <amount> <levels/points>"));
				sender.sendPlaceholderMessage(Main.IMPROPER_USE_MESSAGE + "/xpbank <deposit/withdraw> <amount> <levels/points>");
				break;
			}
			break;
		case "pay":		// /xpbank pay <player> <amount>
			amount = getAmount(args[2], sender);
			if (amount == -1) break;
			//checkBalInstance(sender);
			if (amount > sender.getStoredXp()) {
				sender.sendPlaceholderMessage(ChatColor.RED + "You don't have enough xp to do that! (Balance: " + "%XP_STORED" + ")");
				//sender.sendMessage(Utils.replacePlaceholders(ChatColor.RED + "You don't have enough xp to do that! (Balance: " + "%XP_STORED" + ")", sender));
				break;
			}
			OfflinePlayer offline = Bukkit.getOfflinePlayer(args[1]);
			if (offline != null) {
				if (Main.xps.containsKey(offline.getUniqueId().toString())) {
					if ((long) Main.xps.get(offline.getUniqueId().toString()) + (long) amount > Main.MAX_XP_STORED) {
						//sender.sendMessage(Utils.replacePlaceholders(Main.EXCEEDS_STORE_LIMIT_TARGET));
						sender.sendPlaceholderMessage(Main.EXCEEDS_STORE_LIMIT_TARGET);
						break;
					}
				}
				if (Main.xps.containsKey(offline.getUniqueId().toString())) {
					Main.xps.put(offline.getUniqueId().toString(), Main.xps.get(offline.getUniqueId().toString()) + amount);
					sender.setStoredXp(sender.getStoredXp() - amount);
					//Main.xps.put(sender.getUniqueId().toString(), Main.xps.get(sender.getUniqueId().toString()) - amount);
					if (offline.isOnline()) {
						Player online = offline.getPlayer();
						online.sendMessage(Utils.replacePlaceholders(ChatColor.YELLOW + sender.getName() + " has sent you " + amount + " xp. New balance: " + "%XP_STORED%", online));
					}
					sender.sendPlaceholderMessage(ChatColor.YELLOW + "Transfer complete. New balance: " + "%XP_STORED%");
					//sender.sendMessage(Utils.replacePlaceholders(ChatColor.YELLOW + "Transfer complete. New balance: " + "%XP_STORED%", sender));
				} else {
					if (offline.isOnline()) {
						Main.xps.put(offline.getUniqueId().toString(), amount);
						sender.setStoredXp(sender.getStoredXp() - amount);
						//Main.xps.put(sender.getUniqueId().toString(), Main.xps.get(sender.getUniqueId().toString()) - amount);
						Player online = offline.getPlayer();
						online.sendMessage(Utils.replacePlaceholders(ChatColor.YELLOW + sender.getName() + " has sent you " + amount + " xp. New balance: " + "%XP_STORED%", online));
						sender.sendPlaceholderMessage(ChatColor.YELLOW + "Transfer complete. New balance: " + "%XP_STORED%");
						//sender.sendMessage(Utils.replacePlaceholders(ChatColor.YELLOW + "Transfer complete. New balance: " + "%XP_STORED%", sender));
					} else {
						//sender.sendMessage(Utils.replacePlaceholders(Main.PLAYER_NOT_FOUND_MESSAGE));
						sender.sendPlaceholderMessage(Main.PLAYER_NOT_FOUND_MESSAGE);
						break;
					}
				}
			} else {
				//sender.sendMessage(Utils.replacePlaceholders(Main.PLAYER_NOT_FOUND_MESSAGE));
				sender.sendPlaceholderMessage(Main.PLAYER_NOT_FOUND_MESSAGE);
				break;
			}
			break;
		case "set":		// /xpbank set <player> <amount>
			if (!sender.hasPermission("xpbank.admin")) {
				//sender.sendMessage(Utils.replacePlaceholders(Main.NO_PERM_MESSAGE));
				sender.sendPlaceholderMessage(Main.NO_PERM_MESSAGE);
				break;
			}
			amount = getAmount(args[2], sender);
			if (amount == -1) break;
			if (amount > Main.MAX_XP_STORED) {
				//sender.sendMessage(Utils.replacePlaceholders(Main.EXCEEDS_STORE_LIMIT_TARGET));
				sender.sendPlaceholderMessage(Main.EXCEEDS_STORE_LIMIT_TARGET);
				break;
			}
			offline = Bukkit.getOfflinePlayer(args[1]);
			if (offline == null) {
				sender.sendPlaceholderMessage(Main.PLAYER_NOT_FOUND_MESSAGE);
				//sender.sendMessage(Utils.replacePlaceholders(Main.PLAYER_NOT_FOUND_MESSAGE));
			}
			if (Main.xps.containsKey(offline.getUniqueId().toString())) {
				Main.xps.put(offline.getUniqueId().toString(), amount);
				if (Main.instance.getConfig().getBoolean("notify-on-admin-balance-change") && offline.isOnline()) {
					Player online = offline.getPlayer();
					online.sendMessage(ChatColor.YELLOW + "Your balance has been set to " + amount + " by an admin");
				}
				sender.sendMessage(Utils.replacePlaceholders(ChatColor.YELLOW + offline.getName() + "'s balance is now " + "%XP_STORED%", offline));
				//sender.sendPlaceholderMessage(ChatColor.YELLOW + offline.getName() + "'s balance is now " + "%XP_STORED%");
				break;
			} else {
				if (offline.isOnline()) {
					Main.xps.put(offline.getUniqueId().toString(), amount);
					Player online = offline.getPlayer();
					if (Main.instance.getConfig().getBoolean("notify-on-admin-balance-change"))
						online.sendMessage(ChatColor.YELLOW + "Your balance has been set to " + amount + " by an admin");
					//sender.sendPlaceholderMessage(ChatColor.YELLOW + offline.getName() + "'s balance is now " + "%XP_STORED%");
					sender.sendMessage(Utils.replacePlaceholders(ChatColor.YELLOW + offline.getName() + "'s balance is now " + "%XP_STORED%", offline));
					break;
				} else {
					//sender.sendMessage(Utils.replacePlaceholders(Main.PLAYER_NOT_FOUND_MESSAGE));
					sender.sendPlaceholderMessage(Main.PLAYER_NOT_FOUND_MESSAGE);
					break;
				}
			}
		case "add":		// /xpbank add <player> <amount>
			if (!sender.hasPermission("xpbank.admin")) {
				//sender.sendMessage(Utils.replacePlaceholders(Main.NO_PERM_MESSAGE));
				sender.sendPlaceholderMessage(Main.NO_PERM_MESSAGE);
				break;
			}
			amount = getAmount(args[2], sender);
			if (amount == -1) break;
			if (amount > Main.MAX_XP_STORED) {
				sender.sendPlaceholderMessage(Main.EXCEEDS_STORE_LIMIT_TARGET);
				//sender.sendMessage(Utils.replacePlaceholders(Main.EXCEEDS_STORE_LIMIT_TARGET));
				break;
			}
			offline = Bukkit.getOfflinePlayer(args[1]);
			if (offline == null) {
				sender.sendPlaceholderMessage(Main.PLAYER_NOT_FOUND_MESSAGE);
				//sender.sendMessage(Utils.replacePlaceholders(Main.PLAYER_NOT_FOUND_MESSAGE));
			}
			if (Main.xps.containsKey(offline.getUniqueId().toString())) {
				Main.xps.put(offline.getUniqueId().toString(), amount + Main.xps.get(offline.getUniqueId().toString()));
				if (Main.instance.getConfig().getBoolean("notify-on-admin-balance-change") && offline.isOnline()) {
					Player online = offline.getPlayer();
					online.sendMessage(Utils.replacePlaceholders(ChatColor.YELLOW + "Your xp balance has been set to " + "%XP_STORED%" + " by an admin", online));
				}
				sender.sendMessage(Utils.replacePlaceholders(ChatColor.YELLOW + offline.getName() + "'s balance is now " + "%XP_STORED%", offline));
				//sender.sendPlaceholderMessage(ChatColor.YELLOW + offline.getName() + "'s balance is now " + "%XP_STORED%");
				break;
			} else {
				if (offline.isOnline()) {
					Main.xps.put(offline.getUniqueId().toString(), amount);
					Player online = offline.getPlayer();
					if (Main.instance.getConfig().getBoolean("notify-on-admin-balance-change"))
						online.sendMessage(Utils.replacePlaceholders(ChatColor.YELLOW + "Your balance has been set to " + "%XP_STORED%" + " by an admin", online));
					//sender.sendPlaceholderMessage(ChatColor.YELLOW + offline.getName() + "'s balance is now " + "%XP_STORED%");
					sender.sendMessage(Utils.replacePlaceholders(ChatColor.YELLOW + offline.getName() + "'s balance is now " + "%XP_STORED%", offline));
					break;
				} else {
					//sender.sendMessage(Utils.replacePlaceholders(Main.PLAYER_NOT_FOUND_MESSAGE));
					sender.sendPlaceholderMessage(Main.PLAYER_NOT_FOUND_MESSAGE);
					break;
				}
			}
		case "remove":		// /xpbank remove <player> <amount>
			if (!sender.hasPermission("xpbank.admin")) {
				//sender.sendMessage(Utils.replacePlaceholders(Main.NO_PERM_MESSAGE));
				sender.sendPlaceholderMessage(Main.NO_PERM_MESSAGE);
				break;
			}
			amount = getAmount(args[2], sender);
			if (amount == -1) break;
			offline = Bukkit.getOfflinePlayer(args[1]);
			if (offline == null) {
				sender.sendPlaceholderMessage(Main.PLAYER_NOT_FOUND_MESSAGE);
				//sender.sendMessage(Utils.replacePlaceholders(Main.PLAYER_NOT_FOUND_MESSAGE));
			}
			if (!Main.xps.containsKey(offline.getUniqueId().toString())) {
				if (offline.isOnline()) {
					Main.xps.put(offline.getUniqueId().toString(), 0);
					Player online = offline.getPlayer();
					if (Main.instance.getConfig().getBoolean("notify-on-admin-balance-change"))
						online.sendMessage(Utils.replacePlaceholders(ChatColor.YELLOW + "Your balance has been set to " + "%XP_STORED%" + " by an admin", online));
					sender.sendMessage(Utils.replacePlaceholders(ChatColor.YELLOW + offline.getName() + "'s balance is now " + "%XP_STORED%", offline));
					break;
				} else {
					sender.sendMessage(Utils.replacePlaceholders(Main.PLAYER_NOT_FOUND_MESSAGE));
					break;
				}
			} else { 
				if (Main.xps.get(offline.getUniqueId().toString()) - amount < 0) {
					amount = Main.xps.get(offline.getUniqueId().toString());
				}
				Main.xps.put(offline.getUniqueId().toString(), Main.xps.get(offline.getUniqueId().toString()) - amount);
				if (Main.instance.getConfig().getBoolean("notify-on-admin-balance-change") && offline.isOnline()) {
					Player online = offline.getPlayer();
					online.sendMessage(Utils.replacePlaceholders(ChatColor.YELLOW + "Your balance has been set to " + "%XP_STORED%" + " by an admin", online));
				}
				sender.sendMessage(Utils.replacePlaceholders(ChatColor.YELLOW + offline.getName() + "'s balance is now " + "%XP_STORED%", offline));
				break;
			}
		default:
			sender.sendPlaceholderMessage(Main.IMPROPER_USE_MESSAGE + "/xpbank <deposit/withdraw> <amount>");
			//sender.sendMessage(Utils.replacePlaceholders(Main.IMPROPER_USE_MESSAGE + "/xpbank <deposit/withdraw> <amount>"));
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
//	private void checkBalInstance(OfflinePlayer p) {
//		if (!Main.xps.containsKey(p.getUniqueId().toString())) {
//			Main.xps.put(p.getUniqueId().toString(), 0);
//		}
//	}
	
	

	private void asConsole(CommandSender sender, String[] args) {
		
		
	}
}
