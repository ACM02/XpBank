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

public class WithdrawMenu {
	
	/*
	 * Functions:
	 * - Deposit 25%, 50%, 75%, 100%
	 * - Deposit 1 level, 5 levels, 10 levels, 15 levels
	 */
	
	public static final String INV_NAME = "Withdraw";
	public static final int INV_SIZE = 9*2;
	public static ItemStack[] UIinventory;
	
	public static void init() {
		UIinventory = new ItemStack[INV_SIZE];
		
		ItemStack withdraw25 = new ItemStack(Material.GREEN_CONCRETE);
		ItemMeta meta = withdraw25.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Withdraw 25%");
		withdraw25.setItemMeta(meta);
		UIinventory[0] = withdraw25;
		
		ItemStack withdraw50 = new ItemStack(Material.GREEN_CONCRETE);
		meta = withdraw50.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Withdraw 50%");
		withdraw50.setItemMeta(meta);
		UIinventory[1] = withdraw50;
		
		ItemStack withdraw75 = new ItemStack(Material.GREEN_CONCRETE);
		meta = withdraw75.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Withdraw 75%");
		withdraw75.setItemMeta(meta);
		UIinventory[2] = withdraw75;
		
		ItemStack withdraw100 = new ItemStack(Material.GREEN_CONCRETE);
		meta = withdraw100.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Withdraw 100%");
		withdraw100.setItemMeta(meta);
		UIinventory[3] = withdraw100;
		
		ItemStack withdraw1L = new ItemStack(Material.GREEN_WOOL);
		meta = withdraw1L.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Withdraw 1 level");
		withdraw1L.setItemMeta(meta);
		UIinventory[5] = withdraw1L;
		
		ItemStack withdraw5L = new ItemStack(Material.GREEN_WOOL);
		meta = withdraw5L.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Withdraw 5 levels");
		withdraw5L.setItemMeta(meta);
		UIinventory[6] = withdraw5L;
		
		ItemStack withdraw10L = new ItemStack(Material.GREEN_WOOL);
		meta = withdraw10L.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Withdraw 10 levels");
		withdraw10L.setItemMeta(meta);
		UIinventory[7] = withdraw10L;
		
		ItemStack withdraw15L = new ItemStack(Material.GREEN_WOOL);
		meta = withdraw5L.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Withdraw 15 levels");
		withdraw15L.setItemMeta(meta);
		UIinventory[8] = withdraw15L;
		
		ItemStack withdrawMax = new ItemStack(Material.EMERALD_BLOCK);
		meta = withdrawMax.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Withdraw max");
		withdrawMax.setItemMeta(meta);
		UIinventory[4] = withdrawMax;
		
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
				int amount = 0;
				Utils.checkBalInstance(p);
				if (Main.xps.get(p.getUniqueId().toString()) == 0) {
					p.sendMessage(Utils.replacePlaceholders(Xp.NO_XP_WITHDRAW_MESSAGE, p));
					return;
				}
				int playerTotalXp = Utils.totalXp(p);
				amount = (int) (Main.xps.get(p.getUniqueId().toString()) * percentage);
				if (amount > Main.xps.get(p.getUniqueId().toString())) {
					p.sendMessage(ChatColor.RED + "You only have " + Main.xps.get(p.getUniqueId().toString()) + " xp in the bank");
					return;
				}
				if ((long) playerTotalXp + (long) amount > Main.MAX_XP_HELD) {
					p.sendMessage(Utils.replacePlaceholders(Main.EXCEEDS_HOLD_LIMIT, p));
					return;
				} 
				addXp(amount, p, playerTotalXp);
				int oldBal = Main.xps.get(p.getUniqueId().toString());
				Main.xps.put(p.getUniqueId().toString(), oldBal - amount);
				p.sendMessage(Utils.replacePlaceholders(Xp.WITHDRAW_MESSAGE, p));
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
				if (Main.xps.get(p.getUniqueId().toString()) == 0) {
					p.sendMessage(Utils.replacePlaceholders(Xp.NO_XP_WITHDRAW_MESSAGE, p));
					return;
				}
				int playerTotalXp = Utils.totalXp(p);
				int xpToAdd = Utils.totalXp(p.getLevel() + amount) - Utils.totalXp(p.getLevel());
				if ((long) playerTotalXp + (long) xpToAdd > Main.MAX_XP_HELD) {
					p.sendMessage(Utils.replacePlaceholders(Main.EXCEEDS_HOLD_LIMIT, p));
					return;
				}
				if (xpToAdd > Main.xps.get(p.getUniqueId().toString())) {
					p.sendMessage(ChatColor.RED + "You only have enough xp for " + Utils.getMaxLevel(p, Main.xps.get(p.getUniqueId().toString())) + " levels");
					return;
				}
				addXp(xpToAdd, p, playerTotalXp);
				int oldBal = Main.xps.get(p.getUniqueId().toString());
				Main.xps.put(p.getUniqueId().toString(), oldBal - xpToAdd);
				p.sendMessage(Utils.replacePlaceholders(Xp.WITHDRAW_MESSAGE, p));
				return;
			}
			
			if (name.contains("max")) {
				amount = 0;
				Utils.checkBalInstance(p);
				if (Main.xps.get(p.getUniqueId().toString()) == 0) {
					p.sendMessage(Utils.replacePlaceholders(Xp.NO_XP_WITHDRAW_MESSAGE, p));
					return;
				}
				int playerTotalXp = Utils.totalXp(p);
				if (playerTotalXp >= Main.MAX_XP_HELD) {
					p.sendMessage(Utils.replacePlaceholders(Main.EXCEEDS_HOLD_LIMIT, p));
					return;
				}
				amount = Main.xps.get(p.getUniqueId().toString());
				if (playerTotalXp + amount >= Main.MAX_XP_HELD) {
					amount = Main.MAX_XP_HELD - playerTotalXp;
				}
				if (amount > Main.xps.get(p.getUniqueId().toString())) {
					p.sendMessage(ChatColor.RED + "You only have " + Main.xps.get(p.getUniqueId().toString()) + " xp in the bank");
					return;
				}
				if ((long) playerTotalXp + (long) amount > Main.MAX_XP_HELD) {
					p.sendMessage(Utils.replacePlaceholders(Main.EXCEEDS_HOLD_LIMIT, p));
					return;
				} 
				addXp(amount, p, playerTotalXp);
				int oldBal = Main.xps.get(p.getUniqueId().toString());
				Main.xps.put(p.getUniqueId().toString(), oldBal - amount);
				p.sendMessage(Utils.replacePlaceholders(Xp.WITHDRAW_MESSAGE, p));
				return;
			}
			
		}
	}
	
	private static void addXp(int xp, Player p, int playerTotalXp) {
		int newTotal = playerTotalXp + xp;
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
