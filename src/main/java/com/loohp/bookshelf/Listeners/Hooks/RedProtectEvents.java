package com.loohp.bookshelf.Listeners.Hooks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.API.Events.PlayerOpenBookshelfEvent;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;

public class RedProtectEvents implements Listener {

	@EventHandler(priority=EventPriority.LOWEST)
	public void onRedProtectCheck(PlayerOpenBookshelfEvent event) {
		
		if (!Bookshelf.redProtectHook) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if (RedProtect.get().getAPI().getRegion(event.getLocation()) == null) {
			return;
		}

		if (!RedProtect.get().getAPI().getRegion(event.getLocation()).canChest(player)) {
			player.sendMessage(RedProtect.get().getLanguageManager().get("_redprotect.prefix") + " " + RedProtect.get().getLanguageManager().get("playerlistener.region.cantdoor"));
			event.setCancelled(true);
		}
	}
	
}
