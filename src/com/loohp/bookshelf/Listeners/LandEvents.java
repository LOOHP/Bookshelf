package com.loohp.bookshelf.Listeners;

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

import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.role.enums.RoleSetting;

public class LandEvents implements Listener {
	
	private static LandsIntegration landsAddon;
	
	public static void setup() {
		landsAddon = (landsAddon == null || !(landsAddon instanceof LandsIntegration)) ? new LandsIntegration(Bookshelf.plugin, false) : landsAddon;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLandCheck(PlayerInteractEvent event) {
		
		if (!Bookshelf.LandHook) {
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
		if (player.isSneaking()) {
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
		
		Land land = landsAddon.getLand(event.getClickedBlock().getLocation());
		
		if (land == null || !land.exists()) {
			return;
		}
		
		if (!land.canSetting(player, RoleSetting.INTERACT_CONTAINER, true)) {
			event.setCancelled(true);
		}

	}
	
}
