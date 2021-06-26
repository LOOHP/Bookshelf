package com.loohp.bookshelf.listeners.hooks;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.api.events.PlayerOpenBookshelfEvent;
import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.PlotSquared;
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

import net.kyori.adventure.text.minimessage.Template;

public class PlotSquared6Events implements Listener {
	
	private static final BlockType ADAPTED_BOOKSHELF_TYPE = BukkitAdapter.asBlockType(Material.BOOKSHELF);

	@SuppressWarnings("unchecked")
	@EventHandler(priority=EventPriority.LOWEST)
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
				for (BlockTypeWrapper blockTypeWarpper : (List<BlockTypeWrapper>) flag.getValue()) {
					if (blockTypeWarpper.accepts(ADAPTED_BOOKSHELF_TYPE)) {
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
		
		try {
			BukkitUtil.adapt(event.getPlayer()).sendMessage(
				TranslatableCaption.of("permission.no_permission_event"),
				Template.of("node", String.valueOf(Permission.PERMISSION_ADMIN_BUILD_OTHER))
            );
		} catch (Exception ignore) {}
		
		event.setCancelled(true);
	}
	
}
