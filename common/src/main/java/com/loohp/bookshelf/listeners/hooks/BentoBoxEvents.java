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
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.lists.Flags;

import java.util.Optional;

public class BentoBoxEvents implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBentoBoxCheck(PlayerOpenBookshelfEvent event) {

        if (!Bookshelf.bentoBoxHook) {
            return;
        }

        Player player = event.getPlayer();
        Location location = event.getLocation();

        User user = User.getInstance(player);
        Optional<Island> optisland = BentoBox.getInstance().getIslands().getIslandAt(location);

        if (!optisland.isPresent()) {
            return;
        }

        if (!optisland.get().isAllowed(user, Flags.CONTAINER)) {
            String message = BentoBox.getInstance().getLocalesManager().get("protection.protected").replace("[description]", BentoBox.getInstance().getLocalesManager().get("protection.flags.CONTAINER.hint"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            event.setCancelled(true);
        }
    }

}
