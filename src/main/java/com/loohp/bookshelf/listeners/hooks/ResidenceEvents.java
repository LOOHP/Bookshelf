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

package com.loohp.bookshelf.listeners.hooks;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.api.events.PlayerOpenBookshelfEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ResidenceEvents implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onResidenceCheck(PlayerOpenBookshelfEvent event) {

        if (!Bookshelf.residenceHook) {
            return;
        }

        Player player = event.getPlayer();

        ClaimedResidence area = ResidenceApi.getResidenceManager().getByLoc(event.getLocation());

        if (area == null) {
            return;
        }

        if (!area.getPermissions().playerHas(player, Flags.container, true)) {
            event.setCancelled(true);
            String message = Residence.getInstance().getLM().getMessage("Language.Flag.Deny").replace("%1", Flags.container.name());
            player.sendMessage(message);
        }
    }

}
