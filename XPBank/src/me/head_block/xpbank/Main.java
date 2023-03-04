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
	public static int MAX_LEVEL_STORED = 21099;		// Default max, just saving computation, same value as Utils.level(2000000000)
	public static int MAX_XP_STORED = 2000000000;
	public static int MAX_LEVEL_HELD = 21099;		// Default max, just saving computation, same value as Utils.level(2000000000)
	public static int MAX_XP_HELD = 2000000000;
	
	public static String NO_PERM_MESSAGE;
	public static String IMPROPER_USE_MESSAGE;
	public static boolean GUI_ENABLED = true;
	
	public final String CONFIG_HEADER = "XpBank version " + this.getDescription().getVersion() + "\n"
			+ "Spigot page: https://www.spigotmc.org/resources/xpbank.101132/\n"
			+ "\n"
			+ "----- Config Explanation -----\n"
			+ "maxXpStored: Maximum amount of xp points a user can store in the xp bank. (Whole number > 0 below 2B)\n"
			+ "maxXpHeld: Amount of xp points when the bank will stop adding to the player. (Whole number > 0 below 2B)\n"
			+ "seePlayerBalances: Toggles whether the /xpbal command is enabled\n"
			+ "topXpCommand: Toggles whether the /topxp command is enabled\n"
			+ "guiMenu: Toggles whether the plugin will use the gui menu when /xpbank is used.\n"
			+ "noPermissionMessage: Message sent to players when they run a command they do not have permission to use\n"
			+ "improperUseMessage: Message sent to players when they use improper arguments in a command";
	
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
		
		config.options().header(CONFIG_HEADER);
		
		config.options().copyDefaults(true);
		saveConfig();
		
		MAX_XP_STORED = getConfig().getInt("maxXpStored");
		if (MAX_XP_STORED >= 2000000000) {
			MAX_LEVEL_STORED = 21099;		// Default max, just saving computation, same value as Utils.level(2000000000)
			MAX_XP_STORED = 2000000000;
		} else if (MAX_XP_STORED < 1) {
			MAX_LEVEL_STORED = 21099;		// Default max, just saving computation, same value as Utils.level(2000000000)
			MAX_XP_STORED = 2000000000;
		} else {
			MAX_LEVEL_STORED = Utils.level(MAX_XP_STORED);
		}
		
		MAX_XP_HELD = getConfig().getInt("maxXpHeld");
		if (MAX_XP_HELD >= 2000000000) {
			MAX_LEVEL_HELD = 21099;		// Default max, just saving computation, same value as Utils.level(2000000000)
			MAX_XP_HELD = 2000000000;
		} else if (MAX_XP_HELD < 1) {
			MAX_LEVEL_HELD = 21099;		// Default max, just saving computation, same value as Utils.level(2000000000)
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
		
		// GUI menu config
		//config.addDefault("gui.main-menu.deposit", Utils.createItem(Material.GOLD_INGOT, 1, ChatColor.GREEN + "Deposit XP", ChatColor.YELLOW + "Store up to " + Main.MAX_LEVEL_STORED + " levels"));
		//saveConfig();
		
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
