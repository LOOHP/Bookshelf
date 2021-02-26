package com.loohp.bookshelf.Listeners.Hooks;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.API.Events.PlayerOpenBookshelfEvent;
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.ASkyBlockAPI;

import net.md_5.bungee.api.ChatColor;

public class ASkyBlockEvents implements Listener {

	@EventHandler(priority=EventPriority.LOWEST)
	public void onASkyBlockCheck(PlayerOpenBookshelfEvent event) {
		
		if (!Bookshelf.aSkyBlockHook) {
			return;
		}
		
		Player player = event.getPlayer();
		Location location = event.getLocation();
		
		if (ASkyBlockAPI.getInstance().getIslandAt(event.getLocation()) == null) {
			return;
		}
		
		if (!ASkyBlockAPI.getInstance().getIslandAt(location).getOwner().equals(player.getUniqueId())) {
			if (!ASkyBlockAPI.getInstance().getIslandAt(location).getMembers().contains(player.getUniqueId())) {
				String message = ASkyBlock.getPlugin().myLocale(player.getUniqueId()).islandProtected;
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
				event.setCancelled(true);				
			}
		}
	}
	
}
