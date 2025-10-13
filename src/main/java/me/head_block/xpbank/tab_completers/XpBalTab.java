package me.head_block.xpbank.tab_completers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.head_block.xpbank.Main;

public class XpBalTab implements TabCompleter {

	public XpBalTab(Main plugin) {
		plugin.getCommand("xpbal").setTabCompleter(this);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("xpbank.use")) {
			return new ArrayList<String>();
		}
		List<String> toReturn = new ArrayList<String>();
		if (args.length == 1) {
			List<String> args1 = new ArrayList<String>();
			for (Player p : Bukkit.getOnlinePlayers()) {
				args1.add(p.getName());
			}
			for (String a : args1) {
				if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
					toReturn.add(a);
				}
			}
			return toReturn;	
		}
		return toReturn;
	}

}
