/*
 * This file is part of Bookshelf.
 *
 * Copyright (C) 2026. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2026. Contributors
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

import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.nms.NMS;
import java.io.File;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

public class BookshelfStorageLocationUtils {

    public static File locateBookshelfStorageLocation(World world) {
        if (Bookshelf.version.isNewerOrEqualTo(MCVersion.V26_1)) {
            return new File(world.getWorldFolder(), "bookshelf");
        } else {
            return getPre26_1BookshelfStorageLocation(world);
        }
    }

    public static File getPre26_1BookshelfStorageLocation(World world) {
        World.Environment environment = world.getEnvironment();
        if (environment.equals(World.Environment.NORMAL)) {
            return new File(world.getWorldFolder(), "bookshelf");
        } else if (environment.equals(World.Environment.NETHER)) {
            return new File(world.getWorldFolder(), "DIM-1/bookshelf");
        } else if (environment.equals(World.Environment.THE_END)) {
            return new File(world.getWorldFolder(), "DIM1/bookshelf");
        } else if (environment.equals(World.Environment.CUSTOM)) {
            Key namespacedKey = NMS.getInstance().getWorldNamespacedKey(world);
            return new File(world.getWorldFolder(), namespacedKey.value() + "/bookshelf");
        } else {
            throw new UnsupportedOperationException("Dimension type " + environment + " of world " + world.getName() + " not supported yet!");
        }
    }

    public static void checkAndNotifyWorldMigration(World world) {
        if (Bookshelf.version.isOlderThan(MCVersion.V26_1)) {
            return;
        }
        File legacyStorageLocation = new File(Bukkit.getWorldContainer(), world.getName() + "/bookshelf");
        if (!legacyStorageLocation.exists() || !legacyStorageLocation.isDirectory()) {
            return;
        }
        File newStorageLocation = locateBookshelfStorageLocation(world);
        if (newStorageLocation.exists()) {
            return;
        }
        blank(10);
        warning("===================== ! ALERT ! =====================");
        warning("Old Pre-26.1 bookshelf storage detected for world \"" + world.getName() + "\".");
        warning("According to upgrade instructions, you should have a backup of your world.");
        warning("");
        warning("Please go to your backup, and copy the folder at this location:");
        warning("." + File.separator + world.getName() + File.separator + "bookshelf");
        warning("");
        warning("Then put it in the following directory:");
        warning(newStorageLocation.getParentFile().getPath());
        warning("");
        warning("After that, delete this folder on your server: (Not on your backup)");
        warning("." + File.separator + world.getName() + File.separator + "bookshelf");
        warning("");
        warning("Finally, if this world has nether or end dimensions, do the same for those as well.");
        warning("Their old bookshelf folder will be in either the \"DIM1\" or \"DIM-1\" folder of their world folder.");
        warning("Copy them to the corresponding folder for each dimension.");
        warning("=====================================================");
        warning("Plugin will load correctly once you follow the above steps and restart.");
        warning("Waiting 15 seconds...");
        blank(10);
        wait(TimeUnit.SECONDS, 15);
        throw new IllegalStateException("Bookshelf storage migration for world \"" + world.getName() + "\" not completed");
    }

    private static void blank(int times) {
        for (int i = 0; i < times; i++) {
            Bukkit.getConsoleSender().sendMessage("");
        }
    }

    private static void warning(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Bookshelf] " + message);
    }

    private static void wait(TimeUnit timeUnit, long wait) {
        try {
            timeUnit.sleep(wait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
