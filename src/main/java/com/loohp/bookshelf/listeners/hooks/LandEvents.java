package com.loohp.bookshelf.listeners.hooks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.api.events.PlayerOpenBookshelfEvent;

import me.angeschossen.lands.api.flags.Flags;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Area;

public class LandEvents implements Listener {
	
	private static LandsIntegration landsApi = new LandsIntegration(Bookshelf.plugin);

	@EventHandler(priority=EventPriority.LOWEST)
	public void onLandCheck(PlayerOpenBookshelfEvent event) {
		
		if (!Bookshelf.landsHook) {
			return;
		}
		
		Player player = event.getPlayer();
		
		Area area = landsApi.getAreaByLoc(event.getLocation());
		
		if (area == null) {
			return;
		}
		
		if (!area.hasFlag(player, Flags.INTERACT_CONTAINER, true)) {
			event.setCancelled(true);
		}

	}
	
}
