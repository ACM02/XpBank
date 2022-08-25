package me.head_block.xpbank.listeners;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import me.head_block.xpbank.Main;
import me.head_block.xpbank.ui.MainMenu;

public class MainMenuClick implements Listener {

	@SuppressWarnings("unused")
	private Main plugin;
	
	public MainMenuClick(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getInventory() == null || e.getClickedInventory() == null) return;
		if (e.getAction().name().equals("MOVE_TO_OTHER_INVENTORY") &&
			e.getView().getTitle().equals(MainMenu.INV_NAME)) {
			e.setResult(Result.DENY);
		}
		if (e.getRawSlot() < MainMenu.INV_SIZE && e.getView().getTitle().equals(MainMenu.INV_NAME)) {
			e.setResult(Result.DENY);
			if (e.getCurrentItem() == null) {
				return;
			}
			if (e.getView().getTitle().equals(MainMenu.INV_NAME)) {
				MainMenu.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		}
	}
	
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e) {
		if (e.getView().getTitle().equals(MainMenu.INV_NAME)) {
			Set<Integer> rawSlots = e.getRawSlots();
			int highestSlot = 0;
			int lowestSlot = 100;
			for (Integer slot : rawSlots) {
				if (slot > highestSlot) {
					highestSlot = slot;
				}
				if (slot < lowestSlot) {
					lowestSlot = slot;
				}
			}
			if (highestSlot < MainMenu.INV_SIZE ||
				lowestSlot < MainMenu.INV_SIZE) {
				e.setResult(Result.DENY);
			}
		}
	}
	
}
