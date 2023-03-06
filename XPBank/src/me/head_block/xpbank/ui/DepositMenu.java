package me.head_block.xpbank.ui;

import java.util.List;

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
	
	public static String INV_NAME;
	public static final int INV_SIZE = 9*2;
	public static ItemStack[] UIinventory;
	
	private static ItemStack deposit25;
	private static ItemStack deposit50;
	private static ItemStack deposit75;
	private static ItemStack deposit100;
	
	private static ItemStack depositMax;
	
	private static ItemStack deposit1L;
	private static ItemStack deposit5L;
	private static ItemStack deposit10L;
	private static ItemStack deposit15L;
	
	public static void init() {
		INV_NAME = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("gui.deposit-menu.name"));
		UIinventory = new ItemStack[INV_SIZE];
		
		
		deposit25 = loadItem("gui.deposit-menu.25-percent");
		UIinventory[0] = deposit25;
		
		deposit50 = loadItem("gui.deposit-menu.50-percent");
		UIinventory[1] = deposit50;
		
		
		deposit75 = loadItem("gui.deposit-menu.75-percent");
		UIinventory[2] = deposit75;
		
		deposit100 = loadItem("gui.deposit-menu.75-percent");
		UIinventory[3] = deposit100;
		
		deposit1L = loadItem("gui.deposit-menu.1-level");
		UIinventory[5] = deposit1L;
		
		deposit5L = loadItem("gui.deposit-menu.5-levels");
		UIinventory[6] = deposit5L;
		
		deposit10L = loadItem("gui.deposit-menu.10-levels");
		UIinventory[7] = deposit10L;
		
		deposit15L = loadItem("gui.deposit-menu.15-levels");
		UIinventory[8] = deposit15L;
		
		depositMax = loadItem("gui.deposit-menu.max");
		UIinventory[4] = depositMax;
		
		
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
			if (clicked.equals(deposit25)) {	// ------------------ 25% --------------------------
				percentage = 0.25;
			} else if (clicked.equals(deposit50)) { // ------------------ 50% --------------------------
				percentage = 0.50;
			} else if (clicked.equals(deposit75)) { // ------------------ 75% --------------------------
				percentage = 0.75;
			} else if (clicked.equals(deposit100)) { // ------------------ 100% --------------------------
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
			if (clicked.equals(deposit1L)) { // ------------------ 1 Level --------------------------
				amount = 1;
			} else if (clicked.equals(deposit15L)) { // ------------------ 15 Levels --------------------------
				amount = 15;
			} else if (clicked.equals(deposit10L)) { // ------------------ 10 Levels --------------------------
				amount = 10;
			} else if (clicked.equals(deposit5L)) { // ------------------ 5 Levels --------------------------
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
			
			if (clicked.equals(depositMax)) {
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
