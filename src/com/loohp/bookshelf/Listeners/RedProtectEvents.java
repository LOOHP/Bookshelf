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

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;

public class RedProtectEvents implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onRPCheck(PlayerInteractEvent event) {
		
		if (Bookshelf.RedProtectHook == false) {
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
		
		if (RedProtect.get().getAPI().getRegion(event.getClickedBlock().getLocation()) == null) {
			return;
		}

		if (!RedProtect.get().getAPI().getRegion(event.getClickedBlock().getLocation()).canChest(player)) {
			player.sendMessage(RedProtect.get().lang.get("_redprotect.prefix") + " " + RedProtect.get().lang.get("playerlistener.region.cantdoor"));
			event.setCancelled(true);
		}
	}
	
}
