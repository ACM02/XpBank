package me.head_block.xpbank.utils;

import me.head_block.xpbank.Main;

public abstract class ConfigManager {

	public abstract void initConfig();
	
	public static final String CONFIG_HEADER = "XpBank version " + Main.instance.getDescription().getVersion() + "\n"
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
	
}
