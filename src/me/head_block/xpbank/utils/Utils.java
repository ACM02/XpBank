package me.head_block.xpbank.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.head_block.xpbank.Main;

public class Utils {
	
	public static int getMaxLevel(Player sender, int xpStored) {
		int totalXp = totalXp(sender.getLevel(), xpPointsInBar(sender.getExp(), sender.getExpToLevel()));
		totalXp += xpStored;
		return level(totalXp);
	}
	
	public static int totalXp(int level, int xp) {
		int total = totalXp(level);
		total += xp;
		return total;
	}
	
	public static int totalXp(Player p) {
		return totalXp(p.getLevel(), xpPointsInBar(p.getExp(), p.getExpToLevel()));
	}
	
	public static int totalXp(int level) {
		int total = 0;
		if (level < 16) {
			total = (level*level) + (6*level);
		} else if (level < 31) {
			total = (int) ((2.5*level*level) - (40.5*level) + 360);
		} else if (level >= 31) {
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
			level = (int) (8.1 + (float) Math.sqrt((float)0.4*((float)totalXp-195.975)));
		} else if (totalXp >= 1508) {
			level = (int) (18.0555555555555555 + Math.sqrt((2.0/9.0)*(totalXp-(54215.0/72.0))));
		}
		return level;
	}

	public static float xp(int totalXp) {
		int level = level(totalXp);
		int newTotal = totalXp - totalXp(level);
		return xpToLevelUp(level) / newTotal;
	}
	
	/**
	 * Gets the 0 to 1 float value for the xp in a player's xp bar for a given total xp and level
	 * @param totalXp The total xp the player is carrying
	 * @param level The current xp level of the player
	 * @return The desired float
	 */
	public static float xp(int totalXp, int level) {
		int newTotal = totalXp - totalXp(level);
		return ((float) newTotal / (float)xpToLevelUp(level));
	}
	
