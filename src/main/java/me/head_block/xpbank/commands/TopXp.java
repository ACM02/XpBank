package me.head_block.xpbank.commands;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.head_block.xpbank.Main;
import me.head_block.xpbank.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class TopXp implements CommandExecutor {

	public TopXp(Main plugin) {
		plugin.getCommand("topxp").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("xpbank.use")) {
			sender.sendMessage(Main.NO_PERM_MESSAGE);
			return false;
		}
		if (args.length == 0) {
			ArrayList<String> balances = getTopBals(10);
			String message = ChatColor.YELLOW + "Top balances: ";
			for (int i = 0; i < balances.size(); i++) {
				message += "\n  " + ChatColor.GREEN + "#" + (i + 1) + " " + Bukkit.getOfflinePlayer(UUID.fromString(balances.get(i))).getName()
						+ ": " + Main.xps.get(balances.get(i));
			}
			sender.sendMessage(message);
		} else {
			int amount;
			try {
				amount = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Invalid amount.");
				return false;
			}
			ArrayList<String> balances = getTopBals(amount);
			String message = ChatColor.YELLOW + "Top balances: ";
			for (int i = 0; i < balances.size(); i++) {
				message += "\n  " + ChatColor.GREEN + "#" + (i + 1) + " " + Bukkit.getOfflinePlayer(UUID.fromString(balances.get(i))).getName()
						+ ": " + Main.xps.get(balances.get(i));
			}
			sender.sendMessage(message);
		}
		return false;
	}

	/**
	 * Creates a sorted list of the top balances
	 * @return the sorted list
	 */
	public static ArrayList<String> getTopBals(int numBals) {
		ArrayList<String> toReturn = new ArrayList<>();
		for (String s : Main.xps.keySet()) {
			toReturn.add(s);
		}
		Utils.mergeSort(toReturn);
		while (toReturn.size() > numBals) {
			toReturn.remove(toReturn.size()-1);
		}
		return toReturn;
	}
	
}
