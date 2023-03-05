package me.head_block.xpbank.api;

import org.bukkit.OfflinePlayer;

import me.head_block.xpbank.Main;
import me.head_block.xpbank.utils.Utils;

public class XpBankAPI {
	
	/**
	 * Gets the raw number of experience points a user has in the XpBank
	 * @param p Player's balance to retrieve
	 * @return Player p's balance
	 */
	public static int getBalance(OfflinePlayer p) {
		if (Main.xps.containsKey(p.getUniqueId().toString())) {
			return Main.xps.get(p.getUniqueId().toString());
		} else {
			return 0;
		}
	}
	
	/**
	 * Gets the number of experience points a user has in the XpBank as a number of experience levels
	 * @param p Player's balance to retrieve
	 * @return Player p's balance in levels
	 */
	public static int getBalanceLevels(OfflinePlayer p) {
		if (Main.xps.containsKey(p.getUniqueId().toString())) {
			return Utils.level(Main.xps.get(p.getUniqueId().toString()));
		} else {
			return 0;
		}
	}
	
	/**
	 * Sets the XpBank balance of a player
	 * @param p Player's balance to set
	 * @param amount Amount to set the balance to
	 * @return Success status of balance modification
	 */
	public static boolean setBalance(OfflinePlayer p, int amount) {
		Main.xps.put(p.getUniqueId().toString(), amount);
		return true;
	}
	
	/**
	 * Sets the XpBank balance of a player to a given number of levels
	 * @param p Player's balance to set
	 * @param amount Amount of levels to set the balance to
	 * @return Success status of balance modification
	 */
	public static boolean setBalanceLevels(OfflinePlayer p, int amount) {
		Main.xps.put(p.getUniqueId().toString(), Utils.totalXp(amount));
		return true;
	}
	
//	public static int getHeldXp(Player p) {
//		
//	}
//	
//	public static int getHeldXpLevels(Player p) {
//		
//	}
//	
//	public static boolean setHeldXp(Player p) {
//		
//	}
//	
//	public static boolean setHeldXpLevels(Player p) {
//		
//	}

}
