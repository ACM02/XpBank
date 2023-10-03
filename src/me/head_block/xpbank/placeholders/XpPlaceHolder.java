package me.head_block.xpbank.placeholders;

import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.head_block.xpbank.Main;
import me.head_block.xpbank.utils.Utils;

public class XpPlaceHolder extends PlaceholderExpansion {

	@SuppressWarnings("unused")
	private Main plugin;
	
    @Override
    public boolean canRegister() {
        return true; //(plugin = (Main) Bukkit.getPluginManager().getPlugin(getRequiredPlugin())) != null;
    }
	
    @Override
    public String getRequiredPlugin() {
        return "XpBank";
    }
	
	@Override
	public String getAuthor() {
		return "Head_Block";
	}

	@Override
	public String getIdentifier() {
		return "xp";
	}

	@Override
	public String getVersion() {
		return "1.0.0";
	}
	
    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }
	
    @Override
    public String onRequest(OfflinePlayer player, String params) {
    	if (params.equalsIgnoreCase("held")) {
    		if (player.isOnline()) {
        		return "" + Utils.totalXp(player.getPlayer());
    		}
    	} else if (params.equalsIgnoreCase("stored")) {
    		if (player.isOnline()) {
    			Utils.checkBalInstance(player);
        		return "" + Main.xps.get(player.getUniqueId().toString());
    		}
    	} else if (params.equalsIgnoreCase("max_held_xp")) {
    		return Main.MAX_XP_HELD + "";
    	} else if (params.equalsIgnoreCase("max_held_levels")) {
    		return Main.MAX_LEVEL_HELD + "";
    	} else if (params.equalsIgnoreCase("max_stored_xp")) {
    		return Main.MAX_XP_STORED + "";
    	} else if (params.equalsIgnoreCase("max_stored_levels")) {
    		return Main.MAX_LEVEL_STORED + "";
    	}
    	return "";
    }

}
