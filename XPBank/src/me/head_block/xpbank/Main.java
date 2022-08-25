package me.head_block.xpbank;

import java.io.File;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.head_block.xpbank.commands.TopXp;
import me.head_block.xpbank.commands.Xp;
import me.head_block.xpbank.commands.XpBal;
import me.head_block.xpbank.listeners.DepositMenuClick;
import me.head_block.xpbank.listeners.MainMenuClick;
import me.head_block.xpbank.listeners.WithdrawMenuClick;
import me.head_block.xpbank.tab_completers.TopXpTab;
import me.head_block.xpbank.tab_completers.XpBalTab;
import me.head_block.xpbank.tab_completers.XpTab;
import me.head_block.xpbank.ui.DepositMenu;
import me.head_block.xpbank.ui.MainMenu;
import me.head_block.xpbank.ui.WithdrawMenu;
import me.head_block.xpbank.utils.Metrics;
import me.head_block.xpbank.utils.UpdateChecker;
import me.head_block.xpbank.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {

	public static HashMap<String,Integer> xps = new HashMap<>();
	public static int MAX_LEVEL_STORED;
	public static int MAX_XP_STORED;
	public static int MAX_LEVEL_HELD;
	public static int MAX_XP_HELD;
	
	public static String NO_PERM_MESSAGE;
	public static String IMPROPER_USE_MESSAGE;
	public static boolean GUI_ENABLED = true;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {		
		File dir = getDataFolder();
		
		xps = (HashMap<String, Integer>) Utils.load(new File(getDataFolder(), "xps.dat"));
		
		if (!dir.exists())
			if (!dir.mkdir())
				System.out.println("[" + getDescription().getName() + "] Could not create directory for plugin");
		if (xps == null) {
			xps = new HashMap<String, Integer>();
		}
		
		FileConfiguration config = this.getConfig();
		config.addDefault("maxXpStored", 2000000000);
		config.addDefault("maxXpHeld", 2000000000);
		config.addDefault("seePlayerBalances", true);
		config.addDefault("topXpCommand", true);
		config.addDefault("guiMenu", true);
		
		config.addDefault("noPermissionMessage", "&cYou don't have permission to do that");
		config.addDefault("improperUseMessage", "&cImproper usage. Try ");
		
		
		config.options().copyDefaults(true);
		saveConfig();
		
		MAX_XP_STORED = getConfig().getInt("maxXpStored");
		if (MAX_XP_STORED >= 2000000000) {
			MAX_LEVEL_STORED = Utils.level(2000000000);
			MAX_XP_STORED = 2000000000;
		} else if (MAX_XP_STORED < 1) {
			MAX_LEVEL_STORED = Utils.level(2000000000);
			MAX_XP_STORED = 2000000000;
		} else {
			MAX_LEVEL_STORED = Utils.level(MAX_XP_STORED);
		}
		
		MAX_XP_HELD = getConfig().getInt("maxXpHeld");
		if (MAX_XP_HELD >= 2000000000) {
			MAX_LEVEL_HELD = Utils.level(2000000000);
			MAX_XP_HELD = 2000000000;
		} else if (MAX_XP_HELD < 1) {
			MAX_LEVEL_HELD = Utils.level(2000000000);
			MAX_XP_HELD = 2000000000;
		} else {
			MAX_LEVEL_HELD = Utils.level(MAX_XP_HELD);
		}
		
		NO_PERM_MESSAGE = ChatColor.translateAlternateColorCodes('&', getConfig().getString("noPermissionMessage"));
		IMPROPER_USE_MESSAGE = ChatColor.translateAlternateColorCodes('&', getConfig().getString("improperUseMessage"));
		GUI_ENABLED = getConfig().getBoolean("guiMenu");
		
		new Xp(this);
		new XpTab(this);
		if (config.getBoolean("seePlayerBalances")) {
			new XpBal(this);
			new XpBalTab(this);
		}
		if (config.getBoolean("topXpCommand")) {
			new TopXp(this);
			new TopXpTab(this);
		}
		
		new UpdateChecker(this, 101132).getVersion(version -> {
            if (!this.getDescription().getVersion().equals(version)) {
                getLogger().info("There is a new update available (" + version + ")! Go to https://www.spigotmc.org/resources/xpbank.101132/ to download the new version.");
            }
        });
		
		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this, 14929);
		
		
		MainMenu.init();
		new MainMenuClick(this);
		DepositMenu.init();
		new DepositMenuClick(this);
		WithdrawMenu.init();
		new WithdrawMenuClick(this);
	}

	@Override
	public void onDisable() {
		Utils.save(xps, new File(getDataFolder(), "xps.dat"));
	}
}
