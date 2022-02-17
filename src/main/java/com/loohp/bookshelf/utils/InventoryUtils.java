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

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryUtils {

    public static boolean stillHaveSpace(Inventory inv, Material material) {
        int size = inv.getSize();
        if (inv instanceof PlayerInventory) {
            size = 36;
        }
        for (int i = 0; i < size; i++) {
            ItemStack item = inv.getItem(i);
            if (item == null) {
                return true;
            }
            if (item.getType().equals(Material.AIR)) {
                return true;
            }
            if (item.getType().equals(material)) {
                if (item.getAmount() < item.getType().getMaxStackSize()) {
                    return true;
                }
            }
        }
        return false;
    }

}
