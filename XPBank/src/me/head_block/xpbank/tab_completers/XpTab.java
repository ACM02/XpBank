package me.head_block.xpbank.tab_completers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.head_block.xpbank.Main;

public class XpTab implements TabCompleter {

	public XpTab(Main plugin) {
		plugin.getCommand("xpbank").setTabCompleter(this);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("xpbank.use")) {
			return null;
		}
		List<String> toReturn = new ArrayList<String>();
		if (args.length == 1) {
			List<String> args1 = new ArrayList<String>();
			args1.add("deposit");
			args1.add("withdraw");
			args1.add("pay");
			args1.add("help");
			args1.add("xpheld");
			args1.add("xpstored");
			args1.add("totalxp");
			if (sender.hasPermission("xpbank.admin")) {
				args1.add("set");
				args1.add("add");
				args1.add("remove");
				args1.add("get");
				args1.add("adminhelp");
				args1.add("reload");
			}
			for (String a : args1) {
				if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
					toReturn.add(a);
				}
			}
			return toReturn;
			
		} else if (args.length == 2) {
			List<String> args2 = new ArrayList<String>();
			if (args[0].equalsIgnoreCase("deposit") || args[0].equalsIgnoreCase("withdraw"))
				args2.add("max");
			if (args[0].equalsIgnoreCase("pay")) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					args2.add(p.getName());
				}
			}
			if (sender.hasPermission("xpbank.admin") && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add") || 
					args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("get"))) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					args2.add(p.getName());
				}
			}
			for (String a : args2) {
				if (a.toLowerCase().startsWith(args[1].toLowerCase())) {
					toReturn.add(a);
				}
			}
			return toReturn;
		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase("deposit") || args[0].equalsIgnoreCase("withdraw")) {
				try {
					Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					return toReturn;
				}
				List<String> args3 = new ArrayList<String>();
				args3.add("levels");
				args3.add("points");
				for (String a : args3) {
					if (a.toLowerCase().startsWith(args[2].toLowerCase())) {
						toReturn.add(a);
					}
				}
			}
			return toReturn;
		}
		return toReturn;
	}

}
