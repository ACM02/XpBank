package me.head_block.xpbank.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.head_block.xpbank.Main;
import net.md_5.bungee.api.ChatColor;

public class XpBal implements CommandExecutor {

	public XpBal(Main plugin) {
		plugin.getCommand("xpbal").setExecutor(this);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("xpbank.use")) {
			sender.sendMessage(Main.NO_PERM_MESSAGE);
		}
		if (args.length == 1) {
			OfflinePlayer offline = Bukkit.getOfflinePlayer(args[0]);
			if (offline == null) {
				sender.sendMessage(ChatColor.RED + "Player not found");
			} else if (!Main.xps.containsKey(offline.getUniqueId().toString())) {
				sender.sendMessage(ChatColor.RED + "Player has no balance");
			} else if (Main.xps.containsKey(offline.getUniqueId().toString())) {
				sender.sendMessage(ChatColor.YELLOW + offline.getName() + "'s stored xp is: " + Main.xps.get(offline.getUniqueId().toString()));
			}
		} else {
			sender.sendMessage(Main.IMPROPER_USE_MESSAGE + " /xpbal <player>");
		}
		return false;
	}

}
