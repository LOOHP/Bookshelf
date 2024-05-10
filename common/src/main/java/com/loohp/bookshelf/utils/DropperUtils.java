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

import com.loohp.bookshelf.Bookshelf;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dropper;

public class DropperUtils {

    public static Block getDropperRelative(Block block) {
        if (!block.getType().equals(Material.DROPPER)) {
            return null;
        }
        Block relativeBlock = null;
        if (!Bookshelf.version.isLegacy()) {
            BlockFace face = ((org.bukkit.block.data.type.Dispenser) block.getBlockData()).getFacing();
            Block relative = block.getRelative(face);
            relativeBlock = relative;
        } else {
            Dropper dropper = (Dropper) block.getState();
            @SuppressWarnings("deprecation")
            int data = dropper.getRawData();
            BlockFace relative = BlockFace.DOWN;
            switch (data) {
                case 0:
                    relative = BlockFace.DOWN;
                    break;
                case 1:
                    relative = BlockFace.UP;
                    break;
                case 2:
                    relative = BlockFace.NORTH;
                    break;
                case 3:
                    relative = BlockFace.SOUTH;
                    break;
                case 4:
                    relative = BlockFace.WEST;
                    break;
                case 5:
                    relative = BlockFace.EAST;
                    break;
                case 8:
                    relative = BlockFace.DOWN;
                    break;
                case 9:
                    relative = BlockFace.UP;
                    break;
                case 10:
                    relative = BlockFace.NORTH;
                    break;
                case 11:
                    relative = BlockFace.SOUTH;
                    break;
                case 12:
                    relative = BlockFace.WEST;
                    break;
                case 13:
                    relative = BlockFace.EAST;
                    break;
                default:
                    relative = BlockFace.DOWN;
            }
            relativeBlock = block.getRelative(relative);
        }
        return relativeBlock;
    }

}
