package com.loohp.bookshelf.Listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.loohp.bookshelf.Bookshelf;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class GriefPreventionEvents implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onGPCheck(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (Bookshelf.GriefPreventionHook == false) {
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
		
		if (GriefPrevention.instance.claimsEnabledForWorld(event.getClickedBlock().getWorld()) == false) {
			return;
		}
		
		Location loc = event.getClickedBlock().getLocation();
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, false, null);
		
		if (claim == null) {
			return;
		}
		
		if (claim.allowContainers(player) != null) {
			event.setCancelled(true);
			player.sendMessage(claim.allowContainers(player));
		}
	}
	
}