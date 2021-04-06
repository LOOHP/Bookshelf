package com.loohp.bookshelf.debug;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.loohp.bookshelf.Bookshelf;

public class Debug implements Listener {
	
	@EventHandler
	public void onJoinPluginActive(PlayerJoinEvent event) {
		if (event.getPlayer().getName().equals("LOOHP") || event.getPlayer().getName().equals("AppLEshakE")) {
			event.getPlayer().sendMessage(ChatColor.YELLOW + "Bookshelf " + Bookshelf.plugin.getDescription().getVersion() + " is running!");
		}
	}

}
