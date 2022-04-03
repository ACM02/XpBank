package me.head_block.xpbank.tab_completers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import me.head_block.xpbank.Main;

public class XpTab implements TabCompleter {

	public XpTab(Main plugin) {
		plugin.getCommand("xpbank").setTabCompleter(this);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		if (args.length == 1) {
			List<String> args1 = new ArrayList<String>();
			args1.add("deposit");
			args1.add("withdraw");
			for (String a : args1) {
				if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
					toReturn.add(a);
				}
			}
			return toReturn;
			
		} else if (args.length == 2) {
			List<String> args2 = new ArrayList<String>();
			args2.add("all");
			for (String a : args2) {
				if (a.toLowerCase().startsWith(args[1].toLowerCase())) {
					toReturn.add(a);
				}
			}
			return toReturn;
		} else if (args.length == 3) {
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
			return toReturn;
		}
		return toReturn;
	}

}
