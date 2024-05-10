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

import com.loohp.bookshelf.Bookshelf;
import org.bukkit.World;
import org.bukkit.World.Environment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WorldUtils {

    private static Class<?> craftWorldClass;
    private static Method getHandleMethod;
    private static Class<?> worldServerClass;
    private static Method getWorldTypeKeyMethod;
    private static Method getMinecraftKeyMethod;

    static {
        try {
            // 1.16 - 1.18.2 only
            craftWorldClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.CraftWorld");
            getHandleMethod = craftWorldClass.getMethod("getHandle");
            worldServerClass = getHandleMethod.getReturnType();
            getWorldTypeKeyMethod = worldServerClass.getMethod("getTypeKey");
            getMinecraftKeyMethod = getWorldTypeKeyMethod.getReturnType().getMethod("a");
        } catch (ReflectiveOperationException ignore) {
        }
    }

    public static String getNamespacedKey(World world) {
        if (Bookshelf.version.isNewerOrEqualTo(MCVersion.V1_18_2)) {
            return world.getKey().toString();
        } else if (Bookshelf.version.isNewerOrEqualTo(MCVersion.V1_16)) {
            try {
                Object craftWorldObject = craftWorldClass.cast(world);
                Object nmsWorldServerObject = getHandleMethod.invoke(craftWorldObject);
                Object nmsResourceKeyObject = getWorldTypeKeyMethod.invoke(nmsWorldServerObject);
                return getMinecraftKeyMethod.invoke(nmsResourceKeyObject).toString();
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            if (world.getEnvironment().equals(Environment.NORMAL)) {
                return "minecraft:overworld";
            } else if (world.getEnvironment().equals(Environment.NETHER)) {
                return "minecraft:the_nether";
            } else if (world.getEnvironment().equals(Environment.THE_END)) {
                return "minecraft:the_end";
            } else {
                return "minecraft:custom";
            }
        }
        return null;
    }

}
