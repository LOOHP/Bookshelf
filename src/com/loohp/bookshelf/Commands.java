package com.loohp.bookshelf;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.scheduler.BukkitRunnable;

import com.loohp.bookshelf.Updater.Updater;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor, TabCompleter {

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
			return true;
		}		
		
		if (args[0].equalsIgnoreCase("update")) {
			if (sender.hasPermission("bookshelf.update")) {
				sender.sendMessage(ChatColor.AQUA + "[Bookshelf] BookShelf written by LOOHP!");
				sender.sendMessage(ChatColor.GOLD + "[Bookshelf] You are running BookShelf version: " + Bookshelf.plugin.getDescription().getVersion());
				new BukkitRunnable() {
					public void run() {
						String version = Updater.checkUpdate();
						if (version.equals("latest")) {
							sender.sendMessage(ChatColor.GREEN + "[Bookshelf] You are running the latest version!");
						} else {
							Updater.sendUpdateMessage(version);
						}
					}
				}.runTaskAsynchronously(Bookshelf.plugin);
			} else {
				sender.sendMessage(Bookshelf.NoPermissionToUpdateMessage);
			}
			return true;
		}
		
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Bukkit.spigot().getConfig().getString("messages.unknown-command")));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> tab = new ArrayList<String>();
		if (!label.equalsIgnoreCase("bookshelf")) {
			return tab;
		}
		
		if (args.length <= 1) {
			if (sender.hasPermission("bookshelf.reload")) {
				tab.add("reload");
			}
			if (sender.hasPermission("bookshelf.update")) {
				tab.add("update");
			}
			return tab;
		}
		return tab;
	}
}