	public static int xpToLevelUp(int level) {
		if (level < 16) {
			return 2*level + 7;
		} else if (level < 31) {
			return 5*level-38;
		} else if (level >= 31) {
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
	
	
    /**
     * An efficient implementation of a bubble sort algorithm, it will sort the list into ascending order. No longer used.
     * 
     * @param list the List to sort
     */
    public static void bubbleSort(ArrayList<String> list) {
        if (list == null) return;                   // error check
        boolean sorted = true;                      // flag to stop or not
        for (int i = list.size()-1; i >= 0; i--) {  // traverse list
            sorted = true;                          // assume sorted
            for (int j = 0; j < i; j++) {           // traverse again
                String item1 = list.get(j);  
                String item2 = list.get(j+1);
                if (Main.xps.get(list.get(j)) < Main.xps.get(list.get(j+1))) {   // out of order
                    sorted = false;                 // flag no sorted
                    list.set(j, item2);             // swap positions
                    list.set(j+1, item1);
                } 
            }
            if (sorted) return;                     // return early
        }
    }
    
    
    /**
     * A public diver method for the merge sort, takes in an "int[]" and sorts it using the merge method
     * @param toSort toSort The "int[]" to be passed into the merge sort. toSort will be modified as it will be sorted in place.
     */
    public static void mergeSort(ArrayList<String> toSort) {
        mergeSort(toSort, new ArrayList<String>(), 0, toSort.size()-1);
    }

    /**
     * A private helper method that performs a recursive merge sort between 2 indicies in an array.
     * @param toSort The "int[]" to be sorted, which will be modified as it will be sorted in place.
     * @param target The target array to be used by the merge sort.
     * @param minIndex The index at which the sorting algorithm will start. Not modified.
     * @param maxIndex The index at which the sorting algorithm will end. Not modified.
     */
    private static void mergeSort(ArrayList<String> toSort, ArrayList<String> target, int minIndex, int maxIndex) {
        // Step one, break into sub-arrays
        if (maxIndex-minIndex<=5) {
            // Base case, sort a list 5 or less long
            insertionSort(toSort, minIndex, maxIndex);
        } else {
            int middleIndex = (maxIndex-minIndex)/2 + minIndex;
            mergeSort(toSort, target, minIndex, middleIndex);
            mergeSort(toSort, target, middleIndex + 1, maxIndex);

            // Put it back together
            int leftCounter = minIndex;
            int rightCounter = middleIndex + 1;
            int targetCounter = 0;
            while (leftCounter <= middleIndex || rightCounter <= maxIndex) {
                if (leftCounter > middleIndex) { 
                    // Case 1: left counter has hit its max
                    target.set(targetCounter, toSort.get(rightCounter));  //[targetCounter] = toSort[rightCounter];
                    rightCounter++;
                    targetCounter++;
                } else if (rightCounter > maxIndex) { 
                    // Case 2: right counter has hit its max
                    target.set(targetCounter, toSort.get(leftCounter));				//[targetCounter] = toSort[leftCounter];
                    leftCounter++;
                    targetCounter++;
                } else if (Main.xps.get(toSort.get(leftCounter)) > Main.xps.get(toSort.get(rightCounter))) { 
                    // Case 3: Neither have hit their maxes, the 'left' subarray is smaller
                    target.set(targetCounter, toSort.get(leftCounter));	//[targetCounter] = toSort[leftCounter];
                    targetCounter++;
                    leftCounter++;
                } else { 
                    // Case 4: Neither have hit their maxes, the 'right' subarray is smaller or equal
                    target.set(targetCounter, toSort.get(rightCounter));// [targetCounter] = toSort[rightCounter];
                    targetCounter++;
                    rightCounter++;
                }
            }

            // Copy contents of target into toSort
            for (int i = minIndex; i < maxIndex+1; i++) {
                toSort.set(i, target.get(i-minIndex)); // [i] = target[i-minIndex];
            }
        }
    }
    
    
    /**
     * A public diver method for the insertion sort, takes in an "int[]" and sorts it using the insertion sort method
     * @param toSort The "int[]" to be passed into the insertion sort. toSort will be modified as it will be sorted in place.
     */
    public static void insertionSort(ArrayList<String> toSort) {
        insertionSort(toSort, 0, toSort.size()-1);
    }


    /**
     * A private helper method that performs an iterative
     * insertion sort between 2 indicies in an array.
     * @param toSort The "int[]" to be sorted, which will be modified as it will be sorted in place.
     * @param start The index at which the sorting algorithm will start. Not modified.
     * @param end The index at which the sorting algorithm will end. Not modified.
     */
    private static void insertionSort(ArrayList<String> toSort, int start, int end) {
        // Looping from the start index to the end index
        for (int i = start; i<=end; i++) {
            // Selecting an element to insert
            String toPlace = toSort.get(i);
            int j = i;
            boolean placed = false;
            while (!placed) {
                if (j==start) {
                    // If we have reached the smallest index, place our element
                    toSort.set(j, toPlace); // [j] = toPlace;
                    placed = true;
                } else if (Main.xps.get(toPlace) > Main.xps.get(toSort.get(j-1))) { 
                    // If our element is smaller than the one being checked, shift it over and try the next one
                    toSort.set(j, toSort.get(j-1)); // [j] = toSort[j-1];
                    j--;
                } else {
                    // If our element is larger or equal to the one being checked, place it and finish.
                    toSort.set(j, toPlace); // [j] = toPlace;
                    placed = true;
                }
            }
        }
    }
    
	public static ItemStack createItem(Material type, int count, String displayName, String...lore) {
		ItemStack toReturn = new ItemStack(type, count);
		ItemMeta meta = toReturn.getItemMeta();
		if (displayName != null) {
			meta.setDisplayName(displayName);
		}
		if (!(lore.length == 1 && lore[0] == "")) {
			ArrayList<String> loreList = new ArrayList<String>();
			for (String s : lore) {
				loreList.add(s);
			}
			meta.setLore(loreList);
		}
		toReturn.setItemMeta(meta);
		return toReturn;
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack createItem(Material type, short damage, int count, String displayName, String...lore) {
		ItemStack toReturn = new ItemStack(type, count, damage);
		ItemMeta meta = toReturn.getItemMeta();
		if (displayName != null) {
			meta.setDisplayName(displayName);
		}
		if (!(lore.length == 1 && lore[0] == "")) {
			ArrayList<String> loreList = new ArrayList<String>();
			for (String s : lore) {
				loreList.add(s);
			}
			meta.setLore(loreList);
		}
		toReturn.setItemMeta(meta);
		return toReturn;
	}
	
	public static ItemStack setLore(ItemStack i, String...lore) {
		ItemMeta meta = i.getItemMeta();
		if (!(lore.length == 1 && lore[0] == "")) {
			ArrayList<String> loreList = new ArrayList<String>();
			for (String s : lore) {
				loreList.add(s);
			}
			meta.setLore(loreList);
		}
		i.setItemMeta(meta);
		return i;
	}
	
	public static ItemStack setLore(ItemStack item, List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		if (lore == null || lore.isEmpty()) return item;
		ArrayList<String> loreList = new ArrayList<String>();
		for (String s : lore) {
			loreList.add(s);
		}
		meta.setLore(loreList);
		item.setItemMeta(meta);
		return item;
		
	}
	
	public static ItemStack setLoreLine(ItemStack i, int line, String lore) {
		ItemMeta meta = i.getItemMeta();
		List<String> loreList;
		if (meta.hasLore()) {
			loreList = meta.getLore();
		} else {
			loreList = new ArrayList<String>();
		}
		loreList.set(line, lore);
		meta.setLore(loreList);
		i.setItemMeta(meta);
		return i;
	}
	
	public static ItemStack setDisplayName(ItemStack i, String name) {
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(name);
		i.setItemMeta(meta);
		return i;
	}
	
	public static ItemStack addLore(ItemStack i, String...lore) {
		ItemMeta meta = i.getItemMeta();
		if (!(lore.length == 1 && lore[0] == "")) {
			List<String> loreList;
			if (meta.hasLore()) {
				loreList = meta.getLore();
			} else {
				loreList = new ArrayList<String>();
			}

			for (String s : lore) {
				loreList.add(s);
			}
			meta.setLore(loreList);
		}
		i.setItemMeta(meta);
		return i;
	}

	/*	GUI menu Placeholders
	 *  
	 *  - %MAX_LEVEL_HELD%
	 *  - %MAX_LEVEL_STORED%
	 *  - %MAX_XP_HELD%
	 *  - %MAX_XP_STORED%
	 *  
	 *  - %MAX_LEVEL_HELD_RAW%
	 *  - %MAX_LEVEL_STORED_RAW%
	 *  - %MAX_XP_HELD_RAW%
	 *  - %MAX_XP_STORED_RAW%
	 *  
	 *  - %XP_STORED%
	 *  - %XP_HELD%
	 *  - %TOTAL_XP_LEVEL%
	 *  - %TOTAL_XP%
	 *  
	 *  - %XP_STORED_RAW%
	 *  - %XP_HELD_RAW%
	 *  - %TOTAL_XP_LEVEL_RAW%
	 *  - %TOTAL_XP_RAW%
	 */
	
	
	public static String replacePlaceholders(String text) {
		text = text.replace("%MAX_LEVEL_HELD%", Utils.formatNumber(Main.MAX_LEVEL_HELD));
		text = text.replace("%MAX_LEVEL_STORED%", Utils.formatNumber(Main.MAX_LEVEL_STORED));
		text = text.replace("%MAX_XP_HELD%", Utils.formatNumber(Main.MAX_XP_HELD));
		text = text.replace("%MAX_XP_STORED%", Utils.formatNumber(Main.MAX_XP_STORED));
		
		text = text.replace("%MAX_LEVEL_HELD_RAW%", "" + Main.MAX_LEVEL_HELD);
		text = text.replace("%MAX_LEVEL_STORED_RAW%", "" + Main.MAX_LEVEL_STORED);
		text = text.replace("%MAX_XP_HELD_RAW%", "" + Main.MAX_XP_HELD);
		text = text.replace("%MAX_XP_STORED_RAW%", "" + Main.MAX_XP_STORED);
		return text;
	}
	
	public static String replacePlaceholders(String text, OfflinePlayer p) {
		text = replacePlaceholders(text);
		checkBalInstance(p);
		text = text.replace("%XP_STORED%", Utils.formatNumber(Main.xps.get(p.getUniqueId().toString())));
		text = text.replace("%XP_STORED_RAW%", "" + Main.xps.get(p.getUniqueId().toString()));
		if (p.isOnline()) {
			text = text.replace("%XP_HELD%", Utils.formatNumber(Utils.totalXp(p.getPlayer())));
			text = text.replace("%TOTAL_XP%", Utils.formatNumber((totalXp(p.getPlayer()) + Main.xps.get(p.getUniqueId().toString()))));
			text = text.replace("%TOTAL_XP_LEVEL%", Utils.formatNumber(getMaxLevel(p.getPlayer(), Main.xps.get(p.getPlayer().getUniqueId().toString()))));
			
			text = text.replace("%XP_HELD_RAW%", "" + Utils.totalXp(p.getPlayer()));
			text = text.replace("%TOTAL_XP_RAW%", "" + (totalXp(p.getPlayer()) + Main.xps.get(p.getUniqueId().toString())));
			text = text.replace("%TOTAL_XP_LEVEL_RAW%", "" + getMaxLevel(p.getPlayer(), Main.xps.get(p.getPlayer().getUniqueId().toString())));
		}
		return text;
	}
	
	public static void checkBalInstance(OfflinePlayer p) {
		if (!Main.xps.containsKey(p.getUniqueId().toString())) {
			Main.xps.put(p.getUniqueId().toString(), 0);
		}
	}
	
	public static String formatNumber(int num) {
		int length = ("" + num).length();
		String toReturn = "" + num;
		if (length >= 4 && length < 7) {
			toReturn = "" + Utils.trunc((double)num/1000, 2) + "K";
		} else if (length >= 7 && length < 10) {
			toReturn = "" + Utils.trunc((double)num/1000000, 2)  + "M";
		} else if (length >= 10 && length < 14) {
			toReturn = "" + Utils.trunc((double)num/1000000000, 2)  + "B";
		}
		return toReturn;
	}
	
	public static double trunc(double d, int decimalsToKeep) {
		d *= Math.pow(10, decimalsToKeep);
		d = (int) d;
		d /= Math.pow(10, decimalsToKeep);
		return d;
	}

}
