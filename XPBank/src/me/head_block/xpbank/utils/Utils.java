package me.head_block.xpbank.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Utils {

	public static int getMaxLevel(Player sender, int xpStored) {
		int totalXp = totalXp(sender.getLevel(), xpPointsInBar(sender.getExp(), sender.getExpToLevel()));
		totalXp += xpStored;
		return level(totalXp);
	}
	
	public static int totalXp(int level, int xp) {
		int total = 0;
		if (level <= 16) {
			total = (level*level) + (6*level);
		} else if (level <= 31) {
			total = (int) ((2.5*level*level) - (40.5*level) + 360);
		} else if (level >= 32) {
			total = (int) ((4.5*level*level) - (162.5*level) + 2220);
		}
		total += xp;
		return total;
	}
	
	public static int totalXp(int level) {
		int total = 0;
		if (level < 16) {
			total = (level*level) + (6*level);
		} else if (level < 31) {
			total = (int) ((2.5*level*level) - (40.5*level) + 360);
		} else if (level > 31) {
			total = (int) ((4.5*level*level) - (162.5*level) + 2220);
		}
		return total;
	}
	
	public static int xpPointsInBar(float xp, int xpToLevelUp) {
		return (int) (xp * xpToLevelUp);
	}
	
	public static int level(int totalXp) {
		int level = 0;
		if (totalXp <= 352) {
			level = (int) (Math.sqrt(totalXp + 9) - 3);
		} else if (totalXp <= 1507) {
			level = (int) (8.1 + Math.sqrt(0.4*(totalXp-199.975)));
		} else if (totalXp >= 1508) {
			level = (int) (18.0555555555555555 + Math.sqrt(0.222222222222222*(totalXp-752.98611111111111)));
		}
		return level;
	}

	public static float xp(int totalXp) {
		int level = level(totalXp);
		int newTotal = totalXp - totalXp(level);
		return xpToLevelUp(level) / newTotal;
	}
	
	public static float xp(int totalXp, int level) {
		int newTotal = totalXp - totalXp(level);
		return ((float) newTotal / xpToLevelUp(level));
	}
	
	public static int xpToLevelUp(int level) {
		if (level < 16) {
			return 2*level + 7;
		} else if (level < 31) {
			return 5*level-38;
		} else if (level > 31) {
			return 9*level-158;
		} else {
			return -1;
		}
	}
	
	/**
	 * Saves an object to a file
	 * @param o	object to be saved
	 * @param f file to save the object to
	 */
	public static void save(Object o, File f) {
        try {
            FileOutputStream   stream = new FileOutputStream(f.getAbsoluteFile());
            ObjectOutputStream output = new ObjectOutputStream(stream);
            output.writeObject(o);
            output.close();
            return;
        }
        catch(NullPointerException e) {
            e.printStackTrace();
            return;
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }
	}
	
	/**
	 * Loads an object from a file
	 * @param f the file to read from
	 * @return the loaded object
	 */
	public static Object load(File f) {
        try {
            FileInputStream   stream = new FileInputStream(f.getAbsolutePath());
            ObjectInputStream input  = new ObjectInputStream(stream);
            Object object = input.readObject();
            input.close();
            return object;            
        }
        catch (IOException e) {
        	Bukkit.broadcastMessage("Error loading" + e.getMessage());
            return null;
        } catch (ClassNotFoundException e) {
        	Bukkit.broadcastMessage("Error loading" + e.getMessage());
        	return null;
		}
	}

}
