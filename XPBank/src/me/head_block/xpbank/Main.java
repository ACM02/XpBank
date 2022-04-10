package me.head_block.xpbank;

import java.io.File;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.head_block.xpbank.commands.Xp;
import me.head_block.xpbank.tab_completers.XpTab;
import me.head_block.xpbank.utils.Utils;

public class Main extends JavaPlugin {

	public static HashMap<String,Integer> xps = new HashMap<>();
	public static int MAX_LEVEL;
	public static int MAX_XP;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		new Xp(this);
		new XpTab(this);
		
		File dir = getDataFolder();
		
		xps = (HashMap<String, Integer>) Utils.load(new File(getDataFolder(), "xps.dat"));
		
		if (!dir.exists())
			if (!dir.mkdir())
				System.out.println("[" + getDescription().getName() + "] Could not create directory for plugin");
		if (xps == null) {
			xps = new HashMap<String, Integer>();
		}
		
		FileConfiguration config = this.getConfig();
		config.addDefault("maxXp", 2000000000);
		config.options().copyDefaults(true);
		saveConfig();
		
		MAX_XP = getConfig().getInt("maxXp");
		if (MAX_XP >= 2000000000) {
			MAX_LEVEL = Utils.level(2000000000);
			MAX_XP = 2000000000;
		} else if (MAX_XP < 1) {
			MAX_LEVEL = Utils.level(2000000000);
			MAX_XP = 2000000000;
		} else {
			MAX_LEVEL = Utils.level(MAX_XP);
		}
	}

	@Override
	public void onDisable() {
		Utils.save(xps, new File(getDataFolder(), "xps.dat"));
	}
}
