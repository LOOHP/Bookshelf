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

import com.loohp.bookshelf.BookshelfManager;
import com.loohp.bookshelf.objectholders.BlockPosition;
import com.loohp.bookshelf.objectholders.BookshelfHolder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BookshelfUtils {

    public static String locKey(Location loc) {
        return loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
    }

    public static String posKey(BlockPosition pos) {
        return pos.getX() + "_" + pos.getY() + "_" + pos.getZ();
    }

    public static Location keyLoc(World world, String key) {
        String[] breakdown = key.split("_");
        int x = Integer.parseInt(breakdown[0]);
        int y = Integer.parseInt(breakdown[1]);
        int z = Integer.parseInt(breakdown[2]);
        return new Location(world, x, y, z);
    }

    public static BlockPosition keyPos(World world, String key) {
        String[] breakdown = key.split("_");
        int x = Integer.parseInt(breakdown[0]);
        int y = Integer.parseInt(breakdown[1]);
        int z = Integer.parseInt(breakdown[2]);
        return new BlockPosition(world, x, y, z);
    }

    public static String toBase64(Inventory inventory) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(inventory.getSize());

            // Save every element in the list
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static Inventory fromBase64(String data, String title, BookshelfHolder holder) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = BookshelfManager.createBookshelfInventory(holder, dataInput.readInt(), title == null || title.equals("") ? null : title);

            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }

            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

}
