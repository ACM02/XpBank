package me.head_block.xpbank.ui;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.head_block.xpbank.Main;
import me.head_block.xpbank.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class WithdrawMenu {
	
	/*
	 * Functions:
	 * - Deposit 25%, 50%, 75%, 100%
	 * - Deposit 1 level, 5 levels, 10 levels, 15 levels
	 */
	
	public static String INV_NAME;
	public static final int INV_SIZE = 9*2;
	public static ItemStack[] UIinventory;
	
	private static ItemStack withdraw25;
	private static ItemStack withdraw50;
	private static ItemStack withdraw75;
	private static ItemStack withdraw100;
	
	private static ItemStack withdrawMax;
	
	private static ItemStack withdraw1L;
	private static ItemStack withdraw5L;
	private static ItemStack withdraw10L;
	private static ItemStack withdraw15L;
	
	public static void init() {
		INV_NAME = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("gui.withdraw-menu.name"));
		UIinventory = new ItemStack[INV_SIZE];
		
		withdraw25 = loadItem("gui.withdraw-menu.25-percent");
		UIinventory[0] = withdraw25;
		
		withdraw50 = loadItem("gui.withdraw-menu.50-percent");
		UIinventory[1] = withdraw50;
		
		
		withdraw75 = loadItem("gui.withdraw-menu.75-percent");
		UIinventory[2] = withdraw75;
		
		withdraw100 = loadItem("gui.withdraw-menu.100-percent");
		UIinventory[3] = withdraw100;
		
		withdraw1L = loadItem("gui.withdraw-menu.1-level");
		UIinventory[5] = withdraw1L;
		
		withdraw5L = loadItem("gui.withdraw-menu.5-levels");
		UIinventory[6] = withdraw5L;
		
		withdraw10L = loadItem("gui.withdraw-menu.10-levels");
		UIinventory[7] = withdraw10L;
		
		withdraw15L = loadItem("gui.withdraw-menu.15-levels");
		UIinventory[8] = withdraw15L;
		
		withdrawMax = loadItem("gui.withdraw-menu.max");
		UIinventory[4] = withdrawMax;
		
		ItemStack back = new ItemStack(Material.BARRIER);
		ItemMeta iMeta = back.getItemMeta();
		iMeta.setDisplayName("" + ChatColor.RED + ChatColor.BOLD + "Back");
		back.setItemMeta(iMeta);
		UIinventory[INV_SIZE - 1] = back;
		
	}
	
	private static ItemStack loadItem(String path) {
		ItemStack item = Main.instance.getConfig().getItemStack(path).clone();		
		if (item.hasItemMeta()) {
			String name = item.getItemMeta().getDisplayName();
			name = Utils.replacePlaceholders(name);
			name = ChatColor.translateAlternateColorCodes('&', name);
			Utils.setDisplayName(item, name);
		}
		if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
			List<String> lore = item.getItemMeta().getLore();
			for (int i = 0; i < lore.size(); i++) {
				String loreLine = Utils.replacePlaceholders(lore.get(i));
				lore.set(i, ChatColor.translateAlternateColorCodes('&', loreLine));
			}
			Utils.setLore(item, lore);
		}
		return item;
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
			double percentage = 0.00;
			if (clicked.equals(withdraw25)) {	// ------------------ 25% --------------------------
				percentage = 0.25;
			} else if (clicked.equals(withdraw50)) { // ------------------ 50% --------------------------
				percentage = 0.50;
			} else if (clicked.equals(withdraw75)) { // ------------------ 75% --------------------------
				percentage = 0.75;
			} else if (clicked.equals(withdraw100)) { // ------------------ 100% --------------------------
				percentage = 1.00;
			}
			
			if (percentage > 0.00) {
				int amount = 0;
				Utils.checkBalInstance(p);
				if (Main.xps.get(p.getUniqueId().toString()) == 0) {
					p.sendMessage(Utils.replacePlaceholders(Main.NO_XP_WITHDRAW_MESSAGE, p));
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
				p.sendMessage(Utils.replacePlaceholders(Main.WITHDRAW_MESSAGE, p));
				return;
			}

			
			int amount = 0;
			if (clicked.equals(withdraw1L)) { // ------------------ 1 Level --------------------------
				amount = 1;
			} else if (clicked.equals(withdraw15L)) { // ------------------ 15 Levels --------------------------
				amount = 15;
			} else if (clicked.equals(withdraw10L)) { // ------------------ 10 Levels --------------------------
				amount = 10;
			} else if (clicked.equals(withdraw5L)) { // ------------------ 5 Levels --------------------------
				amount = 5;
			}
			
			if (amount > 0) {
				Utils.checkBalInstance(p);
				if (Main.xps.get(p.getUniqueId().toString()) == 0) {
					p.sendMessage(Utils.replacePlaceholders(Main.NO_XP_WITHDRAW_MESSAGE, p));
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
				p.sendMessage(Utils.replacePlaceholders(Main.WITHDRAW_MESSAGE, p));
				return;
			}
			
			if (clicked.equals(withdrawMax)) {
				amount = 0;
				Utils.checkBalInstance(p);
				if (Main.xps.get(p.getUniqueId().toString()) == 0) {
					p.sendMessage(Utils.replacePlaceholders(Main.NO_XP_WITHDRAW_MESSAGE, p));
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
				p.sendMessage(Utils.replacePlaceholders(Main.WITHDRAW_MESSAGE, p));
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
