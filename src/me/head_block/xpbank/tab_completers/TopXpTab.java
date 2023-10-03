package me.head_block.xpbank.tab_completers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import me.head_block.xpbank.Main;

public class TopXpTab implements TabCompleter {

	public TopXpTab(Main plugin) {
		plugin.getCommand("topxp").setTabCompleter(this);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return new ArrayList<String>();
	}

}
