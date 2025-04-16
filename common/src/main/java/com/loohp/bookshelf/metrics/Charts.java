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

package com.loohp.bookshelf.metrics;

import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.BookshelfManager;

import java.util.concurrent.Callable;

public class Charts {

    public static void loadCharts(Metrics metrics) {
        metrics.addCustomChart(new Metrics.SingleLineChart("total_bookshelves_loaded", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return BookshelfManager.getWorlds().stream().mapToInt(each -> {
                    BookshelfManager manager = BookshelfManager.getBookshelfManager(each);
                    return manager == null ? 0 : manager.size();
                }).sum();
            }
        }));

        metrics.addCustomChart(new Metrics.SimplePie("hoppers_enabled", new Callable<String>() {
            @Override
            public String call() throws Exception {
                String string = "Disabled";
                if (Bookshelf.enableHopperSupport) {
                    string = "Enabled";
                }
                return string;
            }
        }));

        metrics.addCustomChart(new Metrics.SimplePie("droppers_enabled", new Callable<String>() {
            @Override
            public String call() throws Exception {
                String string = "Disabled";
                if (Bookshelf.enableDropperSupport) {
                    string = "Enabled";
                }
                return string;
            }
        }));

        metrics.addCustomChart(new Metrics.SimplePie("enchtable_enabled", new Callable<String>() {
            @Override
            public String call() throws Exception {
                String string = "Disabled";
                if (Bookshelf.enchantmentTable) {
                    string = "Enabled";
                }
                return string;
            }
        }));

        metrics.addCustomChart(new Metrics.SingleLineChart("average_hopper_process_time", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int num = Integer.MAX_VALUE;
                if (Bookshelf.lastHopperTime < 2147483647) {
                    num = (int) Bookshelf.lastHopperTime;
                }
                return num;
            }
        }));

        metrics.addCustomChart(new Metrics.SingleLineChart("average_hopper_minecart_process_time", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int num = Integer.MAX_VALUE;
                if (Bookshelf.lastHoppercartTime < 2147483647) {
                    num = (int) Bookshelf.lastHoppercartTime;
                }
                return num;
            }
        }));
    }

}
