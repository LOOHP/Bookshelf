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

package com.loohp.bookshelf.listeners.hooks;

import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.api.events.PlayerOpenBookshelfEvent;
import me.angeschossen.lands.api.flags.Flags;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class LandEvents implements Listener {

    private static final LandsIntegration landsApi = new LandsIntegration(Bookshelf.plugin);

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLandCheck(PlayerOpenBookshelfEvent event) {

        if (!Bookshelf.landsHook) {
            return;
        }

        Player player = event.getPlayer();

        Area area = landsApi.getAreaByLoc(event.getLocation());

        if (area == null) {
            return;
        }

        if (!area.hasFlag(player, Flags.INTERACT_CONTAINER, true)) {
            event.setCancelled(true);
        }

    }

}
