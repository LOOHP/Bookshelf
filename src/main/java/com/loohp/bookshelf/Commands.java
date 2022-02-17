/*
 * This file is part of Bookshelf.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.loohp.bookshelf;

import com.loohp.bookshelf.updater.Updater;
import com.loohp.bookshelf.updater.Updater.UpdaterResponse;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.LinkedList;
import java.util.List;

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
                Bookshelf.loadConfig();
                sender.sendMessage(ChatColor.GREEN + "[Bookshelf] BookShelf has been reloaded!");
            } else {
                sender.sendMessage(Bookshelf.noPermissionToReloadMessage);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("update")) {
            if (sender.hasPermission("bookshelf.update")) {
                sender.sendMessage(ChatColor.AQUA + "[Bookshelf] BookShelf written by LOOHP!");
                sender.sendMessage(ChatColor.GOLD + "[Bookshelf] You are running BookShelf version: " + Bookshelf.plugin.getDescription().getVersion());
                Bukkit.getScheduler().runTaskAsynchronously(Bookshelf.plugin, () -> {
                    UpdaterResponse version = Updater.checkUpdate();
                    if (version.getResult().equals("latest")) {
                        if (version.isDevBuildLatest()) {
                            sender.sendMessage(ChatColor.GREEN + "[Bookshelf] You are running the latest version!");
                        } else {
                            Updater.sendUpdateMessage(sender, version.getResult(), version.getSpigotPluginId(), true);
                        }
                    } else {
                        Updater.sendUpdateMessage(sender, version.getResult(), version.getSpigotPluginId());
                    }
                });
            } else {
                sender.sendMessage(Bookshelf.noPermissionToUpdateMessage);
            }
            return true;
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Bukkit.spigot().getConfig().getString("messages.unknown-command")));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> tab = new LinkedList<>();
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
