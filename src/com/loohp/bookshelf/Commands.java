package com.loohp.bookshelf;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!label.equalsIgnoreCase("bookshelf")) {
			return true;
		}
		
		if (args.length < 1) {
			sender.sendMessage(ChatColor.AQUA + "[Bookshelf] BookShelf written by LOOHP!");
			sender.sendMessage(ChatColor.GOLD + "[Bookshelf] You are running BookShelf version: " + Bookshelf.plugin.getDescription().getVersion());
			return true;
		}
		
		if (args[0].equalsIgnoreCase("reload")) {
			if (sender.hasPermission("bookshelf.reload")) {
				Bookshelf.plugin.reloadConfig();
				Bookshelf.loadConfig();
				sender.sendMessage(ChatColor.GREEN + "[Bookshelf] BookShelf has been reloaded!");
			} else {
				sender.sendMessage(Bookshelf.NoPermissionToReloadMessage);
			}
		}
		return true;
	}
}
