package com.loohp.bookshelf.listeners.hooks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.api.events.PlayerOpenBookshelfEvent;

public class ResidenceEvents implements Listener {

	@EventHandler(priority=EventPriority.LOWEST)
	public void onResidenceCheck(PlayerOpenBookshelfEvent event) {
		
		if (!Bookshelf.residenceHook) {
			return;
		}
		
		Player player = event.getPlayer();
		
		ClaimedResidence area = ResidenceApi.getResidenceManager().getByLoc(event.getLocation());
		
		if (area == null) {
			return;
		}
		
		if (!area.getPermissions().playerHas(player, Flags.container, true)) {
			event.setCancelled(true);
			String message = Residence.getInstance().getLM().getMessage("Language.Flag.Deny").replace("%1", Flags.container.name());
			player.sendMessage(message);
		}
	}
	
}
