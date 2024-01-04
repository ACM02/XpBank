package me.head_block.xpbank.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.head_block.xpbank.Main;
import me.head_block.xpbank.utils.Utils;

public class XpBankDebug implements CommandExecutor {

	public XpBankDebug(Main plugin) {
		plugin.getCommand("xpbankdebug").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("xpbank.admin")) {
			sender.sendMessage(Main.NO_PERM_MESSAGE);
		}
		
		File in = new File(Main.instance.getDataFolder(), "XpBank Test Data.txt");
		int[] xpValues = new int[5999];
		int[] levelValues = new int[5999];
		int[] xpAtLevelValues = new int[5999];
		int[] xpToLevelUpValues = new int[5999];
		if (!in.exists()) {
			sender.sendMessage("Test File not found");
		} else {
			sender.sendMessage("File found!");
			Scanner scanner;
			try {
				scanner = new Scanner(in);
				int count = 0;
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					String[] values = line.split("\t");
					xpValues[count] = Integer.parseInt(values[0]);
					levelValues[count] = Integer.parseInt(values[1]);
					xpAtLevelValues[count] = Integer.parseInt(values[2]);
					xpToLevelUpValues[count] = Integer.parseInt(values[3]);
					count++;
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sender.sendMessage("Loaded successfully! (" + xpToLevelUpValues.length + " values)");
			
			
			for (int i = 0; i < levelValues.length; i++) {
				if (!testTotalXp(levelValues[i], xpAtLevelValues[i])) {
					sender.sendMessage("TEST #1 FAILED at level " + levelValues[i]);
					sender.sendMessage("    Expected: " + xpAtLevelValues[i] + " and recieved: " + Utils.totalXp(levelValues[i]));
				}
			}
			
			for (int i = 0; i < xpValues.length; i++) {
				if (!testTotalXp(levelValues[i], xpAtLevelValues[i])) {
					sender.sendMessage("TEST #1 FAILED at level " + levelValues[i]);
					sender.sendMessage("    Expected: " + xpAtLevelValues[i] + " and recieved: " + Utils.totalXp(levelValues[i]));
				}
			}
		}
//		if (testTotalXp(0, 0, 0)) {
//		sender.sendMessage("TotalXp PASSED for values: Level: " + 0 + ", Xp: " + 0);
//	}
//	int[] testLevels = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
//			21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
//	int[] testExpected = { 7, 16, 27, 40, 55, 72, 91, 112, 135, 160, 187, 216, 247, 280, 315, 352,
//			394, 441, 493, 550, 612, 679, 751, 828, 910, 997, 1089, 1186, 1288, 1395 };
//	for (int i = 0; i < testExpected.length; i++) {
//		if (!testTotalXp(testLevels[i], testExpected[i])) {
//			sender.sendMessage("TotalXp FAILED for values: Level: " + testLevels[i] + " | Expected: " + testExpected[i]);
//		}
//	}
	
	
//	Player p = (Player) sender;
//	for (int i = 0; i < 10000; i++) {
//		p.setTotalExperience(p.getTotalExperience() + 1);
//		if (Utils.totalXp(p) != p.getTotalExperience()) {
//			p.sendMessage("FAILURE");
//		}
//	}
		
		sender.sendMessage(ChatColor.YELLOW + "Dubug rountine completed successfully");

		
		return false;
	}
	
	@SuppressWarnings("unused")
	private boolean testTotalXp(int level, int xp, int expected) {
		if (Utils.totalXp(level, xp) == expected) return true;
		else return false;
	}
	
	//@SuppressWarnings("unused")
	private boolean testTotalXp(int level, int expected) {
		if (Utils.totalXp(level) == expected) return true;
		else return false;
	}
	
	@SuppressWarnings("unused")
	private boolean testXpPointsInBar(float xp, int xpToLevelUp, int expected) {
		if (Utils.xpPointsInBar(xp, xpToLevelUp) == expected) return true;
		else return false;
	}

	@SuppressWarnings("unused")
	private boolean testXpToLevelUp(int level, int expected) {
		if (Utils.xpToLevelUp(level) == expected) return true;
		else return false;
	}
}
