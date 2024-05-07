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

import org.bukkit.Bukkit;

import java.lang.reflect.AccessibleObject;

public class NMSUtils {

    public static Class<?> getNMSClass(String path, String... paths) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        if (!version.matches("v[0-9]+_[0-9]+_R[0-9]+")) {
            version = "";
        }
        ClassNotFoundException error;
        try {
            return Class.forName(path.replace("%s", version).replaceAll("\\.+", "."));
        } catch (ClassNotFoundException e) {
            error = e;
        }
        for (String classpath : paths) {
            try {
                return Class.forName(classpath.replace("%s", version).replaceAll("\\.+", "."));
            } catch (ClassNotFoundException e) {
                error = e;
            }
        }
        throw error;
    }

    @SafeVarargs
    public static <T extends AccessibleObject> T reflectiveLookup(Class<T> lookupType, ReflectionLookupSupplier<T> methodLookup, ReflectionLookupSupplier<T>... methodLookups) throws ReflectiveOperationException {
        ReflectiveOperationException error;
        try {
            return methodLookup.lookup();
        } catch (ReflectiveOperationException e) {
            error = e;
        }
        for (ReflectionLookupSupplier<T> supplier : methodLookups) {
            try {
                return supplier.lookup();
            } catch (ReflectiveOperationException e) {
                error = e;
            }
        }
        throw error;
    }

    @FunctionalInterface
    public interface ReflectionLookupSupplier<T> {

        T lookup() throws ReflectiveOperationException;

    }

}
