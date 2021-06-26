package com.loohp.bookshelf.listeners.hooks;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.api.events.PlayerOpenBookshelfEvent;
import com.plotsquared.core.PlotSquared;
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

@SuppressWarnings("removal")
public class PlotSquared5Events implements Listener {
	
	private static final BlockType ADAPTED_BOOKSHELF_TYPE = BukkitAdapter.asBlockType(Material.BOOKSHELF);
	
	private static Method hasPlotAreaMethod;
	private static Method wrap;
	private static Constructor<Location> locationConstructor;
	private static Method getApplicablePlotAreaMethod;
	private static Object FLAG_TUTORIAL_USAGE;
	private static Object FLAG_USE;
	private static Method sendMessage;
	
	static {
		try {
			hasPlotAreaMethod = PlotSquared.get().getClass().getMethod("hasPlotArea", String.class);
			wrap = PlotPlayer.class.getMethod("wrap", org.bukkit.entity.Player.class);
			locationConstructor = Location.class.getConstructor(String.class, int.class, int.class, int.class);
			getApplicablePlotAreaMethod = PlotSquared.get().getClass().getMethod("getApplicablePlotArea", Location.class);
			Class<?> captionClass = Class.forName("com.plotsquared.core.configuration.Captions");
			Object[] captionEnum = captionClass.getEnumConstants();
			FLAG_TUTORIAL_USAGE = Stream.of(captionEnum).filter(each -> each.toString().equals("FLAG_TUTORIAL_USAGE")).findFirst().get();
			FLAG_USE = Stream.of(captionEnum).filter(each -> each.toString().equals("FLAG_USE")).findFirst().get();
			sendMessage = MainUtil.class.getMethod("sendMessage", PlotPlayer.class, captionClass, captionClass);
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlotSquaredCheck(PlayerOpenBookshelfEvent event) {
		try {
			if (!Bookshelf.plotSquaredHook) {
				return;
			}
			
			if (!(boolean) hasPlotAreaMethod.invoke(PlotSquared.get(), event.getLocation().getWorld().getName())) {
				return;
			}
			
			org.bukkit.entity.Player bukkitPlayer = event.getPlayer();
			
			PlotPlayer<?> player = (PlotPlayer<?>) wrap.invoke(null, bukkitPlayer);
			
			if (player.hasPermission("plots.admin.interact.other")) {
				return;
			}
			
			org.bukkit.Location bukkitLocation = event.getLocation();
			Location location = locationConstructor.newInstance(bukkitLocation.getWorld().getName(), bukkitLocation.getBlockX(), bukkitLocation.getBlockY(), bukkitLocation.getBlockZ());
			
			PlotArea plotarea = (PlotArea) getApplicablePlotAreaMethod.invoke(PlotSquared.get(), location);
			
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
				sendMessage.invoke(null, player, FLAG_TUTORIAL_USAGE, FLAG_USE);
			} catch (Exception ignore) {}
			
			event.setCancelled(true);
		} catch (Exception e) {
			event.setCancelled(true);
		}
	}
	
}
