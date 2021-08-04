package com.loohp.bookshelf.listeners.hooks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.griefdefender.api.ClanPlayer;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.TrustTypes;
import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.api.events.PlayerOpenBookshelfEvent;

public class GriefDefenderEvents implements Listener {

	@EventHandler(priority=EventPriority.LOWEST)
	public void onLandCheck(PlayerOpenBookshelfEvent event) {
		
		if (!Bookshelf.griefDefenderHook) {
			return;
		}
		
		Player player = event.getPlayer();
		
		Claim claim = GriefDefender.getCore().getClaimAt(event.getLocation());
		ClanPlayer clanPlayer = GriefDefender.getCore().getClanProvider().getClanPlayer(player.getUniqueId());
		
		if (claim == null) {
			return;
		}
		
		if (!claim.getOwnerUniqueId().equals(player.getUniqueId()) && !claim.isUserTrusted(player.getUniqueId(), TrustTypes.CONTAINER) && (clanPlayer == null || !claim.isClanTrusted(clanPlayer.getClan(), TrustTypes.CONTAINER))) {
			event.setCancelled(true);
		}

	}
	
}
