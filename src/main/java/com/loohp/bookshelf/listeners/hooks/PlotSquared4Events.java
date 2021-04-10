package com.loohp.bookshelf.listeners.hooks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.github.intellectualsites.plotsquared.plot.PlotSquared;
import com.github.intellectualsites.plotsquared.plot.config.Captions;
import com.github.intellectualsites.plotsquared.plot.flag.Flags;
import com.github.intellectualsites.plotsquared.plot.object.Location;
import com.github.intellectualsites.plotsquared.plot.object.Plot;
import com.github.intellectualsites.plotsquared.plot.object.PlotArea;
import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;
import com.github.intellectualsites.plotsquared.plot.util.MainUtil;
import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.api.events.PlayerOpenBookshelfEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.block.BlockState;

public class PlotSquared4Events implements Listener {
	
	private static final BlockState ADAPTED_BOOKSHELF_TYPE = BukkitAdapter.adapt(Bukkit.createBlockData(Material.BOOKSHELF));

	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlotSquaredCheck(PlayerOpenBookshelfEvent event) {
		
		if (!Bookshelf.plotSquaredHook) {
			return;
		}
		
		if (!PlotSquared.get().hasPlotArea(event.getLocation().getWorld().getName())) {
			return;
		}
		
		org.bukkit.entity.Player bukkitPlayer = event.getPlayer();
		
		PlotPlayer player = PlotPlayer.wrap(bukkitPlayer);
		
		if (player.hasPermission("plots.admin.interact.other")) {
			return;
		}
		
		org.bukkit.Location bukkitLocation = event.getLocation();
		Location location = new Location(bukkitLocation.getWorld().getName(), bukkitLocation.getBlockX(), bukkitLocation.getBlockY(), bukkitLocation.getBlockZ());
		
		PlotArea plotarea = PlotSquared.get().getApplicablePlotArea(location);
		
		if (plotarea == null) {
			return;
		}
		
		Plot plot = plotarea.getPlot(location);
		
		if (plot == null) {
			return;
		}
		
		if (Flags.USE.contains(plot, ADAPTED_BOOKSHELF_TYPE)) {
			return;
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
			MainUtil.sendMessage(player, Captions.FLAG_TUTORIAL_USAGE, Captions.FLAG_USE);
		} catch (Exception ignore) {}
		
		event.setCancelled(true);
	}
	
}
