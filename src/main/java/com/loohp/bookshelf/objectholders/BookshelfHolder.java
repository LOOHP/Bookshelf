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

package com.loohp.bookshelf.objectholders;

import org.bukkit.block.Block;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;

public class BookshelfHolder implements BlockInventoryHolder {

    private BlockPosition position;
    private String title;
    private Inventory inventory;

    private Unsafe unsafe;

    public BookshelfHolder(BlockPosition position, String title, Inventory inventory) {
        this.position = position;
        this.title = title;
        this.inventory = inventory;
        this.unsafe = null;
    }

    public BlockPosition getPosition() {
        return position;
    }

    @Override
    public Block getBlock() {
        return position.getBlock();
    }

    public String getTitle() {
        return title;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @SuppressWarnings({"DeprecatedIsStillUsed", "deprecation"})
    @Deprecated
    public Unsafe getUnsafe() {
        if (unsafe != null) {
            return unsafe;
        }
        return unsafe = new Unsafe() {
            @Deprecated
            public void setPosition(BlockPosition position) {
                BookshelfHolder.this.position = position;
            }

            @Deprecated
            public void setTitle(String title) {
                BookshelfHolder.this.title = title;
            }

            @Deprecated
            public void setInventory(Inventory inventory) {
                BookshelfHolder.this.inventory = inventory;
            }
        };
    }

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public interface Unsafe {

        /**
         * <b>Dangerous, non-deterministic behavior if not used correctly</b>
         */
        @Deprecated
        void setPosition(BlockPosition position);

        /**
         * <b>Dangerous, non-deterministic behavior if not used correctly</b>
         */
        @Deprecated
        void setTitle(String title);

        /**
         * <b>Dangerous, non-deterministic behavior if not used correctly</b>
         */
        @Deprecated
        void setInventory(Inventory inventory);

    }

}
