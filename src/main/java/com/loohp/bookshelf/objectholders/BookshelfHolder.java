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

import com.loohp.bookshelf.BookshelfManager;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public abstract class BookshelfHolder implements InventoryHolder {

    private BlockPosition position;
    private String title;
    private Inventory inventory;

    private boolean forceOpen;

    private Unsafe unsafe;

    public BookshelfHolder(BlockPosition position, String title, Inventory inventory) {
        this.position = position;
        this.title = title;
        this.inventory = inventory;

        this.forceOpen = false;

        this.unsafe = null;
    }

    private BookshelfManager getManager() {
        return BookshelfManager.getBookshelfManager(position.getWorld());
    }

    public BlockPosition getPosition() {
        return position;
    }

    @MightOverride
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

    @MightOverride
    public void open() {
        forceOpen = true;
        BookshelfManager manager = getManager();
        if (manager != null) {
            manager.getParticleManager().openBookshelf(getBlock());
        }
    }

    @MightOverride
    public void close() {
        forceOpen = false;
        BookshelfManager manager = getManager();
        if (manager != null) {
            manager.getParticleManager().closeBookshelf(getBlock());
        }
    }

    @MightOverride
    public boolean isOpen() {
        if (forceOpen) {
            return true;
        }
        BookshelfManager manager = getManager();
        if (manager != null) {
            return getManager().getParticleManager().isBookshelfOpen(getBlock());
        }
        return false;
    }

    public boolean isForceOpen() {
        return forceOpen;
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

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface MightOverride {
    }

}
