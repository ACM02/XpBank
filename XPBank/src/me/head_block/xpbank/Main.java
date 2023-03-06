package me.head_block.xpbank;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.head_block.xpbank.commands.TopXp;
import me.head_block.xpbank.commands.Xp;
import me.head_block.xpbank.commands.XpBal;
import me.head_block.xpbank.listeners.DepositMenuClick;
import me.head_block.xpbank.listeners.MainMenuClick;
import me.head_block.xpbank.listeners.UpdateAvailableMessage;
import me.head_block.xpbank.listeners.WithdrawMenuClick;
import me.head_block.xpbank.placeholders.XpPlaceHolder;
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
	
	public static String EXCEEDS_HOLD_LIMIT;
	public static String EXCEEDS_STORE_LIMIT_TARGET;
	public static String EXCEEDS_STORE_LIMIT;
	
	
	public static Main instance;
	public static boolean updateAvailable;
	
	public final String CONFIG_HEADER = "XpBank version " + this.getDescription().getVersion() + "\n"
			+ "Spigot page: https://www.spigotmc.org/resources/xpbank.101132/\n"
			+ "\n"
			+ "----- Placeholders -----\n"
			+ "The following placeholders will work in the GUI config and in messages:\n"
			+ "- %MAX_LEVEL_HELD%\n"
			+ "- %MAX_LEVEL_STORED%\n"
			+ "- %MAX_XP_HELD%\n"
			+ "- %MAX_XP_STORED%\n"
			+ "- %XP_HELD\n"
			+ "- %XP_STORED%"
			+ "- %TOTAL_XP%\n"
			+ "- %TOTAL_XP_LEVEL%\n"
			+ "----- Config Explanation -----\n"
			+ "maxXpStored: Maximum amount of xp points a user can store in the xp bank. (Whole number > 0 below 2B)\n"
			+ "maxXpHeld: Amount of xp points when the bank will stop adding to the player. (Whole number > 0 below 2B)\n"
			+ "seePlayerBalances: Toggles whether the /xpbal command is enabled\n"
			+ "topXpCommand: Toggles whether the /topxp command is enabled\n"
			+ "guiMenu: Toggles whether the plugin will use the gui menu when /xpbank is used.\n"
			+ "update-message: Toggles whether the plugin will send a message to admins saying an update is available for the plugin\n"
			+ "notify-on-admin-balance-change: Toggles whether users are sent a message when an admin modifies their balance with admin commands\n"
			+ "messages.noPermission: Message sent to players when they run a command they do not have permission to use\n"
			+ "messages.improperUse: Message sent to players when they use improper arguments in a command\n"
			+ "messages.exeeds-hold-limit: Message sent if a player tries to hold more than the max xp\n"
			+ "messages.exeeds-store-limit-target: Message sent to player if they're trying to give someone xp but it would push them over the max\n"
			+ "messages.exceeds-store-limit: Message sent to player if a player tries to store more than the max xp\n"
			+ "gui.main-menu.name: The name of the main GUI menu\n"
			+ "gui.deposit-menu.name: The name of the deposit GUI menu\n"
			+ "gui.withdraw-menu.name: The name of the wihtdrawl GUI menu\n"
			+ "gui.<menu>.<value>: All other entries of this format are the respective items held in that menu\n"
			+ "----- More info -----\n"
			+ "For more information visit the XpBank wiki at https://github.com/ACM02/XpBank/wiki";
	
	/*
	 * TODO
	 * 
	 * Fixes:
	 * Deposit max in GUI is exceeding store limit (Probably in command too) (Seems like only some cases, and hard to debug...)
	 * 
	 * Future plans:
	 * Permissions page in Wiki
	 * xpbank info command which will give plugin version and link to wiki
	 * Make an economy manager to clean up Xp.java, and the menus (duplicated code)
	 * Back button customization
	 * Add blocks to UI indicating stored and held xp
	 * Better API?
	 * Language files
	 * Even more message config
	 * Comments spaced out in the config for a better look
	 * Better documentation/main page
	 * Pay in xp (GUI)
	 * K,M,B text formatting
	 */
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {	
		instance = this;
		File dir = getDataFolder();
		
		xps = (HashMap<String, Integer>) Utils.load(new File(getDataFolder(), "xps.dat"));
		
		if (!dir.exists())
			if (!dir.mkdir())
				System.out.println("[" + getDescription().getName() + "] Could not create directory for plugin");
		if (xps == null) {
			xps = new HashMap<String, Integer>();
		}
		
		initConfig();
		
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
		
		NO_PERM_MESSAGE = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.noPermission"));
		IMPROPER_USE_MESSAGE = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.improperUse"));
		EXCEEDS_HOLD_LIMIT = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.exeeds-hold-limit"));
		EXCEEDS_STORE_LIMIT_TARGET = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.exeeds-store-limit-target"));
		EXCEEDS_STORE_LIMIT = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.exceeds-store-limit"));
		
		
		GUI_ENABLED = getConfig().getBoolean("guiMenu");
		
		new Xp(this);
		new XpTab(this);
		FileConfiguration config = this.getConfig();
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
        		updateAvailable = true;
            }
        });
		
		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this, 14929);
		
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new XpPlaceHolder().register();
        }
		
		MainMenu.init();
		new MainMenuClick(this);
		DepositMenu.init();
		new DepositMenuClick(this);
		WithdrawMenu.init();
		new WithdrawMenuClick(this);
		new UpdateAvailableMessage(this);
	}
	
	private void initConfig() {
		FileConfiguration config = this.getConfig();
		config.addDefault("maxXpStored", 2000000000);
		config.addDefault("maxXpHeld", 2000000000);
		config.addDefault("seePlayerBalances", true);
		config.addDefault("topXpCommand", true);
		config.addDefault("guiMenu", true);
		config.addDefault("update-message", true);
		config.addDefault("notify-on-admin-balance-change", true);
		
		config.addDefault("messages.noPermission", "&cYou don't have permission to do that");
		config.addDefault("messages.improperUse", "&cImproper usage. Try ");
		config.addDefault("messages.exeeds-hold-limit", "&cInvalid amount. That exceeds the maximum xp that can be held. (" + "%MAX_LEVEL_HELD%" + " levels/" + "%MAX_XP_HELD%" + " points)");
		config.addDefault("messages.exeeds-store-limit-target", "&cInvalid amount. That exceeds the maximum xp that can be held by the target. (" + "%MAX_LEVEL_HELD%" + " levels/" + "%MAX_XP_HELD%" + " points)");
		config.addDefault("messages.exceeds-store-limit", "&cInvalid amount. That exceeds the maximum xp that can be stored. (" + "%MAX_LEVEL_STORED%" + " levels/" + "%MAX_XP_STORED%" + " points)");
		
		/*	GUI menu config
		 *  Placeholders
		 *  - %MAX_LEVEL_HELD%
		 *  - %MAX_LEVEL_STORED%
		 *  - %MAX_XP_HELD%
		 *  - %MAX_XP_STORED%
		 *  - %XP_STORED%
		 *  - %XP_HELD%
		 *  
		 */
		config.addDefault("gui.main-menu.name", "XpBank");
		config.addDefault("gui.main-menu.deposit", Utils.createItem(Material.GOLD_INGOT, 1, "&aDeposit XP", "&eStore up to " + "%MAX_LEVEL_STORED%" + " levels"));
		config.addDefault("gui.main-menu.withdraw", Utils.createItem(Material.IRON_INGOT, 1, "&aWithdraw XP", "&eHold up to " +"%MAX_LEVEL_HELD%" + " levels"));
		config.addDefault("gui.main-menu.xp-stored", Utils.createItem(Material.EXPERIENCE_BOTTLE, 1, "&aXp stored", "&e%XP_STORED%" + "/" + "%MAX_XP_STORED%"));
		config.addDefault("gui.main-menu.xp-held", Utils.createItem(Material.BRICK, 1, "&aXP held", "&e%XP_HELD%" + "/" + "%MAX_XP_HELD%"));
		
		config.addDefault("gui.deposit-menu.name", "Deposit");
		config.addDefault("gui.deposit-menu.25-percent", Utils.createItem(Material.GREEN_CONCRETE, 1, "&aDeposit 25%"));
		config.addDefault("gui.deposit-menu.50-percent", Utils.createItem(Material.GREEN_CONCRETE, 1, "&aDeposit 50%"));
		config.addDefault("gui.deposit-menu.75-percent", Utils.createItem(Material.GREEN_CONCRETE, 1, "&aDeposit 75%"));
		config.addDefault("gui.deposit-menu.100-percent", Utils.createItem(Material.GREEN_CONCRETE, 1, "&aDeposit 100%"));
		
		config.addDefault("gui.deposit-menu.max", Utils.createItem(Material.EMERALD_BLOCK, 1, "&aDeposit max"));
		
		config.addDefault("gui.deposit-menu.1-level", Utils.createItem(Material.GREEN_WOOL, 1, "&aDeposit 1 level"));
		config.addDefault("gui.deposit-menu.5-levels", Utils.createItem(Material.GREEN_WOOL, 1, "&aDeposit 5 levels"));
		config.addDefault("gui.deposit-menu.10-levels", Utils.createItem(Material.GREEN_WOOL, 1, "&aDeposit 10 levels"));
		config.addDefault("gui.deposit-menu.15-levels", Utils.createItem(Material.GREEN_WOOL, 1, "&aDeposit 15 levels"));
		
		
		config.addDefault("gui.withdraw-menu.name", "Withdraw");
		config.addDefault("gui.withdraw-menu.25-percent", Utils.createItem(Material.GREEN_CONCRETE, 1, "&aWithdraw 25%"));
		config.addDefault("gui.withdraw-menu.50-percent", Utils.createItem(Material.GREEN_CONCRETE, 1, "&aWithdraw 50%"));
		config.addDefault("gui.withdraw-menu.75-percent", Utils.createItem(Material.GREEN_CONCRETE, 1, "&aWithdraw 75%"));
		config.addDefault("gui.withdraw-menu.100-percent", Utils.createItem(Material.GREEN_CONCRETE, 1, "&aWithdraw 100%"));
		
		config.addDefault("gui.withdraw-menu.max", Utils.createItem(Material.EMERALD_BLOCK, 1, "&aWithdraw max"));
		
		config.addDefault("gui.withdraw-menu.1-level", Utils.createItem(Material.GREEN_WOOL, 1, "&aWithdraw 1 level"));
		config.addDefault("gui.withdraw-menu.5-levels", Utils.createItem(Material.GREEN_WOOL, 1, "&aWithdraw 5 levels"));
		config.addDefault("gui.withdraw-menu.10-levels", Utils.createItem(Material.GREEN_WOOL, 1, "&aWithdraw 10 levels"));
		config.addDefault("gui.withdraw-menu.15-levels", Utils.createItem(Material.GREEN_WOOL, 1, "&aWithdraw 15 levels"));
		
		
		config.options().header(CONFIG_HEADER);
		
		config.options().copyDefaults(true);
		saveConfig();
	}

	@Override
	public void onDisable() {
		Utils.save(xps, new File(getDataFolder(), "xps.dat"));
	}
	
	public void reloadValues() {
		initConfig();
		
		FileConfiguration config = this.getConfig();
		MAX_XP_STORED = config.getInt("maxXpStored");
		if (MAX_XP_STORED >= 2000000000) {
			MAX_LEVEL_STORED = 21099;		// Default max, just saving computation, same value as Utils.level(2000000000)
			MAX_XP_STORED = 2000000000;
		} else if (MAX_XP_STORED < 1) {
			MAX_LEVEL_STORED = 21099;		// Default max, just saving computation, same value as Utils.level(2000000000)
			MAX_XP_STORED = 2000000000;
		} else {
			MAX_LEVEL_STORED = Utils.level(MAX_XP_STORED);
		}
		
		MAX_XP_HELD = config.getInt("maxXpHeld");
		if (MAX_XP_HELD >= 2000000000) {
			MAX_LEVEL_HELD = 21099;		// Default max, just saving computation, same value as Utils.level(2000000000)
			MAX_XP_HELD = 2000000000;
		} else if (MAX_XP_HELD < 1) {
			MAX_LEVEL_HELD = 21099;		// Default max, just saving computation, same value as Utils.level(2000000000)
			MAX_XP_HELD = 2000000000;
		} else {
			MAX_LEVEL_HELD = Utils.level(MAX_XP_HELD);
		}
		
		NO_PERM_MESSAGE = ChatColor.translateAlternateColorCodes('&', config.getString("messages.noPermission"));
		IMPROPER_USE_MESSAGE = ChatColor.translateAlternateColorCodes('&', config.getString("messages.improperUse"));
		EXCEEDS_HOLD_LIMIT = ChatColor.translateAlternateColorCodes('&', config.getString("messages.exeeds-hold-limit"));
		EXCEEDS_STORE_LIMIT_TARGET = ChatColor.translateAlternateColorCodes('&', config.getString("messages.exeeds-store-limit-target"));
		EXCEEDS_STORE_LIMIT = ChatColor.translateAlternateColorCodes('&', config.getString("messages.exceeds-store-limit"));
		updateAvailable = config.getBoolean("update-message");
		
		GUI_ENABLED = config.getBoolean("guiMenu");
	}

	public static void reloadPlugin() {
		instance.reloadConfig();
		instance.reloadValues();
		DepositMenu.init();
		MainMenu.init();
		WithdrawMenu.init();
	}
}
