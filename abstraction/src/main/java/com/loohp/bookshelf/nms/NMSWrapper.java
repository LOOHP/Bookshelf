/*
 * This file is part of Bookshelf.
 *
 * Copyright (C) 2024. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2024. Contributors
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

package com.loohp.bookshelf.nms;

import com.loohp.bookshelf.objectholders.BookshelfState;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public abstract class NMSWrapper {

    static final ItemStack AIR = new ItemStack(Material.AIR);

    private static Plugin plugin;
    private static NMSWrapper instance;

    @Deprecated
    public static Plugin getPlugin() {
        return plugin;
    }

    @Deprecated
    public static NMSWrapper getInstance() {
        return instance;
    }

    @Deprecated
    public static void setup(NMSWrapper instance, Plugin plugin) {
        NMSWrapper.instance = instance;
        NMSWrapper.plugin = plugin;
    }

    static ItemStack itemOrNull(ItemStack itemStack) {
        return itemStack == null || itemStack.getType().equals(Material.AIR) ? null : itemStack;
    }

    static ItemStack itemNonNull(ItemStack itemStack) {
        return itemStack == null ? AIR : itemStack;
    }

    public abstract Component getItemDisplayName(ItemStack itemStack);

    public abstract BookshelfState getStoredBookshelfState(ItemStack itemStack, int slots);

    public abstract ItemStack withStoredBookshelfState(ItemStack itemStack, BookshelfState bookshelfState);

    public abstract void sendBookshelfWindowOpen(Player player, Inventory inventory, Component title);

}
