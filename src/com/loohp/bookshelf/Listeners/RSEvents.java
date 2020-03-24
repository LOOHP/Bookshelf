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

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.loohp.bookshelf.Bookshelf;

public class RSEvents implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onRSCheck(PlayerInteractEvent event) {
		
		if (Bookshelf.RSHook == false) {
			return;
		}
		
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		
		if (!Bookshelf.version.contains("OLD")) {
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
		
		@SuppressWarnings("static-access")
		ClaimedResidence area = Residence.getInstance().getAPI().getResidenceManager().getByLoc(event.getClickedBlock().getLocation());
		
		if (area == null) {
			return;
		}
		
		if (area.getPermissions().playerHas(player, Flags.container, true) == false) {
			event.setCancelled(true);
			String message = Residence.getInstance().getLM().getMessage("Language.Flag.Deny").replace("%1", Flags.container.name());
			player.sendMessage(message);
		}
	}
	
}
