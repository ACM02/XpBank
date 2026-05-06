package me.head_block.xpbank.controllers;

import me.head_block.xpbank.Main;
import me.head_block.xpbank.exceptions.ExceedsXpStoreLimitException;
import me.head_block.xpbank.exceptions.NoXpException;
import me.head_block.xpbank.exceptions.NotEnoughXpException;
import me.head_block.xpbank.utils.XpBPlayer;

public class XpController {

    // TODO: Deposit levels
    // TODO: Withdraw Xp
    // TODO: Withdraw levels
    // TODO: Payment

    private static void validate_xp_deposit(XpBPlayer player, long amount) {
        int playerTotalXp = player.totalXp();
        if (playerTotalXp == 0) {
            throw new NoXpException();
        } else if (player.totalXp() < amount) {
            throw new NotEnoughXpException();
        } else if ((long) player.getStoredXp() + amount < Main.MAX_XP_STORED) {
            throw new ExceedsXpStoreLimitException();
        }
    }

    public static void deposit_xp(XpBPlayer player, long amount) {
        validate_xp_deposit(player, amount);
        player.removeXp((int) amount);
        int oldBal = player.getStoredXp();
        player.setStoredXp((int) (oldBal + amount));
    }
}
