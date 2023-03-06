package me.head_block.xpbank.ui;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
	
	public static String INV_NAME;
	public static final int INV_SIZE = 9*4;
	public static ItemStack[] UIinventory;
	
	public static void init() {
		INV_NAME = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("gui.main-menu.name"));
		
		UIinventory = new ItemStack[INV_SIZE];
		
		ItemStack deposit = Main.instance.getConfig().getItemStack("gui.main-menu.deposit").clone();
		if (deposit.hasItemMeta()) {
			String name = deposit.getItemMeta().getDisplayName();
			deposit.getItemMeta().setDisplayName(Utils.replacePlaceholders(name));
		}
		if (deposit.hasItemMeta() && deposit.getItemMeta().hasLore()) {
			List<String> lore = deposit.getItemMeta().getLore();
			for (int i = 0; i < lore.size(); i++) {
				lore.set(i, Utils.replacePlaceholders(lore.get(i)));
			}
			Utils.setLore(deposit, lore);
		}
		//ItemStack deposit = Utils.createItem(Material.GOLD_INGOT, 1, ChatColor.GREEN + "Deposit XP", ChatColor.YELLOW + "Store up to " + Main.MAX_LEVEL_STORED + " levels");
		UIinventory[9*1 + 3] = deposit;
		
		
		ItemStack withdraw = Main.instance.getConfig().getItemStack("gui.main-menu.withdraw").clone();
		if (withdraw.hasItemMeta()) {
			String name = withdraw.getItemMeta().getDisplayName();
			withdraw.getItemMeta().setDisplayName(Utils.replacePlaceholders(name));
		}
		if (withdraw.hasItemMeta() && withdraw.getItemMeta().hasLore()) {
			List<String> lore = withdraw.getItemMeta().getLore();
			for (int i = 0; i < lore.size(); i++) {
				lore.set(i, Utils.replacePlaceholders(lore.get(i)));
			}
			Utils.setLore(withdraw, lore);
		}
		//ItemStack withdraw = Utils.createItem(Material.IRON_INGOT, 1, ChatColor.GREEN + "Withdraw XP", ChatColor.YELLOW + "Hold up to " + Main.MAX_LEVEL_HELD + " levels");
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
		
		//ItemStack stored = Utils.createItem(Material.EXPERIENCE_BOTTLE, 1, ChatColor.GREEN + "Xp stored", ChatColor.YELLOW + "" + Main.xps.get(p.getUniqueId().toString()) + "/" + Main.MAX_XP_STORED);
		ItemStack stored = Main.instance.getConfig().getItemStack("gui.main-menu.xp-stored").clone();
		
		if (stored.hasItemMeta()) {
			String name = stored.getItemMeta().getDisplayName();
			stored.getItemMeta().setDisplayName(Utils.replacePlaceholders(name, p));
		}
		if (stored.hasItemMeta() && stored.getItemMeta().hasLore()) {
			List<String> lore = stored.getItemMeta().getLore();
			for (int i = 0; i < lore.size(); i++) {
				lore.set(i, Utils.replacePlaceholders(lore.get(i), p));
			}
			Utils.setLore(stored, lore);
		}
		toOpen.setItem(9*3 + 3, stored);
		
		//ItemStack held = Utils.createItem(Material.BRICK, 1, ChatColor.GREEN + "XP held", "" + ChatColor.YELLOW + Utils.totalXp(p) + "/" + Main.MAX_XP_HELD);
		ItemStack held = Main.instance.getConfig().getItemStack("gui.main-menu.xp-held").clone();
		if (held.hasItemMeta()) {
			String name = held.getItemMeta().getDisplayName();
			held.getItemMeta().setDisplayName(Utils.replacePlaceholders(name, p));
		}
		if (held.hasItemMeta() && held.getItemMeta().hasLore()) {
			List<String> lore = held.getItemMeta().getLore();
			for (int i = 0; i < lore.size(); i++) {
				lore.set(i, Utils.replacePlaceholders(lore.get(i), p));
			}
			Utils.setLore(held, lore);
		}
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
}
