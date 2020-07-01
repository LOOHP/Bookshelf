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
import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.ASkyBlockAPI;

import net.md_5.bungee.api.ChatColor;

public class ASkyBlockEvents implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onASkyBlockCheck(PlayerInteractEvent event) {
		
		if (Bookshelf.ASkyBlockHook == false) {
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
		
		if (ASkyBlockAPI.getInstance().getIslandAt(event.getClickedBlock().getLocation()) == null) {
			return;
		}
		
		if (!ASkyBlockAPI.getInstance().getIslandAt(event.getClickedBlock().getLocation()).getOwner().equals(player.getUniqueId())) {
			if (!ASkyBlockAPI.getInstance().getIslandAt(event.getClickedBlock().getLocation()).getMembers().contains(player.getUniqueId())) {
				String message = ASkyBlock.getPlugin().myLocale(player.getUniqueId()).islandProtected;
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
				event.setCancelled(true);
				
			}
		}
	}
	
}
