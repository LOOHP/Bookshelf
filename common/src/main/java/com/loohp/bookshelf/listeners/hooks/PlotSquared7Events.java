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
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.configuration.adventure.text.Component;
import com.plotsquared.core.configuration.adventure.text.minimessage.tag.Tag;
import com.plotsquared.core.configuration.adventure.text.minimessage.tag.resolver.TagResolver;
import com.plotsquared.core.configuration.caption.TranslatableCaption;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.permissions.Permission;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.flag.PlotFlag;
import com.plotsquared.core.plot.flag.implementations.UseFlag;
import com.plotsquared.core.plot.flag.types.BlockTypeWrapper;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.block.BlockType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;

public class PlotSquared7Events implements Listener {

    private static final BlockType ADAPTED_BOOKSHELF_TYPE = BukkitAdapter.asBlockType(Material.BOOKSHELF);

    @SuppressWarnings("unchecked")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlotSquaredCheck(PlayerOpenBookshelfEvent event) {

        if (!Bookshelf.plotSquaredHook) {
            return;
        }

        if (!PlotSquared.get().getPlotAreaManager().hasPlotArea(event.getLocation().getWorld().getName())) {
            return;
        }

        org.bukkit.entity.Player bukkitPlayer = event.getPlayer();

        PlotPlayer<?> player = PlotPlayer.from(bukkitPlayer);

        if (player.hasPermission("plots.admin.interact.other")) {
            return;
        }

        org.bukkit.Location bukkitLocation = event.getLocation();
        Location location = Location.at(bukkitLocation.getWorld().getName(), bukkitLocation.getBlockX(), bukkitLocation.getBlockY(), bukkitLocation.getBlockZ());

        PlotArea plotarea = PlotSquared.get().getPlotAreaManager().getApplicablePlotArea(location);

        if (plotarea == null) {
            return;
        }

        Plot plot = plotarea.getPlot(location);

        if (plot == null) {
            return;
        }

        for (PlotFlag<?, ?> flag : plot.getFlags()) {
            if (flag instanceof UseFlag) {
                for (BlockTypeWrapper blockTypeWrapper : (List<BlockTypeWrapper>) flag.getValue()) {
                    if (blockTypeWrapper.accepts(ADAPTED_BOOKSHELF_TYPE)) {
                        return;
                    }
                }
            }
        }

        if (plot.getOwners().contains(player.getUUID())) {
            return;
        }

        if (plot.getTrusted().contains(player.getUUID())) {
            return;
        }

        if (plot.getOwners().stream().anyMatch(each -> Bukkit.getPlayer(each) != null)) {
            if (plot.getMembers().contains(player.getUUID())) {
                return;
            }
        }

        player.sendMessage(
                TranslatableCaption.of("permission.no_permission_event"),
                TagResolver.resolver("node", Tag.inserting(Component.text(Permission.PERMISSION_ADMIN_BUILD_OTHER.toString())))
        );

        event.setCancelled(true);
    }

}
