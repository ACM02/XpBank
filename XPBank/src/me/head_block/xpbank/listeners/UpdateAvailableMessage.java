package me.head_block.xpbank.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.head_block.xpbank.Main;
import net.md_5.bungee.api.ChatColor;

public class UpdateAvailableMessage implements Listener {
	
	public UpdateAvailableMessage(Main plugin) {		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (e.getPlayer().hasPermission("xpbank.admin") && Main.updateAvailable && Main.instance.getConfig().getBoolean("update-message")) {
			e.getPlayer().sendMessage(ChatColor.YELLOW + "An upadate is available for XpBank! Please visit https://www.spigotmc.org/resources/xpbank.101132/ to download it.");
		}
	}
}
