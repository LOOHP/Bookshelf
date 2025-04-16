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

package com.loohp.bookshelf.updater;

import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.utils.HTTPRequestUtils;
import com.loohp.platformscheduler.Scheduler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.simple.JSONObject;

public class Updater implements Listener {

    public static final String PLUGIN_NAME = "Bookshelf";

    public static void sendUpdateMessage(CommandSender sender, String version, int spigotPluginId) {
        sendUpdateMessage(sender, version, spigotPluginId, false);
    }

    @SuppressWarnings("deprecation")
    public static void sendUpdateMessage(CommandSender sender, String version, int spigotPluginId, boolean devbuild) {
        if (!version.equals("error")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!devbuild) {
                    player.sendMessage(ChatColor.YELLOW + "[Bookshelf] A new version is available on SpigotMC: " + version);
                    TextComponent url = new TextComponent(ChatColor.GOLD + "https://www.spigotmc.org/resources/" + spigotPluginId);
                    url.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.AQUA + "Click me!").create()));
                    url.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/" + spigotPluginId));
                    player.spigot().sendMessage(url);
                } else {
                    sender.sendMessage(ChatColor.GREEN + "[Bookshelf] You are running the latest release!");
                    TextComponent url = new TextComponent(ChatColor.YELLOW + "[Bookshelf] However, a new Development Build is available if you want to try that!");
                    url.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.AQUA + "Click me!").create()));
                    url.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://ci.loohpjames.com/job/" + PLUGIN_NAME));
                    player.spigot().sendMessage(url);
                }
            } else {
                if (!devbuild) {
                    sender.sendMessage(ChatColor.YELLOW + "[Bookshelf] A new version is available on SpigotMC: " + version);
                    sender.sendMessage(ChatColor.GOLD + "Download: https://www.spigotmc.org/resources/" + spigotPluginId);
                } else {
                    sender.sendMessage(ChatColor.GREEN + "[Bookshelf] You are running the latest release!");
                    sender.sendMessage(ChatColor.YELLOW + "[Bookshelf] However, a new Development Build is available if you want to try that!");
                }
            }
        }
    }

    public static UpdaterResponse checkUpdate() {
        try {
            String localPluginVersion = Bookshelf.plugin.getDescription().getVersion();
            JSONObject response = (JSONObject) HTTPRequestUtils.getJSONResponse("https://api.loohpjames.com/spigot/data").get(PLUGIN_NAME);
            String spigotPluginVersion = (String) ((JSONObject) response.get("latestversion")).get("release");
            String devBuildVersion = (String) ((JSONObject) response.get("latestversion")).get("devbuild");
            int spigotPluginId = (int) (long) ((JSONObject) response.get("spigotmc")).get("pluginid");
            int posOfThirdDot = localPluginVersion.indexOf(".", localPluginVersion.indexOf(".", localPluginVersion.indexOf(".") + 1) + 1);
            Version currentDevBuild = new Version(localPluginVersion);
            Version currentRelease = new Version(localPluginVersion.substring(0, posOfThirdDot >= 0 ? posOfThirdDot : localPluginVersion.length()));
            Version spigotmc = new Version(spigotPluginVersion);
            Version devBuild = new Version(devBuildVersion);
            if (currentRelease.compareTo(spigotmc) < 0) {
                return new UpdaterResponse(spigotPluginVersion, spigotPluginId, currentDevBuild.compareTo(devBuild) >= 0);
            } else {
                return new UpdaterResponse("latest", spigotPluginId, currentDevBuild.compareTo(devBuild) >= 0);
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Bookshelf] Failed to check against \"api.loohpjames.com\" for the latest version.. It could be an internet issue or \"api.loohpjames.com\" is down. If you want disable the update checker, you can disable in config.yml, but we still highly-recommend you to keep your plugin up to date!");
        }
        return new UpdaterResponse("error", -1, false);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Scheduler.runTaskLaterAsynchronously(Bookshelf.plugin, () -> {
            if (Bookshelf.updaterEnabled) {
                Player player = event.getPlayer();
                if (player.hasPermission("Bookshelf.update")) {
                    UpdaterResponse version = Updater.checkUpdate();
                    if (!version.getResult().equals("latest")) {
                        Updater.sendUpdateMessage(player, version.getResult(), version.getSpigotPluginId());
                    }
                }
            }
        }, 100);
    }

    public static class UpdaterResponse {

        private final String result;
        private final int spigotPluginId;
        private final boolean devBuildIsLatest;

        public UpdaterResponse(String result, int spigotPluginId, boolean devBuildIsLatest) {
            this.result = result;
            this.spigotPluginId = spigotPluginId;
            this.devBuildIsLatest = devBuildIsLatest;
        }

        public String getResult() {
            return result;
        }

        public int getSpigotPluginId() {
            return spigotPluginId;
        }

        public boolean isDevBuildLatest() {
            return devBuildIsLatest;
        }

    }

}
