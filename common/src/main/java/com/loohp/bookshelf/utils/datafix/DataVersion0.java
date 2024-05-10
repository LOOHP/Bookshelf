/*
 * This file is part of Bookshelf.
 *
 * Copyright (C) 2024. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2024. Contributors
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.GZIPOutputStream;

public class DataVersion0 implements DataVersion {

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void performUpgrade(File folder) {
        for (File region : folder.listFiles()) {
            if (region.isDirectory() && region.getName().startsWith("r.")) {
                for (File chunk : region.listFiles()) {
                    if (chunk.getName().endsWith(".json")) {
                        File compressedChunk = new File(region, chunk.getName() + ".gz");
                        try (
                            BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(chunk.toPath()),StandardCharsets.UTF_8));
                            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new GZIPOutputStream(Files.newOutputStream(compressedChunk.toPath())), StandardCharsets.UTF_8))
                        ) {
                            reader.lines().forEach(l -> pw.println(l));
                            pw.flush();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        chunk.delete();
                    }
                }
            }
        }
    }

}
