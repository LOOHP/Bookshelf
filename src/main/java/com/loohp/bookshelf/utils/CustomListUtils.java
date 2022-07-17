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

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CustomListUtils {

    public static <T> Iterable<T> reverseIterable(List<T> original) {
        return () -> new Iterator<T>() {
            private final ListIterator<T> itr = original.listIterator(original.size());

            @Override
            public boolean hasNext() {
                return itr.hasPrevious();
            }

            @Override
            public T next() {
                return itr.previous();
            }

            @Override
            public void remove() {
                itr.remove();
            }
        };
    }

}
