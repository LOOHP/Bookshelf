package com.loohp.bookshelf.Listeners;

import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.google.common.collect.Lists;
import com.loohp.bookshelf.Bookshelf;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.RegionResultSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import net.md_5.bungee.api.ChatColor;

public class WorldGuardEvents implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onWGCheck(PlayerInteractEvent event) {
		
		if (Bookshelf.WorldGuardHook == false) {
			return;
		}
		
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		
		if (!Bookshelf.version.isOld()) {
			if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
				return;
			}
		}
		
		Player player = event.getPlayer();
		
		if (!player.hasPermission("bookshelf.use")) {
			return;
		}
		
		if (Bookshelf.cancelOpen.contains(event.getPlayer())) {
			return;
		}
		if (player.isSneaking() == true) {
			return;
		}
		if (event.getClickedBlock() == null) {
			return;
		}
		if (!event.getClickedBlock().getType().equals(Material.BOOKSHELF)) {
			return;
		}
		if (event.getBlockFace().equals(BlockFace.UP) || event.getBlockFace().equals(BlockFace.DOWN)) {
			return;
		}
		
		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
		boolean canBypass = WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld());
		if (canBypass == true) {
			return;
		}
		
		com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(event.getClickedBlock().getLocation().clone());
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		ApplicableRegionSet set = query.getApplicableRegions(loc);
		
		boolean isGlobal = false;
		if (set.size() == 0) {
			isGlobal = true;
		}
		
		if (testFlag(query, Flags.CHEST_ACCESS, loc, localPlayer).equals("false")) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hey!" + ChatColor.GRAY + " Sorry, but you can't open that here.");
			player.playEffect(event.getClickedBlock().getLocation().clone().add(0, 1, 0), Effect.SMOKE, 4);
			return;
		} else if (testFlag(query, Flags.CHEST_ACCESS, loc, localPlayer).equals("true")) {
			return;
		} else {
			if (testFlag(query, Flags.BUILD, loc, localPlayer).equals("false")) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hey!" + ChatColor.GRAY + " Sorry, but you can't open that here.");
				player.playEffect(event.getClickedBlock().getLocation().clone().add(0, 1, 0), Effect.SMOKE, 4);
				return;
			} else if (testFlag(query, Flags.BUILD, loc, localPlayer).equals("true")) {
				return;
			} else {
				if (isGlobal == false) {
					List<ProtectedRegion> regions = Lists.newArrayList();
					
					for (ProtectedRegion reg : set.getRegions()) {
						regions.add(reg);
					}
					
					ApplicableRegionSet setNoGlobal = new RegionResultSet(regions, null);
					
					if (setTestFlag(setNoGlobal, Flags.PASSTHROUGH, localPlayer).equals("false")) {
						event.setCancelled(true);
						player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hey!" + ChatColor.GRAY + " Sorry, but you can't open that here.");
						player.playEffect(event.getClickedBlock().getLocation().clone().add(0, 1, 0), Effect.SMOKE, 4);
						return;
					} else if (setTestFlag(setNoGlobal, Flags.PASSTHROUGH, localPlayer).equals("null")) {
						event.setCancelled(true);
						player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hey!" + ChatColor.GRAY + " Sorry, but you can't open that here.");
						player.playEffect(event.getClickedBlock().getLocation().clone().add(0, 1, 0), Effect.SMOKE, 4);
						return;
					} else {
						return;
					}
				} else {
					List<ProtectedRegion> regions = Lists.newArrayList();
					
					regions.add(WorldGuard.getInstance().getPlatform().getRegionContainer().get(localPlayer.getWorld()).getRegion("__global__"));
					ApplicableRegionSet setOnlyGlobal = new RegionResultSet(regions, null);
					if (setTestFlag(setOnlyGlobal, Flags.PASSTHROUGH, localPlayer).equals("false")) {
						event.setCancelled(true);
						player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hey!" + ChatColor.GRAY + " Sorry, but you can't open that here.");
						player.playEffect(event.getClickedBlock().getLocation().clone().add(0, 1, 0), Effect.SMOKE, 4);
						return;
					} else {
						return;
					}
				}
			}
		}
	}
	
	public String testFlag(RegionQuery query, StateFlag flag, com.sk89q.worldedit.util.Location loc, LocalPlayer localPlayer) {
		if (query.queryState(loc, localPlayer, flag) == null) {
			return "null";	
		} else {
			if (query.queryState(loc, localPlayer, flag).equals(State.ALLOW)) {
				return "true";
			} else {
				return "false";
			}
		}		
	}
	
	public String setTestFlag(ApplicableRegionSet set, StateFlag flag, LocalPlayer localPlayer) {
		if (set.queryState(localPlayer, flag) == null) {
			return "null";	
		} else {
			if (set.queryState(localPlayer, flag).equals(State.ALLOW)) {
				return "true";
			} else {
				return "false";
			}
		}		
	}
	
}
