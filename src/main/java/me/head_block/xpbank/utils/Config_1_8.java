package me.head_block.xpbank.utils;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import me.head_block.xpbank.Main;

public class Config_1_8 extends ConfigManager {
	
	@SuppressWarnings("deprecation")
	@Override
	public void initConfig() {
		FileConfiguration config = Main.instance.getConfig();
		config.addDefault("debug-mode", false);
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
		config.addDefault("messages.deposit", "&aXp deposited. New balance: " + "%XP_STORED%");
		config.addDefault("messages.withdraw", "&aXp withdrawn. New balance: " + "%XP_STORED%");
		config.addDefault("messages.player-not-found", "&cPlayer not found.");
		config.addDefault("messages.xp-stored", "&eYou have " + "%XP_STORED%" + " experience points in the bank. (Enough for level " + 
				"%TOTAL_XP_LEVEL%" + ")");
		config.addDefault("messages.xp-held", "&eYou are holding " + "%XP_HELD%" + " xp");
		config.addDefault("messages.no-xp-to-deposit", "&cYou don't have any XP to deposit");
		config.addDefault("messages.no-xp-to-withdraw", "&cYou don't have any XP to withdraw");
		config.addDefault("messages.total-xp", "&eYour total xp held and in the bank is: " + "%TOTAL_XP%");
		
		config.addDefault("gui.main-menu.name", "XpBank");
		config.addDefault("gui.main-menu.deposit", Utils.createItem(Material.GOLD_INGOT, 1, "&aDeposit XP", "&eStore up to " + "%MAX_LEVEL_STORED%" + " levels"));
		config.addDefault("gui.main-menu.withdraw", Utils.createItem(Material.IRON_INGOT, 1, "&aWithdraw XP", "&eHold up to " +"%MAX_LEVEL_HELD%" + " levels"));
		// config.addDefault("gui.main-menu.xp-stored", Utils.createItem(Material.EXPERIENCE_BOTTLE, 1, "&aXp stored", "&e%XP_STORED%" + "/" + "%MAX_XP_STORED%"));
		// config.addDefault("gui.main-menu.xp-held", Utils.createItem(Material.BRICK, 1, "&aXP held", "&e%XP_HELD%" + "/" + "%MAX_XP_HELD%"));
		
		config.addDefault("gui.main-menu.held-full", Utils.createItem(Material.STAINED_GLASS_PANE, (short) 13, 1, "&eXp held:", "&a%XP_HELD%/%MAX_XP_HELD%"));
		config.addDefault("gui.main-menu.held-empty", Utils.createItem(Material.STAINED_GLASS_PANE, (short) 7, 1, "&eXp held:", "&a%XP_HELD%/%MAX_XP_HELD%"));
		
		config.addDefault("gui.main-menu.stored-full", Utils.createItem(Material.STAINED_GLASS_PANE, (short) 13, 1, "&eXp Stored:", "&a%XP_STORED%/%MAX_XP_STORED%"));
		config.addDefault("gui.main-menu.stored-empty", Utils.createItem(Material.STAINED_GLASS_PANE, (short) 7, 1, "&eXp Stored:", "&a%XP_STORED%/%MAX_XP_STORED%"));
		
		config.addDefault("gui.deposit-menu.name", "Deposit");
		config.addDefault("gui.deposit-menu.25-percent", Utils.createItem(Material.WOOL, (short) 13, 1, "&aDeposit 25%"));
		config.addDefault("gui.deposit-menu.50-percent", Utils.createItem(Material.WOOL, (short) 13, 1, "&aDeposit 50%"));
		config.addDefault("gui.deposit-menu.75-percent", Utils.createItem(Material.WOOL, (short) 13, 1, "&aDeposit 75%"));
		config.addDefault("gui.deposit-menu.100-percent", Utils.createItem(Material.WOOL, (short) 13, 1, "&aDeposit 100%"));
		
		config.addDefault("gui.deposit-menu.max", Utils.createItem(Material.EMERALD_BLOCK, 1, "&aDeposit max"));
		
		config.addDefault("gui.deposit-menu.1-level", Utils.createItem(Material.WOOL, (short) 13, 1, "&aDeposit 1 level"));
		config.addDefault("gui.deposit-menu.5-levels", Utils.createItem(Material.WOOL, (short) 13, 1, "&aDeposit 5 levels"));
		config.addDefault("gui.deposit-menu.10-levels", Utils.createItem(Material.WOOL, (short) 13, 1, "&aDeposit 10 levels"));
		config.addDefault("gui.deposit-menu.15-levels", Utils.createItem(Material.WOOL, (short) 13, 1, "&aDeposit 15 levels"));
		
		
		config.addDefault("gui.withdraw-menu.name", "Withdraw");
		config.addDefault("gui.withdraw-menu.25-percent", Utils.createItem(Material.WOOL, (short) 13, 1, "&aWithdraw 25%"));
		config.addDefault("gui.withdraw-menu.50-percent", Utils.createItem(Material.WOOL, (short) 13, 1, "&aWithdraw 50%"));
		config.addDefault("gui.withdraw-menu.75-percent", Utils.createItem(Material.WOOL, (short) 13, 1, "&aWithdraw 75%"));
		config.addDefault("gui.withdraw-menu.100-percent", Utils.createItem(Material.WOOL, (short) 13, 1, "&aWithdraw 100%"));
		
		config.addDefault("gui.withdraw-menu.max", Utils.createItem(Material.EMERALD_BLOCK, 1, "&aWithdraw max"));
		
		config.addDefault("gui.withdraw-menu.1-level", Utils.createItem(Material.WOOL, (short) 13, 1, "&aWithdraw 1 level"));
		config.addDefault("gui.withdraw-menu.5-levels", Utils.createItem(Material.WOOL, (short) 13, 1, "&aWithdraw 5 levels"));
		config.addDefault("gui.withdraw-menu.10-levels", Utils.createItem(Material.WOOL, (short) 13, 1, "&aWithdraw 10 levels"));
		config.addDefault("gui.withdraw-menu.15-levels", Utils.createItem(Material.WOOL, (short) 13, 1, "&aWithdraw 15 levels"));
		
		
		config.options().header(Main.getConfigHeader());
		
		config.options().copyDefaults(true);
		Main.instance.saveConfig();
	}

}
