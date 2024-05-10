/*
 * This file is part of Bookshelf.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
