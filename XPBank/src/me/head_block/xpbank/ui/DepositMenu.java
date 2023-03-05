package me.head_block.xpbank.ui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.head_block.xpbank.Main;
import me.head_block.xpbank.commands.Xp;
import me.head_block.xpbank.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class DepositMenu {
	
	/*
	 * Functions:
	 * - Deposit 25%, 50%, 75%, 100%
	 * - Deposit 1 level, 5 levels, 10 levels, 15 levels
	 */
	
	public static final String INV_NAME = "Deposit";
	public static final int INV_SIZE = 9*2;
	public static ItemStack[] UIinventory;
	
	public static void init() {
		UIinventory = new ItemStack[INV_SIZE];
		
		ItemStack deposit25 = new ItemStack(Material.GREEN_CONCRETE);
		ItemMeta meta = deposit25.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Deposit 25%");
		deposit25.setItemMeta(meta);
		UIinventory[0] = deposit25;
		
		ItemStack deposit50 = new ItemStack(Material.GREEN_CONCRETE);
		meta = deposit50.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Deposit 50%");
		deposit50.setItemMeta(meta);
		UIinventory[1] = deposit50;
		
		ItemStack deposit75 = new ItemStack(Material.GREEN_CONCRETE);
		meta = deposit75.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Deposit 75%");
		deposit75.setItemMeta(meta);
		UIinventory[2] = deposit75;
		
		ItemStack deposit100 = new ItemStack(Material.GREEN_CONCRETE);
		meta = deposit100.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Deposit 100%");
		deposit100.setItemMeta(meta);
		UIinventory[3] = deposit100;
		
		ItemStack deposit1L = new ItemStack(Material.GREEN_WOOL);
		meta = deposit1L.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Deposit 1 level");
		deposit1L.setItemMeta(meta);
		UIinventory[5] = deposit1L;
		
		ItemStack deposit5L = new ItemStack(Material.GREEN_WOOL);
		meta = deposit5L.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Deposit 5 levels");
		deposit5L.setItemMeta(meta);
		UIinventory[6] = deposit5L;
		
		ItemStack deposit10L = new ItemStack(Material.GREEN_WOOL);
		meta = deposit10L.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Deposit 10 levels");
		deposit10L.setItemMeta(meta);
		UIinventory[7] = deposit10L;
		
		ItemStack deposit15L = new ItemStack(Material.GREEN_WOOL);
		meta = deposit5L.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Deposit 15 levels");
		deposit15L.setItemMeta(meta);
		UIinventory[8] = deposit15L;
		
		ItemStack depositMax = new ItemStack(Material.EMERALD_BLOCK);
		meta = depositMax.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Deposit max");
		depositMax.setItemMeta(meta);
		UIinventory[4] = depositMax;
		
		
		ItemStack back = new ItemStack(Material.BARRIER);
		ItemMeta iMeta = back.getItemMeta();
		iMeta.setDisplayName("" + ChatColor.RED + ChatColor.BOLD + "Back");
		back.setItemMeta(iMeta);
		UIinventory[INV_SIZE - 1] = back;
		
	}
	
	public static void openForPlayer(Player p) {
		Inventory toOpen = Bukkit.createInventory(null, INV_SIZE, INV_NAME);
		toOpen.setContents(UIinventory);
		
		p.openInventory(toOpen);
	}
	
	public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
		if (clicked.getType().equals(Material.BARRIER) && slot < INV_SIZE) {
			MainMenu.openForPlayer(p);
		}
		if (slot < INV_SIZE && clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()) {
			String name = clicked.getItemMeta().getDisplayName();
			double percentage = 0.00;
			if (name.contains("25%")) {	// ------------------ 25% --------------------------
				percentage = 0.25;
			} else if (name.contains("50%")) { // ------------------ 50% --------------------------
				percentage = 0.50;
			} else if (name.contains("75%")) { // ------------------ 75% --------------------------
				percentage = 0.75;
			} else if (name.contains("100%")) { // ------------------ 100% --------------------------
				percentage = 1.00;
			}
			
			if (percentage > 0.00) {
				int totalXP = Utils.totalXp(p);
				int points = (int) (totalXP * percentage);
				Utils.checkBalInstance(p);
				if (points == 0) {
					p.sendMessage(Utils.replacePlaceholders(Xp.NO_XP_DEPOSIT_MESSAGE, p));
				} else if ((long) Main.xps.get(p.getUniqueId().toString()) + (long) points > Main.MAX_XP_STORED) {
					p.sendMessage(Utils.replacePlaceholders(Main.EXCEEDS_STORE_LIMIT));
				} else {
					removeXp(points, p, totalXP);
					long oldBal = Main.xps.get(p.getUniqueId().toString());
					Main.xps.put(p.getUniqueId().toString(), (int) (oldBal + points));
					p.sendMessage(Utils.replacePlaceholders(Xp.DEPOSIT_MESSAGE));
				}
				return;
			}
			
			
			int amount = 0;
			if (name.contains("1 level")) { // ------------------ 1 Level --------------------------
				amount = 1;
			} else if (name.contains("15 levels")) { // ------------------ 15 Levels --------------------------
				amount = 15;
			} else if (name.contains("10 levels")) { // ------------------ 10 Levels --------------------------
				amount = 10;
			} else if (name.contains("5 levels")) { // ------------------ 5 Levels --------------------------
				amount = 5;
			}
			
			if (amount > 0) {
				Utils.checkBalInstance(p);
				if ((long) Main.xps.get(p.getUniqueId().toString()) + (long) Utils.totalXp(amount) > Main.MAX_XP_STORED) {
					p.sendMessage(Utils.replacePlaceholders(Main.EXCEEDS_STORE_LIMIT, p));
				} else if (p.getLevel() < amount) {
					p.sendMessage(ChatColor.RED + "You don't have that many levels");
				} else {
					int xpToLose = Utils.totalXp(p.getLevel()) - Utils.totalXp(p.getLevel() - amount);
					removeXp(xpToLose, p, Utils.totalXp(p));
					int oldBal = Main.xps.get(p.getUniqueId().toString());
					Main.xps.put(p.getUniqueId().toString(), oldBal + xpToLose);
					p.sendMessage(Utils.replacePlaceholders(Xp.DEPOSIT_MESSAGE, p));
				}
				return;
			}
			
			if (name.contains("max")) {
				int totalXP = Utils.totalXp(p);
				if (totalXP == 0) {
					p.sendMessage(Utils.replacePlaceholders(Xp.NO_XP_DEPOSIT_MESSAGE, p));
					return;
				}
				int points = totalXP;
				if (Main.xps.get(p.getUniqueId().toString()) + points >= Main.MAX_XP_STORED) {
					points = Main.MAX_XP_STORED - Main.xps.get(p.getUniqueId().toString());
				}
				if (points == 0) {
					p.sendMessage(Utils.replacePlaceholders(Main.EXCEEDS_STORE_LIMIT, p));
					return;
				}
				Utils.checkBalInstance(p);
				if ((long) Main.xps.get(p.getUniqueId().toString()) + (long) points > Main.MAX_XP_STORED) {
					p.sendMessage(Utils.replacePlaceholders(Main.EXCEEDS_STORE_LIMIT, p));
				} else {
					removeXp(points, p, totalXP);
					long oldBal = Main.xps.get(p.getUniqueId().toString());
					Main.xps.put(p.getUniqueId().toString(), (int) (oldBal + points));
					p.sendMessage(Utils.replacePlaceholders(Xp.DEPOSIT_MESSAGE, p));
				}
				return;
			}
		}
	}
	
	private static void removeXp(int xp, Player p, int playerTotalXp) {
		int newTotal = playerTotalXp - xp;
		int newLevel = Utils.level(newTotal);
		float newXp = Utils.xp(newTotal, newLevel);
		if (newXp == 1) {
			newLevel++;
			newXp = 0;
		}
		p.setLevel(newLevel);
		p.setExp(newXp);
	}
}
