package me.head_block.xpbank.ui;

import java.util.List;

import org.bukkit.Bukkit;
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
	
	private static ItemStack deposit;
	private static ItemStack withdraw;
	
	public static void init() {
		INV_NAME = ChatColor.translateAlternateColorCodes('&', Main.instance.getConfig().getString("gui.main-menu.name"));
		
		UIinventory = new ItemStack[INV_SIZE];
		
		deposit = loadItem("gui.main-menu.deposit");
		//ItemStack deposit = Utils.createItem(Material.GOLD_INGOT, 1, ChatColor.GREEN + "Deposit XP", ChatColor.YELLOW + "Store up to " + Main.MAX_LEVEL_STORED + " levels");
		UIinventory[9*1 + 3] = deposit;
		
		
		withdraw = loadItem("gui.main-menu.withdraw");
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
		ItemStack stored = loadItem("gui.main-menu.xp-stored", p);
		toOpen.setItem(9*3 + 3, stored);
		
		//ItemStack held = Utils.createItem(Material.BRICK, 1, ChatColor.GREEN + "XP held", "" + ChatColor.YELLOW + Utils.totalXp(p) + "/" + Main.MAX_XP_HELD);
		ItemStack held = loadItem("gui.main-menu.xp-held", p);
		toOpen.setItem(9*3 + 5, held);
		
		p.openInventory(toOpen);
	}
	
	public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
		if (clicked.equals(deposit) && slot < INV_SIZE) {
			DepositMenu.openForPlayer(p);
		} else if (clicked.equals(withdraw) && slot < INV_SIZE) {
			WithdrawMenu.openForPlayer(p);
		}
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
	
	private static ItemStack loadItem(String path, Player p) {
		ItemStack item = Main.instance.getConfig().getItemStack(path).clone();		
		if (item.hasItemMeta()) {
			String name = item.getItemMeta().getDisplayName();
			name = Utils.replacePlaceholders(name, p);
			name = ChatColor.translateAlternateColorCodes('&', name);
			Utils.setDisplayName(item, name);
		}
		if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
			List<String> lore = item.getItemMeta().getLore();
			for (int i = 0; i < lore.size(); i++) {
				String loreLine = Utils.replacePlaceholders(lore.get(i), p);
				lore.set(i, ChatColor.translateAlternateColorCodes('&', loreLine));
			}
			Utils.setLore(item, lore);
		}
		return item;
	}
	
}
