package me.head_block.xpbank.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.head_block.xpbank.Main;
import me.head_block.xpbank.controllers.XpController;
import me.head_block.xpbank.exceptions.ExceedsXpLimitException;
import me.head_block.xpbank.exceptions.NoXpException;
import me.head_block.xpbank.exceptions.NotEnoughXpException;
import me.head_block.xpbank.exceptions.PlayerNotFoundException;
import me.head_block.xpbank.ui.MainMenu;
import me.head_block.xpbank.utils.Utils;
import me.head_block.xpbank.utils.XpBPlayer;
import net.md_5.bungee.api.ChatColor;

public class Xp implements CommandExecutor {

	public static final String HELP_MESSAGE = ChatColor.GRAY + "------------ " + ChatColor.YELLOW + "/xpbank help"
			+ ChatColor.GRAY + " ------------\n"
			+ ChatColor.GRAY
			+ "/xpbank - " + ChatColor.AQUA + "Tells you how much xp you have stored or opens the XpBank GUI\n"
			+ ChatColor.GRAY + "/xpbank xpheld - " + ChatColor.AQUA + "Tells you how much xp you are holding\n"
			+ ChatColor.GRAY + "/xpbank xpstored - " + ChatColor.AQUA + "Tells you how much xp you have stored\n"
			+ ChatColor.GRAY + "/xpbank totalxp - " + ChatColor.AQUA
			+ "Tells you how much xp you have stored and held together\n"
			+ ChatColor.GRAY + "/xpbank deposit <amount> - " + ChatColor.AQUA + "Deposits <amount> xp points\n"
			+ ChatColor.GRAY + "/xpbank deposit <amount> <levels/points> - " + ChatColor.AQUA
			+ "Deposits <amount> points or levels\n"
			+ ChatColor.GRAY + "/xpbank deposit max - " + ChatColor.AQUA + "Deposits all xp up to the max ("
			+ "%MAX_XP_STORED%" + " points)\n"
			+ ChatColor.GRAY + "/xpbank withdraw <amount> - " + ChatColor.AQUA + "Withdraws <amount> xp points\n"
			+ ChatColor.GRAY + "/xpbank withdraw <amount> <levels/points> - " + ChatColor.AQUA
			+ "Withdraws <amount> points or levels\n"
			+ ChatColor.GRAY + "/xpbank withdraw max - " + ChatColor.AQUA + "Withdraws all xp up to the max ("
			+ "%MAX_XP_HELD%" + " points)\n"
			+ ChatColor.GRAY + "/xpbank pay <player> <amount> - " + ChatColor.AQUA
			+ "Pays <player> the specified amount";
	public static final String ADMIN_HELP_MESSAGE = ChatColor.GRAY + "------------ " + ChatColor.YELLOW
			+ "/xpbank admin help" + ChatColor.GRAY + " ------------\n"
			+ ChatColor.GRAY + "/xpbank set <player> <amount> - " + ChatColor.AQUA
			+ "Sets <player>'s balance to <amount>\n"
			+ ChatColor.GRAY + "/xpbank add <player> <amount> - " + ChatColor.AQUA
			+ "Adds <amount> to <player>'s balance\n"
			+ ChatColor.GRAY + "/xpbank remove <player> <amount> - " + ChatColor.AQUA
			+ "Removes <amount> from <player>'s balance capping out at 0\n"
			+ ChatColor.GRAY + "/xpbank reload - " + ChatColor.AQUA
			+ "Reloads config values. (Will not enable/disable commands)";

	public static String INFO_MESSAGE;

