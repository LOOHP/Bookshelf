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

import net.md_5.bungee.api.ChatColor;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.lists.Flags;

public class BentoBoxEvents implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBBCheck(PlayerInteractEvent event) {
		
		if (Bookshelf.BentoBoxHook == false) {
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
		
		User user = User.getInstance(player);
		  
		if (!BentoBox.getInstance().getIslands().getIslandAt(event.getClickedBlock().getLocation()).isPresent()) {
			return;
		}
			
		if (!BentoBox.getInstance().getIslands().getIslandAt(event.getClickedBlock().getLocation()).get().isAllowed(user, Flags.CONTAINER)) {
			String message = BentoBox.getInstance().getLocalesManager().get("protection.protected").replace("[description]", BentoBox.getInstance().getLocalesManager().get("protection.flags.CONTAINER.hint"));
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
			event.setCancelled(true);
		}
	}
	
}
