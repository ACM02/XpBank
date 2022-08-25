package me.head_block.xpbank.ui;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.head_block.xpbank.Main;
import me.head_block.xpbank.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class MainMenu {
	
	/*
	 * Sub-pages:
	 * - Deposit
	 * - Withdraw
	 * - Pay
	 * 
	 * Information:
	 * - XP held
	 * - XP in bank
	 * 
	 */
	
	public static final String INV_NAME = "Xp Bank";
	public static final int INV_SIZE = 9*4;
	public static ItemStack[] UIinventory;
	
	public static void init() {
		UIinventory = new ItemStack[INV_SIZE];
		
		ItemStack deposit = new ItemStack(Material.GOLD_INGOT);
		ItemMeta meta = deposit.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Deposit XP");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.YELLOW + "Store up to " + Main.MAX_LEVEL_STORED + " levels");
		meta.setLore(lore);
		deposit.setItemMeta(meta);
		UIinventory[9*1 + 3] = deposit;
		
		ItemStack withdraw = new ItemStack(Material.IRON_INGOT);
		meta = withdraw.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Withdraw XP");
		lore = new ArrayList<String>();
		lore.add(ChatColor.YELLOW + "Hold up to " + Main.MAX_LEVEL_HELD + " levels");
		meta.setLore(lore);
		withdraw.setItemMeta(meta);
		UIinventory[9*1 + 5] = withdraw;
		
		//ItemStack pay = new ItemStack(Material.REDSTONE);
		//meta = pay.getItemMeta();
		//meta.setDisplayName(ChatColor.GREEN + "Pay");
		//lore = new ArrayList<String>();
		//lore.add(ChatColor.YELLOW + "Pay another player with XP");
		//meta.setLore(lore);
		//pay.setItemMeta(meta);
		//UIinventory[9*1 + 6] = pay;
		
	}
	
	public static void openForPlayer(Player p) {
		Inventory toOpen = Bukkit.createInventory(null, INV_SIZE, INV_NAME);
		toOpen.setContents(UIinventory);
		
		ItemStack stored = new ItemStack(Material.EXPERIENCE_BOTTLE);
		ItemMeta meta = stored.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Xp stored");
		ArrayList<String> lore = new ArrayList<String>();
		checkBalInstance(p);
		lore.add(ChatColor.YELLOW + "" + Main.xps.get(p.getUniqueId().toString()) + "/" + Main.MAX_XP_STORED);
		meta.setLore(lore);
		stored.setItemMeta(meta);
		toOpen.setItem(9*3 + 3, stored);
		
		ItemStack held = new ItemStack(Material.BRICK);
		meta = held.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "XP held");
		lore = new ArrayList<String>();
		lore.add("" + ChatColor.YELLOW + Utils.totalXp(p) + "/" + Main.MAX_XP_HELD);
		meta.setLore(lore);
		held.setItemMeta(meta);
		toOpen.setItem(9*3 + 5, held);
		
		p.openInventory(toOpen);
	}
	
	public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
		if (clicked.getType().equals(Material.GOLD_INGOT) && slot < INV_SIZE) {
			DepositMenu.openForPlayer(p);
		} else if (clicked.getType().equals(Material.IRON_INGOT) && slot < INV_SIZE) {
			WithdrawMenu.openForPlayer(p);
		}
	}
	
	private static void checkBalInstance(OfflinePlayer p) {
		if (!Main.xps.containsKey(p.getUniqueId().toString())) {
			Main.xps.put(p.getUniqueId().toString(), 0);
		}
	}
	
}
