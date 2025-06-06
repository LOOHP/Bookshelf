/*
 * This file is part of Bookshelf.
 *
 * Copyright (C) 2025. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2025. Contributors
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

package com.loohp.bookshelf.nms;

import com.loohp.bookshelf.Bookshelf;

import java.lang.reflect.InvocationTargetException;

public class NMS {

    private static NMSWrapper instance;

    @SuppressWarnings({"unchecked", "deprecation"})
    public synchronized static NMSWrapper getInstance() {
        if (instance != null) {
            return instance;
        }
        try {
            Class<NMSWrapper> nmsImplClass = (Class<NMSWrapper>) Class.forName("com.loohp.bookshelf.nms." + Bookshelf.version.name());
            instance = nmsImplClass.getConstructor().newInstance();
            NMSWrapper.setup(instance, Bookshelf.plugin);
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            if (Bookshelf.version.isSupported()) {
                throw new RuntimeException("Missing NMSWrapper implementation for version " + Bookshelf.version.name(), e);
            } else {
                throw new RuntimeException("No NMSWrapper implementation for UNSUPPORTED version " + Bookshelf.version.name(), e);
            }
        }
    }

}
