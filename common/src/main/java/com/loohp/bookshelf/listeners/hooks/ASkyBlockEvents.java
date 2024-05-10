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

import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.api.events.PlayerOpenBookshelfEvent;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ASkyBlockEvents implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onASkyBlockCheck(PlayerOpenBookshelfEvent event) {

        if (!Bookshelf.aSkyBlockHook) {
            return;
        }

        Player player = event.getPlayer();
        Location location = event.getLocation();

        if (ASkyBlockAPI.getInstance().getIslandAt(event.getLocation()) == null) {
            return;
        }

        if (!ASkyBlockAPI.getInstance().getIslandAt(location).getOwner().equals(player.getUniqueId())) {
            if (!ASkyBlockAPI.getInstance().getIslandAt(location).getMembers().contains(player.getUniqueId())) {
                String message = ASkyBlock.getPlugin().myLocale(player.getUniqueId()).islandProtected;
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                event.setCancelled(true);
            }
        }
    }

}
