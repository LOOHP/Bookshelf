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

import com.griefdefender.api.ClanPlayer;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.TrustTypes;
import com.griefdefender.api.provider.ClanProvider;
import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.api.events.PlayerOpenBookshelfEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class GriefDefenderEvents implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLandCheck(PlayerOpenBookshelfEvent event) {

        if (!Bookshelf.griefDefenderHook) {
            return;
        }

        Player player = event.getPlayer();

        Claim claim = GriefDefender.getCore().getClaimAt(event.getLocation());
        ClanProvider clanProvider = GriefDefender.getCore().getClanProvider();
        ClanPlayer clanPlayer = null;
        if (clanProvider != null) {
            clanPlayer = clanProvider.getClanPlayer(player.getUniqueId());
        }

        if (claim == null || claim.isWilderness()) {
            return;
        }

        if (!claim.getOwnerUniqueId().equals(player.getUniqueId()) && !claim.isUserTrusted(player.getUniqueId(), TrustTypes.CONTAINER) && (clanPlayer == null || !claim.isClanTrusted(clanPlayer.getClan(), TrustTypes.CONTAINER))) {
            event.setCancelled(true);
        }

    }

}
