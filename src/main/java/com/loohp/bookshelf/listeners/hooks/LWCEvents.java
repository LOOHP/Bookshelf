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

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Permission.Access;
import com.griefcraft.model.Protection;
import com.griefcraft.model.Protection.Type;
import com.griefcraft.scripting.JavaModule;
import com.griefcraft.scripting.event.LWCAccessEvent;
import com.griefcraft.scripting.event.LWCProtectionInteractEvent;
import com.griefcraft.scripting.event.LWCProtectionRegisterEvent;
import com.griefcraft.scripting.event.LWCProtectionRemovePostEvent;
import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.api.events.PlayerOpenBookshelfEvent;
import com.loohp.bookshelf.objectholders.BookshelfHolder;
import com.loohp.bookshelf.objectholders.LWCRequestOpenData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LWCEvents extends JavaModule {

    public void registerLWCModule() {
        LWC.getInstance().getModuleLoader().registerModule(Bookshelf.plugin, new LWCEvents());
    }

    @Override
    public void onAccessRequest(LWCAccessEvent event) {
        if (!event.getPlayer().hasPermission("bookshelf.use")) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(Bookshelf.plugin, () -> {
            Player player = event.getPlayer();
            if (!Bookshelf.requestOpen.containsKey(player)) {
                return;
            }
            LWCRequestOpenData data = Bookshelf.requestOpen.get(player);
            BookshelfHolder bookshelf = data.getBookshelf();
            Protection protection = event.getProtection();
            if (LWC.getInstance().getPlugin().getLWC().canAccessProtection(player, protection) || !event.getAccess().equals(Access.NONE)) {
                if (event.getProtection().getType().equals(Type.DONATION)) {
                    Bookshelf.isDonationView.add(player.getUniqueId());
                }

                PlayerOpenBookshelfEvent playerOpenBookshelfEvent = new PlayerOpenBookshelfEvent(player, bookshelf, data.getBlockFace(), data.isCancelled());
                Bukkit.getPluginManager().callEvent(playerOpenBookshelfEvent);

                if (!playerOpenBookshelfEvent.isCancelled()) {
                    Bukkit.getScheduler().runTask(Bookshelf.plugin, () -> player.openInventory(bookshelf.getInventory()));
                }
            }
            Bookshelf.requestOpen.remove(player);
        }, 1);
    }

    @Override
    public void onPostRemoval(LWCProtectionRemovePostEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        Bookshelf.lwcCancelOpen.add(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(Bookshelf.plugin, () -> Bookshelf.lwcCancelOpen.remove(player.getUniqueId()), 5);
    }

    @Override
    public void onProtectionInteract(LWCProtectionInteractEvent event) {
        Player player = event.getPlayer();
        if (!event.getResult().equals(Result.CANCEL)) {
            return;
        }
        if (player == null) {
            return;
        }
        Bookshelf.lwcCancelOpen.add(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(Bookshelf.plugin, () -> Bookshelf.lwcCancelOpen.remove(player.getUniqueId()), 5);
    }

    @Override
    public void onRegisterProtection(LWCProtectionRegisterEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        Bookshelf.lwcCancelOpen.add(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(Bookshelf.plugin, () -> Bookshelf.lwcCancelOpen.remove(player.getUniqueId()), 5);
    }

}
