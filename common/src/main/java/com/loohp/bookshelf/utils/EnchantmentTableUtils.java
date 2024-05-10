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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class EnchantmentTableUtils {

    public static List<Block> getBookshelves(Block enchantmentTableBlock) {
        return getBoostableBlockLocations(enchantmentTableBlock, block -> block.getType().equals(Material.BOOKSHELF));
    }

    public static List<Block> getBoostableBlockLocations(Block enchantmentTableBlock, Predicate<Block> filter) {
        List<Block> blocks = new ArrayList<>();
        Location center = enchantmentTableBlock.getLocation();

        for (int y = 0; y <= 1; y++) {
            for (int x = -2; x <= 2; x++) {
                Location loc = new Location(center.getWorld(), center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + 2);
                blocks.add(loc.getBlock());
            }
            for (int x = -2; x <= 2; x++) {
                Location loc = new Location(center.getWorld(), center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() - 2);
                blocks.add(loc.getBlock());
            }
            for (int z = -2; z <= 2; z++) {
                Location loc = new Location(center.getWorld(), center.getBlockX() + 2, center.getBlockY() + y, center.getBlockZ() + z);
                blocks.add(loc.getBlock());
            }
            for (int z = -2; z <= 2; z++) {
                Location loc = new Location(center.getWorld(), center.getBlockX() - 2, center.getBlockY() + y, center.getBlockZ() + z);
                blocks.add(loc.getBlock());
            }
        }

        blocks.removeIf(block -> !isAirBetween(block.getLocation().clone().add(0.5, 0.5, 0.5), center.clone().add(0.5, 0.5, 0.5)) || !filter.test(block));
        return blocks;
    }

    public static boolean isAirBetween(Location loc1, Location loc2) {
        Vector vector = genVec(loc1, loc2).multiply(0.5);
        Location loc = loc1.clone();
        while (true) {
            loc.add(vector);
            if (loc.getBlock().equals(loc1.getBlock())) {
                continue;
            }
            if (!loc.getBlock().getType().equals(Material.AIR)) {
                if (loc.getBlock().equals(loc2.getBlock())) {
                    break;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public static Vector genVec(Location a, Location b) {
        double dX = a.getX() - b.getX();
        double dY = a.getY() - b.getY();
        double dZ = a.getZ() - b.getZ();
        double yaw = Math.atan2(dZ, dX);
        double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);

        Vector vector = new Vector(x, z, y);
        vector.normalize();

        return vector;
    }

}
