package com.loohp.bookshelf.Listeners.Hooks;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.API.Events.PlayerOpenBookshelfEvent;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class GriefPreventionEvents implements Listener {

	@EventHandler(priority=EventPriority.LOWEST)
	public void onGriefPreventionCheck(PlayerOpenBookshelfEvent event) {
		
		if (!Bookshelf.GriefPreventionHook) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if (!player.hasPermission("bookshelf.use")) {
			return;
		}
		
		if (!GriefPrevention.instance.claimsEnabledForWorld(event.getLocation().getWorld())) {
			return;
		}
		
		Location loc = event.getLocation();
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, false, null);
		
		if (claim == null) {
			return;
		}
		
		if (claim.allowContainers(player) != null) {
			event.setCancelled(true);
			player.sendMessage(claim.allowContainers(player));
		}
	}
	
}