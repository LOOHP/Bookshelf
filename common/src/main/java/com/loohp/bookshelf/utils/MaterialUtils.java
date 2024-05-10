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

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MaterialUtils {

    private static final List<Material> nonSolid = new ArrayList<Material>();

    public static void setup() {
        for (Material material : Material.values()) {
            if (!material.isBlock()) {
                continue;
            }
            if (!material.isSolid()) {
                nonSolid.add(material);
            }
        }
    }

    public static List<Material> getNonSolidList() {
        return nonSolid;
    }

    public static Set<Material> getNonSolidSet() {
        return convertListToSet(nonSolid);
    }

    public static <T> Set<T> convertListToSet(List<T> list) {
        Set<T> set = new HashSet<>();

        for (T t : list) {
            set.add(t);
        }

        return set;
    }

}
