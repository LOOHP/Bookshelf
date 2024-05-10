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

import org.bukkit.block.BlockFace;

public class LWCRequestOpenData {

    private final BookshelfHolder bookshelf;
    private final BlockFace blockface;
    private final boolean cancelled;

    public LWCRequestOpenData(BookshelfHolder bookshelf, BlockFace blockface, boolean cancelled) {
        this.bookshelf = bookshelf;
        this.cancelled = cancelled;
        this.blockface = blockface;
    }

    public BookshelfHolder getBookshelf() {
        return bookshelf;
    }

    public BlockFace getBlockFace() {
        return blockface;
    }

    public boolean isCancelled() {
        return cancelled;
    }

}
