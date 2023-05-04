/*
 * This file is part of Bookshelf.
 *
 * Copyright (C) 2023. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2023. Contributors
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

import com.comphenix.net.bytebuddy.dynamic.DynamicType;
import com.comphenix.net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import com.comphenix.net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import com.comphenix.protocol.utility.ByteBuddyFactory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class BookshelfHolderFactory {

    private static final Constructor<? extends BookshelfHolder> CONSTRUCTOR;

    static {
        try {
            DynamicType.Builder<BookshelfHolder> builder = ByteBuddyFactory.getInstance()
                    .createSubclass(BookshelfHolder.class, ConstructorStrategy.Default.IMITATE_SUPER_CLASS)
                    .name(BookshelfHolder.class.getPackage().getName() + ".BookshelfHolderImpl")
                    .implement(InventoryHolder.class);

            builder = implementIfExist(builder, "org.bukkit.inventory.BlockInventoryHolder", "org.bukkit.block.Lidded");

            CONSTRUCTOR = builder.make()
                    .load(BookshelfHolder.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded()
                    .getDeclaredConstructor(BlockPosition.class, String.class, Inventory.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to find BookshelfHolder constructor!", e);
        }
    }

    private static DynamicType.Builder<BookshelfHolder> implementIfExist(DynamicType.Builder<BookshelfHolder> builder, String... interfaces) {
        for (String classPath : interfaces) {
            try {
                builder = builder.implement(Class.forName(classPath));
            } catch (ClassNotFoundException ignore) {
            }
        }
        return builder;
    }

    public static BookshelfHolder newInstance(BlockPosition position, String title, Inventory inventory) {
        try {
            return CONSTRUCTOR.newInstance(position, title, inventory);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
