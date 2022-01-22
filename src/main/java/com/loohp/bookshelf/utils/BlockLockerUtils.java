package com.loohp.bookshelf.utils;

import nl.rutgerkok.blocklocker.BlockLockerAPIv2;
import nl.rutgerkok.blocklocker.BlockLockerPlugin;
import nl.rutgerkok.blocklocker.profile.Profile;
import nl.rutgerkok.blocklocker.protection.Protection;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Optional;

public class BlockLockerUtils {

    public static boolean checkAccess(Player player, Block block) {
        return BlockLockerAPIv2.isAllowed(player, block, true);
    }

    public static boolean isLocked(Block block) {
        return BlockLockerAPIv2.isProtected(block);
    }

    public static boolean canRedstone(Block block) {
        BlockLockerPlugin plugin = BlockLockerAPIv2.getPlugin();
        Optional<Protection> protection = plugin.getProtectionFinder().findProtection(block);
        if (!protection.isPresent()) {
            return true;
        }
        Profile redstoneProfile = plugin.getProfileFactory().fromRedstone();
        return protection.get().isAllowed(redstoneProfile);
    }

}
