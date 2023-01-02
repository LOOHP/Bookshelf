/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BookshelfContentPacket {

    public static BookshelfContentPacket from(BookshelfHolder bookshelf) {
        BlockPosition position = bookshelf.getPosition();
        int filledSlots = 0;
        for (ItemStack item : bookshelf.getInventory()) {
            if (item != null && !item.getType().equals(Material.AIR)) {
                filledSlots++;
            }
        }
        return new BookshelfContentPacket(position.getX(), position.getY(), position.getZ(), filledSlots, bookshelf.getInventory().getSize());
    }

    private final int x;
    private final int y;
    private final int z;
    private final int slotsFilled;
    private final int totalSlots;

    public BookshelfContentPacket(int x, int y, int z, int slotsFilled, int totalSlots) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.slotsFilled = slotsFilled;
        this.totalSlots = totalSlots;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getSlotsFilled() {
        return slotsFilled;
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(outputStream);
        out.writeInt(x);
        out.writeInt(y);
        out.writeInt(z);
        out.writeInt(slotsFilled);
        out.writeInt(totalSlots);
        return outputStream.toByteArray();
    }

}
