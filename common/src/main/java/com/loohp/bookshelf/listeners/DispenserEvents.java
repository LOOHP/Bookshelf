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

package com.loohp.bookshelf.listeners;

import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.BookshelfManager;
import com.loohp.bookshelf.objectholders.BlockPosition;
import com.loohp.bookshelf.objectholders.BookshelfHolder;
import com.loohp.bookshelf.objectholders.TileStateSimulateBookshelfInventory;
import com.loohp.bookshelf.utils.BlockLockerUtils;
import com.loohp.bookshelf.utils.DropperUtils;
import com.loohp.bookshelf.utils.InventoryUtils;
import com.loohp.bookshelf.utils.LWCUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DispenserEvents implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDropper(BlockDispenseEvent event) {
        if (!Bookshelf.enableDropperSupport) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }
        if (!event.getBlock().getType().equals(Material.DROPPER)) {
            return;
        }
        Block relative = DropperUtils.getDropperRelative(event.getBlock());
        if (!relative.getType().equals(Material.BOOKSHELF)) {
            return;
        }
        BookshelfManager manager = BookshelfManager.getBookshelfManager(relative.getWorld());
        if (manager == null) {
            return;
        }
        BookshelfHolder bookshelf = manager.getOrCreateBookshelf(new BlockPosition(relative), null);
        if (Bookshelf.lwcHook) {
            if (!LWCUtils.checkHopperFlagIn(relative)) {
                event.setCancelled(true);
                return;
            }
        }
        if (Bookshelf.blockLockerHook) {
            if (!BlockLockerUtils.canRedstone(relative)) {
                event.setCancelled(true);
                return;
            }
        }
        event.setCancelled(true);
        Inventory inventory = bookshelf.getInventory();
        org.bukkit.block.Dropper d = (org.bukkit.block.Dropper) event.getBlock().getState();
        Inventory dropper = d.getInventory();
        List<Integer> indexes = IntStream.range(0, dropper.getSize()).boxed().collect(Collectors.toList());
        Collections.shuffle(indexes);
        for (int index : indexes) {
            ItemStack each = dropper.getItem(index);
            if (each == null) {
                continue;
            }
            if (Bookshelf.useWhitelist) {
                if (!Bookshelf.whitelist.contains(each.getType().toString().toUpperCase())) {
                    continue;
                }
            }
            if (!InventoryUtils.stillHaveSpace(inventory, each.getType())) {
                continue;
            }
            ItemStack additem = each.clone();
            additem.setAmount(1);
            ItemStack beforeEvent = additem.clone();
            InventoryMoveItemEvent moveItemEvent = new InventoryMoveItemEvent(dropper, additem, new TileStateSimulateBookshelfInventory(bookshelf), true);
            Bukkit.getPluginManager().callEvent(moveItemEvent);
            if (moveItemEvent.isCancelled()) {
                return;
            }
            additem = moveItemEvent.getItem();
            if (beforeEvent.equals(additem)) {
                each.setAmount(each.getAmount() - 1);
                dropper.setItem(index, each);
            }
            inventory.addItem(additem);
            return;
        }
        if (!Bookshelf.version.isLegacy()) {
            event.getBlock().getWorld().playSound(event.getBlock().getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1, 1);
        }
    }

}
