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

import com.loohp.bookshelf.BookshelfManager;
import com.loohp.bookshelf.objectholders.BlockPosition;
import com.loohp.bookshelf.utils.CustomListUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PistonEvents implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (event.isCancelled()) {
            return;
        }
        BookshelfManager manager = BookshelfManager.getBookshelfManager(event.getBlock().getWorld());
        if (manager == null) {
            return;
        }
        Map<Block, BlockPosition> position = new LinkedHashMap<>();
        List<Block> order = new ArrayList<>();
        for (Block block : event.getBlocks()) {
            if (block.getType().equals(Material.BOOKSHELF)) {
                position.put(block, manager.getOrCreateBookshelf(new BlockPosition(block), null).getPosition());
                order.add(block);
            }
        }

        if (order.isEmpty()) {
            return;
        }

        BlockFace dir = event.getDirection();
        for (Block block : CustomListUtils.reverse(order)) {
            Location newLoc = block.getRelative(dir).getLocation().clone();
            manager.move(position.get(block), newLoc.getBlockX(), newLoc.getBlockY(), newLoc.getBlockZ());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (event.isCancelled()) {
            return;
        }
        BookshelfManager manager = BookshelfManager.getBookshelfManager(event.getBlock().getWorld());
        if (manager == null) {
            return;
        }
        Map<Block, BlockPosition> position = new LinkedHashMap<>();
        List<Block> order = new ArrayList<>();
        for (Block block : event.getBlocks()) {
            if (block.getType().equals(Material.BOOKSHELF)) {
                position.put(block, manager.getOrCreateBookshelf(new BlockPosition(block), null).getPosition());
                order.add(block);
            }
        }

        if (order.isEmpty()) {
            return;
        }

        BlockFace dir = event.getDirection();
        for (Block block : CustomListUtils.reverse(order)) {
            Location newLoc = block.getRelative(dir).getLocation().clone();
            manager.move(position.get(block), newLoc.getBlockX(), newLoc.getBlockY(), newLoc.getBlockZ());
        }
    }

}