	public Xp(Main plugin) {
		plugin.getCommand("xpbank").setExecutor(this);

		INFO_MESSAGE = ChatColor.GRAY + "------------ " + ChatColor.YELLOW + "/xpbank info" + ChatColor.GRAY
				+ " ------------\n"
				+ ChatColor.GRAY + "Plugin version: " + ChatColor.AQUA + Main.instance.getDescription().getVersion()
				+ ChatColor.GRAY + " (Newest version: " + ChatColor.AQUA + Main.newestVersion + ChatColor.GRAY + ")\n"
				+ "Spigot page: " + ChatColor.AQUA + "https://www.spigotmc.org/resources/xpbank.101132/ \n"
				+ ChatColor.GRAY + "Wiki page: " + ChatColor.AQUA + "https://github.com/ACM02/XpBank/wiki";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player player)
			asPlayer(new XpBPlayer(player), args);
		else
			asConsole(sender, args);
		return false;
	}

	private void asPlayer(XpBPlayer sender, String[] args) {
		if (!sender.hasPermission("xpbank.use")) {
			sender.sendPlaceholderMessage(Main.NO_PERM_MESSAGE);
			return;
		}
		switch (args.length) {
			case 0 -> // ---------------------- No Arguments ----------------------
				handle0Args(sender);
			case 1 -> // ---------------------- 1 Argument ----------------------
				handle1Arg(sender, args[0]);
			case 2 -> // ---------------------- 2 Arguments ----------------------
				handle2Args(sender, args);
			case 3 -> // ---------------------- 3 Arguments ----------------------
				handle3Args(sender, args);
			default -> sender.sendPlaceholderMessage(Main.IMPROPER_USE_MESSAGE + "/xpbank help for help");
		}
	}

	private void handle0Args(XpBPlayer sender) {
		if (Main.GUI_ENABLED) {
			MainMenu.openForPlayer(sender);
		} else {
			// sender.checkBalInstance();
			sender.sendPlaceholderMessage(Main.XP_STORED_MESSAGE);
		}
	}

	private void handle1Arg(XpBPlayer sender, String arg) {
		switch (arg) {
			case "help" -> // /xpbank help
				sender.sendPlaceholderMessage(HELP_MESSAGE);
			case "adminhelp" -> {
				// /xpbank adminhelp
				if (!sender.hasPermission("xpbank.admin")) {
					sender.sendPlaceholderMessage(Main.NO_PERM_MESSAGE);
				} else {
					sender.sendMessage(ADMIN_HELP_MESSAGE);
				}
			}
			case "xpheld" -> // /xpbank xpheld
				sender.sendPlaceholderMessage(Main.XP_HELD_MESSAGE);
			case "xpstored" -> // /xpbank xpstored
				sender.sendPlaceholderMessage(Main.XP_STORED_MESSAGE);
			case "totalxp" -> // /xpbank totalxp
				sender.sendPlaceholderMessage(Main.TOTAL_XP_MESSAGE);
			case "reload" -> {
				// /xpbank reload
				if (!sender.hasPermission("xpbank.admin")) {
					sender.sendPlaceholderMessage(Main.NO_PERM_MESSAGE);
				} else {
					Main.reloadPlugin();
					sender.sendMessage(ChatColor.GREEN + "Reloaded successfully.");
				}
			}
			case "info" -> {
				// /xpbank info
				if (!sender.hasPermission("xpbank.admin")) {
					sender.sendPlaceholderMessage(Main.NO_PERM_MESSAGE);
				} else {
					sender.sendPlaceholderMessage(ChatColor.GRAY + "------------ " + ChatColor.YELLOW + "/xpbank info"
							+ ChatColor.GRAY + " ------------\n"
							+ ChatColor.GRAY + "Plugin version: " + ChatColor.AQUA
							+ Main.instance.getDescription().getVersion() + ChatColor.GRAY + " (Newest version: "
							+ ChatColor.AQUA + Main.newestVersion + ChatColor.GRAY + ")\n"
							+ "Spigot page: " + ChatColor.AQUA + "https://www.spigotmc.org/resources/xpbank.101132/ \n"
							+ ChatColor.GRAY + "Wiki page: " + ChatColor.AQUA + "https://github.com/ACM02/XpBank/wiki");
				}
			}
			default -> sender.sendPlaceholderMessage(Main.IMPROPER_USE_MESSAGE + "/xpbank help for help");
		}
	}

	@SuppressWarnings("deprecation")
	private void handle2Args(XpBPlayer sender, String[] args) {
		switch (args[0]) {
			// TODO: deposit/withdraw implementations needs testing, but it's a lot cleaner
			// than before
			case "deposit" -> {
				// /xpbank deposit <amount>/max
				deposit_points(sender, args[1]);
			}
			case "withdraw" -> {
				// /xpbank withdraw <amount>/max
				withdraw_points(sender, args[1]);
			}
			case "get" -> {
				// /xpbank get <player>
				if (!sender.hasPermission("xpbank.admin")) {
					sender.sendPlaceholderMessage(Main.NO_PERM_MESSAGE);
					break;
				}
				OfflinePlayer offline = Bukkit.getOfflinePlayer(args[1]);
				if (offline == null) {
					sender.sendPlaceholderMessage(Main.PLAYER_NOT_FOUND_MESSAGE);
				} else if (!Main.xps.containsKey(offline.getUniqueId().toString())) {
					sender.sendMessage(ChatColor.RED + "Player has no balance");
				} else if (Main.xps.containsKey(offline.getUniqueId().toString())) {
					sender.sendPlaceholderMessage(
							ChatColor.YELLOW + offline.getName() + "'s stored xp is: " + "%XP_STORED%");
				}
			}
			default -> sender.sendPlaceholderMessage(Main.IMPROPER_USE_MESSAGE + "/xpbank <deposit/withdraw> <amount>");
		}
	}

	@SuppressWarnings("deprecation")
	private void handle3Args(XpBPlayer sender, String[] args) {
		switch (args[0]) {
			case "deposit" -> {
				// /xpbank deposit <amount> levels/points
				switch (args[2]) {
					case "levels" -> {
						// /xpbank deposit <amount> levels
						deposit_levels(sender, args[1]);
					}
					case "points" -> {
						// /xpbank deposit <amount> points
						deposit_points(sender, args[1]);
					}
					default -> sender.sendPlaceholderMessage(
							Main.IMPROPER_USE_MESSAGE + "/xpbank <deposit/withdraw> <amount> <levels/points>");
				}
			}
			case "withdraw" -> {
				switch (args[2]) {
					case "levels" -> {
						// /xpbank withdraw <amount> levels
						withdraw_levels(sender, args[1]);
					}
					case "points" -> {
						// /xpbank withdraw <amount> points
						withdraw_points(sender, args[1]);
					}
					default -> sender.sendPlaceholderMessage(
							Main.IMPROPER_USE_MESSAGE + "/xpbank <deposit/withdraw> <amount> <levels/points>");
				}
			}

			case "pay" -> {
				// /xpbank pay <player> <amount>
				pay_player(sender, args[1], args[2]);
			}
			case "set" -> {
				// /xpbank set <player> <amount>
				if (!sender.hasPermission("xpbank.admin")) {
					sender.sendPlaceholderMessage(Main.NO_PERM_MESSAGE);
					break;
				}
				int amount = getAmount(args[2], sender);
				if (amount == -1)
					break;
				if (amount > Main.MAX_XP_STORED) {
					sender.sendPlaceholderMessage(Main.EXCEEDS_STORE_LIMIT_TARGET);
					break;
				}
				OfflinePlayer offline = Bukkit.getOfflinePlayer(args[1]);
				if (offline == null) {
					sender.sendPlaceholderMessage(Main.PLAYER_NOT_FOUND_MESSAGE);
				}
				if (Main.xps.containsKey(offline.getUniqueId().toString())) {
					Main.xps.put(offline.getUniqueId().toString(), amount);
					if (Main.instance.getConfig().getBoolean("notify-on-admin-balance-change") && offline.isOnline()) {
						Player online = offline.getPlayer();
						online.sendMessage(
								ChatColor.YELLOW + "Your balance has been set to " + amount + " by an admin");
					}
					sender.sendMessage(Utils.replacePlaceholders(
							ChatColor.YELLOW + offline.getName() + "'s balance is now " + "%XP_STORED%", offline));
				} else {
					if (offline.isOnline()) {
						Main.xps.put(offline.getUniqueId().toString(), amount);
						Player online = offline.getPlayer();
						if (Main.instance.getConfig().getBoolean("notify-on-admin-balance-change"))
							online.sendMessage(
									ChatColor.YELLOW + "Your balance has been set to " + amount + " by an admin");
						sender.sendMessage(Utils.replacePlaceholders(
								ChatColor.YELLOW + offline.getName() + "'s balance is now " + "%XP_STORED%", offline));
					} else {
						sender.sendPlaceholderMessage(Main.PLAYER_NOT_FOUND_MESSAGE);
					}
				}
			}
			case "add" -> {
				// /xpbank add <player> <amount>
				if (!sender.hasPermission("xpbank.admin")) {
					sender.sendPlaceholderMessage(Main.NO_PERM_MESSAGE);
					break;
				}
				int amount = getAmount(args[2], sender);
				if (amount == -1)
					break;
				if (amount > Main.MAX_XP_STORED) {
					sender.sendPlaceholderMessage(Main.EXCEEDS_STORE_LIMIT_TARGET);
					break;
				}
				OfflinePlayer offline = Bukkit.getOfflinePlayer(args[1]);
				if (offline == null) {
					sender.sendPlaceholderMessage(Main.PLAYER_NOT_FOUND_MESSAGE);
				}
				if (Main.xps.containsKey(offline.getUniqueId().toString())) {
					Main.xps.put(offline.getUniqueId().toString(),
							amount + Main.xps.get(offline.getUniqueId().toString()));
					if (Main.instance.getConfig().getBoolean("notify-on-admin-balance-change") && offline.isOnline()) {
						Player online = offline.getPlayer();
						online.sendMessage(Utils.replacePlaceholders(
								ChatColor.YELLOW + "Your xp balance has been set to " + "%XP_STORED%" + " by an admin",
								online));
					}
					sender.sendMessage(Utils.replacePlaceholders(
							ChatColor.YELLOW + offline.getName() + "'s balance is now " + "%XP_STORED%", offline));
				} else {
					if (offline.isOnline()) {
						Main.xps.put(offline.getUniqueId().toString(), amount);
						Player online = offline.getPlayer();
						if (Main.instance.getConfig().getBoolean("notify-on-admin-balance-change"))
							online.sendMessage(Utils.replacePlaceholders(
									ChatColor.YELLOW + "Your balance has been set to " + "%XP_STORED%" + " by an admin",
									online));
						sender.sendMessage(Utils.replacePlaceholders(
								ChatColor.YELLOW + offline.getName() + "'s balance is now " + "%XP_STORED%", offline));
					} else {
						sender.sendPlaceholderMessage(Main.PLAYER_NOT_FOUND_MESSAGE);
					}
				}
			}
			case "remove" -> {
				// /xpbank remove <player> <amount>
				if (!sender.hasPermission("xpbank.admin")) {
					sender.sendPlaceholderMessage(Main.NO_PERM_MESSAGE);
					break;
				}
				int amount = getAmount(args[2], sender);
				if (amount == -1)
					break;
				OfflinePlayer offline = Bukkit.getOfflinePlayer(args[1]);
				if (offline == null) {
					sender.sendPlaceholderMessage(Main.PLAYER_NOT_FOUND_MESSAGE);
				}
				if (!Main.xps.containsKey(offline.getUniqueId().toString())) {
					if (offline.isOnline()) {
						Main.xps.put(offline.getUniqueId().toString(), 0);
						Player online = offline.getPlayer();
						if (Main.instance.getConfig().getBoolean("notify-on-admin-balance-change"))
							online.sendMessage(Utils.replacePlaceholders(
									ChatColor.YELLOW + "Your balance has been set to " + "%XP_STORED%" + " by an admin",
									online));
						sender.sendMessage(Utils.replacePlaceholders(
								ChatColor.YELLOW + offline.getName() + "'s balance is now " + "%XP_STORED%", offline));
					} else {
						sender.sendMessage(Utils.replacePlaceholders(Main.PLAYER_NOT_FOUND_MESSAGE));
					}
				} else {
					if (Main.xps.get(offline.getUniqueId().toString()) - amount < 0) {
						amount = Main.xps.get(offline.getUniqueId().toString());
					}
					Main.xps.put(offline.getUniqueId().toString(),
							Main.xps.get(offline.getUniqueId().toString()) - amount);
					if (Main.instance.getConfig().getBoolean("notify-on-admin-balance-change") && offline.isOnline()) {
						Player online = offline.getPlayer();
						online.sendMessage(Utils.replacePlaceholders(
								ChatColor.YELLOW + "Your balance has been set to " + "%XP_STORED%" + " by an admin",
								online));
					}
					sender.sendMessage(Utils.replacePlaceholders(
							ChatColor.YELLOW + offline.getName() + "'s balance is now " + "%XP_STORED%", offline));
				}
			}
			default -> sender.sendPlaceholderMessage(Main.IMPROPER_USE_MESSAGE + "/xpbank <deposit/withdraw> <amount>");
		}
	}

	private int getAmount(String string, Player p) {
		try {
			int toReturn = Integer.parseInt(string);
			if (toReturn > 0)
				return toReturn;
			else {
				p.sendMessage(ChatColor.RED + "Invalid amount (Must be greater than zero)");
				return -1;
			}
		} catch (NumberFormatException e) {
			p.sendMessage(ChatColor.RED + "Invalid amount");
			return -1;
		}
	}

	private void deposit_points(XpBPlayer player, String amountStr) {
		int playerTotalXp = player.totalXp();
		int amount;
		if (amountStr.equals("max")) {
			amount = Math.min(playerTotalXp, Main.MAX_XP_STORED - player.getStoredXp());
		} else {
			amount = getAmount(amountStr, player);
		}
		if (amount != -1) {
			try {
				XpController.deposit_xp(player, amount);
				player.sendPlaceholderMessage(Main.DEPOSIT_MESSAGE);
			} catch (ExceedsXpLimitException e) {
				player.sendPlaceholderMessage(Main.EXCEEDS_STORE_LIMIT);
			} catch (NotEnoughXpException e) {
				player.sendMessage(ChatColor.RED + "You only have " + playerTotalXp + " xp");
			} catch (NoXpException e) {
				player.sendPlaceholderMessage(Main.NO_XP_DEPOSIT_MESSAGE);
			}
		}
	}

	private void withdraw_points(XpBPlayer player, String amountStr) {
		int playerTotalXp = player.totalXp();
		int amount;
		if (amountStr.equals("max")) {
			amount = Math.min(player.getStoredXp(), Main.MAX_XP_HELD - playerTotalXp);
		} else {
			amount = getAmount(amountStr, player);
		}
		if (amount != -1) {
			try {
				XpController.withdraw_xp(player, amount);
				player.sendPlaceholderMessage(Main.WITHDRAW_MESSAGE);
			} catch (ExceedsXpLimitException e) {
				player.sendPlaceholderMessage(Main.EXCEEDS_HOLD_LIMIT);
			} catch (NotEnoughXpException e) {
				player.sendPlaceholderMessage(ChatColor.RED + "You only have " + "%XP_HELD%"
						+ " xp in the bank");
			} catch (NoXpException e) {
				player.sendPlaceholderMessage(Main.NO_XP_WITHDRAW_MESSAGE);
			}
		}
	}

	private void withdraw_levels(XpBPlayer player, String amountStr) {
		int amount = getAmount(amountStr, player);
		if (amount != -1) {
			try {
				XpController.withdraw_levels(player, amount);
				player.sendPlaceholderMessage(Main.WITHDRAW_MESSAGE);
			} catch (NoXpException e) {
				player.sendPlaceholderMessage(Main.NO_XP_WITHDRAW_MESSAGE);
			} catch (NotEnoughXpException e) {
				player.sendPlaceholderMessage(ChatColor.RED + "You only have enough xp for "
						+ Utils.getMaxLevel(player, Main.xps.get(player.getUniqueId().toString()))
						+ " levels");
			} catch (ExceedsXpLimitException e) {
				player.sendPlaceholderMessage(Main.EXCEEDS_HOLD_LIMIT);
			}
		}
	}

	private void deposit_levels(XpBPlayer player, String amountStr) {
		int amount = getAmount(amountStr, player);
		if (amount != -1) {
			try {
				XpController.deposit_levels(player, amount);
				player.sendPlaceholderMessage(Main.DEPOSIT_MESSAGE);
			} catch (NoXpException e) {
				player.sendPlaceholderMessage(Main.NO_XP_DEPOSIT_MESSAGE);
			} catch (NotEnoughXpException e) {
				player.sendMessage(ChatColor.RED + "You don't have that many levels");
			} catch (ExceedsXpLimitException e) {
				player.sendPlaceholderMessage(Main.EXCEEDS_STORE_LIMIT);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void pay_player(XpBPlayer payer, String payeeName, String amountStr) {
		int amount = getAmount(amountStr, payer);
		OfflinePlayer payee = Bukkit.getOfflinePlayer(payeeName);
		if (amount != -1) {
			if (payee != null) {
				try {
					XpController.pay_player(payer, payee, amount);
					payer.sendPlaceholderMessage(
							ChatColor.YELLOW + "Transfer complete. New balance: " + "%XP_STORED%");
					if (payee.isOnline()) {
						new XpBPlayer(payee.getPlayer()).sendPlaceholderMessage(ChatColor.YELLOW +
								payer.getName()
								+ " has sent you " + amount + " xp. New balance: " + "%XP_STORED%");
					}
				} catch (NotEnoughXpException e) {
					payer.sendPlaceholderMessage(
							ChatColor.RED + "You don't have enough xp to do that! (Balance: " +
									"%XP_STORED" + ")");

				} catch (ExceedsXpLimitException e) {
					payer.sendPlaceholderMessage(Main.EXCEEDS_STORE_LIMIT_TARGET);
				} catch (PlayerNotFoundException e) {
					payer.sendPlaceholderMessage(Main.PLAYER_NOT_FOUND_MESSAGE);
				}
			} else {
				payer.sendPlaceholderMessage(Main.PLAYER_NOT_FOUND_MESSAGE);
			}

		}
	}

	private void asConsole(CommandSender sender, String[] args) {

	}
}
