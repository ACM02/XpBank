package me.head_block.xpbank.controllers;

import me.head_block.xpbank.Main;
import me.head_block.xpbank.exceptions.ExceedsXpLimitException;
import me.head_block.xpbank.exceptions.NoXpException;
import me.head_block.xpbank.exceptions.NotEnoughXpException;
import me.head_block.xpbank.utils.Utils;
import me.head_block.xpbank.utils.XpBPlayer;

public class XpController {

    // TODO: Payment

    private static void validate_xp_deposit(XpBPlayer player, long amount) {
        int playerTotalXp = player.totalXp();
        if (playerTotalXp == 0) {
            throw new NoXpException();
        } else if (player.totalXp() < amount) {
            throw new NotEnoughXpException();
        } else if ((long) player.getStoredXp() + amount > Main.MAX_XP_STORED) {
            throw new ExceedsXpLimitException();
        }
    }

    private static void validate_xp_withdrawl(XpBPlayer player, long amount) {
        int playerTotalXp = player.totalXp();
        if (player.getStoredXp() == 0) {
            throw new NoXpException();
        } else if (amount > player.getStoredXp()) {
            throw new NotEnoughXpException();
        } else if ((long) playerTotalXp + amount > Main.MAX_XP_HELD) {
            throw new ExceedsXpLimitException();
        }
    }

    private static void validate_level_withdrawl(XpBPlayer player, int amount) {
        int playerTotalXp = player.totalXp();
        int xpToAdd = Utils.totalXp(player.getLevel() + amount) - Utils.totalXp(player.getLevel());

        if (player.getStoredXp() == 0) {
            throw new NoXpException();
        } else if (xpToAdd > player.getStoredXp()) {
            throw new NotEnoughXpException();
        } else if ((long) playerTotalXp + (long) xpToAdd > Main.MAX_XP_HELD) {
            throw new ExceedsXpLimitException();
        }
    }

    private static void validate_level_deposit(XpBPlayer player, int amount) {
        int playerTotalXp = player.totalXp();

        if (player.totalXp() == 0) {
            throw new NoXpException();
        } else if (amount > playerTotalXp) {
            throw new NotEnoughXpException();
        } else if ((long) player.getStoredXp() + (long) amount > Main.MAX_XP_STORED) {
            throw new ExceedsXpLimitException();
        }
    }

    public static void deposit_xp(XpBPlayer player, long amount) {
        validate_xp_deposit(player, amount);
        player.removeXp((int) amount);
        int oldBal = player.getStoredXp();
        player.setStoredXp((int) (oldBal + amount));
    }

    public static void withdraw_xp(XpBPlayer player, long amount) {
        validate_xp_withdrawl(player, amount);
        player.addXp((int) amount);
        int oldBal = player.getStoredXp();
        player.setStoredXp(oldBal - (int) amount);
    }

    public static void withdraw_levels(XpBPlayer player, int amount) {
        validate_level_withdrawl(player, amount);
        // TODO: Recalculating this isn't amazing
        int xpToAdd = Utils.totalXp(player.getLevel() + amount) - Utils.totalXp(player.getLevel());

        player.addXp(xpToAdd);
        int oldBal = player.getStoredXp();
        player.setStoredXp(oldBal - xpToAdd);
    }

    public static void deposit_levels(XpBPlayer player, int amount) {
        validate_level_deposit(player, amount);

        player.removeXp(amount);
        int oldBal = player.getStoredXp();
        Main.xps.get(player.getUniqueId().toString());
        player.setStoredXp(oldBal + amount);
    }
}
