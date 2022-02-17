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
import com.loohp.bookshelf.utils.BookshelfUtils;
import com.loohp.bookshelf.utils.MCVersion;
import com.loohp.bookshelf.utils.MaterialUtils;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;

public class CreativeEvents implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCreativePickBlock(InventoryCreativeEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getClick().equals(ClickType.CREATIVE)) {
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCursor();
            if (item.getType().equals(Material.BOOKSHELF) && player.getGameMode().equals(GameMode.CREATIVE) && player.isSneaking() && player.hasPermission("bookshelf.copynbt")) {
                Block block = null;
                if (Bookshelf.version.isNewerOrEqualTo(MCVersion.V1_14)) {
                    block = player.getTargetBlockExact(10, FluidCollisionMode.NEVER);
                } else {
                    block = player.getTargetBlock(MaterialUtils.getNonSolidSet(), 10);
                }
                if (block.getType().equals(Material.BOOKSHELF)) {
                    BookshelfManager manager = BookshelfManager.getBookshelfManager(player.getWorld());
                    if (manager == null) {
                        return;
                    }
                    BookshelfHolder bookshelf = manager.getOrCreateBookshelf(new BlockPosition(block), null);
                    String hash = BookshelfUtils.toBase64(bookshelf.getInventory());
                    String title = bookshelf.getTitle();
                    item = NBTEditor.set(item, hash, "BookshelfContent");
                    item = NBTEditor.set(item, title, "BookshelfTitle");
                    event.setCursor(item);
                }
            }
        }
    }

}
