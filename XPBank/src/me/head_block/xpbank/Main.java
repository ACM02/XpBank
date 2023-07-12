package me.head_block.xpbank;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
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
import me.head_block.xpbank.utils.ConfigManager;
import me.head_block.xpbank.utils.Config_1_13;
import me.head_block.xpbank.utils.Config_1_8;
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
	
	public static String DEPOSIT_MESSAGE = ChatColor.GREEN + "Xp deposited. New balance: " + "%XP_STORED%";
	public static String WITHDRAW_MESSAGE = ChatColor.GREEN + "Xp withdrawn. New balance: " + "%XP_STORED%";
	public static String PLAYER_NOT_FOUND_MESSAGE = ChatColor.RED + "Player not found.";
	public static String XP_STORED_MESSAGE = ChatColor.YELLOW + "You have " + "%XP_STORED%" + " experience points in the bank. (Enough for level " + 
			"%TOTAL_XP_LEVEL%" + ")";
	public static String XP_HELD_MESSAGE = ChatColor.YELLOW + "You are holding " + "%XP_HELD%" + " xp";
	public static String NO_XP_DEPOSIT_MESSAGE = ChatColor.RED + "You don't have any XP to deposit";
	public static String NO_XP_WITHDRAW_MESSAGE = ChatColor.RED + "You don't have any XP to withdraw";
	public static String TOTAL_XP_MESSAGE = ChatColor.YELLOW + "Your total xp held and in the bank is: " + "%TOTAL_XP%";
	
	public static Main instance;
	public static boolean updateAvailable;
	public static String newestVersion = "";
	
	public final String CONFIG_HEADER = "XpBank version " + this.getDescription().getVersion() + "\n"
			+ "Spigot page: https://www.spigotmc.org/resources/xpbank.101132/\n"
			+ "\n"
			+ "----- Placeholders -----\n"
			+ "The following placeholders will work in the GUI config and in messages, along with \"_RAW\" added to the end:\n"
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
			+ "messages.deposit: Message sent to player when they deposit xp \n"
			+ "messages.withdraw: Message sent to player when they withdraw xp\n"
			+ "messages.player-not-found: Message sent to player when the plugin cannot find the player they specified\n"
			+ "messages.xp-stored: Message sent to player saying how much xp they have stored\n"
			+ "messages.xp-held: Message sent to player saying how much xp they are holding\n"
			+ "messages.no-xp-to-deposit: Message sent to player when they try to deposit but have no xp\n"
			+ "messages.no-xp-to-withdraw: Message sent to player when they try to withdraw but have no xp in the bank\n"
			+ "messages.total-xp: Message sent to player saying the total xp they have held and stored\n"
			+ "gui.main-menu.name: The name of the main GUI menu\n"
			+ "gui.deposit-menu.name: The name of the deposit GUI menu\n"
			+ "gui.withdraw-menu.name: The name of the wihtdrawl GUI menu\n"
			+ "gui.<menu>.<value>: All other entries of this format are the respective items held in that menu\n"
			+ "----- More info -----\n"
			+ "For more information visit the XpBank wiki at https://github.com/ACM02/XpBank/wiki";
	
	public static ConfigManager configManager;
	
	/*
	 * TODO
	 * 
	 * Fixes:
	 * Deposit max in GUI is exceeding store limit (Probably in command too) (Seems like only some cases, and hard to debug...)
	 * xpbank info message (New version not being set right because update checker is Async)
	 * 1.8-1.12 version support (Material.GRAY_GLASS_PANE) throws errors because not legacy items
	 * 
	 * Future plans:
	 * More in-depth permissions
	 * Better main page: images(?), uhhhh, I'm bad at graphic design
	 * Shortened command arguments?
	 * Option so users can select if they get sent numbers in levels or points?
	 * Make an economy manager to clean up Xp.java, and the menus (duplicated code)
	 * Back button customization
	 * Better API?
	 * Language files
	 * Even more message config
	 * Comments spaced out in the config for a better look
	 * Better documentation/main page
	 * Pay in xp (GUI)
	 * 
	 * Requested features:
	 * Generic back button in main menu that can be set to run a command specified in config.yml
	 * Option to change positions of items in menus
	 * 
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
		
		String serverVersion = getServerVersion();
		if (serverVersion.contains("1.19") || serverVersion.contains("1.18")|| serverVersion.contains("1.17")|| serverVersion.contains("1.16")|| serverVersion.contains("1.15")|| serverVersion.contains("1.14")|| serverVersion.contains("1.13")) {
			configManager = new Config_1_13();
		} else if (serverVersion.contains("1.8") || serverVersion.contains("1.9") || serverVersion.contains("1.10") || serverVersion.contains("1.11") || serverVersion.contains("1.12")) {
			configManager = new Config_1_8();
		} else {
			configManager = new Config_1_13(); // For future untested versions
		}
		configManager.initConfig();
		
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
		
		newestVersion = getDescription().getVersion();
		new UpdateChecker(this, 101132).getVersion(version -> {
            if (!this.getDescription().getVersion().equals(version)) {
            	newestVersion = version;
                getLogger().info("There is a new update available (" + version + ")! Go to https://www.spigotmc.org/resources/xpbank.101132/ to download the new version.");
        		updateAvailable = true;
            }
        });
		
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

	@Override
	public void onDisable() {
		Utils.save(xps, new File(getDataFolder(), "xps.dat"));
	}
	
	public void reloadValues() {
		configManager.initConfig();
		
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
		DEPOSIT_MESSAGE = ChatColor.translateAlternateColorCodes('&', config.getString("messages.deposit"));
		WITHDRAW_MESSAGE = ChatColor.translateAlternateColorCodes('&', config.getString("messages.withdraw"));
		PLAYER_NOT_FOUND_MESSAGE = ChatColor.translateAlternateColorCodes('&', config.getString("messages.player-not-found"));
		XP_STORED_MESSAGE = ChatColor.translateAlternateColorCodes('&', config.getString("messages.xp-stored"));
		XP_HELD_MESSAGE = ChatColor.translateAlternateColorCodes('&', config.getString("messages.xp-held"));
		NO_XP_DEPOSIT_MESSAGE = ChatColor.translateAlternateColorCodes('&', config.getString("messages.no-xp-to-deposit"));
		NO_XP_WITHDRAW_MESSAGE = ChatColor.translateAlternateColorCodes('&', config.getString("messages.no-xp-to-withdraw"));
		TOTAL_XP_MESSAGE = ChatColor.translateAlternateColorCodes('&', config.getString("messages.total-xp"));
		
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
	
	private static String getServerVersion() {
	    return Bukkit.getBukkitVersion().split("-")[0];
	}
	
	public static String getConfigHeader() {
		return instance.CONFIG_HEADER;
	}
}
