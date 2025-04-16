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

package com.loohp.bookshelf.utils.datafix;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class DataVersions {

    private static final List<DataVersion> DATA_VERSIONS = new ArrayList<>();

    public static final DataVersion DATA_VERSION_0 = register(0, new DataVersion0());

    public static DataVersion register(int version, DataVersion dataVersion) {
        for (int i = DATA_VERSIONS.size(); i <= version; i++) {
            DATA_VERSIONS.add(null);
        }
        DATA_VERSIONS.set(version, dataVersion);
        return dataVersion;
    }

    public static void upgrade(int dataVersion, File folder, IntConsumer completionCallback) {
        for (int i = dataVersion; i < DATA_VERSIONS.size(); i++) {
            DataVersion data = DATA_VERSIONS.get(i);
            if (data != null) {
                data.performUpgrade(folder);
                completionCallback.accept(i);
            }
        }
    }

}
