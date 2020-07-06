package com.loohp.bookshelf.Listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.loohp.bookshelf.Bookshelf;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.configuration.Captions;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.flag.PlotFlag;
import com.plotsquared.core.plot.flag.implementations.UseFlag;
import com.plotsquared.core.plot.flag.types.BlockTypeWrapper;
import com.plotsquared.core.util.MainUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.block.BlockType;

public class PlotSquared5Events implements Listener {
	
	BlockType worldeditBookshelfBlockType = BukkitAdapter.asBlockType(Material.BOOKSHELF);

	@SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlotSquaredCheck(PlayerInteractEvent event) {
		
		if (!Bookshelf.PlotSquaredHook) {
			return;
		}
		
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		
		if (!Bookshelf.version.isOld()) {
			if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
				return;
			}
		}
		
		org.bukkit.entity.Player bukkitPlayer = event.getPlayer();
		
		if (!bukkitPlayer.hasPermission("bookshelf.use")) {
			return;
		}
		
		if (Bookshelf.cancelOpen.contains(event.getPlayer())) {
			return;
		}
		if (bukkitPlayer.isSneaking()) {
			return;
		}
		if (event.getClickedBlock() == null) {
			return;
		}
		if (!event.getClickedBlock().getType().equals(Material.BOOKSHELF)) {
			return;
		}
		if (event.getBlockFace().equals(BlockFace.UP) || event.getBlockFace().equals(BlockFace.DOWN)) {
			return;
		}
		
		PlotPlayer<?> player = PlotPlayer.wrap(bukkitPlayer);
		
		if (player.hasPermission("plots.admin.interact.other")) {
			return;
		}
		
		org.bukkit.Location bukkitLocation = event.getClickedBlock().getLocation();
		Location location = new Location(bukkitLocation.getWorld().getName(), bukkitLocation.getBlockX(), bukkitLocation.getBlockY(), bukkitLocation.getBlockZ());
		
		PlotArea plotarea = PlotSquared.get().getApplicablePlotArea(location);
		
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
					if (blockTypeWarpper.getBlockType().equals(worldeditBookshelfBlockType)) {
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
			MainUtil.sendMessage(player, Captions.FLAG_TUTORIAL_USAGE, Captions.FLAG_USE);
		} catch (Exception ignore) {}
		
		event.setCancelled(true);
	}
	
}
